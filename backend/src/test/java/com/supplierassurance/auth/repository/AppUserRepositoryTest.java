package com.supplierassurance.auth.repository;

import com.supplierassurance.auth.domain.AppUser;
import com.supplierassurance.auth.domain.UserRole;
import com.supplierassurance.organization.domain.Organization;
import com.supplierassurance.organization.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
public class AppUserRepositoryTest {
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void shouldSaveAndRetrieveUserWithinOrganization(){
        Organization organization=organizationRepository.saveAndFlush(new Organization("SAP Test Organization"));
        AppUser appUser=new AppUser(
                organization,"admin@sap-test.example","test-password-hash", UserRole.ADMIN
        );
        AppUser saved=appUserRepository.saveAndFlush(appUser);
        assertNotNull(saved.getId());
        assertEquals(UserRole.ADMIN,saved.getRole());
        assertEquals(organization.getId(),saved.getOrganization().getId());
        AppUser retrieved=appUserRepository.findByOrganization_IdAndEmail(organization.getId(),"admin@sap-test.example").orElseThrow();
        assertEquals(saved.getId(),retrieved.getId());
        assertEquals("admin@sap-test.example",retrieved.getEmail());
        assertTrue(appUserRepository.existsByOrganization_IdAndEmail(retrieved.getOrganization().getId(),retrieved.getEmail()));
    }
}
