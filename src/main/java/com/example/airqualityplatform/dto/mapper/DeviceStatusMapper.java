package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.domain.DeviceStatus;
import com.example.airqualityplatform.dto.external.DeviceStatusExternalDto;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Stream;

public class DeviceStatusMapper {

    /**
     * 외부 DTO + 로컬 Device → DeviceStatus 엔티티
     * 모든 이벤트(timestamp)를 비교하여 가장 최근 시점을 statusAt에 저장합니다.
     */
    public static DeviceStatus toEntity(DeviceStatusExternalDto ext, Device device) {
        Instant now = Instant.now();

        Instant tEnergy = null, tSwitch = null, tFan = null;

        DeviceStatus s = DeviceStatus.builder()
                .device(device)
                .fetchedAt(now)
                .build();

        // components → main 블록이 있으면
        if (ext.getComponents() != null && ext.getComponents().getMain() != null) {
            var main = ext.getComponents().getMain();

            // 1) energy
            if (main.getPowerConsumptionReport() != null
                    && main.getPowerConsumptionReport().getPowerConsumption() != null) {
                var pc = main.getPowerConsumptionReport().getPowerConsumption();
                if (pc.getValue() != null && pc.getValue().getEnergy() != null) {
                    s.setEnergy(pc.getValue().getEnergy());
                }
                if (pc.getTimestamp() != null) {
                    tEnergy = OffsetDateTime.parse(pc.getTimestamp()).toInstant();
                }
            }

            // 2) switch 상태 ("on"/"off")
            if (main.get_switch() != null
                    && main.get_switch().get_switch() != null) {
                var sw = main.get_switch().get_switch();
                s.setPowerState(sw.getValue());
                if (sw.getTimestamp() != null) {
                    tSwitch = OffsetDateTime.parse(sw.getTimestamp()).toInstant();
                }
            }

            // 3) fan mode ("low"/"medium"/"high")
            if (main.getAirConditionerFanMode() != null
                    && main.getAirConditionerFanMode().getFanMode() != null) {
                var fm = main.getAirConditionerFanMode().getFanMode();
                s.setFanMode(fm.getValue());
                if (fm.getTimestamp() != null) {
                    tFan = OffsetDateTime.parse(fm.getTimestamp()).toInstant();
                }
            }
        }

        // 가장 최신 이벤트 시점을 statusAt으로
        Instant statusAt = Stream.of(tEnergy, tSwitch, tFan)
                .filter(Objects::nonNull)
                .max(Instant::compareTo)
                .orElse(now);
        s.setStatusAt(statusAt);

        return s;
    }
}
