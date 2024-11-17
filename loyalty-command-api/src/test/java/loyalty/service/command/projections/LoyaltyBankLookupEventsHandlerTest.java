package loyalty.service.command.projections;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.command.test.utils.LogTestHelper;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
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

import static loyalty.service.command.test.utils.LogTestHelper.assertLogMessageWithMarkers;
import static loyalty.service.core.constants.DomainConstants.*;
import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.constants.LogMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyBankLookupEventsHandlerTest {

    private ListAppender<ILoggingEvent> listAppender;
    public static ILoggingEvent loggingEvent = null;
    public static Logger logger = null;


    @Mock
    private LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    @Mock
    private SmartValidator validator;

    @InjectMocks
    LoyaltyBankLookupEventsHandler loyaltyBankLookupEventsHandler;


    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(LoyaltyBankLookupEventsHandler.class);
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
        loyaltyBankLookupEventsHandler.handle(exception);

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
        loyaltyBankLookupEventsHandler.handle(exception);

        // Assert
        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        loggingEvent = loggedEvents.get(0);
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals(testMessage, loggingEvent.getFormattedMessage());
    }

    @Test
    @DisplayName("Can save new LoyaltyBankLookupEntity on valid LoyaltyBankCreatedEvent")
    void testOn_whenValidLoyaltyBankCreatedEventReceived_shouldSaveLoyaltyBankLookupEntity() {
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
        loyaltyBankLookupEventsHandler.on(event);

        // Assert
        verify(loyaltyBankLookupRepository, times(1)).save(any(LoyaltyBankLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        LogTestHelper.assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.format(LOYALTY_BANK_SAVED_IN_LOOKUP_DB, TEST_LOYALTY_BANK_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(ACCOUNT_ID, event.getAccountId()),
                Markers.append(BUSINESS_ID, event.getBusinessId())
        );
    }


    @Test
    @DisplayName("Cannot save new LoyaltyBankLookupEntity on invalid LoyaltyBankCreatedEvent")
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
        }).when(validator).validate(any(LoyaltyBankLookupEntity.class), any(BindingResult.class));

        // Act & Assert
        IllegalProjectionStateException exception = assertThrows(IllegalProjectionStateException.class, () -> {
            loyaltyBankLookupEventsHandler.on(event);
        });

        // Assert
        assertEquals(exceptionMessage, exception.getLocalizedMessage());
        verify(loyaltyBankLookupRepository, times(0)).save(any(LoyaltyBankLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }

    @Test
    @DisplayName("Can delete existing LoyaltyBankLookupEntity on valid LoyaltyBankDeletedEvent")
    void testOn_whenValidLoyaltyBankDeletedEventReceived_shouldDeleteLoyaltyBankLookupEntity() {
        // Arrange
        LoyaltyBankDeletedEvent event = LoyaltyBankDeletedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .accountId(TEST_ACCOUNT_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        when(loyaltyBankLookupRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID))
                .thenReturn(new LoyaltyBankLookupEntity(TEST_LOYALTY_BANK_ID, TEST_ACCOUNT_ID, TEST_BUSINESS_ID));

        // Act
        loyaltyBankLookupEventsHandler.on(event);

        // Assert
        verify(loyaltyBankLookupRepository, times(1)).findByLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        verify(loyaltyBankLookupRepository, times(1)).delete(any(LoyaltyBankLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        assertLogMessageWithMarkers(
                loggedEvents.get(0),
                Level.INFO,
                MessageFormatter.format(LOYALTY_BANK_DELETED_FROM_LOOKUP_DB, TEST_LOYALTY_BANK_ID).getMessage(),
                Markers.append(REQUEST_ID, event.getRequestId()),
                Markers.append(LOYALTY_BANK_ID, event.getLoyaltyBankId()),
                Markers.append(ACCOUNT_ID, event.getAccountId()),
                Markers.append(BUSINESS_ID, event.getBusinessId())
        );
    }

    @Test
    @DisplayName("Cannot delete LoyaltyBankLookupEntity for loyaltyBank that doesn't exist")
    void testOn_whenLoyaltyBankDeletedEventReceivedForNonExistingLoyaltyBank_shouldThrowException() {
        // Arrange
        LoyaltyBankDeletedEvent event = LoyaltyBankDeletedEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .build();

        when(loyaltyBankLookupRepository.findByLoyaltyBankId(TEST_LOYALTY_BANK_ID)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loyaltyBankLookupEventsHandler.on(event);
        });

        // Assert
        assertEquals(String.format(LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST, event.getLoyaltyBankId()), exception.getLocalizedMessage());
        verify(loyaltyBankLookupRepository, times(1)).findByLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        verify(loyaltyBankLookupRepository, times(0)).delete(any(LoyaltyBankLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }
}