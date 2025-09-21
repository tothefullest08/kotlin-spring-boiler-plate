# 기술 스택 및 Kotlin CQRS 개발 규칙

## 필수 기술 스택
- **Kotlin 2.x (JVM 21)** - 언어 및 코루틴 지원
- **Gradle Kotlin DSL** - 멀티 프로젝트 빌드 도구
- **Spring Boot 3.5.x** - 컨텍스트별 독립 애플리케이션
- **Spring Data JPA / Hibernate** - 영속성 계층
- **MySQL 8.0** - 기본 데이터베이스 (Docker Compose)
- **Test Stack**: JUnit5, Kotest(선택), Mockito-Kotlin 또는 MockK

## 멀티 프로젝트 빌드 설정 예시

### settings.gradle.kts
```kotlin
rootProject.name = "kotlin-spring-boiler-plate"

include("domains:common")
include("domains:shop")
include("domains:order")
include("domains:user")
```

### 루트 build.gradle.kts
```kotlin
plugins {
    id("org.jetbrains.kotlin.jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlin.plugin.spring") version "<kotlin-version>"
    id("org.jetbrains.kotlin.plugin.jpa") version "<kotlin-version>"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    // 루트에서는 특별한 의존성 없이 BOM/관리만 수행
}
```

### Common 모듈 build.gradle.kts
```kotlin
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.jetbrains.kotlin.plugin.jpa")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.security:spring-security-crypto")
    api("io.swagger.core.v3:swagger-annotations:2.2.0")

    implementation(kotlin("stdlib"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.12")
}

kotlin {
    jvmToolchain(21)
}
```

### Kotlin JPA 설정 팁
- `org.jetbrains.kotlin.plugin.jpa`와 `org.jetbrains.kotlin.plugin.noarg` 사용
- `allOpen` 설정으로 `@Entity`, `@MappedSuperclass`, `@Embeddable` 등에 대해 클래스를 열어 Proxy 지원
- JPA 엔티티 생성자를 위한 `no-arg` 플러그인 사용(`kotlin("plugin.serialization")`은 필요 시 별도 적용)

### allOpen / noArg 설정 예시
```kotlin
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
}
```

## Kotlin 도메인 코드 예시
### BaseEntity / AggregateRoot / DomainEntity / ValueObject
```kotlin
@MappedSuperclass
abstract class BaseEntity : Serializable {
    @Column(name = "created_at", updatable = false, nullable = false)
    lateinit var createdAt: Instant
        private set

    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: Instant
        private set

    @PrePersist
    fun onCreate() {
        val now = Instant.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}

abstract class AggregateRoot<T : AggregateRoot<T, ID>, ID>(
    private val domainEvents: MutableList<DomainEvent> = mutableListOf()
) : BaseEntity() {
    abstract val id: ID

    protected fun registerEvent(event: DomainEvent) {
        domainEvents += event
    }

    fun pullEvents(): List<DomainEvent> = domainEvents.toList().also(domainEvents::clear)
}

abstract class DomainEntity<T : DomainEntity<T, ID>, ID> : BaseEntity() {
    abstract val id: ID
}

abstract class ValueObject {
    protected abstract val equalityComponents: Array<out Any?>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ValueObject
        return equalityComponents.contentEquals(other.equalityComponents)
    }

    override fun hashCode(): Int = equalityComponents.contentHashCode()
}
```

### DomainEvent와 ErrorCode
```kotlin
sealed interface DomainEvent {
    val eventId: UUID
    val occurredAt: Instant
    val aggregateId: String
    val aggregateType: String
}

interface ErrorCode {
    val code: String
    val message: String
}

enum class MenuErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    MENU_NOT_FOUND("MENU-DOMAIN-001", "메뉴를 찾을 수 없습니다"),
    INVALID_OPTION_GROUP("MENU-DOMAIN-002", "옵션 그룹 구성이 올바르지 않습니다")
}

class MenuDomainException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
```

### Value Object 구현 예시
```kotlin
class MenuId private constructor(val value: String) : ValueObject() {
    override val equalityComponents = arrayOf(value)

    companion object {
        fun newId(): MenuId = MenuId(UUID.randomUUID().toString())
        fun of(value: String): MenuId = MenuId(value)
    }
}

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

### Aggregate & DomainEntity 구현 예시
```kotlin
@Entity
@Table(name = "menu")
class Menu(
    @Column(name = "shop_id", nullable = false)
    private val shopId: String,
    name: String,
    description: String?,
    price: Money
) : AggregateRoot<Menu, MenuId>() {

    @Id
    @Column(name = "id")
    override val id: MenuId = MenuId.newId()

    @Column(name = "name", nullable = false)
    var name: String = name
        private set

    @Column(name = "description")
    var description: String? = description
        private set

    @Embedded
    var basePrice: Money = price
        private set

    @Column(name = "is_open", nullable = false)
    var opened: Boolean = false
        private set

    fun open() {
        require(!opened) { "이미 공개된 메뉴입니다" }
        opened = true
        registerEvent(MenuOpenedEvent(id.value, shopId, name))
    }
}

@Entity
@Table(name = "menu_option_group")
class OptionGroup(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    val menu: Menu,
    displayName: String
) : DomainEntity<OptionGroup, OptionGroupId>() {

    @Id
    @Column(name = "id")
    override val id: OptionGroupId = OptionGroupId.newId()

    @Column(name = "display_name", nullable = false)
    var name: String = displayName
        private set
}

class OptionGroupId private constructor(val value: String) : ValueObject() {
    override val equalityComponents = arrayOf(value)

