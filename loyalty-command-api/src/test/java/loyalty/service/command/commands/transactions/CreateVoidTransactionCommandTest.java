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

class CreateVoidTransactionCommandTest {

    private CreateVoidTransactionCommand.CreateVoidTransactionCommandBuilder createVoidTransactionCommandBuilder;

    @BeforeEach
    void setup() {
        createVoidTransactionCommandBuilder = CreateVoidTransactionCommand.builder()
                .requestId("test-request-id")
                .loyaltyBankId("test-loyalty-bank-id")
                .points(100)
                .paymentId("test-payment-id");
    }

    @Test
    @DisplayName("Can create valid CreateVoidTransactionCommand")
    void testCreateVoidTransactionCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        CreateVoidTransactionCommand createVoidTransactionCommand = createVoidTransactionCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createVoidTransactionCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create void transaction with invalid requestId")
    void testCreateVoidTransactionCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        CreateVoidTransactionCommand createVoidTransactionCommand = (CreateVoidTransactionCommand) createVoidTransactionCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createVoidTransactionCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create void transaction with invalid loyaltyBankId")
    void testCreateVoidTransactionCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        CreateVoidTransactionCommand createVoidTransactionCommand = (CreateVoidTransactionCommand) createVoidTransactionCommandBuilder
                .loyaltyBankId(loyaltyBankId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createVoidTransactionCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidPointsParams")
    @DisplayName("Cannot create void transaction with invalid points")
    void testCreateVoidTransactionCommand_whenPointsAreInvalid_shouldThrowException(int points) {
        // Arrange
        CreateVoidTransactionCommand createVoidTransactionCommand = (CreateVoidTransactionCommand) createVoidTransactionCommandBuilder
                .points(points)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createVoidTransactionCommand::validate);

        // Assert
        assertEquals(POINTS_CANNOT_BE_LTE_ZERO, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create void transaction with invalid paymentId")
    void testCreateVoidTransactionCommand_whenPaymentIdIsInvalid_shouldThrowException(String paymentId) {
        // Arrange
        CreateVoidTransactionCommand createVoidTransactionCommand = (CreateVoidTransactionCommand) createVoidTransactionCommandBuilder
                .paymentId(paymentId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createVoidTransactionCommand::validate);

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