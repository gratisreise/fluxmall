# Java/JSP 개발 실행 워크플로우 (Task-Oriented)

이미 정의된 기능 요구사항 및 명세서에 따라, 너는 할당된 태스크 단위로 개발을 수행한다.

## 1. 커밋 메시지 규칙 (Commit Convention)
모든 커밋은 아래 형식을 따른다.

```

<type>(<scope>): <subject> [#Task_ID]

[Optional Body: 변경 이유 및 주요 변경 내용]

[Optional Footer: Breaking Changes 알림]

```
- **Types:** `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
- **예시:** `feat(auth): 로그인 서블릿 및 DAO 구현 [#102]`

## 2. 개발 프로세스 (태스크 기반)

1.  **태스크 수락 및 컨텍스트 파악**
    - 할당된 태스크의 명세와 요구사항을 읽고, 관련된 기존 Java 클래스 및 JSP 파일을 식별한다.
    - 해당 태스크가 데이터베이스 스키마 변경을 포함하는지 확인한다.

2.  **구현 전략 수립**
    - 명세서에 정의된 데이터 흐름(DTO -> DAO -> Service -> Servlet -> JSP)에 따라 변경이 필요한 최소 단위부터 코딩을 시작한다.
    - 기존 공통 모듈(Util 클래스 등)을 재사용할 수 있는지 먼저 검토한다.

3.  **단계별 구현 (Java & JSP)**
    - **Back-end:** DAO/Service 로직을 먼저 작성하고 단위 테스트를 수행한다. (JUnit 활용 권장)
    - **Front-end:** JSP 레이어에서는 JSTL/EL을 사용하여 명세서의 UI 요구사항을 반영한다.
    - **Integration:** Servlet에서 비즈니스 로직과 뷰를 연결한다.

4.  **명세 준수 검증 (Self-Review)**
    - 작성이 완료된 코드가 초기 요구사항 정의서 및 기능 태스크의 합격 기준(Acceptance Criteria)을 모두 충족하는지 대조한다.
    - 불필요한 디버그용 `System.out.println`은 모두 제거한다.

5.  **커밋 및 보고**
    - 지정된 컨벤션에 맞춰 커밋을 수행하고, 작업 완료 후 변경된 주요 로직을 요약하여 보고한다.