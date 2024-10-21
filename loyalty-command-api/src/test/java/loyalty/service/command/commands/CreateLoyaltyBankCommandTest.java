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

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create loyalty bank with invalid requestId")
    void testCreateLoyaltyBankCommand_whenRequestIdIsWhitespace_shouldThrowException(String requestId) {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = (CreateLoyaltyBankCommand) createLoyaltyBankCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create loyalty bank with invalid loyaltyBankId")
    void testCreateLoyaltyBankCommand_whenLoyaltyBankIdIsInvalid_shouldThrowException(String loyaltyBankId) {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .loyaltyBankId(loyaltyBankId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(LOYALTY_BANK_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create loyalty bank with invalid accountId")
    void testCreateLoyaltyBankCommand_whenAccountIdIsInvalid_shouldThrowException(String accountId) {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .accountId(accountId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(ACCOUNT_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create loyalty bank with invalid businessId")
    void testCreateLoyaltyBankCommand_whenBusinessIdIsInvalid_shouldThrowException(String businessId) {
        // Arrange
        CreateLoyaltyBankCommand createLoyaltyBankCommand = createLoyaltyBankCommandBuilder
                .businessId(businessId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(BUSINESS_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    private static Stream<Arguments> invalidStringParams() {
        return Stream.of(
                Arguments.arguments((String) null),
                Arguments.arguments(""),
                Arguments.arguments(" ")
        );
    }
}