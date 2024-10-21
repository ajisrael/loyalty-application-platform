package loyalty.service.command.commands.transactions;

import loyalty.service.command.test.utils.TestParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.constants.ExceptionMessages.PAYMENT_ID_CANNOT_BE_EMPTY;
import static org.junit.jupiter.api.Assertions.*;

class CreateCapturedTransactionCommandTest {

    private CreateCapturedTransactionCommand.CreateCapturedTransactionCommandBuilder createCapturedTransactionCommandBuilder;

    @BeforeEach
    void setup() {
        createCapturedTransactionCommandBuilder = CreateCapturedTransactionCommand.builder()
                .requestId("test-request-id")
                .loyaltyBankId("test-loyalty-bank-id")
                .points(100)
                .paymentId("test-payment-id");
    }

    @Test
    @DisplayName("Can create valid CreateCapturedTransactionCommand")
    void testCreateCapturedTransactionCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        CreateCapturedTransactionCommand createCapturedTransactionCommand = createCapturedTransactionCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createCapturedTransactionCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create capture transaction with invalid requestId")
    void testCreateCapturedTransactionCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        CreateCapturedTransactionCommand createCapturedTransactionCommand = (CreateCapturedTransactionCommand) createCapturedTransactionCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createCapturedTransactionCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create capture transaction with invalid loyaltyBankId")
    void testCreateCapturedTransactionCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        CreateCapturedTransactionCommand createCapturedTransactionCommand = (CreateCapturedTransactionCommand) createCapturedTransactionCommandBuilder
                .loyaltyBankId(loyaltyBankId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createCapturedTransactionCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidPointsParams")
    @DisplayName("Cannot create capture transaction with invalid points")
    void testCreateCapturedTransactionCommand_whenPointsAreInvalid_shouldThrowException(int points) {
        // Arrange
        CreateCapturedTransactionCommand createCapturedTransactionCommand = (CreateCapturedTransactionCommand) createCapturedTransactionCommandBuilder
                .points(points)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createCapturedTransactionCommand::validate);

        // Assert
        assertEquals(POINTS_CANNOT_BE_LTE_ZERO, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create capture transaction with invalid paymentId")
    void testCreateCapturedTransactionCommand_whenPaymentIdIsInvalid_shouldThrowException(String paymentId) {
        // Arrange
        CreateCapturedTransactionCommand createCapturedTransactionCommand = (CreateCapturedTransactionCommand) createCapturedTransactionCommandBuilder
                .paymentId(paymentId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createCapturedTransactionCommand::validate);

        // Assert
        assertEquals(PAYMENT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    private static Stream<Arguments> invalidStringParams() {
        return TestParameters.invalidStringParams();
    }

    private static Stream<Arguments> invalidPointsParams() {
        return TestParameters.invalidPointsParams();
    }
}