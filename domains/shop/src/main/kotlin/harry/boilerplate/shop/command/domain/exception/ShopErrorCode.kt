package harry.boilerplate.shop.command.domain.exception

import harry.boilerplate.common.exception.ErrorCode

enum class ShopErrorCode(override val code: String, override val message: String) : ErrorCode {
    SHOP_NAME_REQUIRED("SHOP-DOMAIN-001", "가게 이름은 필수입니다"),
    MIN_ORDER_AMOUNT_REQUIRED("SHOP-DOMAIN-002", "최소 주문 금액은 필수입니다"),
    INVALID_MIN_ORDER_AMOUNT("SHOP-DOMAIN-003", "최소 주문 금액은 0원 이상이어야 합니다"),
    BUSINESS_HOURS_REQUIRED("SHOP-DOMAIN-004", "영업시간 정보는 필수입니다"),
    SHOP_NOT_FOUND("SHOP-DOMAIN-005", "가게를 찾을 수 없습니다"),
    SHOP_CLOSED("SHOP-DOMAIN-006", "현재 영업시간이 아닙니다"),
    INVALID_OPERATING_HOURS("SHOP-DOMAIN-007", "올바르지 않은 영업시간입니다"),
    CLOSE_REASON_REQUIRED("SHOP-DOMAIN-008", "가게 폐점 사유는 필수입니다");
}
