
package com.supplierassurance.verification.domain;

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
import java.util.UUID;

@Entity
@Table(name = "verification_check")
public class VerificationCheck {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "verification_request_id", nullable = false)
    private VerificationRequest verificationRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_type", nullable = false, length = 50)
    private VerificationCheckType checkType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationCheckStatus status;

    @Column(name = "result")
    private String result;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected VerificationCheck() {
        // Required by JPA.
    }

    public VerificationCheck(
            VerificationRequest verificationRequest,
            VerificationCheckType checkType
    ) {
        this.id = UUID.randomUUID();
        this.verificationRequest = verificationRequest;
        this.checkType = checkType;
        this.status = VerificationCheckStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public VerificationRequest getVerificationRequest() {
        return verificationRequest;
    }

    public VerificationCheckType getCheckType() {
        return checkType;
    }

    public VerificationCheckStatus getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
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

    public void start() {
        requireStatus(VerificationCheckStatus.PENDING);

        this.status = VerificationCheckStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
    }

    public void pass(String result) {
        complete(
                VerificationCheckStatus.PASSED,
                result,
                null
        );
    }

    public void fail(String result) {
        complete(
                VerificationCheckStatus.FAILED,
                result,
                null
        );
    }

    public void markUnavailable(String errorMessage) {
        complete(
                VerificationCheckStatus.UNAVAILABLE,
                null,
                errorMessage
        );
    }

    public void skip(String reason) {
        if (status != VerificationCheckStatus.PENDING
                && status != VerificationCheckStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only a pending or in-progress check can be skipped."
            );
        }

        this.status = VerificationCheckStatus.SKIPPED;
        this.result = reason;
        this.completedAt = Instant.now();
    }

    private void complete(
            VerificationCheckStatus finalStatus,
            String result,
            String errorMessage
    ) {
        requireStatus(VerificationCheckStatus.IN_PROGRESS);

        this.status = finalStatus;
        this.result = result;
        this.errorMessage = errorMessage;
        this.completedAt = Instant.now();
    }

    private void requireStatus(VerificationCheckStatus expectedStatus) {
        if (this.status != expectedStatus) {
            throw new IllegalStateException(
                    "Cannot perform this transition from status: " + status
            );
        }
    }
}
