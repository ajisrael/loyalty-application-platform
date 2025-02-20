package loyalty.service.command.aggregates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.command.commands.*;
import loyalty.service.command.commands.rollbacks.RollbackLoyaltyBankCreationCommand;
import loyalty.service.command.commands.transactions.*;
import loyalty.service.command.utils.LogHelper;
import loyalty.service.core.events.loyalty.bank.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.loyalty.bank.AllPointsExpiredEvent;
import loyalty.service.core.events.loyalty.bank.transactions.*;
import loyalty.service.core.events.loyalty.bank.LoyaltyBankCreatedEvent;
import loyalty.service.core.exceptions.FailedToExpireLoyaltyPointsException;
import loyalty.service.core.exceptions.IllegalLoyaltyBankStateException;
import loyalty.service.core.exceptions.InsufficientPointsException;
import loyalty.service.core.utils.MarkerGenerator;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.annotation.MetaDataValue;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static loyalty.service.core.constants.DomainConstants.*;
import static loyalty.service.core.constants.LogMessages.INSUFFICIENT_AVAILABLE_POINTS_FOR_AUTHORIZATION;
import static loyalty.service.core.constants.MetaDataKeys.SKIP_POINTS_CHECK;

@Aggregate
@NoArgsConstructor
@Getter
public class LoyaltyBankAggregate {

    public static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyBankAggregate.class);

    @AggregateIdentifier
    private String loyaltyBankId;
    private String accountId;
    private String businessId;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;


    @CommandHandler
    public LoyaltyBankAggregate(CreateLoyaltyBankCommand command) {
        LoyaltyBankCreatedEvent event = LoyaltyBankCreatedEvent.builder()
                .requestId(command.getRequestId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .accountId(command.getAccountId())
                .businessId(command.getBusinessId())
                .pending(0)
                .earned(0)
                .authorized(0)
                .captured(0)
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(CreatePendingTransactionCommand command) {
        if (this.pending + command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(PENDING);
        }

        PendingTransactionCreatedEvent event = PendingTransactionCreatedEvent.builder()
                .requestId(command.getRequestId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(CreateEarnedTransactionCommand command) {
        if (this.pending - command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(PENDING);
        }

        if (this.earned + command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(EARNED);
        }

        EarnedTransactionCreatedEvent event = EarnedTransactionCreatedEvent.builder()
                .requestId(command.getRequestId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(CreateAwardedTransactionCommand command) {
        if (this.earned + command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(EARNED);
        }

        AwardedTransactionCreatedEvent event = AwardedTransactionCreatedEvent.builder()
                .requestId(command.getRequestId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(CreateAuthorizedTransactionCommand command) {
        if (this.authorized + command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(AUTHORIZED);
        }
        if (this.getAvailablePoints() < command.getPoints()) {
            LOGGER.info(MarkerGenerator.generateMarker(this), INSUFFICIENT_AVAILABLE_POINTS_FOR_AUTHORIZATION, command.getPoints());
            throw new InsufficientPointsException();
        }

        AuthorizedTransactionCreatedEvent event = AuthorizedTransactionCreatedEvent.builder()
                .requestId(command.getRequestId())
                .paymentId(command.getPaymentId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(CreateVoidTransactionCommand command) {
        if (this.authorized - command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(AUTHORIZED);
        }

        VoidTransactionCreatedEvent event = VoidTransactionCreatedEvent.builder()
                .requestId(command.getRequestId())
                .paymentId(command.getPaymentId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(CreateCapturedTransactionCommand command) {
        if (this.authorized - command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(AUTHORIZED);
        }
        if (this.captured + command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(CAPTURED);
        }

        CapturedTransactionCreatedEvent event = CapturedTransactionCreatedEvent.builder()
                .requestId(command.getRequestId())
                .paymentId(command.getPaymentId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(CreateExpirePointsTransactionCommand command) {
        if (this.captured + command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(CAPTURED);
        }

        ExpiredTransactionCreatedEvent event = ExpiredTransactionCreatedEvent.builder()
                .requestId(command.getRequestId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .targetTransactionId(command.getTargetTransactionId())
                .points(command.getPoints())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(ExpireAllPointsCommand command) {
        AllPointsExpiredEvent event = AllPointsExpiredEvent.builder()
                .requestId(command.getRequestId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .accountId(this.accountId)
                .businessId(this.businessId)
                .pendingPointsRemoved(this.pending)
                .authorizedPointsVoided(this.authorized)
                .pointsExpired(this.earned - this.captured)
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(DeleteLoyaltyBankCommand command, @MetaDataValue(SKIP_POINTS_CHECK) Boolean skipPointsCheck) {
        // We always want to make sure all points are expired before deletion to know we have an accurate count for
        // any outstanding balances required to hold in reserve.
        throwExceptionIfLoyaltyBankStillHasAvailablePoints();

        LoyaltyBankDeletedEvent event = LoyaltyBankDeletedEvent.builder()
                .requestId(command.getRequestId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .accountId(this.accountId)
                .businessId(this.businessId)
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(RollbackLoyaltyBankCreationCommand command) {
        LoyaltyBankDeletedEvent event = LoyaltyBankDeletedEvent.builder()
                .requestId(command.getRequestId())
                .loyaltyBankId(command.getLoyaltyBankId())
                .accountId(this.accountId)
                .businessId(this.businessId)
                .build();

        MetaData metaData = MetaData.with("reason", "rollback");

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event, metaData);
    }

    @EventSourcingHandler
    public void on(LoyaltyBankCreatedEvent event) {
        this.loyaltyBankId = event.getLoyaltyBankId();
        this.accountId = event.getAccountId();
        this.businessId = event.getBusinessId();
        this.pending = event.getPending();
        this.earned = event.getEarned();
        this.authorized = event.getAuthorized();
        this.captured = event.getCaptured();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(PendingTransactionCreatedEvent event) {
        this.pending += event.getPoints();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(EarnedTransactionCreatedEvent event) {
        this.pending -= event.getPoints();
        this.earned += event.getPoints();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(AwardedTransactionCreatedEvent event) {
        this.earned += event.getPoints();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(AuthorizedTransactionCreatedEvent event) {
        this.authorized += event.getPoints();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(VoidTransactionCreatedEvent event) {
        this.authorized -= event.getPoints();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(CapturedTransactionCreatedEvent event) {
        this.authorized -= event.getPoints();
        this.captured += event.getPoints();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(ExpiredTransactionCreatedEvent event) {
        this.captured += event.getPoints();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(AllPointsExpiredEvent event) {
        this.pending -= event.getPendingPointsRemoved();
        this.authorized -= event.getAuthorizedPointsVoided();
        this.captured += event.getPointsExpired();

        // Should never throw
        throwExceptionIfLoyaltyBankStillHasAvailablePoints();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(LoyaltyBankDeletedEvent event) {
        AggregateLifecycle.markDeleted();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    protected int getAvailablePoints() {
        return this.earned - this.authorized - this.captured;
    }

    private void throwExceptionIfLoyaltyBankStillHasAvailablePoints() {
        if (this.pending != 0 || this.authorized != 0 || this.earned != this.captured) {
            throw new FailedToExpireLoyaltyPointsException(this.loyaltyBankId);
        }
    }
}
