package loyalty.service.command.data.entities;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import loyalty.service.command.config.AxonConfig;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {AxonConfig.class})
class ExpirationTrackerEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    private ExpirationTrackerEntity expirationTrackerEntity;

    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_TRANSACTION_ID = UUID.randomUUID().toString();
    private static final int TEST_POINTS = 100;
    private static final Instant TEST_TIMESTAMP = Instant.now();
    private static final TransactionEntity TEST_TRANSACTION_ENTITY = new TransactionEntity(
           TEST_TRANSACTION_ID,
           TEST_POINTS,
           TEST_TIMESTAMP,
           TEST_LOYALTY_BANK_ID
    );

    @BeforeEach
    void setup() {
        expirationTrackerEntity = new ExpirationTrackerEntity();
        expirationTrackerEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
    }

    @Test
    @DisplayName("Can store valid ExpirationTrackerEntity")
    void testExpirationTrackerEntity_whenValidBusinessDetailsProvided_shouldReturnStoredBusinessDetails() {
        // Act
        ExpirationTrackerEntity storedExpirationTrackerEntity = testEntityManager.persistAndFlush(expirationTrackerEntity);

        // Assert
        assertEquals(expirationTrackerEntity.getLoyaltyBankId(), storedExpirationTrackerEntity.getLoyaltyBankId(), "LoyaltyBankIds should match");
    }

    @Test
    @DisplayName("Cannot store ExpirationTrackerEntity with same expirationTrackerId")
    void testExpirationTrackerEntity_whenLoyaltyBankIdIsNotUnique_shouldThrowException() {
        // Arrange
        ExpirationTrackerEntity secondExpirationTrackerEntity = new ExpirationTrackerEntity();
        secondExpirationTrackerEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        testEntityManager.persistAndFlush(secondExpirationTrackerEntity);

        // Act & Assert
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(expirationTrackerEntity);
        }, "Should throw PersistenceException due to duplicate expirationTrackerId");
    }

    @Test
    @DisplayName("Cannot store ExpirationTrackerEntity with invalid expirationTrackerId format")
    void testExpirationTrackerEntity_whenInvalidLoyaltyBankIdProvided_shouldThrowException() {
        // Arrange
        expirationTrackerEntity.setLoyaltyBankId("invalid-id");

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(expirationTrackerEntity);
        }, "Should throw ConstraintViolationException due to invalid format");
    }

    @Test
    @DisplayName("Cannot store ExpirationTrackerEntity with null expirationTrackerId")
    void testExpirationTrackerEntity_whenLoyaltyBankIdIsNull_shouldThrowException() {
        // Arrange
        expirationTrackerEntity.setLoyaltyBankId(null);

        // Act & Assert
        assertThrows(IdentifierGenerationException.class, () -> {
            testEntityManager.persistAndFlush(expirationTrackerEntity);
        }, "Should throw IdentifierGenerationException due to null expirationTrackerId");
    }

    @Test
    @DisplayName("Can add TransactionEntity to ExpirationTrackerEntity")
    void testAddTransaction_whenValidTransactionEntityProvided_shouldUpdateExpirationTrackerEntity() {
        // Arrange
        expirationTrackerEntity.addTransaction(TEST_TRANSACTION_ENTITY);

        // Act
        ExpirationTrackerEntity storedExpirationTrackerEntity = testEntityManager.persistAndFlush(expirationTrackerEntity);
        TransactionEntity storedTransactionEntity = storedExpirationTrackerEntity.getTransactionList().get(0);

        // Assert
        assertEquals(expirationTrackerEntity.getLoyaltyBankId(), storedExpirationTrackerEntity.getLoyaltyBankId(), "LoyaltyBankIds should match");
        assertEquals(1, storedExpirationTrackerEntity.getTransactionList().size(), "Size of transaction list should be 1");
        assertEquals(TEST_TRANSACTION_ENTITY.getTransactionId(), storedTransactionEntity.getTransactionId(), "TransactionIds should match");
        assertEquals(TEST_TRANSACTION_ENTITY.getPoints(), storedTransactionEntity.getPoints(), "Points should match");
        assertEquals(TEST_TRANSACTION_ENTITY.getTimestamp(), storedTransactionEntity.getTimestamp(), "Timestamps should match");
        assertEquals(TEST_TRANSACTION_ENTITY.getLoyaltyBankId(), storedTransactionEntity.getLoyaltyBankId(), "LoyaltyBankIds should match");
    }

    @Test
    @DisplayName("Cannot add TransactionEntity with existing transactionId to ExpirationTrackerEntity")
    void testAddTransaction_whenTransactionEntityIsNotUnique_shouldThrowException() {
        // Arrange
        expirationTrackerEntity.addTransaction(TEST_TRANSACTION_ENTITY);
        testEntityManager.persistAndFlush(expirationTrackerEntity);
        ExpirationTrackerEntity secondExpirationTrackerEntity = new ExpirationTrackerEntity();
        secondExpirationTrackerEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        secondExpirationTrackerEntity.addTransaction(TEST_TRANSACTION_ENTITY);

        // Act & Assert
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(secondExpirationTrackerEntity);
        }, "Should throw PersistenceException due to duplicate transactionId");
    }

    @Test
    @DisplayName("Can remove TransactionEntity from ExpirationTrackerEntity")
    void testRemoveTransaction_whenValidTransactionEntityProvided_shouldUpdateExpirationTrackerEntity() {
        // Arrange
        expirationTrackerEntity.addTransaction(TEST_TRANSACTION_ENTITY);
        testEntityManager.persistAndFlush(expirationTrackerEntity);
        expirationTrackerEntity.removeTransaction(TEST_TRANSACTION_ENTITY);

        // Act
        ExpirationTrackerEntity storedExpirationTrackerEntity = testEntityManager.persistAndFlush(expirationTrackerEntity);

        // Assert
        assertEquals(expirationTrackerEntity.getLoyaltyBankId(), storedExpirationTrackerEntity.getLoyaltyBankId(), "LoyaltyBankIds should match");
        assertEquals(0, storedExpirationTrackerEntity.getTransactionList().size(), "Size of transaction list should be 0");
    }
}