package loyalty.service.command.interceptors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.DeleteAccountCommand;
import loyalty.service.command.commands.UpdateAccountCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.core.exceptions.AccountNotFoundException;
import loyalty.service.core.exceptions.EmailExistsForAccountException;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.List;
import java.util.UUID;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountCommandsInterceptorTest {

    private ListAppender<ILoggingEvent> listAppender;
    public static ILoggingEvent loggingEvent = null;
    public static Logger logger = null;

    @Mock
    private AccountLookupRepository accountLookupRepository;

    @InjectMocks
    private AccountCommandsInterceptor accountCommandsInterceptor;

    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EMAIL = "john@test.com";

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(AccountCommandsInterceptor.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
        listAppender.stop();
    }

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

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
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

        String existingAccountId = UUID.randomUUID().toString();
        AccountLookupEntity existingAccount = new AccountLookupEntity(existingAccountId, TEST_EMAIL);

        when(accountLookupRepository.findByEmail(any(String.class))).thenReturn(existingAccount);

        // Act & Assert
        assertThrows(EmailExistsForAccountException.class, () -> {
            CommandMessage<?> result = accountCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw EmailExistsForAccountException");

        // Assert
        verify(accountLookupRepository, times(1)).findByEmail(TEST_EMAIL);

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());
        String commandName = CreateAccountCommand.class.getSimpleName();

        loggingEvent = loggedEvents.get(0);
        assertEquals(Level.INFO, loggingEvent.getLevel());

        String expectedLogMessage = MessageFormatter.format(EMAIL_FOUND_ON_ANOTHER_ACCOUNT_CANCELLING_COMMAND, existingAccountId, commandName).getMessage();
        assertEquals(expectedLogMessage, loggingEvent.getFormattedMessage());

        assertTrue(loggingEvent.getMarkerList().get(0).contains(Markers.append(REQUEST_ID, command.getRequestId())));
        assertTrue(loggingEvent.getMarkerList().get(0).contains(Markers.append("accountId", existingAccountId)));
        assertTrue(loggingEvent.getMarkerList().get(0).contains(Markers.append("email", command.getEmail())));
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

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
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

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());
        String commandName = UpdateAccountCommand.class.getSimpleName();

        loggingEvent = loggedEvents.get(0);
        assertEquals(Level.INFO, loggingEvent.getLevel());

        String expectedLogMessage = MessageFormatter.format(ACCOUNT_NOT_FOUND_CANCELLING_COMMAND, command.getAccountId(), commandName).getMessage();
        assertEquals(expectedLogMessage, loggingEvent.getFormattedMessage());

        assertTrue(loggingEvent.getMarkerList().get(0).contains(Markers.append(REQUEST_ID, command.getRequestId())));
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
        String existingAccountId = UUID.randomUUID().toString();
        AccountLookupEntity existingEmailAccount = new AccountLookupEntity(existingAccountId, updatedEmail);

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

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());
        String commandName = UpdateAccountCommand.class.getSimpleName();

        loggingEvent = loggedEvents.get(0);
        assertEquals(Level.INFO, loggingEvent.getLevel());

        String expectedLogMessage = MessageFormatter.format(EMAIL_FOUND_ON_ANOTHER_ACCOUNT_CANCELLING_COMMAND, existingAccountId, commandName).getMessage();
        assertEquals(expectedLogMessage, loggingEvent.getFormattedMessage());

        assertTrue(loggingEvent.getMarkerList().get(0).contains(Markers.append(REQUEST_ID, command.getRequestId())));
        assertTrue(loggingEvent.getMarkerList().get(0).contains(Markers.append("accountId", existingAccountId)));
        assertTrue(loggingEvent.getMarkerList().get(0).contains(Markers.append("email", command.getEmail())));
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

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
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

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());
        String commandName = DeleteAccountCommand.class.getSimpleName();

        loggingEvent = loggedEvents.get(0);
        assertEquals(Level.INFO, loggingEvent.getLevel());

        String expectedLogMessage = MessageFormatter.format(ACCOUNT_NOT_FOUND_CANCELLING_COMMAND, command.getAccountId(), commandName).getMessage();
        assertEquals(expectedLogMessage, loggingEvent.getFormattedMessage());

        assertTrue(loggingEvent.getMarkerList().get(0).contains(Markers.append(REQUEST_ID, command.getRequestId())));
    }
}
