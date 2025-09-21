package harry.boilerplate.common.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.io.Serializable
import java.time.Instant

@MappedSuperclass
abstract class BaseEntity : Serializable {
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: Instant
        protected set

    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: Instant
        protected set

    @PrePersist
    protected fun onCreate() {
        val now = Instant.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    protected fun onUpdate() {
        updatedAt = Instant.now()
    }
}
