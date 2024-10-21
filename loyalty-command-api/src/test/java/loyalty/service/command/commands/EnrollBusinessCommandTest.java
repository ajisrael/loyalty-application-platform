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

class EnrollBusinessCommandTest {

    private EnrollBusinessCommand.EnrollBusinessCommandBuilder enrollBusinessCommandBuilder;

    @BeforeEach
    void setup() {
        enrollBusinessCommandBuilder = EnrollBusinessCommand.builder()
                .requestId("test-request-id")
                .businessId("test-business-id")
                .businessName("test-business-name");
    }

    @Test
    @DisplayName("Can create valid EnrollBusinessCommand")
    void testEnrollBusinessCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        EnrollBusinessCommand createLoyaltyBankCommand = enrollBusinessCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createLoyaltyBankCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot enroll business with invalid requestId")
    void testEnrollBusinessCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        EnrollBusinessCommand createLoyaltyBankCommand = (EnrollBusinessCommand) enrollBusinessCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot enroll business with invalid businessId")
    void testEnrollBusinessCommand_whenBusinessIdIsInvalid_shouldThrowException(String businessId) {
        // Arrange
        EnrollBusinessCommand createLoyaltyBankCommand = enrollBusinessCommandBuilder
                .businessId(businessId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(BUSINESS_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot enroll business with invalid businessName")
    void testEnrollBusinessCommand_whenBusinessNameIsInvalid_shouldThrowException(String businessName) {
        // Arrange
        EnrollBusinessCommand createLoyaltyBankCommand = enrollBusinessCommandBuilder
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