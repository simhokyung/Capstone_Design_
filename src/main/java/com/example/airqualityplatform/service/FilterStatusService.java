package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.*;
import com.example.airqualityplatform.dto.external.FilterStatusExternalDto;
import com.example.airqualityplatform.dto.mapper.FilterStatusMapper;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilterStatusService {

    private final FilterRepository                filterRepository;
    private final FilterStatusRepository          statusRepository;
    private final DeviceStatusRepository          deviceStatusRepository;
    private final MeasurementRepository           measurementRepository;
    private final RestTemplate                    restTemplate;
    private final SmartThingsTokenService         tokenService;

    @Value("${smartthings.api.base-url}")
    private String baseUrl;

    @Transactional
    public FilterStatus fetchAndSaveStatus(Long filterId) {
        // 1) 필터와 외부 상태(dto) 조회
        Filter filter = filterRepository.findById(filterId)
                .orElseThrow(() -> new ResourceNotFoundException("필터를 찾을 수 없습니다. id=" + filterId));

        URI uri = URI.create(baseUrl + "/devices/" + filter.getDevice().getDeviceId() + "/status");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getCurrentToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<FilterStatusExternalDto> resp = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), FilterStatusExternalDto.class);

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            log.error("SmartThings 조회 실패: deviceId={} status={}",
                    filter.getDevice().getDeviceId(), resp.getStatusCode());
            throw new IllegalStateException("SmartThings API error: " + resp.getStatusCode());
        }

        // 2) 새 상태 엔티티로 매핑
        FilterStatus newStatus = FilterStatusMapper.toEntity(resp.getBody(), filter);

        // 3) 이전 레코드(없으면 초깃값)
        FilterStatus prev = statusRepository
                .findTopByFilter_FilterIdOrderByStatusAtDesc(filterId)
                .orElseGet(() -> {
                    FilterStatus zero = new FilterStatus();
                    zero.setStatusAt(newStatus.getStatusAt());
                    zero.setUsedHours(newStatus.getUsedHours());
                    // raw 사용시간을 초기 누적 가중치로 설정
                    zero.setEffectiveUsageAccumulated(newStatus.getUsedHours());
                    return zero;
                });

        double prevUsage     = prev.getUsedHours();
        double currUsage     = newStatus.getUsedHours();
        double deltaUsage    = Math.max(0.0, currUsage - prevUsage);

        Instant prevAt       = prev.getStatusAt();
        Instant currAt       = newStatus.getStatusAt();
        long totalSeconds    = Duration.between(prevAt, currAt).getSeconds();
        if (totalSeconds <= 0 || deltaUsage <= 0.0) {
            // 변화가 없으면 DB에 INSERT 하지 말고 이전 상태 그대로 반환
            return prev;
        }

        // 4) 구간별 이벤트 타임라인 수집
        List<Instant> timeline = new ArrayList<>();
        timeline.add(prevAt);

        // 4-1) device status 변화 시점
        List<DeviceStatus> states = deviceStatusRepository
                .findByDevice_IdAndStatusAtBetweenOrderByStatusAtAsc(
                        filter.getDevice().getId(), prevAt, currAt);
        timeline.addAll(states.stream()
                .map(DeviceStatus::getStatusAt)
                .collect(Collectors.toList()));

        // 4-2) 측정값 변화 시점
        Date prevDate = Date.from(prevAt);
        Date currDate = Date.from(currAt);
        List<Measurement> measures = measurementRepository
                .findBySensor_SensorIdAndTimestampBetweenOrderByTimestampAsc(
                        filter.getSensor().getSensorId(), prevDate, currDate);
        timeline.addAll(measures.stream()
                .map(m -> m.getTimestamp().toInstant())
                .collect(Collectors.toList()));

        // 마지막 끝점
        timeline.add(currAt);

        // 중복·정렬
        List<Instant> points = timeline.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 5) 구간별 가중치 누적 계산
        double weightedSum = 0.0;
        // 포인터: 각 구간 시작시점의 상태·측정값
        DeviceStatus curState = deviceStatusRepository
                .findTopByDevice_IdAndStatusAtLessThanEqualOrderByStatusAtDesc(
                        filter.getDevice().getId(), prevAt)
                .orElseThrow(() -> new ResourceNotFoundException("이전 디바이스 상태 없음"));
        Measurement  curMeas  = measurementRepository
                .findTopBySensor_SensorIdAndTimestampLessThanEqualOrderByTimestampDesc(
                        filter.getSensor().getSensorId(), prevDate)
                .orElseThrow(() -> new ResourceNotFoundException("이전 측정값 없음"));

        int si = 0, mi = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            Instant start = points.get(i);
            Instant end   = points.get(i+1);
            long    segSec = Duration.between(start, end).getSeconds();
            if (segSec <= 0) continue;

            // 포인터 옮기기: 다음 device state
            while (si < states.size() && !states.get(si).getStatusAt().isAfter(start)) {
                curState = states.get(si++);
            }
            // 포인터 옮기기: 다음 measurement
            while (mi < measures.size()
                    && !measures.get(mi).getTimestamp().toInstant().isAfter(start)) {
                curMeas = measures.get(mi++);
            }

            double fanW = getFanModeWeight(curState.getFanMode());
            // --- PM2.5, PM10 각각 AQI로 변환 후 더 높은 쪽 선택 ---
            double aqi25 = calculateAqi(curMeas.getPm25_m(), Pollutant.PM25);
            double aqi10 = calculateAqi(curMeas.getPm100_m(), Pollutant.PM10);
            double aqi    = Math.max(aqi25, aqi10);
            double aqiW   = getAqiWeight(aqi);

            // 이 구간에 대응하는 filter-on 시간 비율만큼 나눠서:
            double usageHoursSeg = deltaUsage * ((double) segSec / totalSeconds);
            weightedSum += usageHoursSeg * (fanW * aqiW);
        }

        // 6) 최종 누적치 갱신
        double newAccum = prev.getEffectiveUsageAccumulated() + weightedSum;
        newStatus.setEffectiveUsageAccumulated(newAccum);
        newStatus.setUsedHoursStep(deltaUsage);

        // 7) 저장
        return statusRepository.save(newStatus);
    }

    private double getFanModeWeight(String mode) {
        return switch (mode) {
            case "sleep"    -> 0.4;
            case "windfree" -> 0.6;
            case "medium", "smart" -> 1.0;
            case "max"      -> 1.6;
            default         -> 1.0;
        };
    }

    /**
     * AQI 가중치: 지수(AQI) 값에 따라 1.0(0–50), 1.2(51–100), 1.4(101–150),
     * 1.6(151–200), 1.8(201–300), 2.0(301+)
     */
    private double getAqiWeight(double aqi) {
        if (aqi <= 50)    return 1.0;
        else if (aqi <= 100)  return 1.2;
        else if (aqi <= 150)  return 1.4;
        else if (aqi <= 200)  return 1.6;
        else if (aqi <= 300)  return 1.8;
        else                   return 2.0;
    }

    /**
     * 주어진 농도(conc)와 오염물질 타입에 따라
     * EPA AQI 계산식 (선형 보간)으로 실제 AQI 지수를 반환합니다.
     */
    private double calculateAqi(Double conc, Pollutant pollutant) {
        if (conc == null) conc = 0.0;
        // breakpoints: {Clow, Chigh, Ilow, Ihigh}
        double[][] bp = pollutant == Pollutant.PM25
                ? new double[][] {
                {0.0, 12.0,   0,  50},
                {12.1, 35.4, 51, 100},
                {35.5, 55.4,101, 150},
                {55.5,150.4,151, 200},
                {150.5,250.4,201, 300},
                {250.5,500.4,301, 500}
        }
                : new double[][] {
                {0.0, 54.0,   0,  50},
                {55.0,154.0, 51, 100},
                {155.0,254.0,101,150},
                {255.0,354.0,151,200},
                {355.0,424.0,201,300},
                {425.0,604.0,301,500}
        };

        for (var row : bp) {
            double Cl = row[0], Ch = row[1];
            double Il = row[2], Ih = row[3];
            if (conc >= Cl && conc <= Ch) {
                // 선형 보간: Il + (Ih−Il)*(conc−Cl)/(Ch−Cl)
                return Il + (Ih - Il) * (conc - Cl) / (Ch - Cl);
            }
        }
        return 500.0; // 범위 초과 시 최대치
    }

    private enum Pollutant { PM25, PM10 }

    /** (1) DB에 저장된 최신 상태 한 건을 꺼내 옵니다. */
    @Transactional(readOnly = true)
    public FilterStatus getLatestStatus(Long filterId) {
        return statusRepository
                .findTopByFilter_FilterIdOrderByStatusAtDesc(filterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "필터 상태가 없습니다. filterId=" + filterId));
    }

    /** (2) 상태 이력 전체를 내림차순으로 꺼내 옵니다. */
    @Transactional(readOnly = true)
    public List<FilterStatus> getStatuses(Long filterId) {
        if (!filterRepository.existsById(filterId)) {
            throw new ResourceNotFoundException("필터를 찾을 수 없습니다. id=" + filterId);
        }
        return statusRepository.findByFilter_FilterIdOrderByStatusAtDesc(filterId);
    }
}
