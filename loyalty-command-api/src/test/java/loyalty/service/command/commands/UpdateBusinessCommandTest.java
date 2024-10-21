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

class UpdateBusinessCommandTest {

    private UpdateBusinessCommand.UpdateBusinessCommandBuilder updateBusinessCommandBuilder;

    @BeforeEach
    void setup() {
        updateBusinessCommandBuilder = UpdateBusinessCommand.builder()
                .requestId("test-request-id")
                .businessId("test-business-id")
                .businessName("test-business-name");
    }

    @Test
    @DisplayName("Can create valid UpdateBusinessCommand")
    void testUpdateBusinessCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        UpdateBusinessCommand createLoyaltyBankCommand = updateBusinessCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createLoyaltyBankCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot update business with invalid requestId")
    void testUpdateBusinessCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        UpdateBusinessCommand createLoyaltyBankCommand = (UpdateBusinessCommand) updateBusinessCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot update business with invalid businessId")
    void testUpdateBusinessCommand_whenBusinessIdIsInvalid_shouldThrowException(String businessId) {
        // Arrange
        UpdateBusinessCommand createLoyaltyBankCommand = updateBusinessCommandBuilder
                .businessId(businessId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(BUSINESS_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot update business with invalid businessName")
    void testUpdateBusinessCommand_whenBusinessNameIsInvalid_shouldThrowException(String businessName) {
        // Arrange
        UpdateBusinessCommand createLoyaltyBankCommand = updateBusinessCommandBuilder
                .businessName(businessName)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(BUSINESS_NAME_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    private static Stream<Arguments> invalidStringParams() {
        return TestParameters.invalidStringParams();
    }
}