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

class CreateBusinessCommandTest {

    private CreateBusinessCommand.CreateBusinessCommandBuilder createBusinessCommandBuilder;

    @BeforeEach
    void setup() {
        createBusinessCommandBuilder = CreateBusinessCommand.builder()
                .requestId("test-request-id")
                .businessId("test-business-id")
                .businessName("test-business-name");
    }

    @Test
    @DisplayName("Can create valid CreateBusinessCommand")
    void testCreateBusinessCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        CreateBusinessCommand createLoyaltyBankCommand = createBusinessCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createLoyaltyBankCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create business with invalid requestId")
    void testCreateBusinessCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        CreateBusinessCommand createLoyaltyBankCommand = (CreateBusinessCommand) createBusinessCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create business with invalid businessId")
    void testCreateBusinessCommand_whenBusinessIdIsInvalid_shouldThrowException(String businessId) {
        // Arrange
        CreateBusinessCommand createLoyaltyBankCommand = createBusinessCommandBuilder
                .businessId(businessId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(BUSINESS_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot create business with invalid businessName")
    void testCreateBusinessCommand_whenBusinessNameIsInvalid_shouldThrowException(String businessName) {
        // Arrange
        CreateBusinessCommand createLoyaltyBankCommand = createBusinessCommandBuilder
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