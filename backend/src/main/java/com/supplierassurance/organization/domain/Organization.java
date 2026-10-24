package com.supplierassurance.organization.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organization")
public class Organization {
    @Id
    private UUID id;
    @Column(nullable = false, length = 255)
    private String name;
    @Column(name = "created_at",nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at",nullable = false)
    private Instant updatedAt;

    protected Organization(){

    }
    public Organization(String name){
        this.id=UUID.randomUUID();
        this.name=name;
        this.createdAt=Instant.now();
        this.updatedAt=this.createdAt;
    }

    //Getters
    public UUID getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Instant getCreatedAt(){
        return createdAt;
    }
    public Instant getUpdatedAt(){
        return updatedAt;
    }

    public void rename(String name){
        this.name=name;
        this.updatedAt=Instant.now();
    }
}
