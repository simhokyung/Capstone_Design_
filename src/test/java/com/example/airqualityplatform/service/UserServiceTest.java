package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.request.UserSignupRequest;
import com.example.airqualityplatform.dto.response.UserResponseDto;
import com.example.airqualityplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;  // 실제 테스트 대상

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------------------
    // 1. 회원가입 테스트 (registerUser)
    // ---------------------------------------------------------------------
    @Test
    public void testRegisterUser_Success() {
        // GIVEN
        UserSignupRequest signupDto = new UserSignupRequest();
        signupDto.setEmail("newuser@example.com");
        signupDto.setPassword("plaintextPass");
        signupDto.setUsername("NewUser");
        signupDto.setPhoneNumber("01011112222");
        signupDto.setHasAsthma(false);
        signupDto.setHasAllergy(true);
        signupDto.setPreferredTempMin(20.0f);
        signupDto.setPreferredTempMax(25.0f);
        signupDto.setPreferredHumMin(40.0f);
        signupDto.setPreferredHumMax(60.0f);

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plaintextPass")).thenReturn("hashedPass");

        User savedUser = new User();
        savedUser.setUserId(1L);
        savedUser.setEmail("newuser@example.com");
        savedUser.setPassword("hashedPass");
        savedUser.setUsername("NewUser");
        savedUser.setPhoneNumber("01011112222");
        savedUser.setHasAsthma(false);
        savedUser.setHasAllergy(true);
        savedUser.setPreferredTempMin(20.0f);
        savedUser.setPreferredTempMax(25.0f);
        savedUser.setPreferredHumMin(40.0f);
        savedUser.setPreferredHumMax(60.0f);
        savedUser.setCreatedAt(new Date());
        savedUser.setUpdatedAt(new Date());
        savedUser.setIsVerified(false);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // WHEN
        UserResponseDto result = userService.registerUser(signupDto);

        // THEN
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals("NewUser", result.getUsername());
        assertNotNull(result.getIsVerified());
        assertFalse(result.getIsVerified());
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(passwordEncoder, times(1)).encode("plaintextPass");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() {
        UserSignupRequest signupDto = new UserSignupRequest();
        signupDto.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(signupDto);
        });

        assertEquals("이미 가입된 이메일입니다.", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    // ---------------------------------------------------------------------
    // 2. 로그인 테스트 (login)
    // ---------------------------------------------------------------------
    @Test
    public void testLogin_Success() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("login@example.com");
        user.setPassword("hashedPass");

        when(userRepository.findByEmail("login@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plaintext", "hashedPass")).thenReturn(true);

        User result = userService.login("login@example.com", "plaintext");

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(userRepository, times(1)).findByEmail("login@example.com");
        verify(passwordEncoder, times(1)).matches("plaintext", "hashedPass");
    }

    @Test
    public void testLogin_InvalidPassword() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("login@example.com");
        user.setPassword("hashedPass");

        when(userRepository.findByEmail("login@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPlaintext", "hashedPass")).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login("login@example.com", "wrongPlaintext");
        });

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("login@example.com");
        verify(passwordEncoder, times(1)).matches("wrongPlaintext", "hashedPass");
    }

    // ---------------------------------------------------------------------
    // 3. 사용자 조회 (getUserById)
    // ---------------------------------------------------------------------
    @Test
    public void testGetUserById_Success() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("findme@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("findme@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    // ---------------------------------------------------------------------
    // 4. 위치 정보 업데이트 (updateUserLocation)
    // ---------------------------------------------------------------------
    @Test
    public void testUpdateUserLocationWithDto() {
        User user = new User();
        user.setUserId(1L);
        user.setAddress("Old Address");
        user.setLatitude(10.0);
        user.setLongitude(20.0);
        user.setUpdatedAt(new Date());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserLocationUpdateRequestDto updateDto = new UserLocationUpdateRequestDto();
        updateDto.setAddress("New Address");
        updateDto.setLatitude(30.0);
        updateDto.setLongitude(40.0);

        UserResponseDto updatedUser = userService.updateUserLocation(1L, updateDto);

        assertNotNull(updatedUser);
        assertEquals("New Address", updatedUser.getAddress());
        assertEquals(30.0, updatedUser.getLatitude());
        assertEquals(40.0, updatedUser.getLongitude());
        assertNotNull(updatedUser.getUpdatedAt());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
