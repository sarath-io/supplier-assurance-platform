package com.supplierassurance.auth.domain;

import com.supplierassurance.organization.domain.Organization;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user",
   uniqueConstraints = {
           @UniqueConstraint(
                   name = "uk_app_user_organization_email",
                   columnNames = {"organization_id","email"}
           )
   }
)
public class AppUser {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id",nullable = false)
    private Organization organization;
    @Column(nullable = false, length = 255)
    private String email;
    @Column(name="password_hash",nullable = false,length = 255)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private UserRole role;
    @Column(nullable = false,name = "created_at")
    private Instant createdAt;
    @Column(nullable = false,name = "updated_at")
    private Instant updatedAt;

    protected AppUser(){

    }
    public AppUser(Organization organization,String email, String passwordHash,UserRole role){
        this.id=UUID.randomUUID();
        this.organization=organization;
    this.email=email;
    this.passwordHash=passwordHash;
    this.role=role;
    this.createdAt=Instant.now();
    this.updatedAt=this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
