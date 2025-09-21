package harry.boilerplate.shop.command.domain.event

import harry.boilerplate.common.domain.event.DomainEvent
import java.time.Instant
import java.util.UUID

data class ShopClosedEvent(
    override val aggregateId: String,
    val shopName: String,
    val reason: String,
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    override val aggregateType: String = "Shop",
    val version: Int = 1
) : DomainEvent
