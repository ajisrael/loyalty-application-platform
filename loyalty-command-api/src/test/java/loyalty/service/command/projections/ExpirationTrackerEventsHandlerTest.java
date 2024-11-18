package loyalty.service.command.projections;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import loyalty.service.command.data.entities.ExpirationTrackerEntity;
import loyalty.service.command.data.repositories.ExpirationTrackerRepository;
import loyalty.service.command.data.repositories.TransactionRepository;
import loyalty.service.command.test.utils.LogTestHelper;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.transactions.EarnedTransactionCreatedEvent;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static loyalty.service.command.test.utils.LogTestHelper.assertLogMessageWithMarkers;
import static loyalty.service.command.test.utils.LogTestHelper.formatTimestamp;
import static loyalty.service.core.constants.DomainConstants.*;
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

        Instant timestamp = Instant.now();

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
                Markers.append("timestamp", formatTimestamp(timestamp)),
                Markers.append("points", TEST_POINTS)
        );
    }
}