package harry.boilerplate.shop.command.domain.valueObject

import harry.boilerplate.common.domain.entity.EntityId
import java.util.UUID

class MenuId private constructor(value: String) : EntityId(value) {
    companion object {
        fun generate(): MenuId = MenuId(UUID.randomUUID().toString())
        fun of(value: String): MenuId = MenuId(value)
    }
}
