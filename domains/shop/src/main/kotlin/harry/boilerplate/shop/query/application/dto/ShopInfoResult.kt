package harry.boilerplate.shop.query.application.dto

import harry.boilerplate.shop.query.application.readModel.ShopDetailReadModel

data class ShopInfoResult(val shop: ShopDetailReadModel) {
    companion object {
        fun from(shop: ShopDetailReadModel): ShopInfoResult = ShopInfoResult(shop)
    }
}
