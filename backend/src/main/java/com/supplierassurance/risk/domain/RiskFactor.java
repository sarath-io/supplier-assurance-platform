
package com.supplierassurance.risk.domain;

import com.supplierassurance.evidence.domain.Evidence;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "risk_factor")
public class RiskFactor {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "risk_assessment_id", nullable = false)
    private RiskAssessment riskAssessment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evidence_id", nullable = false)
    private Evidence evidence;

    @Enumerated(EnumType.STRING)
    @Column(name = "factor_type", nullable = false, length = 100)
    private RiskFactorType factorType;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int impact;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RiskFactor() {
        // Required by JPA.
    }

    public RiskFactor(
            RiskAssessment riskAssessment,
            Evidence evidence,
            RiskFactorType factorType,
            String description,
            int impact
    ) {
        this.id = UUID.randomUUID();
        this.riskAssessment = Objects.requireNonNull(riskAssessment);
        this.evidence = Objects.requireNonNull(evidence);
        this.factorType = Objects.requireNonNull(factorType);

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Risk factor description must not be blank."
            );
        }

        this.description = description;
        this.impact = impact;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public RiskAssessment getRiskAssessment() {
        return riskAssessment;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public RiskFactorType getFactorType() {
        return factorType;
    }

    public String getDescription() {
        return description;
    }

    public int getImpact() {
        return impact;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
