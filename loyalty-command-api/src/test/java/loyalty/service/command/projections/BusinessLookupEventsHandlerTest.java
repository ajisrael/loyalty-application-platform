package loyalty.service.command.projections;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.entities.BusinessLookupEntity;
import loyalty.service.command.data.repositories.BusinessLookupRepository;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.BusinessEnrolledEvent;
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

import static loyalty.service.core.constants.DomainConstants.*;
import static loyalty.service.core.constants.LogMessages.ACCOUNT_SAVED_IN_LOOKUP_DB;
import static loyalty.service.core.constants.LogMessages.BUSINESS_SAVED_IN_LOOKUP_DB;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessLookupEventsHandlerTest {

    private ListAppender<ILoggingEvent> listAppender;
    public static ILoggingEvent loggingEvent = null;
    public static Logger logger = null;

    @Mock
    private BusinessLookupRepository businessLookupRepository;

    @Mock
    private SmartValidator validator;

    @InjectMocks
    BusinessLookupEventsHandler businessLookupEventsHandler;

    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_NAME = "business name";

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(BusinessLookupEventsHandler.class);
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
        businessLookupEventsHandler.handle(exception);

        // Assert
        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        loggingEvent = loggedEvents.get(0);
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals(testMessage, loggingEvent.getFormattedMessage());
    }

    @Test
    @DisplayName("Logs IllegalProjectionStateExceptions")
    void testHandleException_whenIllegalProjectionStateExceptionReceived_shouldLogError() {
        // Arrange
        String testMessage = "some error";
        IllegalProjectionStateException exception = new IllegalProjectionStateException(testMessage);

        // Act
        businessLookupEventsHandler.handle(exception);

        // Assert
        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        loggingEvent = loggedEvents.get(0);
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals(testMessage, loggingEvent.getFormattedMessage());
    }

    @Test
    @DisplayName("Can save new BusinessLookupEntity on valid BusinessEnrolledEvent")
    void testOn_whenValidBusinessEnrolledEventReceived_shouldSaveBusinessLookupEntity() {
        // Arrange
        BusinessEnrolledEvent event = BusinessEnrolledEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        // Act
        businessLookupEventsHandler.on(event);

        // Assert
        verify(businessLookupRepository, times(1)).save(any(BusinessLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(1, loggedEvents.size());

        loggingEvent = loggedEvents.get(0);
        assertEquals(Level.INFO, loggingEvent.getLevel());
        String expectedLogMessage = MessageFormatter.format(BUSINESS_SAVED_IN_LOOKUP_DB, TEST_BUSINESS_ID).getMessage();
        assertEquals(expectedLogMessage, loggingEvent.getFormattedMessage());

        assertTrue(loggingEvent.getMarkerList().get(0).contains(Markers.append(REQUEST_ID, event.getRequestId())));
        assertTrue(loggingEvent.getMarkerList().get(0).contains(Markers.append(BUSINESS_ID, event.getBusinessId())));
    }

    @Test
    @DisplayName("Cannot save new BusinessLookupEntity on invalid BusinessEnrolledEvent")
    void testOn_whenInvalidBusinessEnrolledEventReceived_shouldThrowException() {
        // Arrange
        BusinessEnrolledEvent event = BusinessEnrolledEvent.builder()
                .requestId(TEST_REQUEST_ID)
                .businessId("invalid-business-id")
                .businessName(TEST_BUSINESS_NAME)
                .build();

        String exceptionMessage = "Invalid businessId format";

        doAnswer(invocation -> {
            BindingResult bindingResult = invocation.getArgument(1);
            bindingResult.rejectValue("businessId", "error.invalid", exceptionMessage);
            return null;
        }).when(validator).validate(any(BusinessLookupEntity.class), any(BindingResult.class));

        // Act & Assert
        IllegalProjectionStateException exception = assertThrows(IllegalProjectionStateException.class, () -> {
            businessLookupEventsHandler.on(event);
        });

        // Assert
        assertEquals(exceptionMessage, exception.getLocalizedMessage());
        verify(businessLookupRepository, times(0)).save(any(BusinessLookupEntity.class));

        List<ILoggingEvent> loggedEvents = listAppender.list;
        assertEquals(0, loggedEvents.size());
    }
}