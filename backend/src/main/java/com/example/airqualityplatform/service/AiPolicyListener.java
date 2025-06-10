package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.dto.mapper.DeviceAutoControlMapper;
import com.example.airqualityplatform.dto.request.AiPolicyRequestDto;
import com.example.airqualityplatform.event.AiPolicySendEvent;
import com.example.airqualityplatform.repository.DeviceAutoControlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiPolicyListener {

    private final RestTemplate rt;
    private final DeviceAutoControlRepository controlRepo;
    private static final String AI_URL = "http://52.64.178.83:8000/receive/userstandard";

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPolicySend(AiPolicySendEvent evt) {
        // fetch join 으로 devices+room을 미리 불러오기 때문에
        DeviceAutoControl control = controlRepo
                .findWithDevicesAndRoomById(evt.getPolicyId())
                .orElseThrow(() -> new IllegalStateException("정책이 없습니다. id=" + evt.getPolicyId()));

        AiPolicyRequestDto dto = DeviceAutoControlMapper.toAiDto(control);
        try {
            rt.postForEntity(AI_URL, dto, Void.class);
            log.info("AI 정책 전송 성공 –– policyId={}", dto.getPolicyId());
        } catch (Exception ex) {
            log.error("AI 정책 전송 실패 –– policyId={} err={}",
                    dto.getPolicyId(), ex.getMessage(), ex);
        }
    }
}