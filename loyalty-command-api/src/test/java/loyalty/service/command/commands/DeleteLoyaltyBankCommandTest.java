package loyalty.service.command.commands;

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

class DeleteLoyaltyBankCommandTest {

    private DeleteLoyaltyBankCommand.DeleteLoyaltyBankCommandBuilder deleteLoyaltyBankCommandBuilder;

    @BeforeEach
    void setup() {
        deleteLoyaltyBankCommandBuilder = DeleteLoyaltyBankCommand.builder()
                .requestId("test-request-id")
                .loyaltyBankId("test-loyaltyBank-id");
    }

    @Test
    @DisplayName("Can create valid DeleteLoyaltyBankCommand")
    void testDeleteLoyaltyBankCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        DeleteLoyaltyBankCommand createLoyaltyBankCommand = deleteLoyaltyBankCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createLoyaltyBankCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot delete loyaltyBank with invalid requestId")
    void testDeleteLoyaltyBankCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        DeleteLoyaltyBankCommand createLoyaltyBankCommand = (DeleteLoyaltyBankCommand) deleteLoyaltyBankCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot delete loyaltyBank with invalid loyaltyBankId")
    void testDeleteLoyaltyBankCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        DeleteLoyaltyBankCommand createLoyaltyBankCommand = deleteLoyaltyBankCommandBuilder
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