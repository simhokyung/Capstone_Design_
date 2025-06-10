// src/main/java/com/example/airqualityplatform/dto/request/DeviceControlRequestDto.java
package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class DeviceControlRequestDto {
    @NotEmpty(message = "commands 목록을 하나 이상 포함해야 합니다.")
    private List<@NotNull Command> commands;

    @Getter @Setter @NoArgsConstructor
    public static class Command {
        @NotNull(message = "component는 필수입니다.")
        private String component;

        @NotNull(message = "capability는 필수입니다.")
        private String capability;

        @NotNull(message = "command는 필수입니다.")
        private String command;

        @NotNull(message = "arguments는 null이 될 수 없습니다.")
        private List<Object> arguments;

        /** 편의 생성자 추가 */
        public Command(String component, String capability, String command, List<Object> arguments) {
            this.component = component;
            this.capability = capability;
            this.command = command;
            this.arguments = arguments;
        }
    }
}
