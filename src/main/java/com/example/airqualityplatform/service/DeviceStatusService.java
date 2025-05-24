package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.DeviceStatus;
import com.example.airqualityplatform.dto.external.DeviceStatusExternalDto;
import com.example.airqualityplatform.dto.mapper.DeviceStatusMapper;
import com.example.airqualityplatform.repository.DeviceRepository;
import com.example.airqualityplatform.repository.DeviceStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class DeviceStatusService {

    private final SmartThingsTokenService tokenService;
    private final DeviceRepository deviceRepo;
    private final DeviceStatusRepository statusRepo;
    private final RestTemplate rt;
    private final String baseUrl;

    public DeviceStatusService(
            SmartThingsTokenService tokenService,
            DeviceRepository deviceRepo,
            DeviceStatusRepository statusRepo,
            RestTemplate rt,
            @Value("${smartthings.api.base-url}") String baseUrl
    ) {
        this.tokenService = tokenService;
        this.deviceRepo    = deviceRepo;
        this.statusRepo    = statusRepo;
        this.rt            = rt;
        this.baseUrl       = baseUrl;
    }

    /**
     * 단일 하드웨어 ID(deviceId)로 SmartThings 상태 조회 → 저장
     */
    public DeviceStatus fetchAndSaveStatusByDeviceId(String deviceId) {
        // 1) hardwareId 컬럼(device_id)으로 로컬 Device 조회
        Device device = deviceRepo.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Device not found for hardware id: " + deviceId));

        // 2) SmartThings API 호출: GET /devices/{deviceId}/status
        String url = String.format("%s/devices/%s/status", baseUrl, deviceId);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getCurrentToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<DeviceStatusExternalDto> resp = rt.exchange(
                url, HttpMethod.GET, req, DeviceStatusExternalDto.class
        );

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("Failed to fetch status for hardware id: " + deviceId);
        }

        // 3) 매핑·저장
        DeviceStatus entity = DeviceStatusMapper.toEntity(resp.getBody(), device);
        return statusRepo.save(entity);
    }

    /**
     * 등록된 모든 디바이스(하드웨어 ID 기준)를 순회하며 상태 동기화
     */
    public void fetchAndSaveAllStatuses() {
        List<Device> all = deviceRepo.findAll();
        for (Device d : all) {
            try {
                fetchAndSaveStatusByDeviceId(d.getDeviceId());
            } catch (Exception e) {
                log.error("Failed to fetch status for hardware id {}: {}", d.getDeviceId(), e.getMessage());
            }
        }
    }
}
