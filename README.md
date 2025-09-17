# 가정용 공기질 관리 플랫폼 

---

## 1) 프로젝트 한 줄 소개

실내 공기질(미세먼지/온습도/CO₂ 등)을 **실시간 모니터링**하고, **예측 + 자동 제어**로 선제 대응하는 가정용 플랫폼을 구현한다.

* **핵심 가치**: 사용자가 체감할 수 있는 공기질 개선과 에너지 절감
---

## 2) 주요 기능 (Features)

* **실시간 수집/시각화**: 센서 데이터(예: PM2.5, 온도, 습도, CO₂)를 1분 단위로 수집·저장·조회한다.
* **예측 + 사전 대응**: 외부 기상/대기질 API(예: OpenWeatherMap/Weatherbit)와 내부 데이터를 조합해 공기질 악화를 예측하고, 공기청정기/환기 장치를 **미리** 켠다.
* **자동 제어 정책**: 사용자 선호(알러지/천식 등)와 필터 수명 상태를 반영해 **맞춤형 제어 시나리오**를 실행한다.
* **SmartThings 연동**: 디바이스 상태 조회/제어, 토큰 관리, 주기적 스케줄러로 상태 동기화한다.
* **필터 수명 관리**: 사용량/입자량 기반으로 필터 잔여 수명을 추정하고 교체 알림을 보낸다.
* **알림/스케줄**: 공기질 악화/회복, 필터 교체 시점 등을 알림으로 통지한다.
* **보안/JWT 인증**: 로그인/토큰 갱신/로그아웃 흐름을 제공한다.

---

## 3) 나의 역할 (What I built)

* 백엔드 총괄(아키텍처 설계, 도메인 모델링, API/인증, 스케줄러, 외부 연동) 역할을 맡았다.
* 직접 가정에서 **IoT 센서 설치 및 데이터 수집 파이프라인**을 구성했다.
* SmartThings/대기질 API 연동, 필터 수명 로직, 사용자 맞춤 제어 정책을 구현했다.

> 팀 구성/기여 비율이 있다면 여기에 구체적으로 적는다. (예: 백엔드 100%, 프론트 협업 1명 등)

---

## 4) 기술 스택 (Tech Stack)

* **Language/Build**: Java 17, Gradle
* **Framework**: Spring Boot, Spring Web, Spring Data JPA, Spring Security (JWT)
* **DB**: MySQL 8.x
* **Docs**: springdoc-openapi (Swagger UI)
* **Libraries**: jjwt, Lombok 등
* **External**: SmartThings REST API, OpenWeatherMap/Weatherbit API
* **Infra(선택)**: AWS EC2/RDS, S3, GitHub Actions (필요 시 기재)

---

## 5) 시스템 구조 (Architecture)

```
[IoT Sensors]──(1분)──>[Backend API]──> [MySQL]
                       │              └── [Schedulers: 상태/필터/알림]
                       ├── SmartThings 제어/상태 동기화
                       └── 대기질/날씨 API(외부)
```

* **도메인**: User, Home/Room, Device, Sensor, Measurement, Filter/FilterStatus, Notification, Schedule, DeviceAutoControl, (AI 예측/제어 배치 등)
* **흐름**: 요청→Controller→Service→Repository(JPA)→DB, 인증 필터(JWT)

---

## 6) 폴더 구조 (요약)

> 실제 레포 구조에 맞춰 경로를 조정한다.

```
backend/
 ├─ src/main/java/com/example/airqualityplatform
 │   ├─ controller/        # REST 컨트롤러 (Auth, Device, Sensor, Measurement, ...)
 │   ├─ service/           # 비즈니스 로직 (SmartThings, 필터수명, 스케줄러 포함)
 │   ├─ domain/            # JPA 엔티티
 │   ├─ repository/        # Spring Data JPA 리포지토리
 │   ├─ dto/               # 요청/응답 DTO
 │   ├─ security/          # JWT 필터/토큰/설정
 │   └─ AirQualityPlatformApplication.java
 └─ src/main/resources/
     ├─ application.properties (또는 application.yml)
     └─ schema.sql / data.sql (선택)
```

---

## 7) 빠른 실행 (Local Quickstart)

### (1) 환경 변수

> 반드시 **환경 변수**로 관리하고, 레포에 시크릿을 커밋하지 않는다.

