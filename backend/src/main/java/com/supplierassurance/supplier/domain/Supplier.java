
package com.supplierassurance.supplier.domain;

import com.supplierassurance.organization.domain.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "supplier",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_supplier_organization_registration",
                        columnNames = {"organization_id", "registration_number"}
                )
        }
)
public class Supplier {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Column(length = 2048)
    private String website;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(length = 1000)
    private String address;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Supplier() {
        // Required by JPA.
    }

    public Supplier(
            Organization organization,
            String name,
            String registrationNumber,
            String website,
            String email,
            String phone,
            String address
    ) {
        this.id = UUID.randomUUID();
        this.organization = organization;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.website = website;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getName() {
        return name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
