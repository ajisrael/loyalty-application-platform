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

class CreateExpirePointsTransactionCommandTest {

    private CreateExpirePointsTransactionCommand.CreateExpirePointsTransactionCommandBuilder createExpirePointsTransactionCommandBuilder;

    @BeforeEach
    void setup() {
        createExpirePointsTransactionCommandBuilder = CreateExpirePointsTransactionCommand.builder()
                .requestId("test-request-id")
                .loyaltyBankId("test-loyalty-bank-id")
                .points(100)
                .targetTransactionId("test-target-transaction-id");
    }

    @Test
    @DisplayName("Can create valid CreateExpirePointsTransactionCommand")
    void testCreateExpirePointsTransactionCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        CreateExpirePointsTransactionCommand createExpirePointsTransactionCommand = createExpirePointsTransactionCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createExpirePointsTransactionCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create expire points transaction with invalid requestId")
    void testCreateExpirePointsTransactionCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        CreateExpirePointsTransactionCommand createExpirePointsTransactionCommand = (CreateExpirePointsTransactionCommand) createExpirePointsTransactionCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createExpirePointsTransactionCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create expire points transaction with invalid loyaltyBankId")
    void testCreateExpirePointsTransactionCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        CreateExpirePointsTransactionCommand createExpirePointsTransactionCommand = (CreateExpirePointsTransactionCommand) createExpirePointsTransactionCommandBuilder
                .loyaltyBankId(loyaltyBankId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createExpirePointsTransactionCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidPointsParams")
    @DisplayName("Cannot create expire points transaction with invalid points")
    void testCreateExpirePointsTransactionCommand_whenPointsAreInvalid_shouldThrowException(int points) {
        // Arrange
        CreateExpirePointsTransactionCommand createExpirePointsTransactionCommand = (CreateExpirePointsTransactionCommand) createExpirePointsTransactionCommandBuilder
                .points(points)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createExpirePointsTransactionCommand::validate);

        // Assert
        assertEquals(POINTS_CANNOT_BE_LTE_ZERO, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create expire points transaction with invalid targetTransactionId")
    void testCreateExpirePointsTransactionCommand_whenTargetTransactionIdIsInvalid_shouldThrowException(String targetTransactionId) {
        // Arrange
        CreateExpirePointsTransactionCommand createExpirePointsTransactionCommand = createExpirePointsTransactionCommandBuilder
                .targetTransactionId(targetTransactionId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createExpirePointsTransactionCommand::validate);

        // Assert
        assertEquals(TARGET_TRANSACTION_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    private static Stream<Arguments> invalidStringParams() {
        return TestParameters.invalidStringParams();
    }

    private static Stream<Arguments> invalidPointsParams() {
        return TestParameters.invalidPointsParams();
    }
}