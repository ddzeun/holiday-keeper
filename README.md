# Holiday Keeper 

전 세계 공휴일 데이터를 관리하는 REST API 서비스입니다.  
[Nager.Date API](https://date.nager.at)를 활용하여 최근 5년간의 공휴일 데이터를 수집·조회·관리합니다.

---

## 📋 목차

- [아키텍처](#-아키텍처)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [실행 방법](#-실행-방법)
- [API 명세](#-api-명세)
- [데이터베이스 설계](#-데이터베이스-설계)
- [테스트](#-테스트)
- [프로젝트 구조](#-프로젝트-구조)

---

## 🏗️ 아키텍처

- 도메인 주도 설계(DDD): 비즈니스 로직을 도메인 엔티티 내부에 캡슐화하여, 서비스 계층이 비대해지는 것을 방지하고 객체지향적 설계를 위해 노력했습니다.

## ✨ 주요 기능

### 1. 데이터 적재
- 애플리케이션 최초 실행 시 자동으로 최근 5년 전 세계 공휴일 데이터 적재
- 병렬 처리를 통한 빠른 초기화 (멀티스레드 활용)
- 현지명 기준 중복 데이터 삭제

### 2. 검색
- 국가 코드, 기간(from~to), 공휴일 타입 등 다양한 필터 조합 지원
- 페이징 처리 (기본 20개)
- JPA Specification을 활용한 동적 쿼리 구성

### 3. 재동기화 (Refresh)
- 특정 연도/국가의 공휴일 데이터를 외부 API에서 재조회
- Upsert 방식으로 기존 데이터 갱신 및 신규 데이터 추가
- API에서 제거된 공휴일은 자동 삭제

### 4. 삭제
- 특정 연도/국가의 공휴일 데이터 일괄 삭제

### 5. 배치 자동화
- 매년 1월 2일 01:00 KST에 전년도 및 금년도 데이터 자동 동기화
- Spring Scheduler를 활용한 Cron 기반 스케줄링

---

## 🛠 기술 스택

### Backend
- Java 21
- Spring Boot 3.4
- Spring Data JPA (Hibernate)
- H2 Database (인메모리)
- WebFlux (외부 API 호출)

### Frontend (시연용 데모)
- React 18 + TypeScript
- Vite (build)
- Axios (HTTP 클라이언트)

### Documentation & Testing
- OpenAPI 3 (Swagger UI)
- JUnit 5 + Mockito
- AssertJ

### 기타
- Lombok
- Validation

---

## 🚀 실행 방법

### 사전 요구사항
- **Java 21** 이상
- **Gradle 8.x**
- **Node.js 18.x**(프론트엔드 실행 시)

### 1. 백엔드 실행

```bash
# 프로젝트 클론
git clone <repository-url>
cd holiday-keeper

# 실행 (Gradle Wrapper 사용)
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew clean build
java -jar build/libs/holiday-keeper-0.0.1-SNAPSHOT.jar
```
### 선택사항: 도커 실행

```bash
# 실행권한 부여
chmod +x local.sh

# 실행
./local.sh
```
> 스크립트 실행 시 테스트는 제외하고 빌드되므로 테스트는 별도 실행해야 합니다.


**실행 후 접속 URL:**
- API 서버: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:holidaydb`
  - Username: `sa`
  - Password: (없음)

### 2. 프론트엔드 실행 (선택)

```bash
cd frontend

# 의존성 설치 (최초 1회)
npm install

# 개발 서버 실행
npm run dev
```

**프론트엔드 접속:** http://localhost:3000

---

## 📡 API 명세

### Base URL
```
http://localhost:8080/api
```

### 공통 응답 형식
모든 API는 다음과 같은 공통 Wrapper로 응답합니다:

```json
{
  "success": true,
  "code": null,
  "message": "요청 메시지 (선택)",
  "data": {
    // 실제 응답 데이터
  }
}
```

---

### 1. 공휴일 조회
**GET** `/api/holidays`

**Query Parameters:**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| countryCode | String | X | 2자리 국가 코드 (대문자) | KR, US |
| from | Date | X | 조회 시작일 | 2025-01-01 |
| to | Date | X | 조회 종료일 | 2025-12-31 |
| type | Enum | X | 공휴일 타입 | PUBLIC, BANK, SCHOOL |
| page | Integer | X | 페이지 번호 (0부터 시작) | 0 |
| size | Integer | X | 페이지 크기 | 20 |

**Response Example:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "date": "2025-01-01",
        "localName": "신정",
        "englishName": "New Year's Day",
        "countryCode": "KR",
        "fixed": false,
        "globalHoliday": true,
        "launchYear": 1949,
        "types": ["PUBLIC"],
        "counties": []
      }
    ],
    "page": {
      "size": 20,
      "number": 0,
      "totalElements": 15,
      "totalPages": 1
    }
  }
}
```

---

### 2. 공휴일 재동기화
**POST** `/api/holidays/refresh`

**Query Parameters:**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| year | Integer | O | 갱신할 연도 |
| countryCode | String | O | 2자리 국가 코드 |

**Example:**
```bash
POST /api/holidays/refresh?year=2025&countryCode=KR
```

**Response:**
```json
{
  "success": true,
  "message": "공휴일 데이터가 새로고침되었습니다."
}
```

---

### 3. 공휴일 삭제
**DELETE** `/api/holidays`

**Query Parameters:**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| year | Integer | O | 삭제할 연도 |
| countryCode | String | O | 2자리 국가 코드 |

**Example:**
```bash
DELETE /api/holidays?year=2024&countryCode=US
```

**Response:**
```json
{
  "success": true,
  "message": "2024년 공휴일 데이터가 삭제되었습니다."
}
```

---

## 🗄️ 데이터베이스 설계

<details>
<summary>ERD 이미지 보기</summary>
<img width="1105" height="395" alt="image" src="https://github.com/user-attachments/assets/8e6780d6-1625-4179-aeba-004c65a17a07" />
</details>

### ERD 개요
- `Country`: 국가 정보
- `Holiday`: 공휴일 정보
- `holiday_types`: 공휴일 타입 (다대다)
- `holiday_counties`: 지역별 공휴일 정보

### 주요 테이블

- Country와 Holiday는 느슨한 참조 관계로 직접적인 FK 제약 조건을 가지지 않습니다.

#### Country
| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 기본키 |
| country_code | VARCHAR(4) | UNIQUE, NOT NULL | 국가 코드 (KR, US) |
| name | VARCHAR(255) | NOT NULL | 국가명 |

#### Holiday
| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 기본키 |
| date | DATE | NOT NULL | 공휴일 날짜 |
| local_name | VARCHAR(255) | NOT NULL | 현지 언어 이름 |
| english_name | VARCHAR(255) | NOT NULL | 영문 이름 |
| country_code | VARCHAR(4) | NOT NULL | 국가 코드 |
| fixed | BOOLEAN | | 고정 공휴일 여부 |
| global_holiday | BOOLEAN | | 전국 공휴일 여부 |
| launch_year | INTEGER | | 공휴일 제정 연도 |
| created_at | TIMESTAMP | | 생성 일시 |
| updated_at | TIMESTAMP | | 수정 일시 |

**Unique Constraint:** `(country_code, date, local_name)`

#### holiday_types (ElementCollection)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| holiday_id | BIGINT | Holiday FK |
| type_name | VARCHAR(50) | PUBLIC, BANK, SCHOOL 등 |

#### holiday_counties (ElementCollection)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| holiday_id | BIGINT | Holiday FK |
| county_code | VARCHAR(255) | 지역 코드 |

---

## 🧪 테스트

### 테스트 실행
```bash
./gradlew clean test
```

#### `./gradlew clean test` 실행 결과
<img width="3052" height="1740" alt="image" src="https://github.com/user-attachments/assets/5ad95997-193b-4fce-bbfb-fffb3d94426b" />


### 테스트 커버리지

#### 1. 단위 테스트
- `HolidayServiceTest`: Service 계층 로직 테스트 (Mock 활용)
- `HolidaySyncServiceTest`: 초기 데이터 적재 로직 테스트

#### 2. 통합 테스트
- `HolidaySearchServiceTest`: 검색 기능 통합 테스트 (실제 DB 사용)
- `HolidayRepositoryTest`: Repository 레이어 테스트

#### 3. API 통합 테스트
- `NagerApiClientIntegrationTest`: 실제 외부 API 호출 테스트 (수동 실행용)

---

## 📁 프로젝트 구조
<details>
<summary>프로젝트 구조 보기</summary>
  
```
holiday-keeper/
├── src/main/java/com/ddzeun/holidaykeeper/
│   ├── HolidayKeeperApplication.java          # 메인 애플리케이션
│   ├── common/                                 # 공통 모듈
│   │   ├── dto/ApiResponse.java               # 공통 응답 Wrapper
│   │   └── exception/                         # 예외 처리
│   │       ├── ErrorCode.java
│   │       ├── ExternalApiException.java
│   │       └── GlobalExceptionHandler.java
│   ├── config/                                # 설정
│   │   ├── NagerApiWebClientConfig.java       # WebClient 설정
│   │   ├── OpenApiConfig.java                 # Swagger 설정
│   │   └── WebConfig.java                     # CORS 설정
│   ├── country/                               # 국가 도메인
│   │   ├── domain/Country.java
│   │   └── repository/CountryRepository.java
│   ├── external/nager/                        # 외부 API 클라이언트
│   │   ├── NagerApiClient.java
│   │   └── dto/
│   └── holiday/                               # 공휴일 도메인
│       ├── application/                       # 비즈니스 로직
│       │   ├── HolidayService.java           # 공휴일 CRUD
│       │   ├── HolidaySearchService.java     # 검색 서비스
│       │   ├── HolidaySyncService.java       # 초기 적재
│       │   ├── HolidaySyncScheduler.java     # 배치 스케줄러
│       │   └── HolidayYearPolicy.java        # 연도 정책
│       ├── domain/Holiday.java               # 엔티티
│       ├── enums/HolidayType.java            # 공휴일 타입
│       ├── presentation/                     # 컨트롤러
│       │   ├── HolidaySearchController.java
│       │   └── dto/HolidaySearchResponse.java
│       └── repository/                       # 저장소
│           ├── HolidayRepository.java
│           └── HolidaySpecification.java     # 동적 쿼리
│
├── frontend/                                  # React 프론트엔드
│   ├── src/
│   │   ├── api/holidayApi.ts                 # API 클라이언트
│   │   ├── types/holiday.ts                  # 타입 정의
│   │   ├── App.tsx                           # 메인 컴포넌트
│   │   └── App.css                           # 스타일
│   └── package.json
│
└── src/test/java/                            # 테스트 코드
    └── com/ddzeun/holidaykeeper/
        ├── HolidayKeeperApplicationTests.java
        ├── external/nager/
        └── holiday/application/
```


</details>


---

## 🎯 주요 설계 결정

### 1. 데이터 중복 방지
- `Holiday` 테이블에 `(country_code, date, local_name)` Unique Constraint 설정
- 초기 적재 시 중복 데이터 필터링(ex. local name은 동일, english name이 다른 경우)

### 2. 병렬 처리
- `HolidaySyncService`에서 `ExecutorService`를 활용한 멀티스레드 처리
- CPU 코어 수의 2배 스레드 풀 사용으로 초기 적재 시간 대폭 단축

### 3. 동적 쿼리
- JPA Specification을 활용한 유연한 검색 조건 조합
- 페이징 처리로 대용량 데이터 효율적 조회

### 4. 예외 처리
- `@RestControllerAdvice`를 통한 전역 예외 처리
- 외부 API 호출 실패 시 `ExternalApiException` 커스텀 예외 발생

### 5. 테스트 전략
- 단위 테스트: Mock 객체로 의존성 격리
- 통합 테스트: 실제 DB 사용으로 전체 흐름 검증
- `@ActiveProfiles("test")`로 테스트 환경 분리

---

# 기타

## 프론트엔드 실행 이미지
<details>
  <summary>
    조회, 삭제, 동기화
  </summary>
  <img width="1114" height="763" alt="image" src="https://github.com/user-attachments/assets/ca35573f-b1a8-448c-9522-99a81535ebe6" />
</details>

<details>
  <summary>
    조회 후 하단 리스트 결과
  </summary>
  <img width="540" height="763" alt="image" src="https://github.com/user-attachments/assets/ed8b3c6f-eb4a-4b26-8d14-11a190f2036f" />
</details>

## 📝 개발 노트

### 선택 구현 사항
✅ 배치 자동화 (Scheduler)  
✅ 프론트엔드 UI  
✅ 단위/통합 테스트  
✅ 병렬 처리 최적화  
✅ CORS 설정  
✅ 전역 예외 처리  
✅ GitHub Actions CI/CD  
✅ Docker 컨테이너화  

### 추가 개선 가능 사항
- Redis 캐싱 추가
- API Rate Limiting

### 고민 사항
- Docker를 사용하여 실행 시 데이터 적재 속도가 현저하게 느려짐. 원인과 해결방안이 있을까



