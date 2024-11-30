package loyalty.service.command.projections;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import loyalty.service.command.data.entities.ExpirationTrackerEntity;
import loyalty.service.command.data.entities.TransactionEntity;
import loyalty.service.command.data.repositories.ExpirationTrackerRepository;
import loyalty.service.command.data.repositories.TransactionRepository;
import loyalty.service.command.test.utils.LogTestHelper;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.transactions.AuthorizedTransactionCreatedEvent;
import loyalty.service.core.events.transactions.AwardedTransactionCreatedEvent;
import loyalty.service.core.events.transactions.EarnedTransactionCreatedEvent;
import loyalty.service.core.events.transactions.VoidTransactionCreatedEvent;
import loyalty.service.core.exceptions.ExpirationTrackerNotFoundException;
import loyalty.service.core.exceptions.IllegalProjectionStateException;
import net.logstash.logback.marker.Markers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static loyalty.service.command.test.utils.LogTestHelper.assertLogMessageWithMarkers;
import static loyalty.service.command.test.utils.LogTestHelper.formatTimestamp;
import static loyalty.service.core.constants.DomainConstants.*;
import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.constants.LogMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpirationTrackerEventsHandlerTest {


    private ListAppender<ILoggingEvent> listAppender;
    public static ILoggingEvent loggingEvent = null;
    public static Logger logger = null;

    @Mock
    private ExpirationTrackerRepository expirationTrackerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SmartValidator validator;

    @InjectMocks
    ExpirationTrackerEventsHandler expirationTrackerEventsHandler;


    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_ID = UUID.randomUUID().toString();
    private static final int TEST_POINTS = 100;
    private static final String TEST_TRANSACTION_ID = UUID.randomUUID().toString();
    private static final String TEST_PAYMENT_ID = UUID.randomUUID().toString();
    private Instant timestamp = Instant.now();


    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(ExpirationTrackerEventsHandler.class);
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
        expirationTrackerEventsHandler.handle(exception);

        // Assert
        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        LogTestHelper.assertLogMessage(loggedEvents.get(0), Level.ERROR, testMessage);
    }

    @Test
    @DisplayName("Logs IllegalProjectionStateExceptions")
    void testHandleException_whenIllegalProjectionStateExceptionReceived_shouldLogError() {
        // Arrange
        String testMessage = "some error";
        IllegalProjectionStateException exception = new IllegalProjectionStateException(testMessage);

        // Act
        expirationTrackerEventsHandler.handle(exception);

        // Assert
        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        loggingEvent = loggedEvents.get(0);
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals(testMessage, loggingEvent.getFormattedMessage());
    }

    @Test
    @DisplayName("Can save new ExpirationTrackerEntity on valid LoyaltyBankCreatedEvent")
    void testOn_whenValidLoyaltyBankCreatedEventReceived_shouldSaveExpirationTrackerEntity() {
        // Arrange
        LoyaltyBankCreatedEvent event = LoyaltyBankCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .accountId(TEST_ACCOUNT_ID)
                .businessId(TEST_BUSINESS_ID)
                .pending(0)
                .earned(0)
                .authorized(0)
                .captured(0)
                .build();

        // Act
        expirationTrackerEventsHandler.on(event);

        // Assert
        verify(expirationTrackerRepository, times(1)).save(any(ExpirationTrackerEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        LogTestHelper.assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.format(EXPIRATION_TRACKER_CREATED_FOR_LOYALTY_BANK, TEST_LOYALTY_BANK_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(TRANSACTION_LIST, new ArrayList<>())
        );
    }

    @Test
    @DisplayName("Cannot save new ExpirationTrackerEntity on invalid LoyaltyBankCreatedEvent")
    void testOn_whenInvalidLoyaltyBankCreatedEventReceived_shouldThrowException() {
        // Arrange
        LoyaltyBankCreatedEvent event = LoyaltyBankCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId("invalid-id")
                .accountId(TEST_ACCOUNT_ID)
                .businessId(TEST_BUSINESS_ID)
                .pending(0)
                .earned(0)
                .authorized(0)
                .captured(0)
                .build();

        String exceptionMessage = "Invalid loyaltyBankId format";

        doAnswer(invocation -> {
            BindingResult bindingResult = invocation.getArgument(1);
            bindingResult.rejectValue("loyaltyBankId", "error.invalid", exceptionMessage);
            return null;
        }).when(validator).validate(any(ExpirationTrackerEntity.class), any(BindingResult.class));

        // Act & Assert
        IllegalProjectionStateException exception = assertThrows(IllegalProjectionStateException.class, () -> {
            expirationTrackerEventsHandler.on(event);
        });

        // Assert
        assertEquals(exceptionMessage, exception.getLocalizedMessage());
        verify(expirationTrackerRepository, times(0)).save(any(ExpirationTrackerEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Can update ExpirationTrackerEntity on valid EarnedTransactionCreatedEvent")
    void testOn_whenValidEarnedTransactionCreatedEventReceived_shouldUpdateExpirationTrackerEntity() {
        // Arrange
        EarnedTransactionCreatedEvent event = EarnedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(TEST_POINTS)
                .build();

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID))
                .thenReturn(new ExpirationTrackerEntity(TEST_LOYALTY_BANK_ID));

        // Act
        expirationTrackerEventsHandler.on(event, timestamp);

        // Assert
        verify(expirationTrackerRepository, times(1)).findByLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        verify(expirationTrackerRepository, times(1)).save(any(ExpirationTrackerEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.format(TRANSACTION_ENTITY_CREATED_FOR_LOYALTY_BANK, TEST_LOYALTY_BANK_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(TRANSACTION_ID, event.getRequestId()),
                Markers.append(TIMESTAMP, formatTimestamp(timestamp)),
                Markers.append(POINTS, TEST_POINTS)
        );
    }

    @Test
    @DisplayName("Cannot update ExpirationTrackerEntity on valid EarnedTransactionCreatedEvent for non existing loyaltyBank")
    void testOn_whenValidEarnedTransactionCreatedEventReceivedForNonExistingLoyaltyBank_shouldThrowException() {
        // Arrange
        EarnedTransactionCreatedEvent event = EarnedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(TEST_POINTS)
                .build();

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID)).thenReturn(null);


        // Act & Assert
        ExpirationTrackerNotFoundException exception = assertThrows(ExpirationTrackerNotFoundException.class, () -> {
            expirationTrackerEventsHandler.on(event, timestamp);
        });

        // Assert
        assertEquals(String.format(EXPIRATION_TRACKER_FOR_LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST, event.getLoyaltyBankId()), exception.getLocalizedMessage());
        verify(expirationTrackerRepository, times(0)).save(any(ExpirationTrackerEntity.class));
        verify(transactionRepository, times(0)).save(any(TransactionEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Cannot update ExpirationTrackerEntity on invalid EarnedTransactionCreatedEvent")
    void testOn_whenInvalidEarnedTransactionCreatedEventReceived_shouldThrowException() {
        // Arrange
        EarnedTransactionCreatedEvent event = EarnedTransactionCreatedEvent.builder()
                .requestId("invalid-id")
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(TEST_POINTS)
                .build();

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID))
                .thenReturn(new ExpirationTrackerEntity(TEST_LOYALTY_BANK_ID));

        String exceptionMessage = "Invalid transactionId format";

        doAnswer(invocation -> {
            BindingResult bindingResult = invocation.getArgument(1);
            bindingResult.rejectValue("transactionId", "error.invalid", exceptionMessage);
            return null;
        }).when(validator).validate(any(TransactionEntity.class), any(BindingResult.class));

        // Act & Assert
        IllegalProjectionStateException exception = assertThrows(IllegalProjectionStateException.class, () -> {
            expirationTrackerEventsHandler.on(event, timestamp);
        });

        // Assert
        assertEquals(exceptionMessage, exception.getLocalizedMessage());
        verify(expirationTrackerRepository, times(0)).save(any(ExpirationTrackerEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Can update ExpirationTrackerEntity on valid AwardedTransactionCreatedEvent")
    void testOn_whenValidAwardedTransactionCreatedEventReceived_shouldUpdateExpirationTrackerEntity() {
        // Arrange
        AwardedTransactionCreatedEvent event = AwardedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(TEST_POINTS)
                .build();

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID))
                .thenReturn(new ExpirationTrackerEntity(TEST_LOYALTY_BANK_ID));

        // Act
        expirationTrackerEventsHandler.on(event, timestamp);

        // Assert
        verify(expirationTrackerRepository, times(1)).findByLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        verify(expirationTrackerRepository, times(1)).save(any(ExpirationTrackerEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.format(TRANSACTION_ENTITY_CREATED_FOR_LOYALTY_BANK, TEST_LOYALTY_BANK_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(TRANSACTION_ID, event.getRequestId()),
                Markers.append(TIMESTAMP, formatTimestamp(timestamp)),
                Markers.append(POINTS, TEST_POINTS)
        );
    }

    @Test
    @DisplayName("Cannot update ExpirationTrackerEntity on valid AwardedTransactionCreatedEvent for non existing loyaltyBank")
    void testOn_whenValidAwardedTransactionCreatedEventReceivedForNonExistingLoyaltyBank_shouldThrowException() {
        // Arrange
        AwardedTransactionCreatedEvent event = AwardedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(TEST_POINTS)
                .build();

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID)).thenReturn(null);


        // Act & Assert
        ExpirationTrackerNotFoundException exception = assertThrows(ExpirationTrackerNotFoundException.class, () -> {
            expirationTrackerEventsHandler.on(event, timestamp);
        });

        // Assert
        assertEquals(String.format(EXPIRATION_TRACKER_FOR_LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST, event.getLoyaltyBankId()), exception.getLocalizedMessage());
        verify(expirationTrackerRepository, times(0)).save(any(ExpirationTrackerEntity.class));
        verify(transactionRepository, times(0)).save(any(TransactionEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Cannot update ExpirationTrackerEntity on invalid AwardedTransactionCreatedEvent")
    void testOn_whenInvalidAwardedTransactionCreatedEventReceived_shouldThrowException() {
        // Arrange
        AwardedTransactionCreatedEvent event = AwardedTransactionCreatedEvent.builder()
                .requestId("invalid-id")
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(TEST_POINTS)
                .build();

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID))
                .thenReturn(new ExpirationTrackerEntity(TEST_LOYALTY_BANK_ID));

        String exceptionMessage = "Invalid transactionId format";

        doAnswer(invocation -> {
            BindingResult bindingResult = invocation.getArgument(1);
            bindingResult.rejectValue("transactionId", "error.invalid", exceptionMessage);
            return null;
        }).when(validator).validate(any(TransactionEntity.class), any(BindingResult.class));

        // Act & Assert
        IllegalProjectionStateException exception = assertThrows(IllegalProjectionStateException.class, () -> {
            expirationTrackerEventsHandler.on(event, timestamp);
        });

        // Assert
        assertEquals(exceptionMessage, exception.getLocalizedMessage());
        verify(expirationTrackerRepository, times(0)).save(any(ExpirationTrackerEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Can update ExpirationTrackerEntity on valid AuthorizedTransactionCreatedEvent for all available points in loyaltyBank")
    void testOn_whenValidAuthorizedTransactionCreatedEventReceivedForAllAvailablePointsInLoyaltyBank_shouldUpdateExpirationTrackerEntity() {
        // Arrange
        AuthorizedTransactionCreatedEvent event = AuthorizedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        ExpirationTrackerEntity expirationTrackerEntity = new ExpirationTrackerEntity(TEST_LOYALTY_BANK_ID);
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionId(TEST_TRANSACTION_ID);
        transactionEntity.setTimestamp(timestamp);
        transactionEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        transactionEntity.setPoints(TEST_POINTS);
        expirationTrackerEntity.addTransaction(transactionEntity);

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID)).thenReturn(expirationTrackerEntity);

        // Act
        expirationTrackerEventsHandler.on(event);

        // Assert
        verify(expirationTrackerRepository, times(1)).findByLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        verify(expirationTrackerRepository, times(1)).save(any(ExpirationTrackerEntity.class));

        ArgumentCaptor<ExpirationTrackerEntity> captor = ArgumentCaptor.forClass(ExpirationTrackerEntity.class);
        verify(expirationTrackerRepository).save(captor.capture());

        ExpirationTrackerEntity savedEntity = captor.getValue();
        assertNotNull(savedEntity, "Saved entity should not be null");
        assertEquals(TEST_LOYALTY_BANK_ID, savedEntity.getLoyaltyBankId());
        assertTrue(savedEntity.getTransactionList().isEmpty(), "Transaction list should be empty");

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(3, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.arrayFormat(
                        APPLYING_REMAINING_AUTHORIZED_POINTS_TO_POINTS_ON_OLDEST_TRANSACTION,
                        Arguments.of(TEST_POINTS, TEST_POINTS, TEST_TRANSACTION_ID).get()).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(TRANSACTION_ID, TEST_TRANSACTION_ID)
        );

        assertLogMessageWithMarkers(
                loggedEvents.get(1),
                Level.INFO,
                MessageFormatter.format(ALL_POINTS_USED_FOR_TRANSACTION_REMOVING_TRANSACTION_FROM_EXPIRATION_TRACKER, TEST_TRANSACTION_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(TRANSACTION_ID, TEST_TRANSACTION_ID)
        );

        assertLogMessageWithMarkers(
                loggedEvents.get(2),
                Level.INFO,
                MessageFormatter.format(AUTHORIZED_POINTS_APPLIED_TO_TRANSACTIONS_FOR_LOYALTY_BANK, TEST_LOYALTY_BANK_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(PAYMENT_ID, event.getPaymentId()),
                Markers.append(POINTS, event.getPoints())
        );
    }

    @Test
    @DisplayName("Can update ExpirationTrackerEntity on valid AuthorizedTransactionCreatedEvent for some available points in loyaltyBank")
    void testOn_whenValidAuthorizedTransactionCreatedEventReceivedForSomeAvailablePointsInLoyaltyBank_shouldUpdateExpirationTrackerEntity() {
        // Arrange
        AuthorizedTransactionCreatedEvent event = AuthorizedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        int availablePoints = TEST_POINTS * 2;

        ExpirationTrackerEntity expirationTrackerEntity = new ExpirationTrackerEntity(TEST_LOYALTY_BANK_ID);
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionId(TEST_TRANSACTION_ID);
        transactionEntity.setTimestamp(timestamp);
        transactionEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        transactionEntity.setPoints(availablePoints);
        expirationTrackerEntity.addTransaction(transactionEntity);

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID)).thenReturn(expirationTrackerEntity);

        // Act
        expirationTrackerEventsHandler.on(event);

        // Assert
        verify(expirationTrackerRepository, times(1)).findByLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        verify(expirationTrackerRepository, times(1)).save(any(ExpirationTrackerEntity.class));

        ArgumentCaptor<ExpirationTrackerEntity> captor = ArgumentCaptor.forClass(ExpirationTrackerEntity.class);
        verify(expirationTrackerRepository).save(captor.capture());

        ExpirationTrackerEntity savedEntity = captor.getValue();
        assertNotNull(savedEntity, "Saved entity should not be null");
        assertEquals(TEST_LOYALTY_BANK_ID, savedEntity.getLoyaltyBankId());
        assertEquals(1, savedEntity.getTransactionList().size(), "Transaction list should have one transaction");

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(2, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.arrayFormat(
                        APPLYING_REMAINING_AUTHORIZED_POINTS_TO_POINTS_ON_OLDEST_TRANSACTION,
                        Arguments.of(TEST_POINTS, availablePoints, TEST_TRANSACTION_ID).get()).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(TRANSACTION_ID, TEST_TRANSACTION_ID)
        );

        assertLogMessageWithMarkers(
                loggedEvents.get(1),
                Level.INFO,
                MessageFormatter.format(AUTHORIZED_POINTS_APPLIED_TO_TRANSACTIONS_FOR_LOYALTY_BANK, TEST_LOYALTY_BANK_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(PAYMENT_ID, event.getPaymentId()),
                Markers.append(POINTS, event.getPoints())
        );
    }

    @Test
    @DisplayName("Cannot update ExpirationTrackerEntity on valid AuthorizedTransactionCreatedEvent for more than available points in loyaltyBank")
    void testOn_whenValidAuthorizedTransactionCreatedEventReceivedForMoreThanAvailablePointsInLoyaltyBank_shouldThrowException() {
        // Arrange
        AuthorizedTransactionCreatedEvent event = AuthorizedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        int availablePoints = TEST_POINTS / 2;

        ExpirationTrackerEntity expirationTrackerEntity = new ExpirationTrackerEntity(TEST_LOYALTY_BANK_ID);
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionId(TEST_TRANSACTION_ID);
        transactionEntity.setTimestamp(timestamp);
        transactionEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        transactionEntity.setPoints(availablePoints);
        expirationTrackerEntity.addTransaction(transactionEntity);

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID)).thenReturn(expirationTrackerEntity);

        // Act & Assert
        IllegalProjectionStateException exception = assertThrows(IllegalProjectionStateException.class, () -> {
            expirationTrackerEventsHandler.on(event);
        });

        // Assert
        verify(expirationTrackerRepository, times(1)).findByLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        verify(expirationTrackerRepository, times(0)).save(any(ExpirationTrackerEntity.class));

        String expectedMessage = String.format(
                AUTHORIZED_MORE_POINTS_THAN_EARNED_CANNOT_PROCESS_AUTHORIZATION_EVENT_FOR_EXPIRATION_TRACKER,
                TEST_LOYALTY_BANK_ID
        );
        assertEquals(expectedMessage, exception.getLocalizedMessage(), "Exception did not have expected message");

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(2, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.arrayFormat(
                        APPLYING_REMAINING_AUTHORIZED_POINTS_TO_POINTS_ON_OLDEST_TRANSACTION,
                        Arguments.of(TEST_POINTS, availablePoints, TEST_TRANSACTION_ID).get()).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(TRANSACTION_ID, TEST_TRANSACTION_ID)
        );

        assertLogMessageWithMarkers(
                loggedEvents.get(1),
                Level.INFO,
                MessageFormatter.format(ALL_POINTS_USED_FOR_TRANSACTION_REMOVING_TRANSACTION_FROM_EXPIRATION_TRACKER, TEST_TRANSACTION_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(TRANSACTION_ID, TEST_TRANSACTION_ID)
        );
    }

    @Test
    @DisplayName("Cannot update ExpirationTrackerEntity on valid AuthorizedTransactionCreatedEvent for non existing loyaltyBank")
    void testOn_whenValidAuthorizedTransactionCreatedEventReceivedForNonExistingLoyaltyBank_shouldThrowException() {
        // Arrange
        AuthorizedTransactionCreatedEvent event = AuthorizedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID)).thenReturn(null);


        // Act & Assert
        ExpirationTrackerNotFoundException exception = assertThrows(ExpirationTrackerNotFoundException.class, () -> {
            expirationTrackerEventsHandler.on(event);
        });

        // Assert
        assertEquals(String.format(EXPIRATION_TRACKER_FOR_LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST, event.getLoyaltyBankId()), exception.getLocalizedMessage());
        verify(expirationTrackerRepository, times(0)).save(any(ExpirationTrackerEntity.class));
        verify(transactionRepository, times(0)).save(any(TransactionEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Can update ExpirationTrackerEntity on valid VoidTransactionCreatedEvent when no available points in loyaltyBank")
    void testOn_whenValidVoidTransactionCreatedEventReceivedWhenNoAvailablePointsInLoyaltyBank_shouldUpdateExpirationTrackerEntity() {
        // Arrange
        VoidTransactionCreatedEvent event = VoidTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        int availablePoints = 0;

        ExpirationTrackerEntity expirationTrackerEntity = new ExpirationTrackerEntity(TEST_LOYALTY_BANK_ID);

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID)).thenReturn(expirationTrackerEntity);

        // Act
        expirationTrackerEventsHandler.on(event, timestamp);

        // Assert
        verify(expirationTrackerRepository, times(1)).findByLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        verify(expirationTrackerRepository, times(1)).save(any(ExpirationTrackerEntity.class));

        ArgumentCaptor<ExpirationTrackerEntity> captor = ArgumentCaptor.forClass(ExpirationTrackerEntity.class);
        verify(expirationTrackerRepository).save(captor.capture());

        ExpirationTrackerEntity savedEntity = captor.getValue();
        assertNotNull(savedEntity, "Saved entity should not be null");
        assertEquals(TEST_LOYALTY_BANK_ID, savedEntity.getLoyaltyBankId());
        assertEquals(1, savedEntity.getTransactionList().size(), "Transaction list should have one transaction");

        int expectedPoints = event.getPoints() + availablePoints;
        assertEquals(expectedPoints, savedEntity.getTransactionList().get(0).getPoints(), "Transaction does not have expected points");

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(2, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.arrayFormat(CREATING_NEW_TRANSACTION_FOR_EXPIRATION_TRACKER, Arguments.of(TEST_REQUEST_ID, TEST_LOYALTY_BANK_ID).get()).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(POINTS, expectedPoints),
                Markers.append(TIMESTAMP, formatTimestamp(timestamp)),
                Markers.append(TRANSACTION_ID, TEST_REQUEST_ID)
        );

        assertLogMessageWithMarkers(
                loggedEvents.get(1),
                Level.INFO,
                MessageFormatter.format(VOIDED_POINTS_APPLIED_TO_EXPIRATION_TRACKER_FOR_LOYALTY_BANK, TEST_LOYALTY_BANK_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId())
        );
    }

    @Test
    @DisplayName("Can update ExpirationTrackerEntity on valid VoidTransactionCreatedEvent for some available points in loyaltyBank")
    void testOn_whenValidVoidTransactionCreatedEventReceivedForSomeAvailablePointsInLoyaltyBank_shouldUpdateExpirationTrackerEntity() {
        // Arrange
        VoidTransactionCreatedEvent event = VoidTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        int availablePoints = TEST_POINTS;

        ExpirationTrackerEntity expirationTrackerEntity = new ExpirationTrackerEntity(TEST_LOYALTY_BANK_ID);
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionId(TEST_TRANSACTION_ID);
        transactionEntity.setTimestamp(timestamp);
        transactionEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        transactionEntity.setPoints(availablePoints);
        expirationTrackerEntity.addTransaction(transactionEntity);

        when(expirationTrackerRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID)).thenReturn(expirationTrackerEntity);

        // Act
        expirationTrackerEventsHandler.on(event, timestamp);

        // Assert
        verify(expirationTrackerRepository, times(1)).findByLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        verify(expirationTrackerRepository, times(1)).save(any(ExpirationTrackerEntity.class));

        ArgumentCaptor<ExpirationTrackerEntity> captor = ArgumentCaptor.forClass(ExpirationTrackerEntity.class);
        verify(expirationTrackerRepository).save(captor.capture());

        ExpirationTrackerEntity savedEntity = captor.getValue();
        assertNotNull(savedEntity, "Saved entity should not be null");
        assertEquals(TEST_LOYALTY_BANK_ID, savedEntity.getLoyaltyBankId());
        assertEquals(1, savedEntity.getTransactionList().size(), "Transaction list should have one transaction");

        int expectedPoints = event.getPoints() + availablePoints;
        assertEquals(expectedPoints, savedEntity.getTransactionList().get(0).getPoints(), "Transaction does not have expected points");

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(2, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.arrayFormat(ADDING_POINTS_TO_OLDEST_TRANSACTION, Arguments.of(TEST_TRANSACTION_ID, TEST_LOYALTY_BANK_ID).get()).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(POINTS, expectedPoints),
                Markers.append(TIMESTAMP, formatTimestamp(timestamp)),
                Markers.append(TRANSACTION_ID, TEST_TRANSACTION_ID)
        );

        assertLogMessageWithMarkers(
                loggedEvents.get(1),
                Level.INFO,
                MessageFormatter.format(VOIDED_POINTS_APPLIED_TO_EXPIRATION_TRACKER_FOR_LOYALTY_BANK, TEST_LOYALTY_BANK_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId())
        );
    }
}