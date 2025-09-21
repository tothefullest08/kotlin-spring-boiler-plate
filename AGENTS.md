---
description: Kotlin Spring Boot 멀티 프로젝트 CQRS/DDD 아키텍처 개발 가이드
globs:
alwaysApply: true
---
# Kotlin Spring Boot 멀티 프로젝트 CQRS/DDD 아키텍처 개발 가이드

## 프로젝트 참고 문서
Kotlin 기반으로 코드를 작성할 때는 반드시 다음 문서들을 우선 확인하세요:

### 📋 Steering 문서 (프로젝트 지침)
- **제품 개요**: `.kiro/steering/product.md` - 바운디드 컨텍스트 구성, 비즈니스 책임 정의
- **기술 스택**: `.kiro/steering/tech.md` - Kotlin/JVM 사양, Gradle 설정, CQRS & 이벤트 패턴 구현 규칙
- **프로젝트 구조**: `.kiro/steering/structure.md` - 멀티 모듈 구조, 패키징 규칙, Kotlin 전용 코드 스타일 가이드
- **테스트 가이드**: `.kiro/steering/test-commands.md` - `--info` 옵션 필수 테스트 명령어와 실행 규칙

### 📋 Specs 문서 (구현 명세)
- `.kiro/specs/food-delivery-system-kotlin/` - 음식 주문/CQRS 통합 요구사항 및 Kotlin 전환 가이드
- 
Specs는 요구사항(EARS), 설계, 작업 계획의 세 문서(`requirements.md`, `design.md`, `tasks.md`)로 구성되며, Kotlin으로 구현 시 변경되는 책임과 데이터 모델을 명확하게 기록해야 합니다.

## 🚨 절대 금지 사항

### 1. 바운디드 컨텍스트 의존성 역참조
```kotlin
// ❌ 금지: 다른 컨텍스트 직접 의존
implementation(project(":domains:shop"))

// ✅ 허용: Common 모듈만 의존
implementation(project(":domains:common"))
```

### 2. 테스트 명령어에서 --info 생략
```bash
# ❌ 금지
./gradlew test

# ✅ 필수
./gradlew test --info
```

### 3. Kotlin 특성 무시한 Java식 코드 복붙
- `null` 안전성 무시, `lateinit` 남용, 데이터 클래스 미사용 금지
- Kotlin 컬렉션/확장 함수 대신 Java API 강제 사용 금지

## ✅ 필수 준수 사항

### 1. Kotlin 스타일 가이드
- `src/main/kotlin` / `src/test/kotlin` 디렉터리 사용
- `data class`, `sealed class`, `value class` 등 Kotlin 문법 적극 활용
- `suspend`/Coroutine 사용 시 명시적으로 문서화하고 컨텍스트 경계를 넘지 않도록 주의

### 2. CQRS 패키지 구조 유지
- `command`/`query` 디렉터리 구분
- 도메인 레이어는 `AggregateRoot`, `DomainEntity`, `ValueObject` Kotlin 버전 상속

### 3. 트랜잭션 어노테이션
- Command: `@Transactional`
- Query: `@Transactional(readOnly = true)`

### 4. 도메인 이벤트 구현
- Common 모듈에는 인터페이스/추상 기반만 정의
- 컨텍스트별 이벤트는 `command/domain/event`에 Kotlin 데이터 클래스로 구현하며, `DomainEvent` 계약 준수

## 🔧 개발 워크플로우
1. Steering 문서와 Specs 문서를 먼저 읽고 Kotlin 적용 범위와 변경 여지를 파악
2. Kotlin DSL `build.gradle.kts`에서 의존성/플러그인 정의 (Lombok 금지)
3. 코드 작성 후 `./gradlew compileKotlin`, `./gradlew test --info` 순으로 검증
4. DDD/CQRS 규칙 준수 여부와 Kotlin idiom 적용 여부(불변성, null 안전성)를 함께 점검

## 💡 Kotlin 전환 체크
- [ ] Java 클래스를 그대로 옮기지 않고 Kotlin idiom으로 재작성했는가?
- [ ] `nullable`/`non-null` 타입, `sealed`/`inline` 등 언어 특성을 적절히 활용했는가?
- [ ] JPA 엔티티는 `open class` + `no-arg` 전략으로 동작하는가?
- [ ] Exception/ErrorCode 구조가 명확하게 유지되었는가?
- [ ] 테스트는 Kotest/Mockito-Kotlin 등 Kotlin 친화 라이브러리 사용 고려했는가?

필요한 사항이 있으면 `.kiro` 문서를 참고하거나 업데이트하여 팀 내 공유를 유지하세요.
