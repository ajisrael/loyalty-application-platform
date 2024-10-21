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

class CreateAccountCommandTest {

    private CreateAccountCommand.CreateAccountCommandBuilder createAccountCommandBuilder;

    @BeforeEach
    void setup() {
        createAccountCommandBuilder = CreateAccountCommand.builder()
                .requestId("test-request-id")
                .accountId("test-account-id")
                .firstName("John")
                .lastName("Doe")
                .email("test@test.com");
    }

    @Test
    @DisplayName("Can create valid CreateAccountCommand")
    void testCreateAccountCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createAccountCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create account with invalid requestId")
    void testCreateAccountCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        CreateAccountCommand createAccountCommand = (CreateAccountCommand) createAccountCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create account with invalid accountId")
    void testCreateAccountCommand_whenAccountIdIsInvalid_shouldThrowException(String accountId) {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .accountId(accountId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(ACCOUNT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create account with invalid firstName")
    void testCreateAccountCommand_whenFirstNameIsInvalid_shouldThrowException(String firstName) {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .firstName(firstName)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(FIRST_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create account with invalid lastName")
    void testCreateAccountCommand_whenLastNameIsInvalid_shouldThrowException(String lastName) {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .lastName(lastName)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(LAST_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create account with invalid email")
    void testCreateAccountCommand_whenEmailIsInvalid_shouldThrowException(String email) {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .email(email)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(EMAIL_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with improperly formatted email")
    void testCreateAccountCommand_whenEmailFormatIsInvalid_shouldThrowException() {
        // Arrange
        String invalidEmailFormat = "not-a-valid-email-format";
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .email(invalidEmailFormat)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(String.format(INVALID_EMAIL_FORMAT, invalidEmailFormat), exception.getLocalizedMessage());
    }
    private static Stream<Arguments> invalidStringParams() {
        return Stream.of(
                Arguments.arguments((String) null),
                Arguments.arguments(""),
                Arguments.arguments(" ")
        );
    }
}