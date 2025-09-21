# Kotlin Food Delivery & CQRS 요구사항

## Context
Kotlin 전환 이후 음식 주문 시스템과 CQRS 패키지 구조 리팩토링을 함께 관리한다. 기존 Java 구현이 만족하던 도메인 규칙을 유지하면서 Kotlin 언어 특성에 맞춘 개선을 목표로 한다.

## EARS 형식 요구사항

### 멀티 컨텍스트 책임
- **WHEN** Shop/Order/User 컨텍스트가 각자 기능을 수행할 때
- **THE SYSTEM SHALL** 서로 독립된 모듈과 데이터베이스를 유지하며, Common 모듈만 공유해야 한다.

### 메뉴 공개 및 비즈니스 규칙
- **WHEN** 메뉴가 옵션 그룹 요건(필수 그룹 1~3개, 유료 옵션 그룹 ≥1)을 만족하면
- **THE SYSTEM SHALL** `Menu.open()` 호출 시 상태를 공개로 전환하고 `MenuOpenedEvent`를 발행해야 한다.

### 주문 생성 흐름
- **WHEN** 사용자가 장바구니를 주문으로 변환할 때
- **THE SYSTEM SHALL** 장바구니 항목의 Shop ID 일치 여부를 검증하고 Order 애그리게이트를 생성해야 한다.

### Kotlin 패키지 구조
- **WHEN** 새로운 Kotlin 클래스를 추가하거나 변환할 때
- **THE SYSTEM SHALL** `harry.boilerplate.<context>.command|query.<layer>` 패키지 규칙과 `src/main/kotlin` 디렉터리 구조를 준수해야 한다.

### 오류 및 이벤트 처리
- **WHEN** 도메인 규칙 위반 또는 상태 변화가 발생하면
- **THE SYSTEM SHALL** `DomainException`과 `ErrorCode`(sealed interface + enum)로 오류를 표현하고, 필요한 경우 도메인 이벤트를 발행해야 한다.

## 수용 기준
1. Kotlin data class/value class 활용으로 DTO와 식별자 타입을 정의했다.
2. Command/Query 핸들러는 transactional 속성과 CQRS 책임을 유지한다.
3. 패키지 구조가 Steering 문서 규칙과 일치하며 컨텍스트 간 직접 의존이 없다.
4. 테스트는 `--info` 옵션으로 실행되며 Kotlin 테스트 프레임워크와 호환된다.
