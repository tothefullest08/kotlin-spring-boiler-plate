# Kotlin Food Delivery & CQRS 작업 계획

## Step 1: 환경 준비
- [x] 루트/모듈 `build.gradle.kts` 구성 (Kotlin/JPA 플러그인, 의존성)
- [x] `settings.gradle.kts`와 Gradle Wrapper 세팅
- [x] 공통 문서/스크립트 복사 및 Kotlin화된 규칙 반영

## Step 2: Common 모듈 전환
- [x] `AggregateRoot`, `BaseEntity`, `DomainEvent`, `ErrorCode` Kotlin 작성
- [x] 공통 Exception/Response/Config Kotlin화
- [x] Kotlin 테스트 유틸리티 구축

## Step 3: Shop 컨텍스트 전환
- [x] Command 애그리게이트/도메인/이벤트/예외 Kotlin 변환 및 JPA 매핑 검증
- [x] Command 애플리케이션/프레젠테이션 계층(Kotlin DTO, Handler, Controller) 작성
- [x] Query 계층(ReadModel, DAO, Controller) Kotlin화 및 통합 테스트 갱신
- [ ] Shop 관련 테스트(`:domains:shop:test --info`) 실행 및 실패 케이스 보완

## Step 4: Order 컨텍스트 전환
- [ ] Cart/Order 애그리게이트 및 ValueObject Kotlin 변환, 금액 계산 로직 검증
- [ ] Command/Query 핸들러 및 DTO Kotlin화, 이벤트 발행/구독 로직 점검
- [ ] 인프라스트럭처 Repository/DAO Kotlin 전환 및 Mock/통합 테스트 정비
- [ ] `:domains:order:test --info` 실행으로 회귀 검증

## Step 5: User 컨텍스트 전환
- [ ] User 애그리게이트/ValueObject Kotlin화 및 인증/식별 로직 보강
- [ ] Command/Query 서비스, Controller, DTO Kotlin 변환 및 validation 강화
- [ ] User 관련 테스트(`:domains:user:test --info`) 작성/보강

## Step 6: 구조 검토 및 정리
- [ ] 패키지/디렉터리 구조 Steering 문서와 일치 여부 확인
- [ ] 불필요한 Java 파일 제거 및 Kotlin 파일 정리
- [ ] 문서/README 업데이트 및 `.kiro` 문서 최신화

## Step 7: 최종 검증
- [ ] `./gradlew compileKotlin`
- [ ] `./gradlew test --info`
- [ ] 각 컨텍스트 bootRun/스모크 테스트 필요 시 실행
