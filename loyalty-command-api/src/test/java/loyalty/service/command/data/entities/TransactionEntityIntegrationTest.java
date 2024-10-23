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

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {AxonConfig.class})
class TransactionEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;
    private TransactionEntity transactionEntity;

    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_TRANSACTION_ID = UUID.randomUUID().toString();
    private static final int TEST_POINTS = 100;
    private static final Instant TEST_TIMESTAMP = Instant.now();

    @BeforeEach
    void setup() {
    transactionEntity = new TransactionEntity(
                TEST_TRANSACTION_ID,
                TEST_POINTS,
                TEST_TIMESTAMP,
                TEST_LOYALTY_BANK_ID
        );
    }

    @Test
    @DisplayName("Can store valid TransactionEntity")
    void testTransactionEntity_whenValidBusinessDetailsProvided_shouldReturnStoredBusinessDetails() {
        // Act
        TransactionEntity storedTransactionEntity = testEntityManager.persistAndFlush(transactionEntity);

        // Assert
        assertEquals(transactionEntity.getLoyaltyBankId(), storedTransactionEntity.getLoyaltyBankId(), "LoyaltyBankIds should match");
    }

    @Test
    @DisplayName("Cannot store TransactionEntity with same transactionId")
    void testTransactionEntity_whenTransactionIdIsNotUnique_shouldThrowException() {
        // Arrange
        TransactionEntity secondTransactionEntity = new TransactionEntity(
                TEST_TRANSACTION_ID,
                TEST_POINTS,
                TEST_TIMESTAMP,
                TEST_LOYALTY_BANK_ID
        );
        testEntityManager.persistAndFlush(secondTransactionEntity);

        // Act & Assert
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(transactionEntity);
        }, "Should throw PersistenceException due to duplicate transactionId");
    }

    @Test
    @DisplayName("Cannot store TransactionEntity with invalid transactionId format")
    void testTransactionEntity_whenInvalidTransactionIdProvided_shouldThrowException() {
        // Arrange
        TransactionEntity invalidTransactionEntity = new TransactionEntity(
                "invalid-id",
                TEST_POINTS,
                TEST_TIMESTAMP,
                TEST_LOYALTY_BANK_ID
        );

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(invalidTransactionEntity);
        }, "Should throw ConstraintViolationException due to invalid format");
    }

    @Test
    @DisplayName("Cannot store TransactionEntity with null transactionId")
    void testTransactionEntity_whenNullTransactionIdProvided_shouldThrowException() {
        // Arrange
        TransactionEntity invalidTransactionEntity = new TransactionEntity(
                null,
                TEST_POINTS,
                TEST_TIMESTAMP,
                TEST_LOYALTY_BANK_ID
        );

        // Act & Assert
        assertThrows(IdentifierGenerationException.class, () -> {
            testEntityManager.persistAndFlush(invalidTransactionEntity);
        }, "Should throw IdentifierGenerationException due to null transactionId");
    }

    @Test
    @DisplayName("Cannot store TransactionEntity with null timestamp")
    void testTransactionEntity_whenNullTimestampProvided_shouldThrowException() {
        // Arrange
        TransactionEntity invalidTransactionEntity = new TransactionEntity(
                TEST_TRANSACTION_ID,
                TEST_POINTS,
                null,
                TEST_LOYALTY_BANK_ID
        );

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(invalidTransactionEntity);
        }, "Should throw ConstraintViolationException due to null timestamp");
    }

    @Test
    @DisplayName("Cannot store TransactionEntity with invalid loyaltyBankId format")
    void testTransactionEntity_whenInvalidLoyaltyBankIdProvided_shouldThrowException() {
        // Arrange
        TransactionEntity invalidTransactionEntity = new TransactionEntity(
                TEST_TRANSACTION_ID,
                TEST_POINTS,
                TEST_TIMESTAMP,
                "invalid-id"
        );

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(invalidTransactionEntity);
        }, "Should throw ConstraintViolationException due to invalid format");
    }

    @Test
    @DisplayName("Cannot store TransactionEntity with null loyaltyBankId")
    void testTransactionEntity_whenNullLoyaltyBankIdProvided_shouldThrowException() {
        // Arrange
        TransactionEntity invalidTransactionEntity = new TransactionEntity(
                TEST_TRANSACTION_ID,
                TEST_POINTS,
                TEST_TIMESTAMP,
                null
        );

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(invalidTransactionEntity);
        }, "Should throw ConstraintViolationException due to null loyaltyBankId");
    }

    @Test
    @DisplayName("Can add points to TransactionEntity")
    void testAddPoints_whenPointsProvided_shouldAddPoints() {
        // Arrange
        int pointsToAdd = 100;
        int expectedPoints = transactionEntity.getPoints() + pointsToAdd;

        // Act
        transactionEntity.addPoints(pointsToAdd);

        // Assert
        assertEquals(expectedPoints, transactionEntity.getPoints(), "Points should be equal");
    }
}