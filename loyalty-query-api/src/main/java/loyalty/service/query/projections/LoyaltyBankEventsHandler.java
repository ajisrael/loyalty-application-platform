package loyalty.service.query.projections;

import loyalty.service.core.data.entities.LoyaltyBankEntity;
import loyalty.service.core.data.repositories.LoyaltyBankRepository;
import loyalty.service.core.events.AllPointsExpiredEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.transactions.*;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.exceptions.LoyaltyBankNotFoundException;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.*;

@Component
@ProcessingGroup("loyalty-bank-group")
public class LoyaltyBankEventsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyBankEventsHandler.class);

    private final LoyaltyBankRepository loyaltyBankRepository;

    public LoyaltyBankEventsHandler(LoyaltyBankRepository loyaltyBankRepository) {
        this.loyaltyBankRepository = loyaltyBankRepository;
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        LOGGER.error(exception.getLocalizedMessage());
    }

    @EventHandler
    public void on(LoyaltyBankCreatedEvent event) {
        LoyaltyBankEntity loyaltyBankEntity = new LoyaltyBankEntity();
        BeanUtils.copyProperties(event, loyaltyBankEntity);
        loyaltyBankRepository.save(loyaltyBankEntity);

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), LOYALTY_BANK_SAVED_IN_DB, event.getLoyaltyBankId());
    }

    @EventHandler
    public void on(LoyaltyBankDeletedEvent event) {
        Optional<LoyaltyBankEntity> loyaltyBankEntityOptional = loyaltyBankRepository.findByLoyaltyBankId(event.getLoyaltyBankId());

        if (loyaltyBankEntityOptional.isPresent()) {
            loyaltyBankRepository.delete(loyaltyBankEntityOptional.get());
            LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), LOYALTY_BANK_DELETED_FROM_DB, event.getLoyaltyBankId());
        } else {
            logAndThrowLoyaltyBankNotFoundException(event.getRequestId(), event.getLoyaltyBankId());
        }
    }

    @EventHandler
    public void on(PendingTransactionCreatedEvent event) {
        Optional<LoyaltyBankEntity> loyaltyBankEntityOptional = loyaltyBankRepository.findByLoyaltyBankId(event.getLoyaltyBankId());

        if (loyaltyBankEntityOptional.isPresent()) {
            LoyaltyBankEntity loyaltyBankEntity = loyaltyBankEntityOptional.get();
            loyaltyBankEntity.setPending(loyaltyBankEntity.getPending() + event.getPoints());
            loyaltyBankRepository.save(loyaltyBankEntity);

            LOGGER.info(
                    MarkerGenerator.generateMarker(event),
                    PROCESSED_EVENT_FOR_LOYALTY_BANK,
                    event.getClass().getSimpleName(),
                    event.getLoyaltyBankId()
            );
        } else {
            logAndThrowLoyaltyBankNotFoundException(event.getRequestId(), event.getLoyaltyBankId());
        }
    }

    @EventHandler
    public void on(EarnedTransactionCreatedEvent event) {
        Optional<LoyaltyBankEntity> loyaltyBankEntityOptional = loyaltyBankRepository.findByLoyaltyBankId(event.getLoyaltyBankId());

        if (loyaltyBankEntityOptional.isPresent()) {
            LoyaltyBankEntity loyaltyBankEntity = loyaltyBankEntityOptional.get();
            loyaltyBankEntity.setPending(loyaltyBankEntity.getPending() - event.getPoints());
            loyaltyBankEntity.setEarned(loyaltyBankEntity.getEarned() + event.getPoints());
            loyaltyBankRepository.save(loyaltyBankEntity);

            LOGGER.info(
                    MarkerGenerator.generateMarker(event),
                    PROCESSED_EVENT_FOR_LOYALTY_BANK,
                    event.getClass().getSimpleName(),
                    event.getLoyaltyBankId()
            );
        } else {
            logAndThrowLoyaltyBankNotFoundException(event.getRequestId(), event.getLoyaltyBankId());
        }
    }

    @EventHandler
    public void on(AwardedTransactionCreatedEvent event) {
        Optional<LoyaltyBankEntity> loyaltyBankEntityOptional = loyaltyBankRepository.findByLoyaltyBankId(event.getLoyaltyBankId());

        if (loyaltyBankEntityOptional.isPresent()) {
            LoyaltyBankEntity loyaltyBankEntity = loyaltyBankEntityOptional.get();
            loyaltyBankEntity.setEarned(loyaltyBankEntity.getEarned() + event.getPoints());
            loyaltyBankRepository.save(loyaltyBankEntity);

            LOGGER.info(
                    MarkerGenerator.generateMarker(event),
                    PROCESSED_EVENT_FOR_LOYALTY_BANK,
                    event.getClass().getSimpleName(),
                    event.getLoyaltyBankId()
            );
        } else {
            logAndThrowLoyaltyBankNotFoundException(event.getRequestId(), event.getLoyaltyBankId());
        }
    }

    @EventHandler
    public void on(AuthorizedTransactionCreatedEvent event) {
        Optional<LoyaltyBankEntity> loyaltyBankEntityOptional = loyaltyBankRepository.findByLoyaltyBankId(event.getLoyaltyBankId());

        if (loyaltyBankEntityOptional.isPresent()) {
            LoyaltyBankEntity loyaltyBankEntity = loyaltyBankEntityOptional.get();
            loyaltyBankEntity.setAuthorized(loyaltyBankEntity.getAuthorized() + event.getPoints());
            loyaltyBankRepository.save(loyaltyBankEntity);

            LOGGER.info(
                    MarkerGenerator.generateMarker(event),
                    PROCESSED_EVENT_FOR_LOYALTY_BANK,
                    event.getClass().getSimpleName(),
                    event.getLoyaltyBankId()
            );
        } else {
            logAndThrowLoyaltyBankNotFoundException(event.getRequestId(), event.getLoyaltyBankId());
        }
    }

    @EventHandler
    public void on(VoidTransactionCreatedEvent event) {
        Optional<LoyaltyBankEntity> loyaltyBankEntityOptional = loyaltyBankRepository.findByLoyaltyBankId(event.getLoyaltyBankId());

        if (loyaltyBankEntityOptional.isPresent()) {
            LoyaltyBankEntity loyaltyBankEntity = loyaltyBankEntityOptional.get();
            loyaltyBankEntity.setAuthorized(loyaltyBankEntity.getAuthorized() - event.getPoints());
            loyaltyBankRepository.save(loyaltyBankEntity);

            LOGGER.info(
                    MarkerGenerator.generateMarker(event),
                    PROCESSED_EVENT_FOR_LOYALTY_BANK,
                    event.getClass().getSimpleName(),
                    event.getLoyaltyBankId()
            );
        } else {
            logAndThrowLoyaltyBankNotFoundException(event.getRequestId(), event.getLoyaltyBankId());
        }
    }

    @EventHandler
    public void on(CapturedTransactionCreatedEvent event) {
        Optional<LoyaltyBankEntity> loyaltyBankEntityOptional = loyaltyBankRepository.findByLoyaltyBankId(event.getLoyaltyBankId());

        if (loyaltyBankEntityOptional.isPresent()) {
            LoyaltyBankEntity loyaltyBankEntity = loyaltyBankEntityOptional.get();
            loyaltyBankEntity.setAuthorized(loyaltyBankEntity.getAuthorized() - event.getPoints());
            loyaltyBankEntity.setCaptured(loyaltyBankEntity.getCaptured() + event.getPoints());
            loyaltyBankRepository.save(loyaltyBankEntity);

            LOGGER.info(
                    MarkerGenerator.generateMarker(event),
                    PROCESSED_EVENT_FOR_LOYALTY_BANK,
                    event.getClass().getSimpleName(),
                    event.getLoyaltyBankId()
            );
        } else {
            logAndThrowLoyaltyBankNotFoundException(event.getRequestId(), event.getLoyaltyBankId());
        }
    }

    @EventHandler
    public void on(AllPointsExpiredEvent event) {
        Optional<LoyaltyBankEntity> loyaltyBankEntityOptional = loyaltyBankRepository.findByLoyaltyBankId(event.getLoyaltyBankId());

        if (loyaltyBankEntityOptional.isPresent()) {
            LoyaltyBankEntity loyaltyBankEntity = loyaltyBankEntityOptional.get();
            loyaltyBankEntity.setPending(loyaltyBankEntity.getPending() - event.getPendingPointsRemoved());
            loyaltyBankEntity.setAuthorized(loyaltyBankEntity.getAuthorized() - event.getAuthorizedPointsVoided());
            loyaltyBankEntity.setCaptured(loyaltyBankEntity.getCaptured() + event.getAvailablePointsCaptured());
            loyaltyBankRepository.save(loyaltyBankEntity);

            LOGGER.info(
                    MarkerGenerator.generateMarker(event),
                    PROCESSED_EVENT_FOR_LOYALTY_BANK,
                    event.getClass().getSimpleName(),
                    event.getLoyaltyBankId()
            );
        } else {
            logAndThrowLoyaltyBankNotFoundException(event.getRequestId(), event.getLoyaltyBankId());
        }
    }

    private void logAndThrowLoyaltyBankNotFoundException(String requestId, String loyaltyBankId) {
        LOGGER.error(Markers.append(REQUEST_ID, requestId), LOYALTY_BANK_NOT_FOUND_IN_DB, loyaltyBankId);
        throw new LoyaltyBankNotFoundException(loyaltyBankId);
    }
}