| KEY                  | 예시                                                                       | 설명               |
| -------------------- | ------------------------------------------------------------------------ | ---------------- |
| `DB_URL`             | `jdbc:mysql://localhost:3306/air?useSSL=false&serverTimezone=Asia/Seoul` | MySQL 연결 URL     |
| `DB_USERNAME`        | `root`                                                                   | DB 계정            |
| `DB_PASSWORD`        | `********`                                                               | DB 비밀번호          |
| `JWT_SECRET`         | 랜덤 64+ 바이트                                                               | JWT 서명 키         |
| `SMARTTHINGS_TOKEN`  | `st-***`                                                                 | SmartThings PAT  |
| `OWM_API_KEY`        | `owm-***`                                                                | OpenWeatherMap 키 |
| `WEATHERBIT_API_KEY` | `wb-***`                                                                 | Weatherbit 키     |

`application.properties` 예시(템플릿):

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jackson.time-zone=Asia/Seoul
jwt.secret=${JWT_SECRET}
external.smartthings.token=${SMARTTHINGS_TOKEN}
external.weather.owm.key=${OWM_API_KEY}
external.weather.weatherbit.key=${WEATHERBIT_API_KEY}
```

### (2) MySQL 준비

```bash
# Docker 예시
docker run -d --name air-mysql -e MYSQL_ROOT_PASSWORD=pass -e MYSQL_DATABASE=air \
  -p 3306:3306 mysql:8
```

### (3) 빌드 & 실행

```bash
cd backend
./gradlew clean build
java -jar build/libs/*-SNAPSHOT.jar
# 또는
./gradlew bootRun
```

* 서버 기본 포트: `8080` (변경 시 README에 명시한다)
* 헬스체크: `GET /actuator/health` (사용 중이면 명시한다)

---

## 8) API 문서 & 사용 예시

* **Swagger UI**: `http://localhost:8080/swagger-ui/index.html` (springdoc 포함 시)
* **OpenAPI JSON**: `/v3/api-docs`

### Auth

```bash
# 회원가입
echo '{"email":"test@ex.com","password":"pass","name":"user"}' \
| http POST :8080/auth/signup

# 로그인 (액세스/리프레시 토큰)
echo '{"email":"test@ex.com","password":"pass"}' \
| http POST :8080/auth/login
```

### 센서/측정치

```bash
# 센서 등록
http POST :8080/sensors name='living-pm' type='PM2_5' roomId:=1

# 측정치 업로드
http POST :8080/measurements sensorId:=1 pm25:=37.4 humidity:=42.0 temp:=25.3 timestamp=='2025-09-17T12:00:00+09:00'
```

### 디바이스 제어(SmartThings)

```bash
# 디바이스 켜기
http POST :8080/devices/123/power action='ON'
```

> 실제 엔드포인트 이름/필드는 프로젝트에 맞게 교체한다. Swagger에서 확인해 복붙한다.

---

## 9) 핵심 설계 포인트 (Why)

* **예측 + 제어**로 단순 알림을 넘어 **사용자 체감 가치**를 만든다.
* **사용자 맞춤 기준치**: 알러지/천식 등 사용자의 컨디션을 반영한다.
* **필터 수명 평가**: 디바이스 사용량·입자량 기반으로 **교체 시점**을 가시화한다.
* **스케줄러**: 디바이스 상태/필터 상태를 주기적으로 동기화해 **자동 운영**을 지향한다.
* **JWT 보안**: 토큰 기반 인증으로 모바일/웹/IoT 게이트웨이 확장성을 고려한다.

---

## 10) 성과 (Numbers)

> 실제 수치가 있다면 반드시 적는다. (예: 공기질 회복 시간 단축 %, 에너지 절감 %, 사용자 알림 클릭률 등)

* 예: 공기질 악화 예측 정확도 **xx%**, 선제 제어로 평균 회복 시간 **yy분 → zz분(-aa%)**

---


## 13) 실행 스크린샷 (선택)

> 대시보드/알림/제어 화면, ERD/클래스 다이어그램 등을 `docs/` 폴더에 추가하고 여기에 삽입한다.

---

## 14) 라이선스

MIT (또는 학교 과제/개인 포트폴리오 용도임을 명시)

---

## 15) 커밋/브랜치 전략 (선택)

* 브랜치: `main`(배포), `feat/*`(기능), `fix/*`(버그), `chore/*`(환경)
* 커밋 메시지: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:` 규칙 사용

---

## 16) 참고 (Acknowledgements)

* SmartThings Platform, OpenWeatherMap, Weatherbit
* 수업/지도교수/팀원 (이름 표기 시 동의 여부 확인)
