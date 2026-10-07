# Product Scope — V1

## 1. Product Goal

The Supplier Assurance Platform helps organizations assess suppliers before engaging in business with them.

The platform collects supplier information, performs verification checks, records supporting evidence, identifies risk indicators, and produces an explainable risk assessment.

The system is designed to support business decision-making. It does not independently declare a supplier legitimate, fraudulent, safe, or unsafe.

---

## 2. Target User

The primary V1 user is a procurement or operations employee responsible for evaluating suppliers.

A business administrator can manage users within the organization.

### V1 Roles

* `ADMIN`
* `USER`

Complex role-based access control is outside the initial scope.

---

## 3. Core User Journey

The primary workflow is:

```text
Register / Login
      |
      v
Add Supplier
      |
      v
Start Verification
      |
      v
Verification Checks
      |
      v
Evidence Collection
      |
      v
Risk Assessment
      |
      v
Review Verification Report
      |
      v
Business Decision
```

---

## 4. V1 Functional Requirements

### 4.1 Authentication

Users must be able to:

* Register
* Login
* Authenticate using credentials
* Access protected APIs
* Belong to an organization

Passwords must never be stored in plain text.

Passwords will be securely hashed using Spring Security's supported password hashing mechanism.

---

### 4.2 Organization Management

Each user belongs to an organization.

Organization data provides the tenant boundary for V1.

Users must only be able to access suppliers belonging to their organization.

Example:

```text
Organization A
    |
    +-- User A1
    +-- User A2
    +-- Supplier A1
    +-- Supplier A2

Organization B
    |
    +-- User B1
    +-- Supplier B1
```

A user from Organization A must not be able to access Supplier B1.

---

### 4.3 Supplier Management

Users can create and view suppliers.

A supplier contains:

* Name
* Registration number
* Website
* Email
* Phone
* Address

Supplier records belong to an organization.

The registration number should be unique within an organization where provided.

---

### 4.4 Verification Requests

A user can initiate a verification request for a supplier.

A verification request represents a historical verification workflow.

Possible lifecycle:

```text
REQUESTED
    |
    v
IN_PROGRESS
    |
    +----> FAILED
    |
    v
COMPLETED
```

Cancellation may be supported as the workflow evolves.

Multiple verification requests may exist for the same supplier.

This allows the system to preserve verification history.

---

### 4.5 Verification Checks

A verification request consists of multiple checks.

Examples:

* GST / registration verification
* Company registration verification
* Website reachability
* Name consistency
* Address consistency

Each check has its own status and result.

A failed technical check does not automatically mean that the supplier is fraudulent.

For example:

```text
Website temporarily unavailable
```

is different from:

```text
Registration record not found
```

The risk assessment interprets these results separately.

---

### 4.6 Evidence

Every meaningful verification result should be supported by evidence where possible.

Evidence records:

* Source type
* Source reference
* Observation timestamp
* Structured data

Example:

```json
{
  "legalName": "ABC Technologies Private Limited",
  "status": "ACTIVE",
  "registrationDate": "2021-04-12"
}
```

Evidence may originate from:

* Supplier input
* Government/public source
* Authorized provider
* Company registry
* Website
* Supplier document

The platform must distinguish between supplier-provided information and externally obtained information.

---

### 4.7 Risk Assessment

The initial risk engine is deterministic and rule-based.

Example risk indicators:

| Risk Indicator         | Example Impact |
| ---------------------- | -------------: |
| Registration inactive  |            +40 |
| Registration not found |            +50 |
| Company name mismatch  |            +20 |
| Address mismatch       |            +15 |
| Website unreachable    |            +10 |
| Invalid HTTPS          |             +5 |
| Recently registered    |             +5 |

The exact scoring rules are subject to refinement during implementation.

Example risk levels:

```text
0 - 20     LOW
21 - 50    MEDIUM
51 - 75    HIGH
76 - 100   CRITICAL
```

The final V1 scoring model will be documented and versioned.

---

### 4.8 Explainable Risk Factors

A risk assessment must explain why a risk score was produced.

Each risk factor should identify:

* Factor type
* Description
* Impact
* Supporting evidence

Example:

```text
Risk Factor:
ADDRESS_MISMATCH

Impact:
+15

Reason:
Supplier-provided address differs from the registered address.

Evidence:
EV-102
```

This creates the relationship:

```text
Risk Factor
     |
     v
Evidence
     |
     v
Source
```

---

## 5. External Data Provider Strategy

External verification sources are isolated behind provider interfaces.

Example:

```java
public interface GstVerificationProvider {

    GstVerificationResult verify(String gstin);
}
```

The V1 system may use mocked or fixture-backed implementations.

The purpose is to make the verification architecture realistic without depending on paid or restricted external APIs.

The core verification workflow must remain real:

```text
Verification Request
        |
        v
Verification Check
        |
        v
Provider
        |
        v
Result
        |
        v
Evidence
        |
        v
Risk Assessment
```

Only the external data source may be simulated.

The system must not claim to perform real government verification unless an authorized provider is actually integrated.

---

## 6. V1 Non-Functional Requirements

### Security

* Passwords must be hashed.
* Protected endpoints require authentication.
* Organization boundaries must be enforced.
* Users must not access another organization's suppliers or verification data.

### Data Integrity

* Foreign key relationships must be enforced.
* Required fields must be non-null.
* Appropriate uniqueness constraints must be applied.
* Verification history must remain consistent.

### Auditability

The system should preserve:

* Verification requests
* Verification checks
* Evidence
* Risk assessments
* Risk factors
* Timestamps

The reason behind a risk assessment should be traceable to evidence.

### Reproducibility

The same evidence evaluated using the same risk model version should produce the same assessment.

---

## 7. V1 Out of Scope

The following are intentionally excluded from V1:

* Microservices
* Kafka
* Redis
* Kubernetes
* Elasticsearch
* Vector databases
* Machine-learning fraud detection
* LLM agents
* RAG
* Real-time government API integrations
* Payments
* Mobile applications
* SSO
* Advanced RBAC
* Multi-region deployment
* Complex notification systems
* Automated supplier approval

These may be considered in later phases when a real requirement justifies them.

---

## 8. Future Evolution

Potential future capabilities include:

* Supplier document uploads
* Document verification
* AI-assisted document analysis
* Evidence-backed RAG
* Asynchronous verification processing
* Kafka-based event processing
* Redis caching
* Notifications
* Advanced audit logging
* External authorized verification providers
* Cloud deployment
* Advanced organization and permission management

Future capabilities should be introduced only when they solve a demonstrated business or engineering problem.

---

## 9. V1 Success Criteria

V1 is considered successful when a user can:

1. Register and authenticate.
2. Create a supplier.
3. Start a verification request.
4. Execute multiple verification checks.
5. Store evidence for those checks.
6. Generate a deterministic risk assessment.
7. View the risk factors contributing to the assessment.
8. Trace risk factors back to supporting evidence.
9. Access only data belonging to their organization.
10. Reproduce the assessment using the same model version and evidence.
