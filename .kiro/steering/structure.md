# 멀티 프로젝트 구조 및 CQRS 패턴 (Kotlin 버전)

## 프로젝트 디렉터리 개요
```
kotlin-spring-boiler-plate/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
├── gradlew, gradlew.bat
├── docker/
├── docker-compose.yml
├── .kiro/
└── domains/
    ├── common/
    │   └── src/
    │       ├── main/kotlin/harry/boilerplate/common/
    │       │   ├── config/
    │       │   ├── domain/
    │       │   │   ├── entity/
    │       │   │   ├── event/
    │       │   │   └── valueObject/
    │       │   ├── exception/
    │       │   └── response/
    │       └── test/kotlin/...
    ├── shop/
    │   └── src/
    │       ├── main/kotlin/harry/boilerplate/shop/
    │       │   ├── command/
    │       │   │   ├── domain/
    │       │   │   │   ├── aggregate/
    │       │   │   │   ├── entity/
    │       │   │   │   ├── event/
    │       │   │   │   ├── exception/
    │       │   │   │   └── valueObject/
    │       │   │   ├── application/
    │       │   │   │   ├── handler/
    │       │   │   │   ├── service/
    │       │   │   │   └── dto/
    │       │   │   ├── infrastructure/
    │       │   │   │   ├── repository/
    │       │   │   │   └── external/
    │       │   │   └── presentation/
    │       │   │       ├── controller/
    │       │   │       └── dto/
    │       │   └── query/
    │       │       ├── application/
    │       │       │   ├── handler/
    │       │       │   ├── readModel/
    │       │       │   └── dto/
    │       │       ├── infrastructure/
    │       │       │   ├── dao/
    │       │       │   └── mapper/
    │       │       └── presentation/
    │       │           └── controller/
    ├── order/ (동일 구조)
    └── user/ (동일 구조)
```

## Kotlin 프로젝트 규칙

1. **디렉터리 명명**: 모든 소스는 `src/main/kotlin`, 테스트는 `src/test/kotlin` 아래에 위치
2. **패키지 네이밍**: `harry.boilerplate.<context>` 유지, 하위 패키지는 도메인 역할 기반으로 구분. `harry.boilerplate`는 최상위 네임스페이스이며, 루트 모듈의 `src/main/kotlin` 하위 디렉터리 구조(`src/main/kotlin/harry/boilerplate/...`)를 그대로 패키지로 반영합니다. 컨텍스트(`shop`, `common`, 등)와 CQRS 레이어(`command`, `query`)는 이 하위 패키지에서 분기합니다.
3. **Command/Query 분리**: Kotlin 파일도 Java와 동일한 서브 디렉터리 구조를 유지하여 가독성 확보
4. **DDD 계층**: `AggregateRoot`, `DomainEntity`, `ValueObject`는 Common 모듈의 Kotlin 추상 클래스를 상속
5. **Configuration/Response/Exception**: 공통 모듈에 위치시키고 각 컨텍스트에서 재사용

## Kotlin 적용 가이드

- **데이터 클래스**: DTO/ReadModel/ValueObject는 `data class`로 정의해 equals/hashCode 자동 구현
- **Sealed 클래스**: ErrorCode, DomainEvent 타입 계층을 명확히 표현할 때 `sealed interface`/`sealed class` 활용
- **Inline value class**: ID/Identifier 타입은 `@JvmInline value class`로 감싸서 타입 안정성 확보
- **확장 함수**: 공통 변환/매핑 로직은 extension function으로 모듈화하되, 바운디드 컨텍스트 경계를 넘지 않도록 위치 주의

### 샘플 파일 배치
```
shop/command/domain/aggregate/Menu.kt
shop/command/application/handler/OpenMenuCommandHandler.kt
shop/query/application/handler/MenuBoardQueryHandler.kt
shop/query/infrastructure/dao/MenuDao.kt
shop/query/presentation/controller/MenuQueryController.kt
```

### Command Aggregate 예시 (`shop/command/domain/aggregate/Menu.kt`)
```kotlin
open class Menu(
    private val shopId: ShopId,
    name: MenuName,
    description: MenuDescription?,
    price: Money
) : AggregateRoot<Menu, MenuId>() {

    @Id
    @Column(name = "id")
    override val id: MenuId = MenuId.newId()

    @Embedded
    var basePrice: Money = price
        private set

    var isOpened: Boolean = false
        private set

    fun open() {
        require(!isOpened) { "이미 공개된 메뉴입니다" }
        isOpened = true
        registerEvent(MenuOpenedEvent(id.value, shopId.value, name.value))
    }
}
```

### Domain Entity 예시 (`shop/command/domain/entity/OptionGroup.kt`)
```kotlin
@Entity
@Table(name = "menu_option_group")
class OptionGroup(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    val menu: Menu,
    displayName: OptionGroupName
) : DomainEntity<OptionGroup, OptionGroupId>() {

    @Id
    @Column(name = "id")
    override val id: OptionGroupId = OptionGroupId.newId()

    @Embedded
    var name: OptionGroupName = displayName
        private set
}
```

### Value Object 예시 (`shop/command/domain/valueObject/Money.kt`)
```kotlin
@Embeddable
class Money private constructor(
    @Column(name = "amount", precision = 10, scale = 2)
    val amount: BigDecimal
) : ValueObject(), Serializable {
    override val equalityComponents = arrayOf(amount)

    companion object {
        fun of(amount: BigDecimal): Money {
            require(amount >= BigDecimal.ZERO) { "금액은 0 이상이어야 합니다" }
            return Money(amount)
        }
    }
}
```

### Query ReadModel 예시 (`shop/query/application/readModel/MenuBoardReadModel.kt`)
```kotlin
data class MenuBoardReadModel(
    val shopId: String,
    val menus: List<MenuSummary>
)

data class MenuSummary(
    val id: String,
    val name: String,
    val price: BigDecimal
)
```

### Controller 계층 예시
```kotlin
@RestController
@RequestMapping("/shops/{shopId}/menus")
class MenuQueryController(private val queryHandler: MenuBoardQueryHandler) {

    @GetMapping
    fun menuBoard(@PathVariable shopId: String): ResponseEntity<MenuBoardReadModel> {
        val result = queryHandler.handle(MenuBoardQuery(shopId))
        return ResponseEntity.ok(result)
    }
}
```

## CQRS 규칙 요약

- Command는 상태 변경 책임, Query는 조회 전용 책임 → Kotlin에서 각각 별도 핸들러/서비스 클래스로 표현
- Command Handler는 `@Transactional`, Query Handler는 `@Transactional(readOnly = true)` 유지
- Domain Event 발행은 Aggregate에 확장 함수나 `addDomainEvent()` 호출로 구현하며, Kotlin 컬렉션 복사 대신 불변 리스트 사용을 고려

## 테스트 구조

- 테스트 패키지는 프로덕션 패키지와 동일한 경로를 따라가며 `src/test/kotlin`에 위치
- Kotest/MockK 사용 시 Common 모듈에 테스트 지원 코드를 두고 공유
- 통합 테스트가 필요한 경우, 스프링 부트 테스트를 Command/Query 별로 분리하여 작성

본 문서는 Kotlin 프로젝트 구조를 표준화하기 위한 규칙이며, 변경 시 팀 합의를 거쳐 업데이트한다.
