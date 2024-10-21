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

class UnenrollLoyaltyBankCommandTest {

    private UnenrollLoyaltyBankCommand.UnenrollLoyaltyBankCommandBuilder unenrollLoyaltyBankCommandBuilder;

    @BeforeEach
    void setup() {
        unenrollLoyaltyBankCommandBuilder = UnenrollLoyaltyBankCommand.builder()
                .requestId("test-request-id")
                .loyaltyBankId("test-loyaltyBank-id");
    }

    @Test
    @DisplayName("Can create valid UnenrollLoyaltyBankCommand")
    void testUnenrollLoyaltyBankCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        UnenrollLoyaltyBankCommand createLoyaltyBankCommand = unenrollLoyaltyBankCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createLoyaltyBankCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot unenroll loyaltyBank with invalid requestId")
    void testUnenrollLoyaltyBankCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        UnenrollLoyaltyBankCommand createLoyaltyBankCommand = (UnenrollLoyaltyBankCommand) unenrollLoyaltyBankCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot unenroll loyaltyBank with invalid loyaltyBankId")
    void testUnenrollLoyaltyBankCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        UnenrollLoyaltyBankCommand createLoyaltyBankCommand = unenrollLoyaltyBankCommandBuilder
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