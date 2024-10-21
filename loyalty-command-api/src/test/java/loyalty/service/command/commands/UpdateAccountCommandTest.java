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
import static loyalty.service.core.constants.ExceptionMessages.INVALID_EMAIL_FORMAT;
import static org.junit.jupiter.api.Assertions.*;

class UpdateAccountCommandTest {

    private UpdateAccountCommand.UpdateAccountCommandBuilder updateAccountCommandBuilder;

    @BeforeEach
    void setup() {
        updateAccountCommandBuilder = UpdateAccountCommand.builder()
                .requestId("test-request-id")
                .accountId("test-account-id")
                .firstName("John")
                .lastName("Doe")
                .email("test@test.com");
    }

    @Test
    @DisplayName("Can create valid UpdateAccountCommand")
    void testUpdateAccountCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        UpdateAccountCommand updateAccountCommand = updateAccountCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(updateAccountCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot update account with invalid requestId")
    void testUpdateAccountCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        UpdateAccountCommand updateAccountCommand = (UpdateAccountCommand) updateAccountCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, updateAccountCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot update account with invalid accountId")
    void testUpdateAccountCommand_whenAccountIdIsInvalid_shouldThrowException(String accountId) {
        // Arrange
        UpdateAccountCommand updateAccountCommand = updateAccountCommandBuilder
                .accountId(accountId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, updateAccountCommand::validate);

        // Assert
        assertEquals(ACCOUNT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot update account with invalid firstName")
    void testUpdateAccountCommand_whenFirstNameIsInvalid_shouldThrowException(String firstName) {
        // Arrange
        UpdateAccountCommand updateAccountCommand = updateAccountCommandBuilder
                .firstName(firstName)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, updateAccountCommand::validate);

        // Assert
        assertEquals(FIRST_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot update account with invalid lastName")
    void testUpdateAccountCommand_whenLastNameIsInvalid_shouldThrowException(String lastName) {
        // Arrange
        UpdateAccountCommand updateAccountCommand = updateAccountCommandBuilder
                .lastName(lastName)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, updateAccountCommand::validate);

        // Assert
        assertEquals(LAST_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot update account with invalid email")
    void testUpdateAccountCommand_whenEmailIsInvalid_shouldThrowException(String email) {
        // Arrange
        UpdateAccountCommand updateAccountCommand = updateAccountCommandBuilder
                .email(email)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, updateAccountCommand::validate);

        // Assert
        assertEquals(EMAIL_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot update account with improperly formatted email")
    void testUpdateAccountCommand_whenEmailFormatIsInvalid_shouldThrowException() {
        // Arrange
        String invalidEmailFormat = "not-a-valid-email-format";
        UpdateAccountCommand updateAccountCommand = updateAccountCommandBuilder
                .email(invalidEmailFormat)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, updateAccountCommand::validate);

        // Assert
        assertEquals(String.format(INVALID_EMAIL_FORMAT, invalidEmailFormat), exception.getLocalizedMessage());
    }

    private static Stream<Arguments> invalidStringParams() {
        return TestParameters.invalidStringParams();
    }
}