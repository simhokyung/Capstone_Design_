### 1) 🔧센서와 서버 간 데이터 통신 구현 문제
센서에서 측정값을 전송하는 서버가 TelosAir(센서 제조사) 서버로 고정되어 있어 플랫폼에서 센서의 측정값을 받으려면 TelosAir 웹 사이트에서 값을 읽어내야 함. 

이는 데이터 접근성이 떨어지고, 시스템 활용에 제약이 생긴다.


**해결방안**: 센서 인터넷 연결 시 PC를 경유하게 하여 통신 내역을 확인, 플렛폼 서버로 전송
<img width="478" height="276" alt="image" src="https://github.com/user-attachments/assets/f84168be-3de8-4fa0-a7b0-b44c0f0f21d3" />



**수행내용**:

(1) 센서 인터넷 연결 시, PC의 핫스팟을 통한 무선 연결로 PC를 경유하게 하고, 센서가 보내는 패킷의 구조 및 전송 방법 분석


<img width="442" height="259" alt="image" src="https://github.com/user-attachments/assets/39eab49f-3477-4233-a0ec-122e011ea8d5" />



(2) Scapy 라이브러리를 활용하여 패킷 스니핑 및 추출, JSON형태로 되어 있는 센서값을 HTTP POST 방식으로 플랫폼 서버로 전송(1분 주기)하는 프로그램 작성




<img width="478" height="429" alt="image" src="https://github.com/user-attachments/assets/42816cfe-ded7-45bb-a01f-615b575b4f23" />





 (3) 서버에서 정상적으로 센서 측정값이 수신됨을 확인했음


 <img width="478" height="106" alt="image" src="https://github.com/user-attachments/assets/e4da00c9-a5ba-491f-b6d8-49caf6c01d38" />





### 2) 🔧 Room ↔ Home 양방향 매핑으로 JSON 순환참조 문제

**증상**  
- `GET /homes`, `GET /homes/{id}`, `GET /homes/{homeId}/rooms` 응답이 수천 줄로 비정상적으로 커지거나 직렬화가 지연된다.  
- `Home.rooms → Room.home → Home.rooms …` 순환으로 중첩이 반복된다.

**원인**  
- 컨트롤러에서 **엔티티를 그대로 반환**함.  
- JPA 양방향 연관관계가 Jackson 직렬화와 결합되어 **무한 순환 구조**가 발생한다.

**해결**  
- **엔티티 대신 DTO 반환**으로 전환하고, 직렬화 **깊이를 1로 제한**한다.  
- 매핑은 Service 레이어에서 수행하며 `HomeMapper`, `RoomMapper`를 사용한다.  
- Room API는 **계층형 경로**(`/homes/{homeId}/rooms`)로 범위를 명확히 한다.

**핵심 코드 (요약)**

```java
// HomeService
@Transactional(readOnly = true)
public List<HomeResponseDto> getAllHomes() {
    return homeRepository.findAll().stream()
        .map(HomeMapper::toResponseDto)  // 엔티티 → DTO
        .toList();
}

@Transactional(readOnly = true)
public HomeResponseDto getHomeById(Long homeId) {
    Home home = homeRepository.findById(homeId)
        .orElseThrow(() -> new ResourceNotFoundException("집을 찾을 수 없습니다. id: " + homeId));
    return HomeMapper.toResponseDto(home);  // 엔티티 → DTO
}



// RoomService
@Transactional
public RoomResponseDto createRoom(Long homeId, RoomRequestDto dto) {
    Home home = homeRepository.findById(homeId)
        .orElseThrow(() -> new ResourceNotFoundException("홈을 찾을 수 없습니다. id: " + homeId));
    Room room = RoomMapper.toEntity(dto, null);
    room.setHome(home);
    return RoomMapper.toResponseDto(roomRepository.save(room));  // DTO 반환
}

@Transactional(readOnly = true)
public List<RoomResponseDto> getAllRoomsByHomeId(Long homeId) {
    if (!homeRepository.existsById(homeId))
        throw new ResourceNotFoundException("홈을 찾을 수 없습니다. id: " + homeId);
    return roomRepository.findByHome_HomeId(homeId).stream()
        .map(RoomMapper::toResponseDto)  // 엔티티 → DTO
        .toList();
}




// Controller — DTO만 반환 (발췌)
@RestController @RequestMapping("/homes")
@RequiredArgsConstructor
public class HomeController {
  private final HomeService homeService;
  @GetMapping("/{homeId}")
  public ResponseEntity<HomeResponseDto> getHomeById(@PathVariable Long homeId) {
    return ResponseEntity.ok(homeService.getHomeById(homeId));
  }
}

@RestController @RequestMapping("/homes/{homeId}/rooms")
@RequiredArgsConstructor
public class RoomController {
  private final RoomService roomService;
  @GetMapping
  public ResponseEntity<List<RoomResponseDto>> getAllRooms(@PathVariable Long homeId) {
    return ResponseEntity.ok(roomService.getAllRoomsByHomeId(homeId));
  }
}
```


**효과**
- 응답이 필요 필드만 포함되며 크기 정상화, 직렬화 순환 제거로 지연/메모리 사용 감소.
- 내부 엔티티 구조 노출을 막아 캡슐화가 강화된다.

**재발 방지**
- 컨트롤러에서 엔티티 직접 반환 금지 원칙 유지.

