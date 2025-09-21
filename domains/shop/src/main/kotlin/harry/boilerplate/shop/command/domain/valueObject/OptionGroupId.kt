package harry.boilerplate.shop.command.domain.valueObject

import harry.boilerplate.common.domain.entity.EntityId
import java.util.UUID

class OptionGroupId private constructor(value: String) : EntityId(value) {
    companion object {
        fun generate(): OptionGroupId = OptionGroupId(UUID.randomUUID().toString())
        fun of(value: String): OptionGroupId = OptionGroupId(value)
    }
}
