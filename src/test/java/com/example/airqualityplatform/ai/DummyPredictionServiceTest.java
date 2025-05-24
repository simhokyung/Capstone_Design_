package com.example.airqualityplatform.ai;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import static org.junit.jupiter.api.Assertions.*;

public class DummyPredictionServiceTest {

    private final PredictionService predictionService = new DummyPredictionService();

    @Test
    public void testPredictAirQuality() throws Exception {
        Long sensorId = 1L;
        int hours = 3;
        List<PredictionResponse> predictions = predictionService.predictAirQuality(sensorId, hours);

        // 예측 데이터 개수가 입력한 hours와 일치해야 함
        assertEquals(hours, predictions.size());

        // 각 예측 데이터의 sensorId와 시간 포맷 확인
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (int i = 0; i < hours; i++) {
            PredictionResponse pr = predictions.get(i);
            assertEquals(sensorId, pr.getSensorId());
            // DummyPredictionService에서는 온도가 22.0 + i로 증가하도록 구현되었다고 가정
            assertEquals(22.0 + (i + 1), pr.getPredictedTemperature());
            // 미래 시각의 포맷이 올바른지 파싱하여 예외가 없도록 확인
            assertNotNull(sdf.parse(pr.getFutureTimestamp()));
        }
    }
}
