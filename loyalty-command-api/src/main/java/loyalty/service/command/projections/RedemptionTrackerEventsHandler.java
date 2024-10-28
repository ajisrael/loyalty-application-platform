package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.command.data.entities.RedemptionTrackerEntity;
import loyalty.service.command.data.repositories.RedemptionTrackerRepository;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.transactions.AuthorizedTransactionCreatedEvent;
import loyalty.service.core.events.transactions.CapturedTransactionCreatedEvent;
import loyalty.service.core.events.transactions.VoidTransactionCreatedEvent;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Component;

import java.util.List;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;


@Component
@AllArgsConstructor
@ProcessingGroup("redemption-tracker-group")
public class RedemptionTrackerEventsHandler {

    private RedemptionTrackerRepository redemptionTrackerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedemptionTrackerEventsHandler.class);

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        LOGGER.error(exception.getLocalizedMessage());
    }

    @EventHandler
    public void on(AuthorizedTransactionCreatedEvent event) {
        RedemptionTrackerEntity redemptionTracker = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                event.getPoints(),
                0
        );

        redemptionTrackerRepository.save(redemptionTracker);

        Marker marker = MarkerGenerator.generateMarker(redemptionTracker);
        marker.add(Markers.append(REQUEST_ID,event.getRequestId()));

        LOGGER.info(marker, "Authorize transaction tracked");
    }

    @EventHandler
    public void on(VoidTransactionCreatedEvent event) {
        RedemptionTrackerEntity redemptionTracker = redemptionTrackerRepository.findByPaymentId(event.getPaymentId());

        redemptionTracker.voidAuthorizedPoints(event.getPoints());
        int availablePoints = redemptionTracker.getPointsAvailableForRedemption();

        // should never throw
        if (availablePoints < 0) {
            throw new IllegalStateException("Voided points cannot exceed available points");
        }

        Marker marker = MarkerGenerator.generateMarker(redemptionTracker);
        marker.add(Markers.append(REQUEST_ID,event.getRequestId()));

        if (availablePoints == 0) {
            redemptionTrackerRepository.delete(redemptionTracker);
            LOGGER.info(marker, "Void transaction tracked, no more points available, deleting tracker");
        } else {
            redemptionTrackerRepository.save(redemptionTracker);
            LOGGER.info(marker, "Void transaction tracked");
        }
    }

    @EventHandler
    public void on(CapturedTransactionCreatedEvent event) {
        RedemptionTrackerEntity redemptionTracker = redemptionTrackerRepository.findByPaymentId(event.getPaymentId());

        redemptionTracker.addCapturedPoints(event.getPoints());
        int availablePoints = redemptionTracker.getPointsAvailableForRedemption();

        // should never throw
        if (availablePoints < 0) {
            throw new IllegalStateException("Captured points cannot exceed max points");
        }

        Marker marker = MarkerGenerator.generateMarker(redemptionTracker);
        marker.add(Markers.append(REQUEST_ID,event.getRequestId()));

        if (availablePoints == 0) {
            redemptionTrackerRepository.delete(redemptionTracker);
            LOGGER.info(marker, "Capture transaction tracked, no more points available, deleting tracker");
        } else {
            redemptionTrackerRepository.save(redemptionTracker);
            LOGGER.info(marker, "Capture transaction tracked");
        }
    }

    @EventHandler
    public void on(LoyaltyBankDeletedEvent event) {
        List<RedemptionTrackerEntity> redemptionTrackerEntities = redemptionTrackerRepository.findByLoyaltyBankId(event.getLoyaltyBankId());
        redemptionTrackerRepository.deleteAll(redemptionTrackerEntities);

        LOGGER.info(
                MarkerGenerator.generateMarker(event),
                "Deleted {} redemption trackers due to {}",
                redemptionTrackerEntities.size(),
                event.getClass().getSimpleName()
        );
    }
}
