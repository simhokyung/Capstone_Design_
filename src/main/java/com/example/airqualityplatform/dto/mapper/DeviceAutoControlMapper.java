// src/main/java/com/example/airqualityplatform/dto/mapper/DeviceAutoControlMapper.java
package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.domain.Device;
import com.example.airqualityplatform.dto.request.DeviceAutoControlRequestDto;
import com.example.airqualityplatform.dto.response.DeviceAutoControlResponseDto;
import com.example.airqualityplatform.dto.request.AiPolicyRequestDto;  // AI 전송용 DTO

import java.util.stream.Collectors;

public class DeviceAutoControlMapper {

    public static DeviceAutoControl toEntity(DeviceAutoControlRequestDto dto, DeviceAutoControl entity) {
        if (entity == null) {
            entity = new DeviceAutoControl();
        }
        entity.setPm25Threshold(dto.getPm25Threshold());
        entity.setCo2Threshold(dto.getCo2Threshold());
        entity.setPm100Threshold(dto.getPm100Threshold());
        entity.setVocThreshold(dto.getVocThreshold());
        return entity;
    }

    public static DeviceAutoControlResponseDto toResponseDto(DeviceAutoControl entity) {
        DeviceAutoControlResponseDto dto = new DeviceAutoControlResponseDto();
        dto.setControlId(entity.getControlId());
        dto.setRoomId(entity.getDevices().isEmpty()
                ? null
                : entity.getDevices().get(0).getRoom().getRoomId());
        dto.setDeviceIds(entity.getDevices().stream()
                .map(Device::getId)
                .collect(Collectors.toList()));
        dto.setPm25Threshold(entity.getPm25Threshold());
        dto.setCo2Threshold(entity.getCo2Threshold());
        dto.setPm100Threshold(entity.getPm100Threshold());
        dto.setVocThreshold(entity.getVocThreshold());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /** AI 전송용 DTO로 변환 */
    public static AiPolicyRequestDto toAiDto(DeviceAutoControl entity) {
        // AiPolicyRequestDto는 아래와 같이 정의되어 있어야 합니다.
        // package com.example.airqualityplatform.dto.request;
        //
        // public class AiPolicyRequestDto {
        //     private Long policyId;
        //     private Long roomId;
        //     private Thresholds thresholds;
        //     private List<String> deviceIds;
        //     // getters/setters...
        //     public static class Thresholds {
        //         private Double pm25;
        //         private Double pm100;
        //         private Double co2;
        //         private Double voc;
        //         // getters/setters...
        //     }
        // }

        AiPolicyRequestDto dto = new AiPolicyRequestDto();
        dto.setPolicyId(entity.getControlId());
        dto.setRoomId(entity.getDevices().isEmpty()
                ? null
                : entity.getDevices().get(0).getRoom().getRoomId());

        AiPolicyRequestDto.Thresholds thr = new AiPolicyRequestDto.Thresholds();
        thr.setPm25(entity.getPm25Threshold());
        thr.setPm100(entity.getPm100Threshold());
        thr.setCo2(entity.getCo2Threshold());
        thr.setVoc(entity.getVocThreshold());
        dto.setThresholds(thr);

        dto.setDeviceIds(
                entity.getDevices().stream()
                        .map(Device::getDeviceId)  // SmartThings deviceId (UUID)
                        .collect(Collectors.toList())
        );

        return dto;
    }
}
