package harry.boilerplate.common.domain.entity

abstract class ValueObject {
    protected abstract val equalityComponents: Array<out Any?>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ValueObject
        return equalityComponents.contentEquals(other.equalityComponents)
    }

    override fun hashCode(): Int = equalityComponents.contentHashCode()
}
