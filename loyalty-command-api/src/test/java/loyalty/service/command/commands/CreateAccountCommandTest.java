package loyalty.service.command.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Test
    @DisplayName("Cannot create account with null requestId")
    void testCreateAccountCommand_whenRequestIdIsNull_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = (CreateAccountCommand) createAccountCommandBuilder
                .requestId(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with empty requestId")
    void testCreateAccountCommand_whenRequestIdIsEmpty_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = (CreateAccountCommand) createAccountCommandBuilder
                .requestId("")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with whitespace requestId")
    void testCreateAccountCommand_whenRequestIdIsWhitespace_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = (CreateAccountCommand) createAccountCommandBuilder
                .requestId(" ")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with null accountId")
    void testCreateAccountCommand_whenAccountIdIsNull_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .accountId(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(ACCOUNT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with empty accountId")
    void testCreateAccountCommand_whenAccountIdIsEmpty_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .accountId("")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(ACCOUNT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with whitespace accountId")
    void testCreateAccountCommand_whenAccountIdIsWhitespace_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .accountId(" ")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(ACCOUNT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with null firstName")
    void testCreateAccountCommand_whenFirstNameIsNull_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .firstName(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(FIRST_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with empty firstName")
    void testCreateAccountCommand_whenFirstNameIsEmpty_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .firstName("")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(FIRST_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with whitespace firstName")
    void testCreateAccountCommand_whenFirstNameIsWhitespace_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .firstName(" ")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(FIRST_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with null lastName")
    void testCreateAccountCommand_whenLastNameIsNull_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .lastName(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(LAST_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with empty lastName")
    void testCreateAccountCommand_whenLastNameIsEmpty_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .lastName("")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(LAST_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with whitespace lastName")
    void testCreateAccountCommand_whenLastNameIsWhitespace_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .lastName(" ")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(LAST_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with null email")
    void testCreateAccountCommand_whenEmailIsNull_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .email(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(EMAIL_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with empty email")
    void testCreateAccountCommand_whenEmailIsEmpty_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .email("")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(EMAIL_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with whitespace email")
    void testCreateAccountCommand_whenEmailIsWhitespace_shouldThrowException() {
        // Arrange
        CreateAccountCommand createAccountCommand = createAccountCommandBuilder
                .email(" ")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createAccountCommand::validate);

        // Assert
        assertEquals(EMAIL_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with invalid email")
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
}