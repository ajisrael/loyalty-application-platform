package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.command.data.entities.ExpirationTrackerEntity;
import loyalty.service.command.data.entities.TransactionEntity;
import loyalty.service.command.data.repositories.ExpirationTrackerRepository;
import loyalty.service.command.data.repositories.TransactionRepository;
import loyalty.service.core.events.AllPointsExpiredEvent;
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

import java.time.Instant;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;


@Component
@AllArgsConstructor
@ProcessingGroup("expiration-tracker-group")
public class ExpirationTrackerEventsHandler {

    private ExpirationTrackerRepository expirationTrackerRepository;
    private TransactionRepository transactionRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpirationTrackerEventsHandler.class);

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {
        LOGGER.error(exception.getLocalizedMessage());
        throw exception;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        LOGGER.error(exception.getLocalizedMessage());
    }

    @EventHandler
    public void on(LoyaltyBankCreatedEvent event) {
        String loyaltyBankId = event.getLoyaltyBankId();
        ExpirationTrackerEntity expirationTrackerEntity = new ExpirationTrackerEntity(loyaltyBankId);
        expirationTrackerRepository.save(expirationTrackerEntity);

        Marker marker = MarkerGenerator.generateMarker(expirationTrackerEntity);
        marker.add(Markers.append(REQUEST_ID,event.getRequestId()));

        LOGGER.info(marker, "Expiration tracker created for loyalty bank {}", loyaltyBankId);
    }

    @EventHandler
    public void on(EarnedTransactionCreatedEvent event, @Timestamp java.time.Instant eventTimestamp) {
        createAndSaveTransactionForLoyaltyBank(event, eventTimestamp, event.getLoyaltyBankId());
    }

    @EventHandler
    public void on(AwardedTransactionCreatedEvent event, @Timestamp java.time.Instant eventTimestamp) {
        createAndSaveTransactionForLoyaltyBank(event, eventTimestamp, event.getLoyaltyBankId());
    }

    @EventHandler
    public void on(AuthorizedTransactionCreatedEvent event) {
        String loyaltyBankId = event.getLoyaltyBankId();
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);
        applyPointsToTransactions(event.getPoints(), expirationTrackerEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);

        Marker marker = MarkerGenerator.generateMarker(expirationTrackerEntity);
        marker.add(Markers.append(REQUEST_ID,event.getRequestId()));

        LOGGER.info(marker, "Authorized points applied to transactions for loyalty bank {}", loyaltyBankId);
    }

    @EventHandler
    public void on(VoidTransactionCreatedEvent event, @Timestamp Instant eventTimeStamp) {
        String loyaltyBankId = event.getLoyaltyBankId();
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);

        if (expirationTrackerEntity.getTransactionList().isEmpty()) {
            expirationTrackerEntity.getTransactionList().add(
                    new TransactionEntity(event.getRequestId(), event.getPoints(), eventTimeStamp, loyaltyBankId)
            );
        } else {
            expirationTrackerEntity.getTransactionList().get(0).addPoints(event.getPoints());
        }

        expirationTrackerRepository.save(expirationTrackerEntity);

        Marker marker = MarkerGenerator.generateMarker(expirationTrackerEntity);
        marker.add(Markers.append(REQUEST_ID,event.getRequestId()));

        LOGGER.info(marker, "Voided points applied to transactions for loyalty bank {}", loyaltyBankId);
    }

    @EventHandler
    public void on(ExpiredTransactionCreatedEvent event) {
        String loyaltyBankId = event.getLoyaltyBankId();
        TransactionEntity transactionEntity = transactionRepository.findByTransactionId(event.getTargetTransactionId());

        if (transactionEntity.getPoints() != event.getPoints()) {
            Marker marker = MarkerGenerator.generateMarker(transactionEntity);
            marker.add(MarkerGenerator.generateMarker(event));
            LOGGER.error(marker, "Points expired do not match transaction");
        }

        transactionRepository.deleteById(event.getTargetTransactionId());

        Marker marker = MarkerGenerator.generateMarker(transactionEntity);
        marker.add(Markers.append(REQUEST_ID,event.getRequestId()));

        LOGGER.info(marker, "Removed transaction for loyalty bank {} due to expiration", loyaltyBankId);
    }

    @EventHandler
    public void on(AllPointsExpiredEvent event) {
        String loyaltyBankId = event.getLoyaltyBankId();
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);
        expirationTrackerEntity.getTransactionList().clear();
        expirationTrackerRepository.save(expirationTrackerEntity);

        Marker marker = MarkerGenerator.generateMarker(expirationTrackerEntity);
        marker.add(Markers.append(REQUEST_ID,event.getRequestId()));

        LOGGER.info(marker, "Transactions cleared for loyalty bank {}", loyaltyBankId);
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

    private void createAndSaveTransactionForLoyaltyBank(AbstractTransactionEvent event, Instant eventTimestamp, String loyaltyBankId) {
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);
        TransactionEntity transactionEntity = new TransactionEntity(event.getRequestId(), event.getPoints(), eventTimestamp, loyaltyBankId);
        expirationTrackerEntity.addTransaction(transactionEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);

        Marker marker = MarkerGenerator.generateMarker(transactionEntity);
        marker.add(Markers.append(REQUEST_ID,event.getRequestId()));

        LOGGER.info(marker, "TransactionEntity created for loyalty bank {}", loyaltyBankId);
    }

    private void applyPointsToTransactions(int points, ExpirationTrackerEntity expirationTrackerEntity) {
        while (points > 0) {
            TransactionEntity oldestTransaction = expirationTrackerEntity.getTransactionList().get(0);
            oldestTransaction.setPoints(oldestTransaction.getPoints() - points);
            points = oldestTransaction.getPoints() * -1;

            if (oldestTransaction.getPoints() < 0) {
                expirationTrackerEntity.removeTransaction(oldestTransaction);
            }
        }
    }
}