    companion object {
        fun newId(): OptionGroupId = OptionGroupId(UUID.randomUUID().toString())
    }
}
```

### Domain Event 구현 예시
```kotlin
data class MenuOpenedEvent(
    override val aggregateId: String,
    val shopId: String,
    val menuName: String,
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    override val aggregateType: String = "Menu"
) : DomainEvent
```
## CQRS Handler/DTO 샘플
```kotlin
@Service
@Transactional
class OpenMenuCommandHandler(private val menuRepository: MenuRepository) {
    fun handle(command: OpenMenuCommand): CommandResult {
        val menu = menuRepository.findById(command.menuId)
            ?: throw MenuDomainException(MenuErrorCode.MENU_NOT_FOUND)
        menu.open()
        return CommandResult.success(menu.id.value)
    }
}

data class OpenMenuCommand(val menuId: String)

@Service
@Transactional(readOnly = true)
class MenuBoardQueryHandler(private val menuDao: MenuDao) {
    fun handle(query: MenuBoardQuery): MenuBoardReadModel =
        menuDao.findMenuBoard(query.shopId)
}

data class MenuBoardQuery(val shopId: String)

data class MenuBoardReadModel(val menus: List<MenuSummary>)

data class MenuSummary(val id: String, val name: String, val price: BigDecimal)
```

## Kotlin 테스트 예시
```kotlin
@ExtendWith(MockKExtension::class)
class OpenMenuCommandHandlerTest {
    @MockK lateinit var menuRepository: MenuRepository
    @InjectMockKs lateinit var handler: OpenMenuCommandHandler

    @Test
    fun `메뉴 공개 성공`() {
        val menu = mockk<Menu>(relaxed = true) {
            every { id } returns MenuId("menu-1")
        }
        every { menuRepository.findById(any()) } returns menu

        val result = handler.handle(OpenMenuCommand("menu-1"))

        verify { menu.open() }
        assertThat(result.success).isTrue()
    }
}
```

## CQRS 아키텍처 레이어

- **Presentation**: REST Controller (Command/Query 분리), Kotlin `data class` DTO 반환
- **Application**: Handler/Service 계층, Command는 상태 변경 중심, Query는 읽기 전용
- **Domain**: Aggregate/Entity/ValueObject, Domain Event 저장, Kotlin 불변성 활용
- **Infrastructure**: Repository/DAO, 외부 API 연동, 구현체는 Command/Query 별도로 작성

### Repository 구현 가이드
- Command 쪽 인프라스트럭처 리포지토리는 도메인 포트(`ShopRepository`, `MenuRepository`)를 구현하고, 내부에서는 `Spring Data JpaRepository`에 위임해 영속성을 처리합니다.
- `JpaRepository` 인터페이스는 인프라 패키지에 정의하고, 어댑터 구현체가 주입받아 저장/조회/삭제를 호출합니다. 이때 도메인 ID 값 객체(`ShopId`, `MenuId`)와 문자열 키 매핑을 책임집니다.
- 단순 CRUD는 기본 메서드를 사용하고, 주문/가게 단위 조회처럼 파생 쿼리가 필요한 경우 `findAllByShopIdValueOrderByCreatedAtAsc`와 같이 프로퍼티 기반 네이밍 규칙을 따릅니다.
- 트랜잭션 경계(`@Transactional`) 안에서 JPA가 자동으로 flush/commit을 수행하므로, 즉시 쿼리 동기화가 필요한 특수 상황이 아니라면 수동 `flush()` 호출을 피합니다.
- Query 쪽 DAO는 복잡한 조인/뷰 모델 구성이 많으므로 `EntityManager` 기반 구현을 유지해도 됩니다. 단, Command 계층과 의존성이 교차되지 않도록 주의합니다.
- 연관 컬렉션은 애그리거트가 생성된 직후에도 바로 사용할 수 있도록 `mutableListOf()`와 같은 빈 컬렉션으로 선할당합니다. Hibernate는 영속화 시점에 내부적으로 `PersistentBag` 등으로 래핑해 주기 때문에, 선할당을 해도 변경 감지 기능은 그대로 유지됩니다.

## 네이밍 및 Kotlin 관례
- Command Handler: `OpenMenuCommandHandler`
- Query Handler: `MenuBoardQueryHandler`
- DTO/ReadModel: `MenuReadModel`, `CreateMenuRequest`
- Domain Event: `MenuOpenedEvent : DomainEvent`
- ErrorCode: `enum class MenuErrorCode(override val code: String, override val message: String) : ErrorCode`

## 주요 명령어
```bash
./gradlew compileKotlin
./gradlew :domains:shop:bootRun
./gradlew :domains:order:test --info
./gradlew ktlintCheck # 사용 시
```

## Docker & DB
- `docker-compose.yml`을 활용해 MySQL 컨테이너 실행
- Kotlin 전환 후에도 초기 스키마/시드 스크립트 유지
- 로컬 개발과 테스트 데이터베이스 분리를 위해 `.env` 혹은 application profile 사용 가능

## Kotlin 특화 베스트 프랙티스
- Null-safety: 도메인 로직에서 nullable을 최소화하고, validation을 명시적으로 처리
- 불변성: `val`/`immutable list` 사용, 필요 시 `copy`로 새로운 상태 생성
- 확장 함수: 도메인 변환 로직을 모듈화하되, 확장 대상이 다른 컨텍스트를 침범하지 않도록 주의
- 코루틴: 비동기 처리 도입 시 Command/Query 경계와 트랜잭션 범위에 영향 없는지 검토

문서 내용은 Kotlin 전환 상태를 기준으로 유지되며, 새로운 기술 요소 도입 시 업데이트한다.
