package loyalty.service.command.test.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Marker;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogTestHelper {

    private static final DateTimeFormatter logTimestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z")
            .withZone(ZoneId.systemDefault());

    public static void logContainsMarkers(ILoggingEvent loggingEvent, LogstashMarker... expectedMarkers) {
        for (Marker marker : expectedMarkers) {
            assertTrue(loggingEvent.getMarkerList().get(0).contains(marker), "ExpectedMarker: " + marker + " not found");
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

    public static String formatTimestamp(Instant timestamp) {
        return logTimestampFormatter.format(timestamp);
    }
}