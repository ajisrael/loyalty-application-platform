package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.command.data.entities.BusinessLookupEntity;
import loyalty.service.command.data.entities.ExpirationTrackerEntity;
import loyalty.service.command.data.entities.TransactionEntity;
import loyalty.service.command.data.repositories.ExpirationTrackerRepository;
import loyalty.service.command.data.repositories.TransactionRepository;
import loyalty.service.core.events.AllPointsExpiredEvent;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.transactions.*;
import loyalty.service.core.exceptions.IllegalProjectionStateException;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import java.time.Instant;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;


@Component
@ProcessingGroup("expiration-tracker-group")
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
        LOGGER.error(exception.getLocalizedMessage());
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

        marker.add(MarkerGenerator.generateMarker(expirationTrackerEntity));

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
        marker = Markers.append(REQUEST_ID,event.getRequestId());

        String loyaltyBankId = event.getLoyaltyBankId();
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);
        applyPointsToTransactions(event.getPoints(), expirationTrackerEntity);
        validateEntity(expirationTrackerEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);

        marker.add(MarkerGenerator.generateMarker(expirationTrackerEntity));

        LOGGER.info(marker, "Authorized points applied to transactions for loyalty bank {}", loyaltyBankId);
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

        marker.add(MarkerGenerator.generateMarker(expirationTrackerEntity));

        LOGGER.info(marker, "Voided points applied to transactions for loyalty bank {}", loyaltyBankId);
    }

    @EventHandler
    public void on(ExpiredTransactionCreatedEvent event) {
        marker = Markers.append(REQUEST_ID,event.getRequestId());

        String loyaltyBankId = event.getLoyaltyBankId();
        TransactionEntity transactionEntity = transactionRepository.findByTransactionId(event.getTargetTransactionId());

        if (transactionEntity.getPoints() != event.getPoints()) {
            marker.add(MarkerGenerator.generateMarker(transactionEntity));
            marker.add(MarkerGenerator.generateMarker(event));
            LOGGER.error(marker, "Points expired do not match transaction");
        }

        transactionRepository.deleteById(event.getTargetTransactionId());

        marker.add(MarkerGenerator.generateMarker(transactionEntity));

        LOGGER.info(marker, "Removed transaction for loyalty bank {} due to expiration", loyaltyBankId);
    }

    @EventHandler
    public void on(AllPointsExpiredEvent event) {
        marker = Markers.append(REQUEST_ID,event.getRequestId());

        String loyaltyBankId = event.getLoyaltyBankId();
        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);
        expirationTrackerEntity.getTransactionList().clear();
        validateEntity(expirationTrackerEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);

        marker.add(MarkerGenerator.generateMarker(expirationTrackerEntity));

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
        marker = Markers.append(REQUEST_ID,event.getRequestId());

        ExpirationTrackerEntity expirationTrackerEntity = expirationTrackerRepository.findByLoyaltyBankId(loyaltyBankId);
        TransactionEntity transactionEntity = new TransactionEntity(event.getRequestId(), event.getPoints(), eventTimestamp, loyaltyBankId);
        validateEntity(transactionEntity);
        expirationTrackerEntity.addTransaction(transactionEntity);
        validateEntity(expirationTrackerEntity);
        expirationTrackerRepository.save(expirationTrackerEntity);

        marker.add(MarkerGenerator.generateMarker(transactionEntity));

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
}
