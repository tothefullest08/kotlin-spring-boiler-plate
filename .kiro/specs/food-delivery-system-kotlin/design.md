# Kotlin Food Delivery & CQRS 설계

## 아키텍처 개요
- 멀티 모듈 (`domains/common`, `domains/shop`, `domains/order`, `domains/user`)
- Kotlin + Spring Boot 3.5, Gradle Kotlin DSL
- CQRS 패턴 유지: Command/Query 애플리케이션 계층 분리
- Common 모듈의 `AggregateRoot`, `DomainEvent`, `ErrorCode`를 Kotlin으로 재설계

## 구조 지침
```
context/
  command/
    domain/
      aggregate/
      entity/
      event/
      exception/
      valueObject/
    application/
      handler/
      service/
      dto/
    infrastructure/
      repository/
      external/
    presentation/
      controller/
      dto/
  query/
    application/
      handler/
      readModel/
      dto/
    infrastructure/
      dao/
      mapper/
    presentation/
      controller/
```

## Kotlin 설계 포인트
- Aggregate: `open class Menu(...) : AggregateRoot<Menu, MenuId>()`
- ValueObject/ID: `@JvmInline value class MenuId(val value: String)`
- Domain Event: `data class MenuOpenedEvent(...) : DomainEvent`
- ErrorCode: `sealed interface ErrorCode { val code: String; val message: String }`
- DTO/ReadModel: `data class`로 정의, 필요 시 확장 함수로 변환 로직 작성

## 데이터/도메인 모델
- Shop: 가게 정보, 영업 상태, 메뉴 목록
- Menu: 옵션 그룹, 가격 정책, 공개 상태
- Cart: 사용자/가게 식별자, 항목 목록, 합계 계산
- Order: 주문 상태 전이, 금액 검증, 이벤트 발행
- User: 사용자 식별자 관리, 주문 연계

## 빌드/테스트 전략
- Gradle Kotlin DSL에서 Kotlin/JPA 플러그인 구성
- `compileKotlin`, `test --info` 파이프라인 유지
- Kotest/MockK 등 Kotlin 친화 테스트 도구 사용 고려
- Docker Compose(MySQL)로 인프라 구성
