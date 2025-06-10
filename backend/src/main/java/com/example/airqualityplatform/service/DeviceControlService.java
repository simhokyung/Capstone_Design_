package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.dto.request.DeviceControlRequestDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceControlService {

    private final DeviceRepository deviceRepo;
    private final SmartThingsTokenService tokenService;
    private final RestTemplate rt;
    @Value("${smartthings.api.base-url}") private String baseUrl;

    /** 정책 해제 없이 바로 커맨드 전송만 할 때(자동제어용) */
    public void sendCommandsRaw(String deviceId, DeviceControlRequestDto dto) {
        String url = baseUrl + "/devices/" + deviceId + "/commands";
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(tokenService.getCurrentToken());
        h.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DeviceControlRequestDto> req = new HttpEntity<>(dto, h);
        try {
            var resp = rt.exchange(url, HttpMethod.POST, req, Void.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("SmartThings failed: " + resp.getStatusCode());
            }
            log.info("SmartThings 제어 성공: {}", deviceId);
        } catch (HttpStatusCodeException e) {
            log.error("SmartThings API 오류: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalStateException(e);
        }
    }

    /** 사용자 수동제어: 기존 동작과 동일하게 정책 해제 후 전송 */
    @Transactional
    public void sendManualCommands(String deviceId, DeviceControlRequestDto dto) {
        Device d = deviceRepo.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("기기가 없습니다. deviceId=" + deviceId));
        if (d.getPolicy() != null) {
            d.setPolicy(null);
            deviceRepo.save(d);
            log.info("정책 일시 해제: {}", deviceId);
        }
        sendCommandsRaw(deviceId, dto);
    }
}
