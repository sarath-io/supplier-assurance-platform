
package com.supplierassurance.risk.domain;

import com.supplierassurance.verification.domain.VerificationRequest;
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
@Table(name = "risk_assessment")
public class RiskAssessment {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "verification_request_id", nullable = false)
    private VerificationRequest verificationRequest;

    @Column(nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(name = "model_version", nullable = false)
    private String modelVersion;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RiskAssessment() {
        // Required by JPA.
    }

    public RiskAssessment(
            VerificationRequest verificationRequest,
            int score,
            String modelVersion
    ) {
        this.id = UUID.randomUUID();
        this.verificationRequest =
                Objects.requireNonNull(verificationRequest);

        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(
                    "Risk score must be between 0 and 100."
            );
        }

        if (modelVersion == null || modelVersion.isBlank()) {
            throw new IllegalArgumentException(
                    "Model version must not be blank."
            );
        }

        this.score = score;
        this.riskLevel = RiskLevel.fromScore(score);
        this.modelVersion = modelVersion;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public VerificationRequest getVerificationRequest() {
        return verificationRequest;
    }

    public int getScore() {
        return score;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
