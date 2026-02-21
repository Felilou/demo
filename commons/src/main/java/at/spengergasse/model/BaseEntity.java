package at.spengergasse.model;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.UUID;

public abstract class BaseEntity extends AbstractPersistable<Long> {

    UUID uuid;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID();
    }

    @PreRemove
    public void preRemove() {
        this.uuid = null;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
