// src/main/java/com/example/airqualityplatform/service/DeviceControlService.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.dto.request.DeviceControlRequestDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.DeviceAutoControlRepository;
import com.example.airqualityplatform.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceControlService {

    private final DeviceRepository deviceRepo;
    private final DeviceAutoControlRepository policyRepo;
    private final SmartThingsTokenService tokenService;
    private final RestTemplate restTemplate;

    @Value("${smartthings.api.base-url}")
    private String baseUrl;

    /**
     * 1) 로컬 DB에서 자동제어 정책 연결 해제 (pause)
     * 2) SmartThings API 를 통해 수동제어 커맨드 전송
     */
    @Transactional
    public void sendManualCommands(String deviceId, DeviceControlRequestDto dto) {
        // 1) device 조회
        Device device = deviceRepo.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "기기를 찾을 수 없습니다. deviceId=" + deviceId));

        // 2) 자동제어 정책이 연결되어 있으면 분리
        if (device.getPolicy() != null) {
            Long policyId = device.getPolicy().getControlId();
            log.info("Device {} 에 설정된 자동제어 정책(id={})를 일시 해제합니다.", deviceId, policyId);
            device.setPolicy(null);
            deviceRepo.save(device);
        }

        // 3) SmartThings API 호출 준비
        String url = String.format("%s/devices/%s/commands", baseUrl, deviceId);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getCurrentToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<DeviceControlRequestDto> req = new HttpEntity<>(dto, headers);

        // 4) API 호출
        try {
            ResponseEntity<Void> resp = restTemplate.exchange(url, HttpMethod.POST, req, Void.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                log.error("SmartThings 명령 실패: deviceId={}, status={}", deviceId, resp.getStatusCode());
                throw new IllegalStateException("SmartThings command failed: " + resp.getStatusCode());
            }
            log.info("SmartThings 명령 전송 성공: deviceId={}", deviceId);
        } catch (HttpStatusCodeException e) {
            log.error("SmartThings API 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalStateException("SmartThings API error: " + e.getStatusCode(), e);
        }
    }

    /**
     *  저장된 정책(policyId)을 해당 기기에 다시 연결(resume)하여
     *  자동제어를 재개합니다.
     */
    @Transactional
    public void resumeAutoControl(String deviceId, Long policyId) {
        Device device = deviceRepo.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "기기를 찾을 수 없습니다. deviceId=" + deviceId));

        DeviceAutoControl policy = policyRepo.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "정책을 찾을 수 없습니다. policyId=" + policyId));

        device.setPolicy(policy);
        deviceRepo.save(device);
        log.info("Device {} 에 정책(id={}) 재적용 완료", deviceId, policyId);
    }
}
