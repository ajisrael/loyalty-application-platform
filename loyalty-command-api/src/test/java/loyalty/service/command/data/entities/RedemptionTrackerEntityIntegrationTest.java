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

import static loyalty.service.core.constants.ExceptionMessages.CANNOT_CAPTURE_MORE_POINTS_THAN_AVAILABLE;
import static loyalty.service.core.constants.ExceptionMessages.CANNOT_VOID_MORE_POINTS_THAN_AVAILABLE;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {AxonConfig.class})
class RedemptionTrackerEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    private RedemptionTrackerEntity redemptionTrackerEntity;

    private static final String TEST_PAYMENT_ID = UUID.randomUUID().toString();
    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final int TEST_AUTHORIZED_POINTS = 100;
    private static final int TEST_CAPTURED_POINTS = 100;

    @BeforeEach
    void setup() {
        redemptionTrackerEntity = new RedemptionTrackerEntity();
        redemptionTrackerEntity.setPaymentId(TEST_PAYMENT_ID);
        redemptionTrackerEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        redemptionTrackerEntity.setAuthorizedPoints(TEST_AUTHORIZED_POINTS);
        redemptionTrackerEntity.setCapturedPoints(0);
    }

    @Test
    @DisplayName("Can store valid RedemptionTrackerEntity")
    void testRedemptionTrackerEntity_whenValidBusinessDetailsProvided_shouldReturnStoredBusinessDetails() {
        // Act
        RedemptionTrackerEntity storedRedemptionTrackerEntity = testEntityManager.persistAndFlush(redemptionTrackerEntity);

        // Assert
        assertEquals(redemptionTrackerEntity.getPaymentId(), storedRedemptionTrackerEntity.getPaymentId(), "PaymentIds should match");
        assertEquals(redemptionTrackerEntity.getLoyaltyBankId(), storedRedemptionTrackerEntity.getLoyaltyBankId(), "LoyaltyBankIds should match");
        assertEquals(redemptionTrackerEntity.getAuthorizedPoints(), storedRedemptionTrackerEntity.getAuthorizedPoints(), "AuthorizedPointss should match");
        assertEquals(redemptionTrackerEntity.getCapturedPoints(), storedRedemptionTrackerEntity.getCapturedPoints(), "CapturedPointss should match");
    }

    @Test
    @DisplayName("Cannot store RedemptionTrackerEntity with same paymentId")
    void testRedemptionTrackerEntity_whenPaymentIdIsNotUnique_shouldThrowException() {
        // Arrange
        RedemptionTrackerEntity secondRedemptionTrackerEntity = new RedemptionTrackerEntity();
        secondRedemptionTrackerEntity.setPaymentId(TEST_PAYMENT_ID);
        secondRedemptionTrackerEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        secondRedemptionTrackerEntity.setAuthorizedPoints(TEST_AUTHORIZED_POINTS);
        secondRedemptionTrackerEntity.setCapturedPoints(0);

        testEntityManager.persistAndFlush(secondRedemptionTrackerEntity);

        // Act & Assert
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(redemptionTrackerEntity);
        }, "Should throw PersistenceException due to duplicate paymentId");
    }

    @Test
    @DisplayName("Cannot store RedemptionTrackerEntity with invalid paymentId format")
    void testRedemptionTrackerEntity_whenInvalidPaymentIdProvided_shouldThrowException() {
        // Arrange
        redemptionTrackerEntity.setPaymentId("invalid-id");

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(redemptionTrackerEntity);
        }, "Should throw ConstraintViolationException due to invalid format");
    }

    @Test
    @DisplayName("Cannot store RedemptionTrackerEntity with null paymentId")
    void testRedemptionTrackerEntity_whenPaymentIdIsNull_shouldThrowException() {
        // Arrange
        redemptionTrackerEntity.setPaymentId(null);

        // Act & Assert
        assertThrows(IdentifierGenerationException.class, () -> {
            testEntityManager.persistAndFlush(redemptionTrackerEntity);
        }, "Should throw IdentifierGenerationException due to null paymentId");
    }

    @Test
    @DisplayName("Cannot store RedemptionTrackerEntity with invalid loyaltyBankId format")
    void testRedemptionTrackerEntity_whenInvalidLoyaltyBankIdProvided_shouldThrowException() {
        // Arrange
        redemptionTrackerEntity.setLoyaltyBankId("invalid-id");

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(redemptionTrackerEntity);
        }, "Should throw ConstraintViolationException due to invalid format");
    }

    @Test
    @DisplayName("Cannot store RedemptionTrackerEntity with null loyaltyBankId")
    void testRedemptionTrackerEntity_whenLoyaltyBankIdIsNull_shouldThrowException() {
        // Arrange
        redemptionTrackerEntity.setLoyaltyBankId(null);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(redemptionTrackerEntity);
        }, "Should throw ConstraintViolationException due to null loyaltyBankId");
    }

    @Test
    @DisplayName("Can track void transactions")
    void testVoidAuthorizedPoints_whenValidPointsProvided_shouldSubtractFromAuthorizedPoints() {
        // Act
        redemptionTrackerEntity.voidAuthorizedPoints(TEST_AUTHORIZED_POINTS);

        // Assert
        assertEquals(0, redemptionTrackerEntity.getAuthorizedPoints(), "AuthorizedPoints should be 0");
    }

    @Test
    @DisplayName("Cannot void more points than available")
    void testVoidAuthorizedPoints_whenInvalidPointsProvided_shouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            redemptionTrackerEntity.voidAuthorizedPoints(TEST_AUTHORIZED_POINTS * 2);
        }, "Should throw IllegalArgumentException due to voiding more points than available");

        // Assert
        assertEquals(CANNOT_VOID_MORE_POINTS_THAN_AVAILABLE, exception.getLocalizedMessage(), "Messages should match");
    }

    @Test
    @DisplayName("Can track void transactions")
    void testAddCapturedPoints_whenValidPointsProvided_shouldIncreaseCapturedPoints() {
        // Act
        redemptionTrackerEntity.addCapturedPoints(TEST_CAPTURED_POINTS);

        // Assert
        assertEquals(TEST_CAPTURED_POINTS, redemptionTrackerEntity.getCapturedPoints(), "CapturedPoints should be 0");
    }

    @Test
    @DisplayName("Cannot capture more points than available")
    void testAddCapturedPoints_whenInvalidPointsProvided_shouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            redemptionTrackerEntity.addCapturedPoints(TEST_CAPTURED_POINTS * 2);
        }, "Should throw IllegalArgumentException due to capturing more points than available");

        // Assert
        assertEquals(CANNOT_CAPTURE_MORE_POINTS_THAN_AVAILABLE, exception.getLocalizedMessage(), "Messages should match");
    }

    @Test
    @DisplayName("Can get correct points available for redemption")
    void testGetPointsAvailableForRedemption() {
        // Arrange
        int expectedPoints = redemptionTrackerEntity.getAuthorizedPoints() - redemptionTrackerEntity.getCapturedPoints();

        // Assert
        assertEquals(expectedPoints, redemptionTrackerEntity.getPointsAvailableForRedemption(), "Points should match");
    }
}