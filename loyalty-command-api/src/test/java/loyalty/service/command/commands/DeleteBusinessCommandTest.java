package loyalty.service.command.commands;

import loyalty.service.command.test.utils.TestParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static loyalty.service.core.constants.ExceptionMessages.BUSINESS_ID_CANNOT_BE_EMPTY;
import static loyalty.service.core.constants.ExceptionMessages.REQUEST_ID_CANNOT_BE_EMPTY;
import static org.junit.jupiter.api.Assertions.*;

class DeleteBusinessCommandTest {

    private DeleteBusinessCommand.DeleteBusinessCommandBuilder deleteBusinessCommandBuilder;

    @BeforeEach
    void setup() {
        deleteBusinessCommandBuilder = DeleteBusinessCommand.builder()
                .requestId("test-request-id")
                .businessId("test-business-id");
    }

    @Test
    @DisplayName("Can create valid DeleteBusinessCommand")
    void testDeleteBusinessCommand_whenParametersAreValid_shouldPassValidation() {
        // Arrange
        DeleteBusinessCommand createLoyaltyBankCommand = deleteBusinessCommandBuilder.build();

        // Act & Assert
        assertDoesNotThrow(createLoyaltyBankCommand::validate);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot delete business with invalid requestId")
    void testDeleteBusinessCommand_whenRequestIdIsInvalid_shouldThrowException(String requestId) {
        // Arrange
        DeleteBusinessCommand createLoyaltyBankCommand = (DeleteBusinessCommand) deleteBusinessCommandBuilder
                .requestId(requestId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(REQUEST_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidStringParams")
    @DisplayName("Cannot delete business with invalid businessId")
    void testDeleteBusinessCommand_whenBusinessIdIsInvalid_shouldThrowException(String businessId) {
        // Arrange
        DeleteBusinessCommand createLoyaltyBankCommand = deleteBusinessCommandBuilder
                .businessId(businessId)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, createLoyaltyBankCommand::validate);

        // Assert
        assertEquals(BUSINESS_ID_CANNOT_BE_EMPTY, exception.getLocalizedMessage());
    }

    private static Stream<Arguments> invalidStringParams() {
        return TestParameters.invalidStringParams();
    }
}