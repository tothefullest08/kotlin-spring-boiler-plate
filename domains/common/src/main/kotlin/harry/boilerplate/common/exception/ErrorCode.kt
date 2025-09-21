package harry.boilerplate.common.exception

/**
 * 에러 코드 인터페이스
 * 형식: {DOMAIN}-{LAYER}-{CODE}
 * 예시: SHOP-DOMAIN-001, ORDER-APP-002
 */
interface ErrorCode {
    val code: String
    val message: String
}
