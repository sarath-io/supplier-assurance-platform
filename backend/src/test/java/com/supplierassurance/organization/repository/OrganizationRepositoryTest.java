package com.supplierassurance.organization.repository;

import com.supplierassurance.organization.domain.Organization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class OrganizationRepositoryTest {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Test
    void shouldSaveAndRetrieveOrganization(){
        Organization organization=new Organization("SAP test organization");
        Organization saved=organizationRepository.saveAndFlush(organization);
        assertNotNull(saved.getId());
        Optional<Organization> retrieved=organizationRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("SAP test organization",retrieved.get().getName());
        assertNotNull(retrieved.get().getCreatedAt());
        assertNotNull(retrieved.get().getUpdatedAt());
    }

}
