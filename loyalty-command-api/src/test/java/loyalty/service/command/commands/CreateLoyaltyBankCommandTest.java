package loyalty.service.command.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateLoyaltyBankCommandTest {

    private CreateLoyaltyBankCommand.CreateLoyaltyBankCommandBuilder createLoyaltyBankCommandBuilder;

    @BeforeEach
    void setup() {
        createLoyaltyBankCommandBuilder = CreateLoyaltyBankCommand.builder()
                .requestId("test-request-id")
                .accountId("test-account-id")
                .loyaltyBankId("test-loyalty-bank-id")
                .businessId("test-business-id");
    }

    @Test
    @DisplayName("Can create valid CreateLoyaltyBankCommand")
    void testCreateLoyaltyBankCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createLoyaltyBankCommand::validate);
    }

    @Test
    @DisplayName("Cannot create account with null requestId")
    void testCreateLoyaltyBankCommand_whenRequestIdIsNull_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = (CreateLoyaltyBankCommand) createLoyaltyBankCommandBuilder
                .requestId(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with empty requestId")
    void testCreateLoyaltyBankCommand_whenRequestIdIsEmpty_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = (CreateLoyaltyBankCommand) createLoyaltyBankCommandBuilder
                .requestId("")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with whitespace requestId")
    void testCreateLoyaltyBankCommand_whenRequestIdIsWhitespace_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = (CreateLoyaltyBankCommand) createLoyaltyBankCommandBuilder
                .requestId(" ")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with null loyaltyBankId")
    void testCreateLoyaltyBankCommand_whenLoyaltyBankIdIsNull_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .loyaltyBankId(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with empty loyaltyBankId")
    void testCreateLoyaltyBankCommand_whenLoyaltyBankIdIsEmpty_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .loyaltyBankId("")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with whitespace loyaltyBankId")
    void testCreateLoyaltyBankCommand_whenLoyaltyBankIdIsWhitespace_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .loyaltyBankId(" ")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with null accountId")
    void testCreateLoyaltyBankCommand_whenAccountIdIsNull_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .accountId(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(ACCOUNT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with empty accountId")
    void testCreateLoyaltyBankCommand_whenAccountIdIsEmpty_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .accountId("")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(ACCOUNT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with whitespace accountId")
    void testCreateLoyaltyBankCommand_whenAccountIdIsWhitespace_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .accountId(" ")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(ACCOUNT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with null businessId")
    void testCreateLoyaltyBankCommand_whenBusinessIdIsNull_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .businessId(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(BUSINESS_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with empty businessId")
    void testCreateLoyaltyBankCommand_whenBusinessIdIsEmpty_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .businessId("")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(BUSINESS_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Cannot create account with whitespace businessId")
    void testCreateLoyaltyBankCommand_whenBusinessIdIsWhitespace_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .businessId(" ")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(BUSINESS_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }
}