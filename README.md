# Supplier Assurance Platform

An evidence-driven supplier verification, due diligence, and risk assessment platform.

## Overview

The Supplier Assurance Platform helps organizations assess suppliers before engaging in business with them.

Supplier information is often distributed across registration records, websites, submitted documents, and other sources. This makes verification difficult to perform consistently and difficult to audit.

The platform brings these verification activities into a single workflow.

It collects supplier information, performs verification checks, stores supporting evidence, identifies risk indicators, and produces an explainable risk assessment.

The platform does not claim that a supplier is "legitimate." Instead, it presents verified facts, evidence, inconsistencies, and unknowns so that business users can make informed decisions.

## Core Capabilities

* User registration and authentication
* Organization-based tenant isolation
* Supplier management
* Supplier verification requests
* Multiple verification checks
* Evidence and source tracking
* Deterministic risk assessment
* Explainable risk factors
* Verification history
* Audit-friendly evidence trail

## High-Level Architecture

The initial implementation will use a modular monolith architecture.

```text
React UI
    |
    v
Spring Boot API
    |
    +-- Authentication
    +-- Organization
    +-- Supplier
    +-- Verification
    +-- Risk Assessment
    +-- Evidence
    |
    v
PostgreSQL
```

The architecture is intentionally kept simple for the initial version. Distributed technologies such as Kafka, Redis, and microservices will only be introduced when a concrete scalability or reliability requirement justifies them.

## Technology Stack

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* PostgreSQL
* Flyway

### Testing

* JUnit 5
* Mockito
* Testcontainers

### Frontend

* React

### Infrastructure

* Docker

Additional infrastructure and cloud services may be introduced in later phases when justified by system requirements.

## Verification Model

The platform treats evidence as a first-class concept.

A verification check does not simply produce a boolean such as:

```text
gstVerified = true
```

Instead, it produces evidence describing:

* What was checked
* Where the information came from
* When it was observed
* What was found
* How the result contributed to the assessment

This allows verification results to be explained and audited.

## Risk Assessment

Risk assessment is initially deterministic and rule-based.

Example risk indicators include:

* Registration not found
* Registration inactive
* Company name mismatch
* Address mismatch
* Website unreachable
* Other verification inconsistencies

Each risk assessment records the model version used to calculate the result so that historical assessments remain reproducible when the rules evolve.

## External Data Providers

External government or registry data sources are isolated behind provider interfaces.

For example:

```java
public interface GstVerificationProvider {

    GstVerificationResult verify(String gstin);
}
```

The initial implementation may use mocked or fixture-backed providers.

This allows the complete verification workflow to be developed and tested without depending on paid or restricted external APIs.

Authorized external providers can be integrated later without changing the core verification domain.

## Project Status

Currently in the architecture and domain design phase.

### Planned Development Phases

1. Product and domain definition
2. Database and architecture design
3. Spring Boot modular monolith
4. Authentication and organization management
5. Supplier management
6. Verification workflow
7. Evidence management
8. Deterministic risk assessment
9. Automated testing
10. Dockerization
11. Asynchronous processing
12. Event-driven architecture where justified
13. AI-assisted document analysis
14. Production hardening and deployment

## Engineering Principles

The project follows these principles:

* Prefer simple architecture before introducing distributed systems.
* Treat evidence and provenance as first-class data.
* Keep business decisions explainable.
* Separate external providers from core domain logic.
* Make verification processing idempotent.
* Design for tenant isolation.
* Version risk assessment logic.
* Record timestamps in UTC.
* Evolve the database through versioned migrations.
* Introduce new infrastructure only when there is a demonstrated requirement.

## Disclaimer

The initial project may use mocked or simulated external verification providers.

The project does not claim to provide authoritative government verification unless an authorized external provider is explicitly integrated.

## License

License to be decided.
