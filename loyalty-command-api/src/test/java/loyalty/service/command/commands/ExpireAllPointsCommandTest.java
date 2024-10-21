package loyalty.service.command.commands;

import loyalty.service.command.test.utils.TestParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_ID_CANNOT_BE_EMPTY;
import static loyalty.service.core.constants.ExceptionMessages.REQUEST_ID_CANNOT_BE_EMPTY;
import static org.junit.jupiter.api.Assertions.*;

class ExpireAllPointsCommandTest {

    private ExpireAllPointsCommand.ExpireAllPointsCommandBuilder expireAllPointsCommandBuilder;

    @BeforeEach
    void setup() {
        expireAllPointsCommandBuilder = ExpireAllPointsCommand.builder()
                .requestId("test-request-id")
                .loyaltyBankId("test-loyaltyBank-id");
    }

    @Test
    @DisplayName("Can create valid ExpireAllPointsCommand")
    void testExpireAllPointsCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        ExpireAllPointsCommand createLoyaltyBankCommand = expireAllPointsCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createLoyaltyBankCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot expire all points in loyaltyBank with invalid requestId")
    void testExpireAllPointsCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        ExpireAllPointsCommand createLoyaltyBankCommand = (ExpireAllPointsCommand) expireAllPointsCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot expire all points in loyaltyBank with invalid loyaltyBankId")
    void testExpireAllPointsCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        ExpireAllPointsCommand createLoyaltyBankCommand = expireAllPointsCommandBuilder
                .loyaltyBankId(loyaltyBankId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    private static Stream<Arguments> invalidStringParams() {
        return TestParameters.invalidStringParams();
    }
}