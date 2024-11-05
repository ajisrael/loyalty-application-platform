package loyalty.service.command.interceptors;

import loyalty.service.command.commands.AbstractCommand;
import loyalty.service.command.commands.CreateAccountCommand;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateCommandInterceptorTest {

    @InjectMocks
    private ValidateCommandInterceptor validateCommandInterceptor;

    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EMAIL = "john@test.com";

    @Test
    @DisplayName("Can handle valid AbstractCommand")
    void testHandle_whenAbstractCommandHandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        CreateAccountCommand command = CreateAccountCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .build();

        // Act
        CommandMessage<?> result = validateCommandInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
    }

    @Test
    @DisplayName("Can handle invalid AbstractCommand and throw exception")
    void testHandle_whenAbstractCommandHandledAndCommandIsInvalid_shouldThrowException() {
        // Arrange
        AbstractCommand command = mock(AbstractCommand.class);
        doThrow(new IllegalArgumentException("Invalid command")).when(command).validate();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            validateCommandInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw IllegalArgumentException for invalid command");

        // Verify validation was called
        verify(command, times(1)).validate();
    }
}
