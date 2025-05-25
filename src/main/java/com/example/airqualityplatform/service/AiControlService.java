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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiControlService {

    /* ───────── DI ───────── */
    private final AiControlBatchRepository batchRepo;
    private final TaskScheduler            scheduler;
    private final DeviceControlService     controlService;
    private final NotificationService      notificationService;

    /* ───────── AI → 제어 배치 수신 ───────── */
    @Transactional
    public AiControlBatchResponseDto ingestControl(AiControlBatchRequestDto dto) {

        /* 1. 배치 엔티티 저장 */
        Instant ts = Instant.parse(dto.getTimestamp());     // ISO-8601 문자열 그대로 파싱
        AiControlBatch batch = AiControlBatch.builder()
                .timestamp(ts)
                .deviceId(dto.getDeviceId())
                .build();

        dto.getControlResult().forEach(segDto ->
                batch.addSegment(
                        AiControlSegment.builder()
                                .startMinute(segDto.getStartMinute())
                                .endMinute(segDto.getEndMinute())
                                .airPurifier(segDto.getAirPurifier())
                                .fanMode(segDto.getFanMode())
                                .ventilation(segDto.getVentilation())
                                .build())
        );
        AiControlBatch saved = batchRepo.save(batch);

        /* 2. 세그먼트 각각을 예약 작업으로 등록 */
        saved.getSegments().forEach(seg -> {
            Instant exec = saved.getTimestamp().plus(seg.getStartMinute(), ChronoUnit.MINUTES);
            scheduler.schedule(() -> applySegment(saved.getDeviceId(), seg),
                    Date.from(exec));
        });

        /* 3. DTO 로 변환해 반환 */
        return AiControlBatchResponseDto.fromEntity(saved);
    }

    /* ───────── 실제 세그먼트 적용 ───────── */
    private void applySegment(String deviceId, AiControlSegment seg) {

        /* SmartThings 호출용 DTO 구성 */
        List<DeviceControlRequestDto.Command> cmds = new ArrayList<>();

        DeviceControlRequestDto.Command powerCmd = new DeviceControlRequestDto.Command();
        powerCmd.setComponent("main");
        powerCmd.setCapability("switch");
        powerCmd.setCommand(seg.getAirPurifier());          // "on" | "off"
        powerCmd.setArguments(List.of());
        cmds.add(powerCmd);

        DeviceControlRequestDto.Command fanCmd = new DeviceControlRequestDto.Command();
        fanCmd.setComponent("main");
        fanCmd.setCapability("airConditionerFanMode");
        fanCmd.setCommand("setFanMode");
        fanCmd.setArguments(List.of(seg.getFanMode()));     // ["sleep"] 등
        cmds.add(fanCmd);

        DeviceControlRequestDto payload = new DeviceControlRequestDto();
        payload.setCommands(cmds);

        /* SmartThings 로 전송 */
        controlService.sendCommandsRaw(deviceId, payload);

        /* 환기 알림 */
        if (Boolean.TRUE.equals(seg.getVentilation())) {
            notificationService.notifyVentilation(deviceId, seg.getStartMinute());
        }
    }

    /* ───────── 최신 배치 조회 ───────── */
    @Transactional(readOnly = true)
    public AiControlBatchResponseDto getLatestBatch() {
        AiControlBatch b = batchRepo.findFirstByOrderByTimestampDesc()
                .orElseThrow(() -> new IllegalStateException("저장된 배치가 없습니다."));
        return AiControlBatchResponseDto.fromEntity(b);
    }
}
