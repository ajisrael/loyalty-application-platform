package loyalty.service.command.data.entities;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import loyalty.service.command.config.AxonConfig;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {AxonConfig.class})
class BusinessLookupEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    private BusinessLookupEntity businessLookupEntity;

    private static final String TEST_BUSINESS_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setup() {
        businessLookupEntity = new BusinessLookupEntity();
        businessLookupEntity.setBusinessId(TEST_BUSINESS_ID);
    }

    @Test
    @DisplayName("Can store valid BusinessLookupEntity")
    void testBusinessLookupEntity_whenValidBusinessDetailsProvided_shouldReturnStoredBusinessDetails() {
        // Act
        BusinessLookupEntity storedBusinessLookupEntity = testEntityManager.persistAndFlush(businessLookupEntity);

        // Assert
        assertEquals(businessLookupEntity.getBusinessId(), storedBusinessLookupEntity.getBusinessId(), "BusinessIds should match");
    }

    @Test
    @DisplayName("Cannot store BusinessLookupEntity with same businessId")
    void testBusinessLookupEntity_whenBusinessIdIsNotUnique_shouldThrowException() {
        // Arrange
        BusinessLookupEntity secondBusinessLookupEntity = new BusinessLookupEntity();
        secondBusinessLookupEntity.setBusinessId(TEST_BUSINESS_ID);

        testEntityManager.persistAndFlush(secondBusinessLookupEntity);

        // Act & Assert
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(businessLookupEntity);
        }, "Should throw PersistenceException due to duplicate businessId");
    }

    @Test
    @DisplayName("Cannot store BusinessLookupEntity with invalid businessId format")
    void testBusinessLookupEntity_whenInvalidBusinessIdProvided_shouldThrowException() {
        // Arrange
        businessLookupEntity.setBusinessId("invalid-id");

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(businessLookupEntity);
        }, "Should throw ConstraintViolationException due to invalid format");
    }

    @Test
    @DisplayName("Cannot store BusinessLookupEntity with null businessId")
    void testBusinessLookupEntity_whenBusinessIdIsNull_shouldThrowException() {
        // Arrange
        businessLookupEntity.setBusinessId(null);

        // Act & Assert
        assertThrows(IdentifierGenerationException.class, () -> {
            testEntityManager.persistAndFlush(businessLookupEntity);
        }, "Should throw IdentifierGenerationException due to null businessId");
    }
}