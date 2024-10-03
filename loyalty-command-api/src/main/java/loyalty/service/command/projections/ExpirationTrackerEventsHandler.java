package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.command.data.entities.ExpirationTrackerEntity;
import loyalty.service.command.data.entities.TransactionEntity;
import loyalty.service.command.data.repositories.ExpirationTrackerRepository;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.transactions.*;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;


@Component
@AllArgsConstructor
@ProcessingGroup("expiration-tracker-group")
public class ExpirationTrackerEventsHandler {

    private ExpirationTrackerRepository expirationTrackerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpirationTrackerEventsHandler.class);

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
        ExpirationTrackerEntity expirationTrackerEntity = new ExpirationTrackerEntity(event.getLoyaltyBankId());

        expirationTrackerRepository.save(expirationTrackerEntity);
    }

    @EventHandler
    public void on(EarnedTransactionCreatedEvent event, @Timestamp java.time.Instant eventTimestamp) {
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(event.getLoyaltyBankId());
        TransactionEntity transactionEntity = new TransactionEntity(event.getRequestId(), event.getPoints(), eventTimestamp);
        expirationTrackerEntity.addTransaction(transactionEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);
    }

    @EventHandler
    public void on(AwardedTransactionCreatedEvent event, @Timestamp java.time.Instant eventTimestamp) {
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(event.getLoyaltyBankId());
        TransactionEntity transactionEntity = new TransactionEntity(event.getRequestId(), event.getPoints(), eventTimestamp);
        expirationTrackerEntity.addTransaction(transactionEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);
    }

    @EventHandler
    public void on(CapturedTransactionCreatedEvent event, @Timestamp java.time.Instant eventTimestamp) {
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(event.getLoyaltyBankId());
        applyCapturedPointsToTransactions(event.getPoints(), expirationTrackerEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);
    }

    private void applyCapturedPointsToTransactions(int points, ExpirationTrackerEntity expirationTrackerEntity) {
        while (points > 0) {
            TransactionEntity oldestTransaction = expirationTrackerEntity.getTransactionList().get(0);
            oldestTransaction.setPoints(oldestTransaction.getPoints() - points);
            points = oldestTransaction.getPoints() * -1;

            if (oldestTransaction.getPoints() < 0) {
                expirationTrackerEntity.removeTransaction(oldestTransaction);
            }
        }
    }

    @EventHandler
    public void on(LoyaltyBankDeletedEvent event) {
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(event.getLoyaltyBankId());
        expirationTrackerRepository.delete(expirationTrackerEntity);

        LOGGER.info(
                MarkerGenerator.generateMarker(event),
                "Deleted expiration tracker due to {}",
                event.getClass().getSimpleName()
        );
    }
}
