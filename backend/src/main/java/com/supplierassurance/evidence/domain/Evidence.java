
package com.supplierassurance.evidence.domain;

import com.supplierassurance.verification.domain.VerificationCheck;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "evidence")
public class Evidence {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "verification_check_id", nullable = false)
    private VerificationCheck verificationCheck;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 50)
    private EvidenceSourceType sourceType;

    @Column(name = "source_reference")
    private String sourceReference;

    @Column(name = "observed_at", nullable = false)
    private Instant observedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> data = new HashMap<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Evidence() {
        // Required by JPA.
    }

    public Evidence(
            VerificationCheck verificationCheck,
            EvidenceSourceType sourceType,
            String sourceReference,
            Instant observedAt,
            Map<String, Object> data
    ) {
        this.id = UUID.randomUUID();
        this.verificationCheck =
                Objects.requireNonNull(verificationCheck);
        this.sourceType = Objects.requireNonNull(sourceType);
        this.sourceReference = sourceReference;
        this.observedAt = Objects.requireNonNull(observedAt);
        this.data = data == null
                ? new HashMap<>()
                : new HashMap<>(data);
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public VerificationCheck getVerificationCheck() {
        return verificationCheck;
    }

    public EvidenceSourceType getSourceType() {
        return sourceType;
    }

    public String getSourceReference() {
        return sourceReference;
    }

    public Instant getObservedAt() {
        return observedAt;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
