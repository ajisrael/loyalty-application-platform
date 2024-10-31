package loyalty.service.command.interceptors;

import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.DeleteAccountCommand;
import loyalty.service.command.commands.UpdateAccountCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.core.exceptions.AccountNotFoundException;
import loyalty.service.core.exceptions.EmailExistsForAccountException;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountCommandsInterceptorTest {

    @Mock
    private AccountLookupRepository accountLookupRepository;

    @InjectMocks
    private AccountCommandsInterceptor accountCommandsInterceptor;

    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EMAIL = "john@test.com";

    @Test
    @DisplayName("Can handle valid CreateAccountCommand")
    void testHandle_whenCreateAccountCommandHandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        CreateAccountCommand command = CreateAccountCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .build();

        when(accountLookupRepository.findByEmail(any(String.class))).thenReturn(null);

        // Act
        CommandMessage<?> result = accountCommandsInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
        verify(accountLookupRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("Can handle invalid CreateAccountCommand for already existing email")
    void testHandle_whenCreateAccountCommandHandledAndEmailAlreadyExists_shouldThrowException() {
        // Arrange
        CreateAccountCommand command = CreateAccountCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .build();

        AccountLookupEntity existingAccount = new AccountLookupEntity(UUID.randomUUID().toString(), TEST_EMAIL);

        when(accountLookupRepository.findByEmail(any(String.class))).thenReturn(existingAccount);

        // Act & Assert
        assertThrows(EmailExistsForAccountException.class, () -> {
            CommandMessage<?> result = accountCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw EmailExistsForAccountException");

        // Assert
        verify(accountLookupRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("Can handle valid UpdateAccountCommand")
    void testHandle_whenUpdateAccountCommandHandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        UpdateAccountCommand command = UpdateAccountCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .build();

        AccountLookupEntity existingAccount = new AccountLookupEntity(TEST_ACCOUNT_ID, TEST_EMAIL);

        when(accountLookupRepository.findByAccountId(any(String.class))).thenReturn(existingAccount);
        when(accountLookupRepository.findByEmail(any(String.class))).thenReturn(null);

        // Act
        CommandMessage<?> result = accountCommandsInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(accountLookupRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("Can handle invalid UpdateAccountCommand for non existing account")
    void testHandle_whenUpdateAccountCommandHandledAndAccountDoesNotExist_shouldThrowException() {
        // Arrange
        UpdateAccountCommand command = UpdateAccountCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .build();

        when(accountLookupRepository.findByAccountId(any(String.class))).thenReturn(null);

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            CommandMessage<?> result = accountCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw AccountNotFoundException");

        // Assert
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(accountLookupRepository, times(0)).findByEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("Can handle invalid UpdateAccountCommand for already existing email")
    void testHandle_whenUpdateAccountCommandHandledAndEmailAlreadyExistsOnAnotherAccount_shouldThrowException() {
        // Arrange
        String updatedEmail = "test@test.com";
        UpdateAccountCommand command = UpdateAccountCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(updatedEmail)
                .build();

        AccountLookupEntity existingAccount = new AccountLookupEntity(TEST_ACCOUNT_ID, TEST_EMAIL);
        AccountLookupEntity existingEmailAccount = new AccountLookupEntity(UUID.randomUUID().toString(), updatedEmail);

        when(accountLookupRepository.findByAccountId(any(String.class))).thenReturn(existingAccount);
        when(accountLookupRepository.findByEmail(any(String.class))).thenReturn(existingEmailAccount);

        // Act & Assert
        assertThrows(EmailExistsForAccountException.class, () -> {
            CommandMessage<?> result = accountCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw EmailExistsForAccountException");

        // Assert
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(accountLookupRepository, times(1)).findByEmail(updatedEmail);
    }

    @Test
    @DisplayName("Can handle valid DeleteAccountCommand")
    void testHandle_whenDeleteAccountCommandHandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        DeleteAccountCommand command = DeleteAccountCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .build();

        AccountLookupEntity existingAccount = new AccountLookupEntity(TEST_ACCOUNT_ID, TEST_EMAIL);

        when(accountLookupRepository.findByAccountId(any(String.class))).thenReturn(existingAccount);

        // Act
        CommandMessage<?> result = accountCommandsInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
    }

    @Test
    @DisplayName("Can handle invalid DeleteAccountCommand for non existing account")
    void testHandle_whenDeleteAccountCommandHandledAndAccountDoesNotExist_shouldThrowException() {
        // Arrange
        DeleteAccountCommand command = DeleteAccountCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .build();

        when(accountLookupRepository.findByAccountId(any(String.class))).thenReturn(null);

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            CommandMessage<?> result = accountCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw AccountNotFoundException");

        // Assert
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
    }
}