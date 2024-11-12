package loyalty.service.command.utils;

import loyalty.service.command.commands.AbstractCommand;
import loyalty.service.core.events.AbstractEvent;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.Marker;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;

public class LogHelper {

    private LogHelper() {
        throw new IllegalStateException("utility class");
    }

    public static void logCommandIssuingEvent(Logger logger, AbstractCommand command, AbstractEvent event) {
        Marker marker = MarkerGenerator.generateMarker(event);
        marker.add(Markers.append(REQUEST_ID, command.getRequestId()));
        logger.info(marker, "{} received, issuing {}", command.getClass().getSimpleName(), event.getClass().getSimpleName());
    }

    public static void logEventIssuingCommand(Logger logger, AbstractEvent event, AbstractCommand command) {
        Marker marker = MarkerGenerator.generateMarker(event);
        marker.add(Markers.append(REQUEST_ID, event.getRequestId()));
        logger.info(marker, "{} received, issuing {}", event.getClass().getSimpleName(), command.getClass().getSimpleName());
    }

    public static void logEventProcessed(Logger logger, AbstractEvent event) {
        Marker marker = MarkerGenerator.generateMarker(event);
        logger.debug(marker, "{} processed", event.getClass().getSimpleName());
    }
}
