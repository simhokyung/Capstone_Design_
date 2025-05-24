package com.example.airqualityplatform.ai;

import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class DummyPredictionService implements PredictionService {

    @Override
    public List<PredictionResponse> predictAirQuality(Long sensorId, int hours) {
        List<PredictionResponse> predictions = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        // 입력된 hours만큼 앞으로 1시간 단위 예측 데이터 생성
        for (int i = 1; i <= hours; i++) {
            calendar.add(Calendar.HOUR, 1);
            predictions.add(new PredictionResponse(
                    sensorId,
                    sdf.format(calendar.getTime()),
                    22.0 + i,   // 예: 시간에 따라 온도가 1씩 증가
                    45.0,       // 고정된 더미 습도
                    800.0,      // 고정된 더미 CO₂
                    0.12,       // 고정된 더미 VOC
                    50.0,       // 고정된 더미 PM2.5
                    60.0        // 고정된 더미 PM10
            ));
        }
        return predictions;
    }
}
