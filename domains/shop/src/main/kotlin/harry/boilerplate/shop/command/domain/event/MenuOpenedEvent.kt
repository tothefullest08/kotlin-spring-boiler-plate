package harry.boilerplate.shop.command.domain.event

import harry.boilerplate.common.domain.event.DomainEvent
import java.time.Instant
import java.util.UUID

data class MenuOpenedEvent(
    override val aggregateId: String,
    val shopId: String,
    val menuName: String,
    val description: String?,
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    override val aggregateType: String = "Menu",
    val version: Int = 1
) : DomainEvent
