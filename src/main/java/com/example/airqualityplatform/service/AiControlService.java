// src/main/java/com/example/airqualityplatform/service/AiControlService.java
package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.AiControlBatch;
import com.example.airqualityplatform.domain.AiControlSegment;
import com.example.airqualityplatform.dto.request.AiControlBatchRequestDto;
import com.example.airqualityplatform.dto.request.DeviceControlRequestDto;
import com.example.airqualityplatform.dto.response.AiControlBatchResponseDto;
import com.example.airqualityplatform.repository.AiControlBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiControlService {

    private final AiControlBatchRepository batchRepo;
    private final TaskScheduler scheduler;
    private final DeviceControlService controlService;
    private final NotificationService notificationService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public AiControlBatchResponseDto ingestControl(AiControlBatchRequestDto dto) {
        // 1) timestamp 파싱
        LocalDateTime ldt = LocalDateTime.parse(dto.getTimestamp(), FMT);
        Instant batchTs = ldt.toInstant(ZoneOffset.UTC);

        // 2) Batch + Segment 엔티티 생성
        AiControlBatch batch = AiControlBatch.builder()
                .timestamp(batchTs)
                .deviceId(dto.getDeviceId())
                .build();
        dto.getControl_result().forEach(segDto -> {
            batch.addSegment(AiControlSegment.builder()
                    .startMinute(segDto.getStartMinute())
                    .endMinute(segDto.getEndMinute())
                    .airPurifier(segDto.getAirPurifier())
                    .fanMode(segDto.getFanMode())
                    .ventilation(segDto.getVentilation())
                    .build());
        });
        AiControlBatch saved = batchRepo.save(batch);

        // 3) 각 세그먼트 예약 스케줄링
        saved.getSegments().forEach(seg -> {
            Instant execTime = saved.getTimestamp()
                    .plus(seg.getStartMinute(), ChronoUnit.MINUTES);
            scheduler.schedule(() -> {
                try {
                    applySegment(saved.getDeviceId(), seg);
                } catch (Exception e) {
                    log.error("세그먼트 적용 실패 [{}-{}]: {}",
                            seg.getStartMinute(), seg.getEndMinute(), e.getMessage());
                }
            }, Date.from(execTime));
        });

        // 4) DTO 리턴
        return AiControlBatchResponseDto.fromEntity(saved);
    }

    private void applySegment(String deviceId, AiControlSegment seg) {
        // 1) SmartThings 제어
        DeviceControlRequestDto cmd = new DeviceControlRequestDto();
        cmd.setCommands(List.of(
                new DeviceControlRequestDto.Command("main", "switch", seg.getAirPurifier(), List.of()),
                new DeviceControlRequestDto.Command("main",
                        "airConditionerFanMode",
                        "setFanMode",
                        List.of(seg.getFanMode()))
        ));
        controlService.sendCommandsRaw(deviceId, cmd);

        // 2) 환기 알림
        if (seg.getVentilation()) {
            notificationService.notifyVentilation(deviceId, seg.getStartMinute());
        }
    }

    @Transactional(readOnly = true)
    public AiControlBatchResponseDto getLatestBatch() {
        AiControlBatch b = batchRepo.findFirstByOrderByTimestampDesc()
                .orElseThrow(() -> new IllegalStateException("배치가 없습니다."));
        return AiControlBatchResponseDto.fromEntity(b);
    }
}
