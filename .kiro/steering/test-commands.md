# 테스트 명령어 가이드 (Kotlin)

## 🚨 필수 규칙: 모든 테스트 명령어에 --info 옵션 포함
Kotlin 코드에서도 동일하게 `--info` 옵션은 필수이다. CI/CD 및 로컬 실행 모두 준수해야 한다.

## 📋 표준 테스트 명령어
```bash
# Common 모듈 테스트
./gradlew :domains:common:test --info

# Shop Context 테스트
./gradlew :domains:shop:test --info

# Order Context 테스트
./gradlew :domains:order:test --info

# User Context 테스트
./gradlew :domains:user:test --info

# 전체 테스트
./gradlew test --info
```

## 📌 Kotlin 테스트 추가 지침
- Kotest를 사용할 경우: `./gradlew <module>:kotest --info` (커스텀 태스크 정의 시)
- MockK/Mockito-Kotlin 사용 시, `testImplementation`에 관련 의존성 포함 확인
- `--tests` 옵션으로 개별 클래스/메서드 지정 가능 (한글 테스트 이름도 문자열로 지정 가능)

## 💡 --info 사용 이유
- 실패 테스트의 StackTrace, Assertion 메시지를 즉시 확인
- Kotlin 코루틴/비동기 테스트 시 로그가 줄어들지 않도록 `--info` 필요
- CI 로그 분석 속도 향상

## ❌ 금지된 실행 예시
```bash
./gradlew test
./gradlew :domains:shop:test --tests MenuTest
```

## ✅ 권장 실행 예시
```bash
./gradlew :domains:shop:test --tests "MenuServiceTest" --info
./gradlew test --info --parallel
./gradlew :domains:order:test --info --stacktrace
```

## 🔧 IDE/툴 연동
- IntelliJ Gradle Run Configuration에 `--info` 추가
- VSCode/Task Runner/Makefile 등 자동화 스크립트에서도 동일하게 설정

## 📊 결과 해석
- 성공: `BUILD SUCCESSFUL`, `tests passed`
- 실패: 실패 테스트 이름, 메시지, Kotlin 파일 경로 (`MenuServiceTest.kt:42`) 확인 가능

항상 `compileKotlin` → `test --info` 순서로 실행하여 Kotlin 컴파일 오류를 먼저 확인하자.
