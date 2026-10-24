-- ============================================================
-- Supplier Assurance Platform
-- V1 - Initial Database Schema
-- ============================================================

-- ============================================================
-- 1. ORGANIZATION
-- ============================================================

CREATE TABLE organization (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);


-- ============================================================
-- 2. APP USER
-- ============================================================

CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_app_user_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id),

    CONSTRAINT uq_app_user_organization_email
        UNIQUE (organization_id, email),

    CONSTRAINT chk_app_user_role
        CHECK (role IN ('ADMIN', 'USER'))
);

CREATE INDEX idx_app_user_organization_id
    ON app_user(organization_id);


-- ============================================================
-- 3. SUPPLIER
-- ============================================================

CREATE TABLE supplier (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100),
    website VARCHAR(2048),
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_supplier_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id),

    CONSTRAINT uq_supplier_organization_registration_number
        UNIQUE (organization_id, registration_number),

    CONSTRAINT chk_supplier_registration_number_not_blank
        CHECK (
            registration_number IS NULL
            OR LENGTH(TRIM(registration_number)) > 0
        )
);

CREATE INDEX idx_supplier_organization_id
    ON supplier(organization_id);

CREATE INDEX idx_supplier_organization_name
    ON supplier(organization_id, name);


-- ============================================================
-- 4. VERIFICATION REQUEST
-- ============================================================

CREATE TABLE verification_request (
    id UUID PRIMARY KEY,
    supplier_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    requested_at TIMESTAMP WITH TIME ZONE NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_verification_request_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES supplier(id),

    CONSTRAINT chk_verification_request_status
        CHECK (
            status IN (
                'REQUESTED',
                'IN_PROGRESS',
                'COMPLETED',
                'FAILED',
                'CANCELLED'
            )
        ),

    CONSTRAINT chk_verification_request_dates
        CHECK (
            completed_at IS NULL
            OR started_at IS NULL
            OR completed_at >= started_at
        )
);

CREATE INDEX idx_verification_request_supplier_id
    ON verification_request(supplier_id);

CREATE INDEX idx_verification_request_status
    ON verification_request(status);


-- ============================================================
-- 5. VERIFICATION CHECK
-- ============================================================

CREATE TABLE verification_check (
    id UUID PRIMARY KEY,
    verification_request_id UUID NOT NULL,
    check_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    result VARCHAR(100),
    error_message TEXT,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_verification_check_request
        FOREIGN KEY (verification_request_id)
        REFERENCES verification_request(id),

    CONSTRAINT chk_verification_check_status
        CHECK (
            status IN (
                'PENDING',
                'IN_PROGRESS',
                'PASSED',
                'FAILED',
                'SKIPPED',
                'UNAVAILABLE'
            )
        ),

    CONSTRAINT chk_verification_check_dates
        CHECK (
            completed_at IS NULL
            OR started_at IS NULL
            OR completed_at >= started_at
        )
);

CREATE INDEX idx_verification_check_request_id
    ON verification_check(verification_request_id);

CREATE INDEX idx_verification_check_status
    ON verification_check(status);


-- ============================================================
-- 6. EVIDENCE
-- ============================================================

CREATE TABLE evidence (
    id UUID PRIMARY KEY,
    verification_check_id UUID NOT NULL,
    source_type VARCHAR(100) NOT NULL,
    source_reference VARCHAR(2048),
    observed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    data JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_evidence_verification_check
        FOREIGN KEY (verification_check_id)
        REFERENCES verification_check(id)
);

CREATE INDEX idx_evidence_verification_check_id
    ON evidence(verification_check_id);

CREATE INDEX idx_evidence_source_type
    ON evidence(source_type);


-- ============================================================
-- 7. RISK ASSESSMENT
-- ============================================================

CREATE TABLE risk_assessment (
    id UUID PRIMARY KEY,
    verification_request_id UUID NOT NULL,
    score INTEGER NOT NULL,
    risk_level VARCHAR(50) NOT NULL,
    model_version VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_risk_assessment_verification_request
        FOREIGN KEY (verification_request_id)
        REFERENCES verification_request(id),

    CONSTRAINT chk_risk_assessment_score
        CHECK (score >= 0 AND score <= 100),

    CONSTRAINT chk_risk_assessment_level
        CHECK (
            risk_level IN (
                'LOW',
                'MEDIUM',
                'HIGH',
                'CRITICAL'
            )
        )
);

CREATE INDEX idx_risk_assessment_verification_request_id
    ON risk_assessment(verification_request_id);


-- ============================================================
-- 8. RISK FACTOR
-- ============================================================

CREATE TABLE risk_factor (
    id UUID PRIMARY KEY,
    risk_assessment_id UUID NOT NULL,
    evidence_id UUID NOT NULL,
    factor_type VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    impact INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_risk_factor_risk_assessment
        FOREIGN KEY (risk_assessment_id)
        REFERENCES risk_assessment(id),

    CONSTRAINT fk_risk_factor_evidence
        FOREIGN KEY (evidence_id)
        REFERENCES evidence(id)
);

CREATE INDEX idx_risk_factor_risk_assessment_id
    ON risk_factor(risk_assessment_id);

CREATE INDEX idx_risk_factor_evidence_id
    ON risk_factor(evidence_id);


-- ============================================================
-- END OF V1
-- ============================================================