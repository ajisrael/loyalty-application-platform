package loyalty.service.command.test.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Marker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogTestHelper {

    public static void logContainsMarkers(ILoggingEvent loggingEvent, LogstashMarker... expectedMarkers) {
        for (Marker marker : expectedMarkers) {
            assertTrue(loggingEvent.getMarkerList().get(0).contains(marker));
        }
    }

    public static void assertLogMessage(ILoggingEvent loggingEvent, Level expectedLevel, String expectedMessage) {
        assertEquals(expectedLevel, loggingEvent.getLevel());
        assertEquals(expectedMessage, loggingEvent.getFormattedMessage());
    }

    public static void assertLogMessageWithMarkers(ILoggingEvent loggingEvent, Level expectedLevel, String expectedMessage, LogstashMarker... expectedMarkers) {
        assertLogMessage(loggingEvent, expectedLevel, expectedMessage);
        logContainsMarkers(loggingEvent, expectedMarkers);
    }
}