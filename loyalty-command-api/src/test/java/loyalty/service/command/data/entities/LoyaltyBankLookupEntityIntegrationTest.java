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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {AxonConfig.class})
class LoyaltyBankLookupEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    private LoyaltyBankLookupEntity loyaltyBankLookupEntity;

    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setup() {
        loyaltyBankLookupEntity = new LoyaltyBankLookupEntity();
        loyaltyBankLookupEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        loyaltyBankLookupEntity.setAccountId(TEST_ACCOUNT_ID);
        loyaltyBankLookupEntity.setBusinessId(TEST_BUSINESS_ID);
    }

    @Test
    @DisplayName("Can store valid LoyaltyBankLookupEntity")
    void testLoyaltyBankLookupEntity_whenValidBusinessDetailsProvided_shouldReturnStoredBusinessDetails() {
        // Act
        LoyaltyBankLookupEntity storedLoyaltyBankLookupEntity = testEntityManager.persistAndFlush(loyaltyBankLookupEntity);

        // Assert
        assertEquals(loyaltyBankLookupEntity.getBusinessId(), storedLoyaltyBankLookupEntity.getBusinessId(), "BusinessIds should match");
    }

    @Test
    @DisplayName("Cannot store LoyaltyBankLookupEntity with same loyaltyBankId")
    void testLoyaltyBankLookupEntity_whenLoyaltyBankIdIsNotUnique_shouldThrowException() {
        // Arrange
        LoyaltyBankLookupEntity secondLoyaltyBankLookupEntity = new LoyaltyBankLookupEntity();
        secondLoyaltyBankLookupEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        secondLoyaltyBankLookupEntity.setAccountId(UUID.randomUUID().toString());
        secondLoyaltyBankLookupEntity.setBusinessId(UUID.randomUUID().toString());

        testEntityManager.persistAndFlush(secondLoyaltyBankLookupEntity);

        // Act & Assert
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(loyaltyBankLookupEntity);
        }, "Should throw PersistenceException due to duplicate loyaltyBankId");
    }

    @Test
    @DisplayName("Cannot store LoyaltyBankLookupEntity with invalid loyaltyBankId format")
    void testLoyaltyBankLookupEntity_whenInvalidLoyaltyBankIdProvided_shouldThrowException() {
        // Arrange
        loyaltyBankLookupEntity.setLoyaltyBankId("invalid-id");

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(loyaltyBankLookupEntity);
        }, "Should throw ConstraintViolationException due to invalid format");
    }

    @Test
    @DisplayName("Cannot store LoyaltyBankLookupEntity with null loyaltyBankId")
    void testLoyaltyBankLookupEntity_whenLoyaltyBankIdIsNull_shouldThrowException() {
        // Arrange
        loyaltyBankLookupEntity.setLoyaltyBankId(null);

        // Act & Assert
        assertThrows(IdentifierGenerationException.class, () -> {
            testEntityManager.persistAndFlush(loyaltyBankLookupEntity);
        }, "Should throw IdentifierGenerationException due to null loyaltyBankId");
    }

    @Test
    @DisplayName("Cannot store LoyaltyBankLookupEntity with invalid accountId format")
    void testLoyaltyBankLookupEntity_whenInvalidAccountIdProvided_shouldThrowException() {
        // Arrange
        loyaltyBankLookupEntity.setAccountId("invalid-id");

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(loyaltyBankLookupEntity);
        }, "Should throw ConstraintViolationException due to invalid format");
    }

    @Test
    @DisplayName("Cannot store LoyaltyBankLookupEntity with null accountId")
    void testLoyaltyBankLookupEntity_whenAccountIdIsNull_shouldThrowException() {
        // Arrange
        loyaltyBankLookupEntity.setAccountId(null);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(loyaltyBankLookupEntity);
        }, "Should throw ConstraintViolationException due to null accountId");
    }

    @Test
    @DisplayName("Cannot store LoyaltyBankLookupEntity with invalid businessId format")
    void testLoyaltyBankLookupEntity_whenInvalidBusinessIdProvided_shouldThrowException() {
        // Arrange
        loyaltyBankLookupEntity.setBusinessId("invalid-id");

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(loyaltyBankLookupEntity);
        }, "Should throw ConstraintViolationException due to invalid format");
    }

    @Test
    @DisplayName("Cannot store LoyaltyBankLookupEntity with null businessId")
    void testLoyaltyBankLookupEntity_whenBusinessIdIsNull_shouldThrowException() {
        // Arrange
        loyaltyBankLookupEntity.setBusinessId(null);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(loyaltyBankLookupEntity);
        }, "Should throw ConstraintViolationException due to null businessId");
    }
}