package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.AiControlBatch;
import com.example.airqualityplatform.domain.AiControlSegment;
import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.dto.request.AiControlBatchRequestDto;
import com.example.airqualityplatform.dto.request.DeviceControlRequestDto;
import com.example.airqualityplatform.dto.response.AiControlBatchResponseDto;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.AiControlBatchRepository;
import com.example.airqualityplatform.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiControlService {

    private final AiControlBatchRepository batchRepo;
    private final TaskScheduler            scheduler;
    private final DeviceControlService     controlService;
    private final NotificationService      notificationService;
    private final DeviceRepository         deviceRepo;

    /**
     * AI에서 내려준 제어 배치 수신 및 스케줄링
     */
    @Transactional
    public AiControlBatchResponseDto ingestControl(AiControlBatchRequestDto dto) {
        Instant originalTs = Instant.parse(dto.getTimestamp());
        Instant now        = Instant.now();
        Instant batchTs    = originalTs.isAfter(now) ? originalTs : now;
        log.info("[ingestControl] originalTs={}, now={}, batchTs={}", originalTs, now, batchTs);

        AiControlBatch batch = AiControlBatch.builder()
                .timestamp(batchTs)
                .deviceId(dto.getDeviceId())
                .build();
        dto.getControlResult().forEach(segDto ->
                batch.addSegment(AiControlSegment.builder()
                        .startMinute(segDto.getStartMinute())
                        .endMinute(segDto.getEndMinute())
                        .airPurifier(segDto.getAirPurifier())
                        .fanMode(segDto.getFanMode())
                        .ventilation(segDto.getVentilation())
                        .build())
        );
        AiControlBatch saved = batchRepo.save(batch);

        saved.getSegments().forEach(seg -> {
            Instant desired = batchTs.plus(seg.getStartMinute(), ChronoUnit.MINUTES);
            Instant exec    = desired.isAfter(now) ? desired : now;
            log.info("[ingestControl] Schedule segment startMinute={} (desired={}, exec={})",
                    seg.getStartMinute(), desired, exec);
            scheduler.schedule(() -> applySegment(saved.getDeviceId(), seg), Date.from(exec));
        });

        return AiControlBatchResponseDto.fromEntity(saved);
    }

    /**
     * 세그먼트 실제 적용: SmartThings 호출 및 알림
     */
    private void applySegment(String deviceId, AiControlSegment seg) {
        List<DeviceControlRequestDto.Command> cmds = new ArrayList<>();

        // 1) 현재 전원 상태 조회
        boolean shouldSendSwitch;
        try {
            Device device = deviceRepo.findByDeviceId(deviceId)
                    .orElseThrow(() -> new ResourceNotFoundException("기기를 찾을 수 없음: " + deviceId));
            // Boolean 타입 비교를 위해 Boolean.TRUE.equals 사용
            boolean currentPower = Boolean.TRUE.equals(device.getPower());
            boolean desiredPower = "on".equalsIgnoreCase(seg.getAirPurifier());
            shouldSendSwitch = (currentPower != desiredPower);
        } catch (Exception e) {
            log.warn("[applySegment] 디바이스 상태 조회 실패, switch 명령 강제 전송: {}", deviceId);
            shouldSendSwitch = true;
        }

        // 2) 전원 명령 (상태가 다를 때만)
        if (shouldSendSwitch) {
            DeviceControlRequestDto.Command powerCmd = new DeviceControlRequestDto.Command();
            powerCmd.setComponent("main");
            powerCmd.setCapability("switch");
            powerCmd.setCommand(seg.getAirPurifier());
            powerCmd.setArguments(List.of());
            cmds.add(powerCmd);
        }

        // 3) 팬 모드 명령은 항상 전송
        DeviceControlRequestDto.Command fanCmd = new DeviceControlRequestDto.Command();
        fanCmd.setComponent("main");
        fanCmd.setCapability("airConditionerFanMode");
        fanCmd.setCommand(seg.getFanMode());
        fanCmd.setArguments(List.of(seg.getFanMode()));
        cmds.add(fanCmd);

        DeviceControlRequestDto payload = new DeviceControlRequestDto();
        payload.setCommands(cmds);

        controlService.sendCommandsRaw(deviceId, payload);

        if (Boolean.TRUE.equals(seg.getVentilation())) {
            notificationService.notifyVentilation(deviceId, seg.getStartMinute());
        }
    }

    /**
     * 최신 배치 조회
     */
    @Transactional(readOnly = true)
    public AiControlBatchResponseDto getLatestBatch() {
        AiControlBatch b = batchRepo.findFirstByOrderByTimestampDesc()
                .orElseThrow(() -> new IllegalStateException("저장된 배치가 없습니다."));
        return AiControlBatchResponseDto.fromEntity(b);
    }
}
