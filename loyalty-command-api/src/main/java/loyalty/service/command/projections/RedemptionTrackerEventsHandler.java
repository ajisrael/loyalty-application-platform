package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.entities.RedemptionTrackerEntity;
import loyalty.service.command.data.repositories.RedemptionTrackerRepository;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.transactions.AuthorizedTransactionCreatedEvent;
import loyalty.service.core.events.transactions.CapturedTransactionCreatedEvent;
import loyalty.service.core.events.transactions.VoidTransactionCreatedEvent;
import loyalty.service.core.exceptions.IllegalProjectionStateException;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import java.util.List;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;


@Component
@ProcessingGroup("redemption-tracker-group")
public class RedemptionTrackerEventsHandler {

    private final RedemptionTrackerRepository redemptionTrackerRepository;
    private final SmartValidator validator;
    private Marker marker = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedemptionTrackerEventsHandler.class);

    public RedemptionTrackerEventsHandler(RedemptionTrackerRepository redemptionTrackerRepository, SmartValidator validator) {
        this.redemptionTrackerRepository = redemptionTrackerRepository;
        this.validator = validator;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        LOGGER.error(marker, exception.getLocalizedMessage());
    }

    @ExceptionHandler(resultType = IllegalProjectionStateException.class)
    public void handle(IllegalProjectionStateException exception) {
        LOGGER.error(marker, exception.getLocalizedMessage());
    }

    @EventHandler
    public void on(AuthorizedTransactionCreatedEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity(
                event.getPaymentId(),
                event.getLoyaltyBankId(),
                event.getPoints(),
                0
        );

        validateEntity(redemptionTrackerEntity);
        redemptionTrackerRepository.save(redemptionTrackerEntity);

        marker.add(MarkerGenerator.generateMarker(redemptionTrackerEntity));

        LOGGER.info(marker, "Authorize transaction tracked");
    }

    @EventHandler
    public void on(VoidTransactionCreatedEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        RedemptionTrackerEntity redemptionTrackerEntity = redemptionTrackerRepository.findByPaymentId(event.getPaymentId());

        redemptionTrackerEntity.voidAuthorizedPoints(event.getPoints());
        int availablePoints = redemptionTrackerEntity.getPointsAvailableForRedemption();

        // should never throw
        if (availablePoints < 0) {
            throw new IllegalStateException("Voided points cannot exceed available points");
        }

        marker.add(MarkerGenerator.generateMarker(redemptionTrackerEntity));

        if (availablePoints == 0) {
            redemptionTrackerRepository.delete(redemptionTrackerEntity);
            LOGGER.info(marker, "Void transaction tracked, no more points available, deleting tracker");
        } else {
            validateEntity(redemptionTrackerEntity);
            redemptionTrackerRepository.save(redemptionTrackerEntity);
            LOGGER.info(marker, "Void transaction tracked");
        }
    }

    @EventHandler
    public void on(CapturedTransactionCreatedEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        RedemptionTrackerEntity redemptionTrackerEntity = redemptionTrackerRepository.findByPaymentId(event.getPaymentId());

        redemptionTrackerEntity.addCapturedPoints(event.getPoints());
        int availablePoints = redemptionTrackerEntity.getPointsAvailableForRedemption();

        // should never throw
        if (availablePoints < 0) {
            throw new IllegalStateException("Captured points cannot exceed max points");
        }

        marker.add(MarkerGenerator.generateMarker(redemptionTrackerEntity));

        if (availablePoints == 0) {
            redemptionTrackerRepository.delete(redemptionTrackerEntity);
            LOGGER.info(marker, "Capture transaction tracked, no more points available, deleting tracker");
        } else {
            validateEntity(redemptionTrackerEntity);
            redemptionTrackerRepository.save(redemptionTrackerEntity);
            LOGGER.info(marker, "Capture transaction tracked");
        }
    }

    @EventHandler
    public void on(LoyaltyBankDeletedEvent event) {
        marker = MarkerGenerator.generateMarker(event);

        List<RedemptionTrackerEntity> redemptionTrackerEntities = redemptionTrackerRepository.findByLoyaltyBankId(event.getLoyaltyBankId());
        redemptionTrackerRepository.deleteAll(redemptionTrackerEntities);

        LOGGER.info(
                marker,
                "Deleted {} redemption trackers due to {}",
                redemptionTrackerEntities.size(),
                event.getClass().getSimpleName()
        );
    }

    private void validateEntity(RedemptionTrackerEntity entity) {
        BindingResult bindingResult = new BeanPropertyBindingResult(entity, "redemptionTrackerEntity");
        validator.validate(entity, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new IllegalProjectionStateException(bindingResult.getFieldError().getDefaultMessage());
        }
    }
}
