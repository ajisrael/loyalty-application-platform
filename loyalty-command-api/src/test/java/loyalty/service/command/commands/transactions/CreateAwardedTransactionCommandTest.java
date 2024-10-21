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

class CreateAwardedTransactionCommandTest {

    private CreateAwardedTransactionCommand.CreateAwardedTransactionCommandBuilder createAwardedTransactionCommandBuilder;

    @BeforeEach
    void setup() {
        createAwardedTransactionCommandBuilder = CreateAwardedTransactionCommand.builder()
                .requestId("test-request-id")
                .loyaltyBankId("test-loyalty-bank-id")
                .points(100);
    }

    @Test
    @DisplayName("Can create valid CreateAwardedTransactionCommand")
    void testCreateAwardedTransactionCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        CreateAwardedTransactionCommand createAwardedTransactionCommand = createAwardedTransactionCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createAwardedTransactionCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create award transaction with invalid requestId")
    void testCreateAwardedTransactionCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        CreateAwardedTransactionCommand createAwardedTransactionCommand = (CreateAwardedTransactionCommand) createAwardedTransactionCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAwardedTransactionCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create award transaction with invalid loyaltyBankId")
    void testCreateAwardedTransactionCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        CreateAwardedTransactionCommand createAwardedTransactionCommand = (CreateAwardedTransactionCommand) createAwardedTransactionCommandBuilder
                .loyaltyBankId(loyaltyBankId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAwardedTransactionCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidPointsParams")
    @DisplayName("Cannot create award transaction with invalid points")
    void testCreateAwardedTransactionCommand_whenPointsAreInvalid_shouldThrowException(int points) {
        // Arrange
        CreateAwardedTransactionCommand createAwardedTransactionCommand = (CreateAwardedTransactionCommand) createAwardedTransactionCommandBuilder
                .points(points)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAwardedTransactionCommand::validate);

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