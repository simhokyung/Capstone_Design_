package com.example.airqualityplatform.domain;

import com.example.airqualityplatform.service.DeviceStatusService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeviceStatusScheduler {

    private final DeviceStatusService statusService;

    public DeviceStatusScheduler(DeviceStatusService statusService) {
        this.statusService = statusService;
    }

    /**
     * 애플리케이션 시작 후 1분마다,
     * 등록된 모든 디바이스의 최신 상태를 SmartThings 에서 가져와 저장합니다.
     */
    @Scheduled(fixedDelay = 60 * 1000)
    public void syncAllDeviceStatuses() {
        statusService.fetchAndSaveAllStatuses();
    }
}
