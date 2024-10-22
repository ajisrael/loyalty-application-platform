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

class CreatePendingTransactionCommandTest {

    private CreatePendingTransactionCommand.CreatePendingTransactionCommandBuilder createPendingTransactionCommandBuilder;

    @BeforeEach
    void setup() {
        createPendingTransactionCommandBuilder = CreatePendingTransactionCommand.builder()
                .requestId("test-request-id")
                .loyaltyBankId("test-loyalty-bank-id")
                .points(100);
    }

    @Test
    @DisplayName("Can create valid CreatePendingTransactionCommand")
    void testCreatePendingTransactionCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        CreatePendingTransactionCommand createPendingTransactionCommand = createPendingTransactionCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createPendingTransactionCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create pending transaction with invalid requestId")
    void testCreatePendingTransactionCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        CreatePendingTransactionCommand createPendingTransactionCommand = (CreatePendingTransactionCommand) createPendingTransactionCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createPendingTransactionCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create pending transaction with invalid loyaltyBankId")
    void testCreatePendingTransactionCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        CreatePendingTransactionCommand createPendingTransactionCommand = (CreatePendingTransactionCommand) createPendingTransactionCommandBuilder
                .loyaltyBankId(loyaltyBankId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createPendingTransactionCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidPointsParams")
    @DisplayName("Cannot create pending transaction with invalid points")
    void testCreatePendingTransactionCommand_whenPointsAreInvalid_shouldThrowException(int points) {
        // Arrange
        CreatePendingTransactionCommand createPendingTransactionCommand = (CreatePendingTransactionCommand) createPendingTransactionCommandBuilder
                .points(points)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createPendingTransactionCommand::validate);

        // Assert
        assertEquals(POINTS_CANNOT_BE_LTE_ZERO, exception.getLocalizedMessage());
    }

    private static Stream<Arguments> invalidStringParams() {
        return TestParameters.invalidStringParams();
    }

    private static Stream<Arguments> invalidPointsParams() {
        return TestParameters.invalidPointsParams();
    }
}