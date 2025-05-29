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

    /**
     * AI에서 내려준 제어 배치 수신 및 스케줄링
     */
    @Transactional
    public AiControlBatchResponseDto ingestControl(AiControlBatchRequestDto dto) {
        // 1) AI 타임스탬프 파싱 및 도착 시점 보정
        Instant originalTs = Instant.parse(dto.getTimestamp());
        Instant now        = Instant.now();
        // AI 타임스탬프가 과거라면 현재 시각으로 대체
        Instant batchTs    = originalTs.isAfter(now) ? originalTs : now;
        log.info("[ingestControl] originalTs={}, now={}, batchTs={}", originalTs, now, batchTs);

        // 2) 배치 엔티티 저장
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

        // 3) 세그먼트 별 스케줄 등록 (도착 시점을 기준으로 보정)
        saved.getSegments().forEach(seg -> {
            Instant desired = batchTs.plus(seg.getStartMinute(), ChronoUnit.MINUTES);
            Instant exec    = desired.isAfter(now) ? desired : now;
            log.info("[ingestControl] Schedule segment startMinute={} (desired={}, exec={})",
                    seg.getStartMinute(), desired, exec);
            scheduler.schedule(() -> applySegment(saved.getDeviceId(), seg), Date.from(exec));
        });

        // 4) 응답 DTO 변환 및 반환
        return AiControlBatchResponseDto.fromEntity(saved);
    }

    /**
     * 세그먼트 실제 적용: SmartThings 호출 및 알림
     */
    private void applySegment(String deviceId, AiControlSegment seg) {
        // SmartThings 제어 명령 구성
        List<DeviceControlRequestDto.Command> cmds = new ArrayList<>();

        DeviceControlRequestDto.Command powerCmd = new DeviceControlRequestDto.Command();
        powerCmd.setComponent("main");
        powerCmd.setCapability("switch");
        powerCmd.setCommand(seg.getAirPurifier());
        powerCmd.setArguments(List.of());
        cmds.add(powerCmd);

        DeviceControlRequestDto.Command fanCmd = new DeviceControlRequestDto.Command();
        fanCmd.setComponent("main");
        fanCmd.setCapability("airConditionerFanMode");
        fanCmd.setCommand("setFanMode");
        fanCmd.setArguments(List.of(seg.getFanMode()));
        cmds.add(fanCmd);

        DeviceControlRequestDto payload = new DeviceControlRequestDto();
        payload.setCommands(cmds);

        // SmartThings에 명령 전송
        controlService.sendCommandsRaw(deviceId, payload);

        // 환기 알림
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
