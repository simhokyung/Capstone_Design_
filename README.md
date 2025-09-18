# 🏠가정용 공기질 관리 플랫폼 

**AI 예측**과 **최적제어** 알고리즘을 적용해 


**에너지 절감**과 **사전 대응 기능**을 갖춘 가정용 공기질 관리 플랫폼을 개발한다.

---

## 개발 배경
<img width="907" height="400" alt="image" src="https://github.com/user-attachments/assets/739a4e7d-c974-4601-a3e8-bc5a99267195" />


국립 환경 과학원에 따르면 한국인은 


**하루 중 86%의 시간을 실내 환경**에서 생활



<img width="923" height="328" alt="image" src="https://github.com/user-attachments/assets/704b5277-fe31-4e88-9c33-fe0020954487" />


정부는 실내 공기질 관리법으로 다중 이용 시설에서 **실내 공기질 관리를 의무화** 


2024년 그 **기준**을 **강화**


이에따라 **실내 공기질 관리**에 다방면으로 **도움**을 줄 수 있는


**가정용 공기질 관리 플랫폼**을 개발하게 되었습니다.

---

## 2) 주요 기능 (Features)

* **AI기반 공기질 예측**: 운영 환경에서의 **데이터를 수집**하여 **학습**하고, 현재 상태를 바탕으로 1시간 내의 실내 공기질 상태를 **1분 단위**로 **예측**
* **예측값 기반 사전 대응**: 예측값을 바탕으로 공기청정기의 **최적 제어** 스케줄을 10분 단위로 제공
* **필터 수명 계산**: 필터 수명은 단순 시간 누적이 아니라, 공기청정기의 **팬 속도**와 **공기질 지수**(AQI)를 가중치로 반영해 **실제 사용 환경을 고려**해 계산
* **맞춤형 공기질 관리 제공**: 사용자에게 천식, 알러지 여부를 입력받아 **공기질 관리 기준치를 설정**하여 사용자 **맞춤형** 공기질 관리 서비스를 제공
* **시각화 정보 제공(그래프, 히트맵)**: 다수의 IoT센서를 활용해 센서간 간격을 좁혀 **해상도 높은 데이터**를 사용,

사용자에게 실내 공기질 상태를 **히트맵 형식**으로 제공해 **오염원을 한눈**에 파악할 수 있게 한다


---

## 3) 앱 설명




<img width="213" height="522" alt="image" src="https://github.com/user-attachments/assets/29dd8504-17ba-4208-b57d-d0b171c3bb4c" />


**홈 화면**에서는 **실내외 공기질 상태** 및 **공기질 예측 정보**를 **요약**해서 볼 수 있다.












<img width="229" height="551" alt="image" src="https://github.com/user-attachments/assets/bf084fe2-ed51-4104-94df-4a8ff4ae6390" /> <img width="225" height="550" alt="image" src="https://github.com/user-attachments/assets/242a24c6-2989-4dc5-9202-95904710d940" />



(1)**실외 화면**에서는 **실외 공기질 상태** 및 **24시간 예측 그래프**를 확인할 수 있으며, 

(2)각 항목에 대한 설명 및 단계별 기준치를 확인할 수 있다.













<img width="242" height="591" alt="image" src="https://github.com/user-attachments/assets/90c85e16-41a6-4d0c-a35c-a6749140e624" /> <img width="242" height="591" alt="image" src="https://github.com/user-attachments/assets/0fecb07e-70cf-4676-8c4f-7aa99e85f6d9" /> <img width="242" height="591" alt="image" src="https://github.com/user-attachments/assets/4c8c996a-a8f8-4b10-9956-7d702c18a1c2" />

(1)**실내 화면**에서는 **실내 공기질 상태** 및 **1시간 예측 그래프**를 확인할 수 있으며, 

(2)**방별 예측 그래프**도 확인할 수 있다. 

(3)최근 24시간 내 실내 공기질 상태를 **히트맵** 형태로 볼 수 있어 **오염원을 한눈에 파악**할 수 있다.











<img width="242" height="591" alt="image" src="https://github.com/user-attachments/assets/a3f832fe-86a8-49f8-903b-1d313ed5f0f8" /> <img width="241" height="572" alt="image" src="https://github.com/user-attachments/assets/c715fe92-ea56-4120-976a-5301305c49d2" />




