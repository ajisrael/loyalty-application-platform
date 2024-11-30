package loyalty.service.command.projections;

import loyalty.service.command.data.entities.ExpirationTrackerEntity;
import loyalty.service.command.data.entities.TransactionEntity;
import loyalty.service.command.data.repositories.ExpirationTrackerRepository;
import loyalty.service.command.data.repositories.TransactionRepository;
import loyalty.service.core.events.AllPointsExpiredEvent;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.transactions.*;
import loyalty.service.core.exceptions.ExpirationTrackerNotFoundException;
import loyalty.service.core.exceptions.IllegalProjectionStateException;
import loyalty.service.core.exceptions.TransactionNotFoundException;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import java.time.Instant;
import java.util.List;

import static loyalty.service.core.constants.DomainConstants.*;
import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.constants.LogMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfEntityDoesNotExist;
import static loyalty.service.core.utils.MarkerGenerator.generateMarker;


@Component
@ProcessingGroup(EXPIRATION_TRACKER_GROUP)
public class ExpirationTrackerEventsHandler {

    private final ExpirationTrackerRepository expirationTrackerRepository;
    private final TransactionRepository transactionRepository;
    private final SmartValidator validator;
    private Marker marker = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpirationTrackerEventsHandler.class);

    public ExpirationTrackerEventsHandler(ExpirationTrackerRepository expirationTrackerRepository, TransactionRepository transactionRepository, SmartValidator validator) {
        this.expirationTrackerRepository = expirationTrackerRepository;
        this.transactionRepository = transactionRepository;
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
    public void on(LoyaltyBankCreatedEvent event) {
        marker = Markers.append(REQUEST_ID,event.getRequestId());

        String loyaltyBankId = event.getLoyaltyBankId();
        ExpirationTrackerEntity expirationTrackerEntity = new ExpirationTrackerEntity(loyaltyBankId);

        validateEntity(expirationTrackerEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);

        marker.add(generateMarker(expirationTrackerEntity));

        LOGGER.info(marker, EXPIRATION_TRACKER_CREATED_FOR_LOYALTY_BANK, loyaltyBankId);
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
        marker = Markers.append(REQUEST_ID,event.getRequestId());

        String loyaltyBankId = event.getLoyaltyBankId();
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);
        throwExceptionIfExpirationTrackerDoesNotExist(expirationTrackerEntity, loyaltyBankId);

        applyPointsToTransactions(event.getPoints(), expirationTrackerEntity);
        validateEntity(expirationTrackerEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);

        marker = generateMarker(event);

        LOGGER.info(marker, AUTHORIZED_POINTS_APPLIED_TO_TRANSACTIONS_FOR_LOYALTY_BANK, loyaltyBankId);
    }

    @EventHandler
    public void on(VoidTransactionCreatedEvent event, @Timestamp Instant eventTimeStamp) {
        marker = Markers.append(REQUEST_ID,event.getRequestId());

        String loyaltyBankId = event.getLoyaltyBankId();
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);

        if (expirationTrackerEntity.getTransactionList().isEmpty()) {
            TransactionEntity transactionEntity = new TransactionEntity(event.getRequestId(), event.getPoints(), eventTimeStamp, loyaltyBankId);
            validateEntity(transactionEntity);
            expirationTrackerEntity.getTransactionList().add(transactionEntity);
        } else {
            TransactionEntity transactionEntity = expirationTrackerEntity.getTransactionList().get(0);
            transactionEntity.addPoints(event.getPoints());
            validateEntity(transactionEntity);
        }

        validateEntity(expirationTrackerEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);

        marker.add(generateMarker(expirationTrackerEntity));

        LOGGER.info(marker, "Voided points applied to transactions for loyalty bank {}", loyaltyBankId);
    }

    @EventHandler
    public void on(ExpiredTransactionCreatedEvent event) {
        marker = Markers.append(REQUEST_ID,event.getRequestId());

        String loyaltyBankId = event.getLoyaltyBankId();
        TransactionEntity transactionEntity = transactionRepository.findByTransactionId(event.getTargetTransactionId());

        if (transactionEntity.getPoints() != event.getPoints()) {
            marker.add(generateMarker(transactionEntity));
            marker.add(generateMarker(event));
            LOGGER.error(marker, "Points expired do not match transaction");
        }

        transactionRepository.deleteById(event.getTargetTransactionId());

        marker.add(generateMarker(transactionEntity));

        LOGGER.info(marker, "Removed transaction for loyalty bank {} due to expiration", loyaltyBankId);
    }

    @EventHandler
    public void on(AllPointsExpiredEvent event) {
        marker = Markers.append(REQUEST_ID,event.getRequestId());

        String loyaltyBankId = event.getLoyaltyBankId();
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);
        // TODO: BUG clearing transaction list doesn't remove transactions from table in db
        expirationTrackerEntity.getTransactionList().clear();
        validateEntity(expirationTrackerEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);

        marker.add(generateMarker(expirationTrackerEntity));

        LOGGER.info(marker, "Transactions cleared for loyalty bank {}", loyaltyBankId);
    }

    @EventHandler
    public void on(LoyaltyBankDeletedEvent event) {
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(event.getLoyaltyBankId());
        throwExceptionIfEntityDoesNotExist(expirationTrackerEntity, String.format(EXPIRATION_TRACKER_FOR_LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST, event.getLoyaltyBankId()));
        expirationTrackerRepository.delete(expirationTrackerEntity);

        LOGGER.info(
                generateMarker(event),
                "Deleted expiration tracker due to {}",
                event.getClass().getSimpleName()
        );
    }

    private void createAndSaveTransactionForLoyaltyBank(AbstractTransactionEvent event, Instant eventTimestamp, String loyaltyBankId) {
        marker = Markers.append(REQUEST_ID,event.getRequestId());

        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);
        throwExceptionIfExpirationTrackerDoesNotExist(expirationTrackerEntity, event.getLoyaltyBankId());

        TransactionEntity transactionEntity = new TransactionEntity(event.getRequestId(), event.getPoints(), eventTimestamp, loyaltyBankId);
        validateEntity(transactionEntity);
        expirationTrackerEntity.addTransaction(transactionEntity);
        validateEntity(expirationTrackerEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);

        marker.add(generateMarker(transactionEntity));

        LOGGER.info(marker, TRANSACTION_ENTITY_CREATED_FOR_LOYALTY_BANK, loyaltyBankId);
    }

    private void applyPointsToTransactions(int points, ExpirationTrackerEntity expirationTrackerEntity) {
        while (points > 0) {
            List<TransactionEntity> transactions = expirationTrackerEntity.getTransactionList();

            if (transactions.isEmpty()) {
                throw new IllegalProjectionStateException("Authorized more points than earned, unable to process authorization event for expiration tracker");
            }

            TransactionEntity oldestTransaction = expirationTrackerEntity.getTransactionList().get(0);
            String transactionId = oldestTransaction.getTransactionId();
            int transactionPoints = oldestTransaction.getPoints();

            marker.add(Markers.append(LOYALTY_BANK_ID, oldestTransaction.getLoyaltyBankId()));
            marker.add(Markers.append(TRANSACTION_ID, transactionId));

            LOGGER.info(marker, APPLYING_REMAINING_AUTHORIZED_POINTS_TO_POINTS_ON_OLDEST_TRANSACTION, points, transactionPoints, transactionId);

            transactionPoints -= points;
            oldestTransaction.setPoints(transactionPoints);
            points = transactionPoints * -1;

            if (transactionPoints <= 0) {
                LOGGER.info(marker, ALL_POINTS_USED_FOR_TRANSACTION_REMOVING_TRANSACTION_FROM_EXPIRATION_TRACKER, transactionId);
                expirationTrackerEntity.removeTransaction(oldestTransaction);
            }
        }
    }

    private void validateEntity(ExpirationTrackerEntity entity) {
        BindingResult bindingResult = new BeanPropertyBindingResult(entity, "expirationTrackerEntity");
        validator.validate(entity, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new IllegalProjectionStateException(bindingResult.getFieldError().getDefaultMessage());
        }
    }

    private void validateEntity(TransactionEntity entity) {
        BindingResult bindingResult = new BeanPropertyBindingResult(entity, "transactionEntity");
        validator.validate(entity, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new IllegalProjectionStateException(bindingResult.getFieldError().getDefaultMessage());
        }
    }

    private void throwExceptionIfExpirationTrackerDoesNotExist(ExpirationTrackerEntity entity, String loyaltyBankId) {
        if (entity == null) {
            throw new ExpirationTrackerNotFoundException(loyaltyBankId);
        }
    }

    private void throwExceptionIfTransactionDoesNotExist(TransactionEntity entity) {
        if (entity == null) {
            throw new TransactionNotFoundException();
        }
    }
}
