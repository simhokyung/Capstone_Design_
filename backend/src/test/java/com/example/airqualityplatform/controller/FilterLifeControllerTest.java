package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.service.FilterLifeService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FilterLifeControllerTest는 FilterLifeController에 대한 MockMvc 테스트 코드입니다.
 * FilterLifeController가 /devices/{deviceId}/filter-usage 엔드포인트를 처리한다고 가정합니다.
 */

@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@WebMvcTest(FilterLifeController.class)
public class FilterLifeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilterLifeService filterLifeService;

    /**
     * 필터 소모율이 특정 범위를 넘으면 교체 권장 메시지를 반환한다고 가정
     * 예: "/devices/1/filter-usage" → "필터 소모율 85.0%, 교체 권장 시점입니다."
     */
    @Test
    public void testGetFilterUsage() throws Exception {
        String deviceId = String.valueOf(1L);
        // 예: calculateFilterSoiling()가 0.85(85%)를 반환
        when(filterLifeService.calculateFilterSoiling(ArgumentMatchers.eq(deviceId)))
                .thenReturn(0.85);

        // GET /devices/1/filter-usage 호출 시 필터LifeController가 특정 메시지를 반환한다고 가정
        // 예시 메시지: "필터 소모율 85.0%, 교체 권장 시점입니다."
        String expectedMessage = "필터 소모율 85.0%, 교체 권장 시점입니다.";

        // 모킹: 기존에 컨트롤러에서 필터 소모율 0.85 이상이면 특정 메시지를 반환하므로,
        // 테스트에서는 응답이 expectedMessage인지 검증
        mockMvc.perform(get("/devices/{deviceId}/filter-usage", deviceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }
}
