package loyalty.service.command.projections;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.core.events.account.AccountCreatedEvent;
import loyalty.service.core.events.account.AccountDeletedEvent;
import loyalty.service.core.events.account.AccountEmailChangedEvent;
import loyalty.service.core.exceptions.IllegalProjectionStateException;
import net.logstash.logback.marker.Markers;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import java.util.List;
import java.util.UUID;

import static loyalty.service.command.test.utils.LogTestHelper.assertLogMessage;
import static loyalty.service.command.test.utils.LogTestHelper.assertLogMessageWithMarkers;
import static loyalty.service.core.constants.DomainConstants.*;
import static loyalty.service.core.constants.ExceptionMessages.ACCOUNT_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.constants.LogMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountLookupEventsHandlerTest {

    private ListAppender<ILoggingEvent> listAppender;
    public static Logger logger = null;

    @Mock
    private AccountLookupRepository accountLookupRepository;

    @Mock
    private SmartValidator validator;

    @InjectMocks
    AccountLookupEventsHandler accountLookupEventsHandler;

    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_EMAIL = "test@test.com";

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(AccountLookupEventsHandler.class);
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
    @DisplayName("Logs IllegalArgumentExceptions")
    void testHandleException_whenIllegalArgumentExceptionReceived_shouldLogError() {
        // Arrange
        String testMessage = "some error";
        IllegalArgumentException exception = new IllegalArgumentException(testMessage);

        // Act
        accountLookupEventsHandler.handle(exception);

        // Assert
        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());
        assertLogMessage(loggedEvents.get(0), Level.ERROR, testMessage);
    }

    @Test
    @DisplayName("Logs IllegalProjectionStateExceptions")
    void testHandleException_whenIllegalProjectionStateExceptionReceived_shouldLogError() {
        // Arrange
        String testMessage = "some error";
        IllegalProjectionStateException exception = new IllegalProjectionStateException(testMessage);

        // Act
        accountLookupEventsHandler.handle(exception);

        // Assert
        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());
        assertLogMessage(loggedEvents.get(0), Level.ERROR, testMessage);
    }

    @Test
    @DisplayName("Can save new AccountLookupEntity on valid AccountCreatedEvent")
    void testOn_whenValidAccountCreatedEventReceived_shouldSaveAccountLookupEntity() {
        // Arrange
        AccountCreatedEvent event = AccountCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName("John")
                .lastName("Doe")
                .email(TEST_EMAIL)
                .build();

        // Act
        accountLookupEventsHandler.on(event);

        // Assert
        verify(accountLookupRepository, times(1)).save(any(AccountLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.format(ACCOUNT_SAVED_IN_LOOKUP_DB, TEST_ACCOUNT_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(ACCOUNT_ID, event.getAccountId()),
                Markers.append(EMAIL, event.getEmail())
        );
    }

    @Test
    @DisplayName("Cannot save new AccountLookupEntity on invalid AccountCreatedEvent")
    void testOn_whenInvalidAccountCreatedEventReceived_shouldThrowException() {
        // Arrange
        AccountCreatedEvent event = AccountCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName("John")
                .lastName("Doe")
                .email("invalid-email") // Doesn't actually matter as the mock invocation is what triggers the exception
                .build();

        String exceptionMessage = "Invalid email format";

        doAnswer(invocation -> {
            BindingResult bindingResult = invocation.getArgument(1);
            bindingResult.rejectValue("email", "error.invalid", exceptionMessage);
            return null;
        }).when(validator).validate(any(AccountLookupEntity.class), any(BindingResult.class));

        // Act & Assert
        IllegalProjectionStateException exception = assertThrows(IllegalProjectionStateException.class, () -> {
            accountLookupEventsHandler.on(event);
        });

        // Assert
        assertEquals(exceptionMessage, exception.getLocalizedMessage());
        verify(accountLookupRepository, times(0)).save(any(AccountLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Can update existing AccountLookupEntity on valid AccountEmailChangedEvent")
    void testOn_whenValidAccountEmailChangedEventReceived_shouldUpdateAccountLookupEntity() {
        // Arrange
        String newEmail = "new@email.com";

        AccountEmailChangedEvent event = AccountEmailChangedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .oldEmail(TEST_EMAIL)
                .newEmail(newEmail)
                .build();

        when(accountLookupRepository.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(new AccountLookupEntity(TEST_ACCOUNT_ID, TEST_EMAIL));

        // Act
        accountLookupEventsHandler.on(event);

        // Assert
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(accountLookupRepository, times(1)).save(new AccountLookupEntity(TEST_ACCOUNT_ID, newEmail));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.format(ACCOUNT_UPDATED_IN_LOOKUP_DB, TEST_ACCOUNT_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(ACCOUNT_ID, event.getAccountId()),
                Markers.append(EMAIL, event.getNewEmail())
        );
    }

    @Test
    @DisplayName("Cannot update AccountLookupEntity for account that doesn't exist")
    void testOn_whenAccountEmailChangedEventReceivedForNonExistingAccount_shouldThrowException() {
        // Arrange
        AccountEmailChangedEvent event = AccountEmailChangedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .oldEmail(TEST_EMAIL)
                .newEmail(TEST_EMAIL) // Doesn't actually matter as the mock invocation is what triggers the exception
                .build();

        when(accountLookupRepository.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountLookupEventsHandler.on(event);
        });

        // Assert
        assertEquals(String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, event.getAccountId()), exception.getLocalizedMessage());
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(accountLookupRepository, times(0)).save(any(AccountLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Cannot update existing AccountLookupEntity on invalid AccountEmailChangedEvent")
    void testOn_whenInvalidAccountEmailChangedEventReceived_shouldThrowException() {
        // Arrange
        AccountEmailChangedEvent event = AccountEmailChangedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .oldEmail(TEST_EMAIL)
                .newEmail("invalid-email") // Doesn't actually matter as the mock invocation is what triggers the exception
                .build();

        String exceptionMessage = "Invalid email format";

        when(accountLookupRepository.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(new AccountLookupEntity(TEST_ACCOUNT_ID, TEST_EMAIL));

        doAnswer(invocation -> {
            BindingResult bindingResult = invocation.getArgument(1);
            bindingResult.rejectValue("email", "error.invalid", exceptionMessage);
            return null;
        }).when(validator).validate(any(AccountLookupEntity.class), any(BindingResult.class));

        // Act & Assert
        IllegalProjectionStateException exception = assertThrows(IllegalProjectionStateException.class, () -> {
            accountLookupEventsHandler.on(event);
        });

        // Assert
        assertEquals(exceptionMessage, exception.getLocalizedMessage());
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(accountLookupRepository, times(0)).save(any(AccountLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Can delete existing AccountLookupEntity on valid AccountDeletedEvent")
    void testOn_whenValidAccountDeletedEventReceived_shouldDeleteAccountLookupEntity() {
        // Arrange
        AccountDeletedEvent event = AccountDeletedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .build();

        when(accountLookupRepository.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(new AccountLookupEntity(TEST_ACCOUNT_ID, TEST_EMAIL));

        // Act
        accountLookupEventsHandler.on(event);

        // Assert
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(accountLookupRepository, times(1)).delete(any(AccountLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.format(ACCOUNT_DELETED_FROM_LOOKUP_DB, TEST_ACCOUNT_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(ACCOUNT_ID, event.getAccountId()),
                Markers.append(EMAIL, TEST_EMAIL)
        );
    }

    @Test
    @DisplayName("Cannot delete AccountLookupEntity for account that doesn't exist")
    void testOn_whenAccountDeletedEventReceivedForNonExistingAccount_shouldThrowException() {
        // Arrange
        AccountDeletedEvent event = AccountDeletedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .build();

        when(accountLookupRepository.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountLookupEventsHandler.on(event);
        });

        // Assert
        assertEquals(String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, event.getAccountId()), exception.getLocalizedMessage());
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(accountLookupRepository, times(0)).delete(any(AccountLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }
}