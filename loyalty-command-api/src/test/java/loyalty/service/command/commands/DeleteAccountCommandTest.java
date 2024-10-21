package loyalty.service.command.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static org.junit.jupiter.api.Assertions.*;

class DeleteAccountCommandTest {

    private DeleteAccountCommand.DeleteAccountCommandBuilder deleteAccountCommandBuilder;

    @BeforeEach
    void setup() {
        deleteAccountCommandBuilder = DeleteAccountCommand.builder()
                .requestId("test-request-id")
                .accountId("test-account-id");
    }

    @Test
    @DisplayName("Can create valid DeleteAccountCommand")
    void testDeleteAccountCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        DeleteAccountCommand createLoyaltyBankCommand = deleteAccountCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createLoyaltyBankCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot delete account with invalid requestId")
    void testDeleteAccountCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        DeleteAccountCommand createLoyaltyBankCommand = (DeleteAccountCommand) deleteAccountCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot delete account with invalid accountId")
    void testDeleteAccountCommand_whenAccountIdIsInvalid_shouldThrowException(String accountId) {
        // Arrange
        DeleteAccountCommand createLoyaltyBankCommand = deleteAccountCommandBuilder
                .accountId(accountId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(ACCOUNT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    private static Stream<Arguments> invalidStringParams() {
        return Stream.of(
                Arguments.arguments((String) null),
                Arguments.arguments(""),
                Arguments.arguments(" ")
        );
    }
}