(1)**스케줄 수정 화면**에서는 사용자의 외출 시간을 입력해 **불필요한 에너지 소비를 줄일** 수 있다.

(2)**관리 기준치 수정 화면**에서는 사용자에게 **맞춤형 공기질 관리 서비스**를 제공하기 위해 관리 임계치를 입력받는다. 

이때 알러지, 천식 여부에 따라 **관리 임계치를 추천**해주며, 필요시 세부 설정을 할 수 있다.













<img width="241" height="574" alt="image" src="https://github.com/user-attachments/assets/d52d63a7-957a-4a03-a0a3-4b4c8152730f" /> <img width="241" height="567" alt="image" src="https://github.com/user-attachments/assets/ec393fb8-44e0-42f0-a431-ec0266575d08" />




(1)사용자가 **자동제어**를 적용할 공간을 고른다. 

(2)그 공간에서 적용할 **공기질 기준치**를 선택한다.

이 기준치의 **초기값**은 **사용자가 위에서 셋팅**한 관리 임계치 값이다.

이 기준치를 **그대로** 사용해도 되고, 공간에 따라 **수정**해서 사용해도 된다. 
ex) 방은 기준치를 엄격하게, 주방은 널널하게

기준치를 설정하면, 방의 정보와 임계치 값이 AI 서버로 넘어가고,

AI는 이 정보를 받아 방 별로 **최적제어**를 내려 공기청정기를 제어한다.



  









---

## 3) 나의 역할 (What I built)
<img width="671" height="453" alt="image" src="https://github.com/user-attachments/assets/aa5d05c8-a96b-484f-851c-058f98b46433" />


* 팀장으로, 백엔드 역할을 맡았다.
* 아키텍처 설계, 도메인 모델링, ERD 테이블, API 명세서 등을 작성하였다.
* 1분마다 수신되는 센서&공기청정기 데이터 -> DB저장 -> 병합하여 AI 서버에 전송하는 흐름 완성.
* SmartThings/대기질 API 연동, 필터 수명 로직, 사용자 맞춤 제어 정책을 구현했다.
* 스프링 서버를 AWS EC2에 배포.

---

## 4) 기술 스택(백엔드) (Tech Stack)

* **Language/Build**: Java 17, Gradle
* **Framework**: Spring Boot, Spring Web, Spring Data JPA, Spring Security (JWT)
* **DB**: MySQL
* **Libraries**: jjwt, Lombok 등
* **External**: SmartThings REST API, OpenWeatherMap/Weatherbit API
* **Infra**: AWS EC2/RDS, S3, GitHub Actions

---

## 5) 시스템 구조 (Architecture)

<img width="1333" height="620" alt="image" src="https://github.com/user-attachments/assets/4b855d61-8f65-437a-9538-9164070a3ef7" />


---

## 6) ERD
<img width="3316" height="5842" alt="diagram" src="https://github.com/user-attachments/assets/dc102fe8-46a0-47b0-ac81-8d37d68f1196" />


---

## 7) 빠른 실행 (Local Quickstart)

### (1) 환경 변수


| KEY                  | 예시                                                                     | 설명             |
| -------------------- | ------------------------------------------------------------------------ | ---------------- |
| `DB_URL`             | `jdbc:mysql://localhost:3306/air?useSSL=false&serverTimezone=Asia/Seoul` | MySQL 연결 URL   |
| `DB_USERNAME`        | `root`                                                                   | DB 계정          |
| `DB_PASSWORD`        | `********`                                                               | DB 비밀번호       |
| `JWT_SECRET`         | 랜덤 64+ 바이트                                                           | JWT 서명 키       |
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

* 서버 기본 포트: `8080`
---

## 8) API 문서 & 사용 예시

**API 명세서 주소**: <https://solar-skate-88b.notion.site/API-1f31bcaecabd80ebb3aae45ffce4eaa4#2031bcaecabd80b1b0d8f06554be7522>

---

## 10) 성과 (Numbers)

<img width="795" height="181" alt="image" src="https://github.com/user-attachments/assets/99d4a92f-2301-4ea3-9719-0ec434b86a5d" />

LSTM 기반 공기질 예측 모델의 성능을 평가한 결과, 

예측값이 전체적으로 실제 측정값과 높은 유사도를 보이며 안정적인 성능을 나타냈다. 

---

## 11) 트러블슈팅

---



