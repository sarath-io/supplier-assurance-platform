package com.supplierassurance.verification.domain;

import com.supplierassurance.supplier.domain.Supplier;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verification_request")
public class VerificationRequest {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "supplier_id",nullable = false)
    private Supplier supplier;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private VerificationRequestStatus status;
    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;
    @Column(name = "started_at")
    private Instant startedAt;
    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected VerificationRequest(){}

    public VerificationRequest(Supplier supplier) {
        this.id = UUID.randomUUID();
        this.supplier = supplier;
        this.status = VerificationRequestStatus.REQUESTED;

        Instant now = Instant.now();
        this.requestedAt = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public VerificationRequestStatus getStatus() {
        return status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void start() {
        requireStatus(VerificationRequestStatus.REQUESTED);

        Instant now = Instant.now();
        this.status = VerificationRequestStatus.IN_PROGRESS;
        this.startedAt = now;
        this.updatedAt = now;
    }

    public void complete() {
        requireStatus(VerificationRequestStatus.IN_PROGRESS);

        Instant now = Instant.now();
        this.status = VerificationRequestStatus.COMPLETED;
        this.completedAt = now;
        this.updatedAt = now;
    }

    public void fail() {
        if (status != VerificationRequestStatus.REQUESTED
                && status != VerificationRequestStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only an active verification request can fail."
            );
        }

        finishAs(VerificationRequestStatus.FAILED);
    }

    public void cancel() {
        if (status != VerificationRequestStatus.REQUESTED
                && status != VerificationRequestStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only an active verification request can be cancelled."
            );
        }

        finishAs(VerificationRequestStatus.CANCELLED);
    }

    private void finishAs(VerificationRequestStatus finalStatus) {
        Instant now = Instant.now();
        this.status = finalStatus;
        this.completedAt = now;
        this.updatedAt = now;
    }

    private void requireStatus(VerificationRequestStatus expectedStatus) {
        if (this.status != expectedStatus) {
            throw new IllegalStateException(
                    "Cannot perform this transition from status: " + status
            );
        }
    }
}
