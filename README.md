# 📚 BookSpace
독서 커뮤니티 플랫폼 - AI 기반 유저의 상황과 감정에 따른 도서 추천과 활발한 독서 커뮤니티를 제공하는 웹 애플리케이션

<br>

## 📋 목차

- [프로젝트 소개](#프로젝트-소개)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [API 문서](#api-문서)

## 🎯 프로젝트 소개
BookSpace는 독서를 사랑하는 사람들을 위한 커뮤니티 플랫폼입니다. AI 기반 도서 추천, 도서 검색 및 리뷰 작성, 도서 관련 게시글 공유의 다양한 기능을 제공합니다.

### Team 10. NorthFace (SSAFY 14th 17-10)
**팀원: 김도희, 박서영**

### 핵심 가치
- **AI 감성 분석**: 사용자의 현재 감정과 상황을 분석하여 맞춤형 도서 추천
- **실시간 커뮤니티**: 활발한 독서 토론과 의견 공유
- **도서 찜 및 리뷰**: 독자들의 평가와 반응 기반 인기 도서 선별

<br>

## ✨ 주요 기능

### 1. 사용자 인증 및 관리
- 회원가입/로그인 (JWT 기반 인증)
- 이메일 인증 (6자리 인증 코드)
- 비밀번호 찾기 (이메일 인증 또는 재설정 링크)
- 프로필 관리 및 수정

### 2. 도서 검색 및 조회
- 알라딘 API 연동 도서 검색
- 도서 상세 정보 조회
- 베스트셀러, 신간 도서 조회
- 찜 목록 관리

### 3. 리뷰 시스템
- 별점 및 텍스트 리뷰 작성
- 리뷰 수정/삭제
- 도서별 리뷰 목록 조회
- 마이페이지 리뷰 관리

### 4. 커뮤니티 게시판
- 게시글 작성/수정/삭제
- 도서 연동 게시글 작성
- 무한 스크롤 구현
- 실시간 새 게시글 알림
- 스크롤 위치 복원
- 게시글 좋아요

### 5. 댓글 시스템
- 댓글/대댓글 작성
- 재귀적 댓글 구조 (깊이 제한 없음)
- 댓글 수정/삭제
- 인라인 수정 기능

### 6. AI 도서 추천
- GPT-4.1-mini 기반 AI 챗봇
- 감정 및 상황 분석 기반 도서 추천
- 실시간 채팅 인터페이스
- 추천 도서 상세 정보 모달

<br>

## 🧩 System Architecture
- Frontend: Vue 3 + Vite
- Backend: Spring Boot 3 + MyBatis + MySQL
- External: Aladin API, GMS(GPT-4.1-mini), Gmail SMTP

<br>

## 🛠 기술 스택

### Frontend
- **프레임워크**: Vue.js 3 (Composition API)
- **빌드 도구**: Vite 7.2.4
- **스타일링**: Tailwind CSS 3.4.14
- **상태 관리**: Pinia 3.0.4
- **HTTP 클라이언트**: Axios 1.13.2
- **서버 상태 관리**: TanStack Query (Vue Query) 5.92.1
- **라우팅**: Vue Router 4.6.3
- **폼 검증**: Vuelidate 2.0.3
- **UI 컴포넌트**: PrimeVue 4.5.2, Heroicons, shadcn, Chart.js, Embla Carousel / Swiper

### Backend
- **프레임워크**: Spring Boot 3.4.0
- **언어**: Java 17
- **보안**: Spring Security 6.4.x + JWT (JJWT 0.13.0)
- **ORM**: MyBatis 3.0.3
- **데이터베이스**: MySQL
- **이메일**: Spring Mail (Gmail SMTP)
- **AOP**: Spring AOP
- **검증**: Bean Validation

### External Services
- **알라딘 API**: 도서 정보 조회 및 검색
- **GMS API (GPT-4.1-mini)**: AI 기반 도서 추천
- **Gmail SMTP**: 이메일 인증 코드 발송

### Infrastructure
- **Frontend 배포 (작업 중)**: local (ngrok)
- **Backend 배포 (작업 중)**: local (ngrok)
- **Database**: -
- **CDN & Security**: -

<br>

## 📁 프로젝트 구조

```
BookSpace/
├── frontend/                 # Vue.js 프론트엔드
│   ├── src/
│   │   ├── api/              # API 클라이언트
│   │   ├── components/       # Vue 컴포넌트
│   │   │   ├── ai-recommend/ # AI 추천 관련
│   │   │   ├── book/        # 도서 관련
│   │   │   ├── community/   # 커뮤니티 관련
│   │   │   ├── profile/     # 프로필 관련
│   │   │   ├── review/      # 리뷰 관련
│   │   │   └── ui/          # UI 컴포넌트
│   │   ├── composables/     # Composable 함수
│   │   ├── router/          # 라우터 설정
│   │   ├── stores/          # Pinia 스토어
│   │   ├── views/           # 페이지 컴포넌트
│   │   └── styles/          # 전역 스타일
│   ├── package.json
│   └── vite.config.js
│
├── backend/                  # Spring Boot 백엔드
│   ├── src/main/java/com/bookspace/
│   │   ├── domain/          # 도메인별 패키지
│   │   │   ├── user/        # 사용자 도메인
│   │   │   ├── book/        # 도서 도메인
│   │   │   ├── review/      # 리뷰 도메인
│   │   │   ├── post/        # 게시글 도메인
│   │   │   ├── email/        # 이메일 도메인
│   │   │   └── ...
│   │   ├── global/          # 전역 설정
│   │   │   ├── config/      # 설정 클래스
│   │   │   ├── exception/   # 예외 처리
│   │   │   └── security/    # 보안 설정
│   │   └── BookspaceApplication.java
│   ├── src/main/resources/
│   │   ├── application.yaml # 설정 파일
│   │   └── mappers/          # MyBatis 매퍼 XML
│   └── build.gradle
│
├── ERD         # 아키텍처 문서
├── PRESENTATION  발표 ppt 자료
└── README.md    # 프로젝트 README
```

<br>

## 🚀 Getting Started (Local)

### 1) Requirements

- Node.js LTS
- Java 17
- MySQL 8.x
- **Gradle (또는 Gradle Wrapper 사용)**

### 2) Environment Variables

```markdown
### 환경 변수 설정

백엔드 프로젝트 루트에 `.env` 파일을 생성하고 다음 환경 변수를 설정하세요:

```env
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=bookspace
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_ACCESS_TOKEN_VALIDITY_MS=3600000

# Aladin API
ALADIN_TTB_KEY=your_aladin_api_key

# Email (개발 모드)
MAIL_HOST=smtp.ethereal.email
MAIL_PORT=587
MAIL_USERNAME=your_email_username
MAIL_PASSWORD=your_email_password
EMAIL_DEV_MODE=true

# GMS API (AI 추천용)
GMS_PROVIDER_KEY=your_gms_provider_key
```

## 프로젝트를 마치며
### 박서영
처음 진행해 본 프로젝트라 모든 과정이 쉽지 않았고, 0에서부터 시작한다는 막막함이 있었습니다. 하지만 직접 구현한 기능이 실제로 동작하는 것을 보면서 뿌듯함을 느낄 수 있었고, 특히 AI 기능은 가장 고민이 많고 어려운 파트였지만 그만큼 의미 있는 작업이었다고 생각합니다. 또한 Git 브랜치 전략과 PR/이슈 작성으로 작업을 정리하며 협업 프로세스와 소통 방식에 대해서도 배울 수 있었습니다. 더불어 도서 상세 조회를 기반으로 리뷰 작성·수정·삭제, 평점/개수 실시간 반영, 비로그인 사용자 제한, 찜하기 연동 등 핵심 기능을 구현하면서 백엔드–프론트 연동 흐름을 이해하고 문제를 해결하는 경험을 쌓을 수 있었고, 이번 프로젝트를 통해 많이 배우고 성장할 수 있었습니다.

### 김도희
이번 프로젝트를 진행하면서 프론트엔드와 백엔드 기능을 구현하면서 화면에서의 사용자 액션이 어떤 API로 연결되고, 서버에서는 어떤 검증과 로직을 거쳐 DB에 반영되며, 다시 응답이 프론트에서 상태로 관리되고 UI로 표현되는지까지 하나의 사이클로 경험했다.
그 과정에서 어려움도 있었지만, 막히는 부분이 생길 때마다 함께 고민해주고, 코드 리뷰로 방향을 잡아준 사람들이 있어서 기간 내에 완성할 수 있었다. 특히 일정이 촉박하고 변수도 많았는데, 팀원들의 한 마디 조언이나 작은 도움 하나가 다음 단계로 넘어가는 결정적인 계기가 되는 순간이 많았다. 무엇보다 함께 도와주고 응원해준 팀원들에게 정말 감사하고, 이번 경험을 기반으로 다음 프로젝트에서는 더 안정적이고 완성도 높은 서비스를 만들어보고 싶다.
