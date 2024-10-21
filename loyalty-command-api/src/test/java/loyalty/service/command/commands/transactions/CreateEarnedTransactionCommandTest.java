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

class CreateEarnedTransactionCommandTest {

    private CreateEarnedTransactionCommand.CreateEarnedTransactionCommandBuilder createEarnedTransactionCommandBuilder;

    @BeforeEach
    void setup() {
        createEarnedTransactionCommandBuilder = CreateEarnedTransactionCommand.builder()
                .requestId("test-request-id")
                .loyaltyBankId("test-loyalty-bank-id")
                .points(100);
    }

    @Test
    @DisplayName("Can create valid CreateEarnedTransactionCommand")
    void testCreateEarnedTransactionCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        CreateEarnedTransactionCommand createEarnedTransactionCommand = createEarnedTransactionCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createEarnedTransactionCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create earn transaction with invalid requestId")
    void testCreateEarnedTransactionCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        CreateEarnedTransactionCommand createEarnedTransactionCommand = (CreateEarnedTransactionCommand) createEarnedTransactionCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createEarnedTransactionCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create earn transaction with invalid loyaltyBankId")
    void testCreateEarnedTransactionCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        CreateEarnedTransactionCommand createEarnedTransactionCommand = (CreateEarnedTransactionCommand) createEarnedTransactionCommandBuilder
                .loyaltyBankId(loyaltyBankId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createEarnedTransactionCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidPointsParams")
    @DisplayName("Cannot create earn transaction with invalid points")
    void testCreateEarnedTransactionCommand_whenPointsAreInvalid_shouldThrowException(int points) {
        // Arrange
        CreateEarnedTransactionCommand createEarnedTransactionCommand = (CreateEarnedTransactionCommand) createEarnedTransactionCommandBuilder
                .points(points)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createEarnedTransactionCommand::validate);

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