# 제품 개요 (Kotlin 전환)

Kotlin 기반 멀티 모듈 Spring Boot 애플리케이션으로 음식 주문 시스템을 구현한다. 기존 Java 프로젝트가 제공하던 CQRS/DDD 아키텍처를 그대로 계승하면서, Kotlin 특성을 활용하여 코드 간결성과 안전성을 강화한다.

## 멀티 프로젝트 구조

### Shop Context (가게)
- **경로**: `domains/shop`
- **패키지**: `harry.boilerplate.shop`
- **포트**: 8084
- **책임**: 가게 운영, 메뉴/옵션 관리, 메뉴 공개 이벤트 발행
- **주요 애그리게이트**: `Shop`, `Menu`

### Order Context (주문)
- **경로**: `domains/order`
- **패키지**: `harry.boilerplate.order`
- **포트**: 8085
- **책임**: 장바구니, 주문 생성, 주문 내역 관리
- **주요 애그리게이트**: `Cart`, `Order`

### User Context (사용자)
- **경로**: `domains/user`
- **패키지**: `harry.boilerplate.user`
- **포트**: 8086
- **책임**: 사용자 식별, 주문/장바구니 연계
- **주요 애그리게이트**: `User`

## 핵심 비즈니스 규칙

- **Shop**: 영업 시간 기반의 개점/휴무 판단, 메뉴 관리, 가게 상태 이벤트
- **Menu**: 옵션 그룹 조건을 만족해야 공개 가능, 가격 및 옵션 정책 유지
- **Order**: 단일 가게 장바구니, 주문 생성 시 금액 검증, 상태 전이 이벤트
- **User**: 사용자 인증/식별, 주문과 사용자 매핑

CQRS 패턴을 유지하며 Command/Query 사이드를 명확히 분리한다. 이벤트 기반 통신 규칙과 REST API 경계를 준수하여 컨텍스트 간 독립성을 보장한다.

## Kotlin 전환 지향점

- Java 코드를 단순 변환하지 않고 Kotlin idiom 적용 (data class, sealed class, value class)
- 불변성 유지, null-safety 보장, 함수형 컬렉션 연산 활용
- 코루틴 사용 시 문서화 및 이벤트 처리 흐름과의 일관성 검토
- 테스트 스택은 Kotest, Mockito-Kotlin, MockK 등 Kotlin 친화 도구 활용을 권장

## 컨텍스트 간 상호작용

- 의존성 역참조 금지: Common 모듈만 공유
- API 기반 통신: REST + DTO를 통해 데이터 교환
- 이벤트 기반 통신: `DomainEvent` 구현체를 통해 비동기 전파, 퍼블리셔/컨슈머 분리

본 문서는 Kotlin 전환 이후에도 유지보수되는 상위 개요 문서로, 변경 사항 발생 시 즉시 업데이트해야 한다.
