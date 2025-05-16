# DatePicker

> 팀원 또는 지인 간의 공통 가능한 시간대를 효율적으로 찾기 위해  
> 일정 정보를 간편하게 공유하고 시각적으로 확인할 수 있는 일정 조율 웹서비스

---

## 📅 프로젝트 개요

- **프로젝트명:** DatePicker  
- **개발 기간:** 2025-05-08 ~ 2025-05-16  
- **핵심 목표:** 여러 사용자 간의 효율적인 일정 조율 및 겹치는 시간대 시각화  
- **주요 기능:**
  - 이벤트 생성 및 사용자 초대
  - 참여자들이 선택한 시간대 데이터를 기반으로 겹치는 인원 수 시각화

---

## 팀원 소개
| 이름   | 역할         | 담당 기능 및 설명 |
|--------|--------------|-------------------|
| 장승범| 팀장 / 백엔드 | 인증 / 인가, USER 및 SCHEDULE API 구현 |
| 이종규 | 백엔드   | EVENT API 및 오버랩 일정, 알림 로직 구현  |
| 신승우 | 프론트엔드       | UI 설계 및 프론트엔드 기능 구현 |

## 🛠 사용 기술

### Backend
- Java 17
- Spring Boot
- Spring Security
- Spring WebSocket – 실시간 초대 알림 기능
- MyBatis
- MySQL
- Swagger – API 문서화

### Frontend
- HTML/CSS/JavaScript
- Tailwind CSS
- SockJS, StompJS – WebSocket 클라이언트
- AlertifyJS, SweetAlert2 – 알림 및 팝업

---

## ERD

![ERD](https://github.com/user-attachments/assets/c992f57a-f706-438b-95c0-a43ad1d7ea1a)

---

## 실행화면

![gif](https://github.com/user-attachments/assets/2127ea95-3490-4ada-b249-fc33c4443f49)

---

## 🧱 프로젝트 구조

src/  
├── main/  
│ ├── java/com/mycom/myapp/  
│ │ ├── config/ # 설정 관련 클래스  
│ │ ├── events/ # 이벤트 관련 컨트롤러, 서비스, DTO  
│ │ ├── notifications/ # 실시간 알림 관련 로직  
│ │ ├── schedules/ # 스케줄 관련 로직  
│ │ └── users/ # 사용자 인증 및 관리 로직  
│ └── resources/  
│ ├── mapper/ # MyBatis 매퍼 XML 파일  
│ ├── static/ # 정적 리소스 (HTML, JS, CSS)  
│ └── application.yml # 설정 파일  
└── test/ # 테스트 코드

---

## 🚀 프로젝트 실행 방법

### 1. 환경 설정

- **Java 17** 이상 설치
- **MySQL 8.x** 설치 및 데이터베이스 생성
- **포트**: 기본적으로 `8080`번 사용

### 2. MySQL 설정

```sql
CREATE DATABASE datepicker DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

```

`application.yml` 혹은 `application.properties`에 본인의 DB 정보 기입:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/datepicker
    username: your_db_user
    password: your_db_password

```

### 3. 프로젝트 실행

```bash
./mvnw spring-boot:run

```

### 4. 프론트엔드 접속

-   브라우저에서 `http://localhost:8080/index.html` 접속
    
-   이벤트 생성 및 사용자 초대, 시간대 시각화 등을 이용할 수 있음
    

----------

## 📑 API 문서

-   Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
    
----------

## 🧪 테스트

-   JUnit 5 및 AssertJ 기반 단위 테스트 작성
    
-   테스트 코드는 `src/test` 하위에 위치
    

----------

