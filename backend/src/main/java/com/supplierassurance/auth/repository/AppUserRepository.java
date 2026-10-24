package com.supplierassurance.auth.repository;

import com.supplierassurance.auth.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByOrganization_IdAndEmail(UUID organizationId,String email);
    boolean existsByOrganization_IdAndEmail(UUID organizationId,String email);
}
