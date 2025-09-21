package harry.boilerplate.shop.command.domain.valueObject

import harry.boilerplate.common.domain.entity.EntityId
import java.util.UUID

class ShopId private constructor(value: String) : EntityId(value) {
    companion object {
        fun generate(): ShopId = ShopId(UUID.randomUUID().toString())
        fun of(value: String): ShopId = ShopId(value)
    }
}
