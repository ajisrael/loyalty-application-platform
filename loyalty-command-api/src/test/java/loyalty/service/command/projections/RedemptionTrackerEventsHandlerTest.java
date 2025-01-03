package loyalty.service.command.projections;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.entities.RedemptionTrackerEntity;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.command.data.repositories.RedemptionTrackerRepository;
import loyalty.service.command.test.utils.LogTestHelper;
import loyalty.service.core.events.loyalty.bank.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.loyalty.bank.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.loyalty.bank.transactions.AuthorizedTransactionCreatedEvent;
import loyalty.service.core.events.loyalty.bank.transactions.CapturedTransactionCreatedEvent;
import loyalty.service.core.events.loyalty.bank.transactions.VoidTransactionCreatedEvent;
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
import org.springframework.validation.SmartValidator;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static loyalty.service.core.constants.DomainConstants.*;
import static loyalty.service.core.constants.DomainConstants.BUSINESS_ID;
import static loyalty.service.core.constants.ExceptionMessages.CANNOT_CAPTURE_MORE_POINTS_THAN_AVAILABLE;
import static loyalty.service.core.constants.ExceptionMessages.CANNOT_VOID_MORE_POINTS_THAN_AVAILABLE;
import static loyalty.service.core.constants.LogMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedemptionTrackerEventsHandlerTest {

    private ListAppender<ILoggingEvent> listAppender;
    public static ILoggingEvent loggingEvent = null;
    public static Logger logger = null;

    @Mock
    private RedemptionTrackerRepository redemptionTrackerRepository;

    @Mock
    private SmartValidator validator;

    @InjectMocks
    RedemptionTrackerEventsHandler redemptionTrackerEventsHandler;

    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_PAYMENT_ID = UUID.randomUUID().toString();
    private static final int TEST_POINTS = 100;
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(RedemptionTrackerEventsHandler.class);
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
        redemptionTrackerEventsHandler.handle(exception);

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
        redemptionTrackerEventsHandler.handle(exception);

        // Assert
        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        loggingEvent = loggedEvents.get(0);
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals(testMessage, loggingEvent.getFormattedMessage());
    }

    @Test
    @DisplayName("Can save new RedemptionTracker on valid AuthorizedTransactionCreatedEvent")
    void testOn_whenValidAuthorizedTransactionEventReceived_shouldSaveRedemptionTrackerEntity() {
        // Arrange
        AuthorizedTransactionCreatedEvent event = AuthorizedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(TEST_POINTS)
                .paymentId(TEST_PAYMENT_ID)
                .build();

        // Act
        redemptionTrackerEventsHandler.on(event);

        // Assert
        RedemptionTrackerEntity expectedRedemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                event.getPoints(),
                0
        );

        verify(redemptionTrackerRepository, times(1)).save(expectedRedemptionTrackerEntity);

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        LogTestHelper.assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                AUTHORIZE_TRANSACTION_TRACKED,
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(AUTHORIZED_POINTS, TEST_POINTS),
                Markers.append(CAPTURED_POINTS, 0),
                Markers.append(LOYALTY_BANK_ID, TEST_LOYALTY_BANK_ID),
                Markers.append(PAYMENT_ID, TEST_PAYMENT_ID),
                Markers.append(POINTS_AVAILABLE_FOR_REDEMPTION, expectedRedemptionTrackerEntity.getPointsAvailableForRedemption())
        );
    }

    @Test
    @DisplayName("Can delete RedemptionTracker on valid VoidTransactionCreatedEvent for all points available")
    void testOn_whenValidVoidTransactionCreatedEventReceivedForAllAvailablePoints_shouldDeleteRedemptionTrackerEntity() {
        // Arrange
        VoidTransactionCreatedEvent event = VoidTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(TEST_POINTS)
                .paymentId(TEST_PAYMENT_ID)
                .build();

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                event.getPoints(),
                0
        );

        when(redemptionTrackerRepository.findByPaymentId(event.getPaymentId())).thenReturn(redemptionTrackerEntity);

        // Act
        redemptionTrackerEventsHandler.on(event);

        // Assert
        RedemptionTrackerEntity expectedRedemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                0,
                0
        );

        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
        verify(redemptionTrackerRepository, times(1)).delete(expectedRedemptionTrackerEntity);

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        LogTestHelper.assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                VOID_TRANSACTION_TRACKED_DELETING_TRACKER,
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(AUTHORIZED_POINTS, expectedRedemptionTrackerEntity.getAuthorizedPoints()),
                Markers.append(CAPTURED_POINTS, expectedRedemptionTrackerEntity.getCapturedPoints()),
                Markers.append(LOYALTY_BANK_ID, expectedRedemptionTrackerEntity.getLoyaltyBankId()),
                Markers.append(PAYMENT_ID, expectedRedemptionTrackerEntity.getPaymentId()),
                Markers.append(POINTS_AVAILABLE_FOR_REDEMPTION, expectedRedemptionTrackerEntity.getPointsAvailableForRedemption())
        );
    }

    @Test
    @DisplayName("Can update RedemptionTracker on valid VoidTransactionCreatedEvent for less than all points available")
    void testOn_whenValidVoidTransactionCreatedEventReceivedForLessThanAllPointsAvailable_shouldUpdateRedemptionTrackerEntity() {
        // Arrange
        final int VOID_POINTS = TEST_POINTS / 2;

        VoidTransactionCreatedEvent event = VoidTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(VOID_POINTS)
                .paymentId(TEST_PAYMENT_ID)
                .build();

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                TEST_POINTS,
                0
        );

        when(redemptionTrackerRepository.findByPaymentId(event.getPaymentId())).thenReturn(redemptionTrackerEntity);

        // Act
        redemptionTrackerEventsHandler.on(event);

        // Assert
        RedemptionTrackerEntity expectedRedemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                TEST_POINTS - VOID_POINTS,
                0
        );

        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
        verify(redemptionTrackerRepository, times(1)).save(expectedRedemptionTrackerEntity);

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        LogTestHelper.assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                VOID_TRANSACTION_TRACKED,
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(AUTHORIZED_POINTS, expectedRedemptionTrackerEntity.getAuthorizedPoints()),
                Markers.append(CAPTURED_POINTS, expectedRedemptionTrackerEntity.getCapturedPoints()),
                Markers.append(LOYALTY_BANK_ID, expectedRedemptionTrackerEntity.getLoyaltyBankId()),
                Markers.append(PAYMENT_ID, expectedRedemptionTrackerEntity.getPaymentId()),
                Markers.append(POINTS_AVAILABLE_FOR_REDEMPTION, expectedRedemptionTrackerEntity.getPointsAvailableForRedemption())
        );
    }

    @Test
    @DisplayName("Cannot update RedemptionTracker on valid VoidTransactionCreatedEvent for more than all points available")
    void testOn_whenValidVoidTransactionCreatedEventReceivedForMoreThanAllPointsAvailable_shouldThrowException() {
        // Arrange
        final int VOID_POINTS = TEST_POINTS * 2;

        VoidTransactionCreatedEvent event = VoidTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(VOID_POINTS)
                .paymentId(TEST_PAYMENT_ID)
                .build();

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                TEST_POINTS,
                0
        );

        when(redemptionTrackerRepository.findByPaymentId(event.getPaymentId())).thenReturn(redemptionTrackerEntity);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            redemptionTrackerEventsHandler.on(event);
        });

        // Assert
        assertEquals(CANNOT_VOID_MORE_POINTS_THAN_AVAILABLE, exception.getLocalizedMessage());
        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
        verify(redemptionTrackerRepository, times(0)).delete(any(RedemptionTrackerEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Can delete RedemptionTracker on valid CapturedTransactionCreatedEvent for all points available")
    void testOn_whenValidCapturedTransactionCreatedEventReceivedForAllAvailablePoints_shouldDeleteRedemptionTrackerEntity() {
        // Arrange
        CapturedTransactionCreatedEvent event = CapturedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(TEST_POINTS)
                .paymentId(TEST_PAYMENT_ID)
                .build();

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                event.getPoints(),
                0
        );

        when(redemptionTrackerRepository.findByPaymentId(event.getPaymentId())).thenReturn(redemptionTrackerEntity);

        // Act
        redemptionTrackerEventsHandler.on(event);

        // Assert
        RedemptionTrackerEntity expectedRedemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                TEST_POINTS,
                TEST_POINTS
        );

        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
        verify(redemptionTrackerRepository, times(1)).delete(expectedRedemptionTrackerEntity);

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        LogTestHelper.assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                CAPTURE_TRANSACTION_TRACKED_DELETING_TRACKER,
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(AUTHORIZED_POINTS, expectedRedemptionTrackerEntity.getAuthorizedPoints()),
                Markers.append(CAPTURED_POINTS, expectedRedemptionTrackerEntity.getCapturedPoints()),
                Markers.append(LOYALTY_BANK_ID, expectedRedemptionTrackerEntity.getLoyaltyBankId()),
                Markers.append(PAYMENT_ID, expectedRedemptionTrackerEntity.getPaymentId()),
                Markers.append(POINTS_AVAILABLE_FOR_REDEMPTION, expectedRedemptionTrackerEntity.getPointsAvailableForRedemption())
        );
    }


    @Test
    @DisplayName("Can update RedemptionTracker on valid CapturedTransactionCreatedEvent for less than all points available")
    void testOn_whenValidCapturedTransactionCreatedEventReceivedForLessThanAllPointsAvailable_shouldUpdateRedemptionTrackerEntity() {
        // Arrange
        final int POINTS_CAPTURED = TEST_POINTS / 2;

        CapturedTransactionCreatedEvent event = CapturedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(POINTS_CAPTURED)
                .paymentId(TEST_PAYMENT_ID)
                .build();

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                TEST_POINTS,
                0
        );

        when(redemptionTrackerRepository.findByPaymentId(event.getPaymentId())).thenReturn(redemptionTrackerEntity);

        // Act
        redemptionTrackerEventsHandler.on(event);

        // Assert
        RedemptionTrackerEntity expectedRedemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                TEST_POINTS,
                POINTS_CAPTURED
        );

        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
        verify(redemptionTrackerRepository, times(1)).save(expectedRedemptionTrackerEntity);

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        LogTestHelper.assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                CAPTURE_TRANSACTION_TRACKED,
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(AUTHORIZED_POINTS, expectedRedemptionTrackerEntity.getAuthorizedPoints()),
                Markers.append(CAPTURED_POINTS, expectedRedemptionTrackerEntity.getCapturedPoints()),
                Markers.append(LOYALTY_BANK_ID, expectedRedemptionTrackerEntity.getLoyaltyBankId()),
                Markers.append(PAYMENT_ID, expectedRedemptionTrackerEntity.getPaymentId()),
                Markers.append(POINTS_AVAILABLE_FOR_REDEMPTION, expectedRedemptionTrackerEntity.getPointsAvailableForRedemption())
        );
    }

    @Test
    @DisplayName("Cannot update RedemptionTracker on valid CapturedTransactionCreatedEvent for more than all points available")
    void testOn_whenValidCapturedTransactionCreatedEventReceivedForMoreThanAllPointsAvailable_shouldThrowException() {
        // Arrange
        final int POINTS_CAPTURED = TEST_POINTS * 2;

        CapturedTransactionCreatedEvent event = CapturedTransactionCreatedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .points(POINTS_CAPTURED)
                .paymentId(TEST_PAYMENT_ID)
                .build();

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                TEST_POINTS,
                0
        );

        when(redemptionTrackerRepository.findByPaymentId(event.getPaymentId())).thenReturn(redemptionTrackerEntity);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            redemptionTrackerEventsHandler.on(event);
        });

        // Assert
        assertEquals(CANNOT_CAPTURE_MORE_POINTS_THAN_AVAILABLE, exception.getLocalizedMessage());
        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
        verify(redemptionTrackerRepository, times(0)).delete(any(RedemptionTrackerEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Can delete all RedemptionTrackers with same loyaltyBankId on valid LoyaltyBankDeletedEvent")
    void testOn_whenValidLoyaltyBankDeletedEventReceived_shouldDeleteRedemptionTrackerEntitiesForLoyaltyBank() {
        // Arrange
        LoyaltyBankDeletedEvent event = LoyaltyBankDeletedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .accountId(TEST_ACCOUNT_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        List<RedemptionTrackerEntity> redemptionTrackerEntities = new ArrayList<>();

        redemptionTrackerEntities.add(new RedemptionTrackerEntity(
                TEST_REQUEST_ID,
                event.getLoyaltyBankId(),
                TEST_POINTS,
                0
        ));

        redemptionTrackerEntities.add(new RedemptionTrackerEntity(
                UUID.randomUUID().toString(),
                event.getLoyaltyBankId(),
                TEST_POINTS * 2,
                TEST_POINTS
        ));

        when(redemptionTrackerRepository.findByLoyaltyBankId(event.getLoyaltyBankId())).thenReturn(redemptionTrackerEntities);

        // Act
        redemptionTrackerEventsHandler.on(event);

        // Assert
        verify(redemptionTrackerRepository, times(1)).findByLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        verify(redemptionTrackerRepository, times(1)).deleteAll(redemptionTrackerEntities);

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        LogTestHelper.assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.format(DELETED_REDEMPTION_TRACKERS, redemptionTrackerEntities.size(), "LoyaltyBankDeletedEvent").getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(ACCOUNT_ID, event.getAccountId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(BUSINESS_ID, event.getBusinessId())
        );
    }
}