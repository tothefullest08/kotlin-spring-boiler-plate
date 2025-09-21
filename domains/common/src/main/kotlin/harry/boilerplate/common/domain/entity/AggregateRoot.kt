package harry.boilerplate.common.domain.entity

import harry.boilerplate.common.domain.event.DomainEvent

abstract class AggregateRoot<T : AggregateRoot<T, ID>, ID> : BaseEntity() {
    private val domainEvents: MutableList<DomainEvent> = mutableListOf()

    abstract val id: ID

    protected fun registerEvent(event: DomainEvent) {
        domainEvents += event
    }

    fun pullDomainEvents(): List<DomainEvent> = domainEvents.toList().also { domainEvents.clear() }
}
