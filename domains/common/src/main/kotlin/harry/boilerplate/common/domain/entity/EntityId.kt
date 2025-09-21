package harry.boilerplate.common.domain.entity

import java.util.UUID

abstract class EntityId protected constructor(val value: String = UUID.randomUUID().toString()) : ValueObject() {

    init {
        require(value.isNotBlank()) { "ID value cannot be null or empty" }
    }

    override val equalityComponents: Array<out Any?> = arrayOf(value)

    override fun toString(): String = value
}
