##  트러블슈팅
---

### 1) 🔧센서와 서버 간 데이터 통신 구현 문제

**이슈**  
- 센서에서 측정값을 전송하는 서버가 TelosAir(센서 제조사) 서버로 고정되어 있어 플랫폼에서 센서의 측정값을 받으려면 TelosAir 웹 사이트에서 값을 읽어내야 함. 
- 이는 데이터 접근성이 떨어지고, 시스템 활용에 제약이 생긴다.



**해결방안**
- 센서 인터넷 연결 시 PC를 경유하게 하여 통신 내역을 확인, 플렛폼 서버로 전송
<img width="478" height="276" alt="image" src="https://github.com/user-attachments/assets/f84168be-3de8-4fa0-a7b0-b44c0f0f21d3" />



**수행내용**:

- (1) 센서 인터넷 연결 시, PC의 핫스팟을 통한 무선 연결로 PC를 경유하게 하고, 센서가 보내는 패킷의 구조 및 전송 방법 분석


<img width="442" height="259" alt="image" src="https://github.com/user-attachments/assets/39eab49f-3477-4233-a0ec-122e011ea8d5" />



- (2) Scapy 라이브러리를 활용하여 패킷 스니핑 및 추출, JSON형태로 되어 있는 센서값을 HTTP POST 방식으로 플랫폼 서버로 전송(1분 주기)하는 프로그램 작성




<img width="478" height="429" alt="image" src="https://github.com/user-attachments/assets/42816cfe-ded7-45bb-a01f-615b575b4f23" />





 - (3) 서버에서 정상적으로 센서 측정값이 수신됨을 확인했음


 <img width="478" height="106" alt="image" src="https://github.com/user-attachments/assets/e4da00c9-a5ba-491f-b6d8-49caf6c01d38" />


---


### 2) 🔧 Room ↔ Home 양방향 매핑으로 JSON 순환참조 문제

**이슈**  
- `GET /homes`, `GET /homes/{id}`에서 응답이 수천 줄로 비정상적으로 커짐
- `Home.rooms → Room.home → Home.rooms …` 순환으로 중첩이 반복된다.

**원인**  
- 컨트롤러에서 **엔티티를 그대로 반환**함.  
- JPA 양방향 연관관계가 Jackson 직렬화와 결합되어 **무한 순환 구조**가 발생한다.

**해결**  
- **엔티티 대신 DTO 반환**으로 전환하고, 직렬화 **깊이를 1로 제한**한다.  
- Service 레이어에서 엔티티→DTO 변환을 수행하여 표현 모델을 분리한다.

**핵심 코드 (요약)**

```java
// HomeService — 엔티티 → DTO (핵심만)
@Transactional(readOnly = true)
public HomeResponseDto getHomeById(Long homeId) {
    Home home = homeRepository.findById(homeId)
        .orElseThrow(() -> new ResourceNotFoundException("집을 찾을 수 없습니다. id: " + homeId));
    return HomeMapper.toResponseDto(home);  // 엔티티 → DTO
}


// RoomService — 홈 하위 룸을 조회하고 DTO로 반환 (핵심만)
@Transactional(readOnly = true)
public List<RoomResponseDto> getAllRoomsByHomeId(Long homeId) {
    if (!homeRepository.existsById(homeId))
        throw new ResourceNotFoundException("홈을 찾을 수 없습니다. id: " + homeId);
    return roomRepository.findByHome_HomeId(homeId).stream()
        .map(RoomMapper::toResponseDto)   // 엔티티 → DTO
        .toList();
}


// Controller — DTO만 반환 (핵심만)
@GetMapping("/{homeId}")
public ResponseEntity<HomeResponseDto> getHomeById(@PathVariable Long homeId) {
    return ResponseEntity.ok(homeService.getHomeById(homeId));
}


```


**효과**
- 응답이 필요 필드만 포함되며 크기 정상화.
- 직렬화 순환 제거로 지연/메모리 사용 감소.
- 내부 엔티티 구조 노출을 막아 캡슐화가 강화됨.

---

### 3) 🔧 인증 토큰 교체 자동화

**이슈**
- 공기청정기의 상태 확인 및 제어를 하기 위한 인증 토큰의 유효 기한이 24시간으로 고정되어 있음
- 플랫폼 서버에서 가지고 있는 인증 토큰을 주기적으로 교체해야 함.

**해결방안**
- 24시간 주기로 인증 토큰 재발급 및 서버 전송 프로그램 작성, 인증 토큰 교체 자동화

**수행내용**
- (1) 인증 토큰 저장 위치 확인
<img width="478" height="250" alt="image" src="https://github.com/user-attachments/assets/6c123b67-eec7-4b2e-8a60-aa60956227ca" />


<img width="353" height="139" alt="image" src="https://github.com/user-attachments/assets/af550780-f38a-4b48-b168-b3da0e7e6aa0" />





- (2) 해당 파일에서 인증 토큰 내용 파싱 및 서버 전송(24시간 주기) 프로그램 작성
<img width="478" height="321" alt="image" src="https://github.com/user-attachments/assets/e64e8052-196c-405d-a93b-9a00fd305104" />



- (3) 플랫폼 서버에서 인증 토큰을 정상적으로 수신함을 확인
<img width="478" height="41" alt="image" src="https://github.com/user-attachments/assets/a9e0795a-e34c-48c6-97b1-aec29b4b9e56" />




