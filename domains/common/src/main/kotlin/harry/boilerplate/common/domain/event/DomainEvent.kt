package harry.boilerplate.common.domain.event

import java.time.Instant
import java.util.UUID

sealed interface DomainEvent {
    val eventId: UUID
    val occurredAt: Instant
    val aggregateId: String
    val aggregateType: String
}
