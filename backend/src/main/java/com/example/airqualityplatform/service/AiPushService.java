package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.AiPushState;
import com.example.airqualityplatform.dto.ai.AiMeasurementDto;
import com.example.airqualityplatform.dto.ai.AiDeviceStatusDto;
import com.example.airqualityplatform.dto.ai.AiPayloadDto;
import com.example.airqualityplatform.repository.AiPushStateRepository;
import com.example.airqualityplatform.repository.DeviceStatusRepository;
import com.example.airqualityplatform.repository.MeasurementRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiPushService {

    private final MeasurementRepository     measurementRepo;
    private final DeviceStatusRepository   statusRepo;
    private final AiPushStateRepository    stateRepo;
    private final RestTemplate             restTemplate;

    @Value("${ai.endpoint.url}")
    private String aiUrl;

    private AiPushState state;

    @PostConstruct
    public void initState() {
        this.state = stateRepo.findById(1L)
                .orElseGet(() -> {
                    AiPushState initial = new AiPushState();
                    initial.setLastMeasurementTime(new Date(0));
                    initial.setLastStatusTime(Instant.EPOCH);
                    return stateRepo.save(initial);
                });
    }

    @Scheduled(fixedRateString = "${ai.push.fixedRate}")
    public void pushNewToAi() {
        Date    lastMeasurementTime = state.getLastMeasurementTime();
        Instant lastStatusTime      = state.getLastStatusTime();

        List<AiMeasurementDto> ms = measurementRepo.findNewMeasurements(lastMeasurementTime);
        List<AiDeviceStatusDto> ss = statusRepo.findNewStatuses(lastStatusTime);

        if (ms.isEmpty() && ss.isEmpty()) {
            log.debug("No new data to push to AI.");
            return;
        }

        // 보정 로직
        for (AiMeasurementDto m : ms) {
            if (m.getSensorId().equals(48007L)) {
                m.setTemperature(m.getTemperature() - 8.5);
            } else if (m.getSensorId().equals(44213L)) {
                m.setTemperature(m.getTemperature() - 7);
            }
        }

        AiPayloadDto payload = new AiPayloadDto(ms, ss);

        try {
            ResponseEntity<Void> resp = restTemplate.postForEntity(aiUrl, payload, Void.class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                if (!ms.isEmpty()) {
                    state.setLastMeasurementTime(ms.get(ms.size() - 1).getTimestamp());
                }
                if (!ss.isEmpty()) {
                    state.setLastStatusTime(ss.get(ss.size() - 1).getFetchedAt());
                }
                stateRepo.save(state);
                log.info("Pushed {} measurements and {} statuses to AI", ms.size(), ss.size());
            } else {
                log.error("Failed to push to AI. status={}", resp.getStatusCode());
            }
        } catch (Exception ex) {
            log.error("Error during AI push", ex);
        }
    }
}
