package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.DeviceAutoControl;
import com.example.airqualityplatform.dto.mapper.DeviceAutoControlMapper;
import com.example.airqualityplatform.dto.request.DeviceAutoControlRequestDto;
import com.example.airqualityplatform.dto.response.DeviceAutoControlResponseDto;
import com.example.airqualityplatform.repository.DeviceAutoControlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeviceAutoControlServiceTest {

    @InjectMocks
    private DeviceAutoControlService service;

    @Mock
    private DeviceAutoControlRepository repository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAutoControl() {
        // 요청 DTO 생성
        DeviceAutoControlRequestDto req = new DeviceAutoControlRequestDto();
        req.setPm25Threshold(35.0);
        req.setCo2Threshold(900.0);
        req.setActionPower(true);
        req.setActionMode("auto");
        req.setActionFanSpeed(3);

        // Mapper를 통해 엔티티 생성
        DeviceAutoControl entity = DeviceAutoControlMapper.toEntity(req, null);
        entity.setControlId(1L);
        Date now = new Date();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        when(repository.save(any(DeviceAutoControl.class))).thenReturn(entity);

        DeviceAutoControlResponseDto response = service.createAutoControl(req);
        assertNotNull(response);
        assertEquals(1L, response.getControlId());
        verify(repository, times(1)).save(any(DeviceAutoControl.class));
    }

    @Test
    public void testGetAllAutoControls() {
        DeviceAutoControl dac1 = new DeviceAutoControl();
        dac1.setControlId(10L);
        DeviceAutoControl dac2 = new DeviceAutoControl();
        dac2.setControlId(11L);

        when(repository.findAll()).thenReturn(Arrays.asList(dac1, dac2));
        assertEquals(2, service.getAllAutoControls().size());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testGetAutoControlById() {
        DeviceAutoControl dac = new DeviceAutoControl();
        dac.setControlId(20L);
        when(repository.findById(20L)).thenReturn(Optional.of(dac));

        DeviceAutoControlResponseDto response = service.getAutoControlById(20L);
        assertNotNull(response);
        assertEquals(20L, response.getControlId());
        verify(repository, times(1)).findById(20L);
    }

    @Test
    public void testUpdateAutoControl() {
        Long controlId = 20L;
        DeviceAutoControl existing = new DeviceAutoControl();
        existing.setControlId(controlId);
        existing.setPm25Threshold(30.0);
        existing.setCo2Threshold(800.0);
        existing.setActionPower(true);
        existing.setActionMode("manual");
        existing.setActionFanSpeed(2);

        DeviceAutoControlRequestDto req = new DeviceAutoControlRequestDto();
        req.setPm25Threshold(40.0);
        req.setCo2Threshold(900.0);
        req.setActionPower(false);
        req.setActionMode("auto");
        req.setActionFanSpeed(3);

        when(repository.findById(controlId)).thenReturn(Optional.of(existing));
        when(repository.save(any(DeviceAutoControl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeviceAutoControlResponseDto response = service.updateAutoControl(controlId, req);
        assertNotNull(response);
        assertEquals(40.0, response.getPm25Threshold());
        assertEquals(900.0, response.getCo2Threshold());
        assertFalse(response.getActionPower());
        assertEquals("auto", response.getActionMode());
        verify(repository, times(1)).findById(controlId);
        verify(repository, times(1)).save(any(DeviceAutoControl.class));
    }

    @Test
    public void testDeleteAutoControl() {
        Long controlId = 30L;
        doNothing().when(repository).deleteById(controlId);
        service.deleteAutoControl(controlId);
        verify(repository, times(1)).deleteById(controlId);
    }
}
