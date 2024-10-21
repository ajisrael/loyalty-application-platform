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
import static org.junit.jupiter.api.Assertions.*;

class CreateAuthorizedTransactionCommandTest {

    private CreateAuthorizedTransactionCommand.CreateAuthorizedTransactionCommandBuilder createAuthorizedTransactionCommandBuilder;

    @BeforeEach
    void setup() {
        createAuthorizedTransactionCommandBuilder = CreateAuthorizedTransactionCommand.builder()
                .requestId("test-request-id")
                .loyaltyBankId("test-loyalty-bank-id")
                .points(100)
                .paymentId("test-payment-id");
    }

    @Test
    @DisplayName("Can create valid CreateAuthorizedTransactionCommand")
    void testCreateAuthorizedTransactionCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        CreateAuthorizedTransactionCommand createAuthorizedTransactionCommand = createAuthorizedTransactionCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createAuthorizedTransactionCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create authorize transaction with invalid requestId")
    void testCreateAuthorizedTransactionCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        CreateAuthorizedTransactionCommand createAuthorizedTransactionCommand = (CreateAuthorizedTransactionCommand) createAuthorizedTransactionCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAuthorizedTransactionCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create authorize transaction with invalid loyaltyBankId")
    void testCreateAuthorizedTransactionCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        CreateAuthorizedTransactionCommand createAuthorizedTransactionCommand = (CreateAuthorizedTransactionCommand) createAuthorizedTransactionCommandBuilder
                .loyaltyBankId(loyaltyBankId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAuthorizedTransactionCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidPointsParams")
    @DisplayName("Cannot create authorize transaction with invalid points")
    void testCreateAuthorizedTransactionCommand_whenPointsAreInvalid_shouldThrowException(int points) {
        // Arrange
        CreateAuthorizedTransactionCommand createAuthorizedTransactionCommand = (CreateAuthorizedTransactionCommand) createAuthorizedTransactionCommandBuilder
                .points(points)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAuthorizedTransactionCommand::validate);

        // Assert
        assertEquals(POINTS_CANNOT_BE_LTE_ZERO, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create authorize transaction with invalid paymentId")
    void testCreateAuthorizedTransactionCommand_whenPaymentIdIsInvalid_shouldThrowException(String paymentId) {
        // Arrange
        CreateAuthorizedTransactionCommand createAuthorizedTransactionCommand = (CreateAuthorizedTransactionCommand) createAuthorizedTransactionCommandBuilder
                .paymentId(paymentId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAuthorizedTransactionCommand::validate);

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