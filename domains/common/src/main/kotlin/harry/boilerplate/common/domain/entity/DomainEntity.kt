package harry.boilerplate.common.domain.entity

abstract class DomainEntity<T : DomainEntity<T, ID>, ID> : BaseEntity() {
    abstract val id: ID
}
