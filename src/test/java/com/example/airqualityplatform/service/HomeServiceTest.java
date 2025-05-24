package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.Home;
import com.example.airqualityplatform.dto.mapper.HomeMapper;
import com.example.airqualityplatform.dto.request.HomeRequestDto;
import com.example.airqualityplatform.dto.response.HomeResponseDto;
import com.example.airqualityplatform.repository.HomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HomeServiceTest {

    @InjectMocks
    private HomeService homeService;

    @Mock
    private HomeRepository homeRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateHome() {
        HomeRequestDto req = new HomeRequestDto();
        req.setHomeName("Test Home");
        req.setAddress("123 Test Street");

        // HomeMapper를 통해 엔티티로 변환
        Home homeEntity = HomeMapper.toEntity(req, null);
        homeEntity.setHomeId(1L);
        Date now = new Date();
        homeEntity.setCreatedAt(now);
        homeEntity.setUpdatedAt(now);

        when(homeRepository.save(any(Home.class))).thenReturn(homeEntity);

        HomeResponseDto response = homeService.createHome(req);
        assertNotNull(response);
        assertEquals(1L, response.getHomeId());
        assertEquals("Test Home", response.getHomeName());
        assertEquals("123 Test Street", response.getAddress());
        verify(homeRepository, times(1)).save(any(Home.class));
    }

    @Test
    public void testGetAllHomes() {
        Home home1 = new Home();
        home1.setHomeId(1L);
        home1.setHomeName("Home1");
        home1.setAddress("Address1");
        home1.setCreatedAt(new Date());
        home1.setUpdatedAt(new Date());

        Home home2 = new Home();
        home2.setHomeId(2L);
        home2.setHomeName("Home2");
        home2.setAddress("Address2");
        home2.setCreatedAt(new Date());
        home2.setUpdatedAt(new Date());

        when(homeRepository.findAll()).thenReturn(Arrays.asList(home1, home2));

        assertEquals(2, homeService.getAllHomes().size());
        verify(homeRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateHome() {
        Long homeId = 1L;
        Home existing = new Home();
        existing.setHomeId(homeId);
        existing.setHomeName("Old Home");
        existing.setAddress("Old Address");
        existing.setCreatedAt(new Date());
        existing.setUpdatedAt(new Date());

        HomeRequestDto updateReq = new HomeRequestDto();
        updateReq.setHomeName("New Home");
        updateReq.setAddress("New Address");

        when(homeRepository.findById(homeId)).thenReturn(Optional.of(existing));
        when(homeRepository.save(any(Home.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HomeResponseDto updated = homeService.updateHome(homeId, updateReq);
        assertNotNull(updated);
        assertEquals("New Home", updated.getHomeName());
        assertEquals("New Address", updated.getAddress());
        verify(homeRepository, times(1)).findById(homeId);
        verify(homeRepository, times(1)).save(any(Home.class));
    }

    @Test
    public void testDeleteHome() {
        Long homeId = 1L;
        doNothing().when(homeRepository).deleteById(homeId);
        homeService.deleteHome(homeId);
        verify(homeRepository, times(1)).deleteById(homeId);
    }
}
