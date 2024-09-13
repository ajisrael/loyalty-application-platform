package loyalty.service.command.aggregates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.command.commands.*;
import loyalty.service.core.events.transactions.AuthorizedTransactionCreatedEvent;
import loyalty.service.core.events.transactions.EarnedTransactionCreatedEvent;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.transactions.PendingTransactionCreatedEvent;
import loyalty.service.core.events.transactions.CapturedTransactionCreatedEvent;
import loyalty.service.core.exceptions.IllegalLoyaltyBankStateException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import static loyalty.service.core.constants.DomainConstants.*;

@Aggregate
@NoArgsConstructor
@Getter
public class LoyaltyBankAggregate {

    @AggregateIdentifier
    private String loyaltyBankId;
    private String accountId;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;


    @CommandHandler
    public LoyaltyBankAggregate(CreateLoyaltyBankCommand command) {
        LoyaltyBankCreatedEvent event = LoyaltyBankCreatedEvent.builder()
                .loyaltyBankId(command.getLoyaltyBankId())
                .accountId(command.getAccountId())
                .pending(0)
                .earned(0)
                .authorized(0)
                .captured(0)
                .build();

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(CreatePendingTransactionCommand command) {
        if (this.pending + command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(PENDING);
        }

        PendingTransactionCreatedEvent event = PendingTransactionCreatedEvent.builder()
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

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
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(CreateAuthorizedTransactionCommand command) {
        if (this.authorized + command.getPoints() < 0) {
            throw new IllegalLoyaltyBankStateException(AUTHORIZED);
        }

        AuthorizedTransactionCreatedEvent event = AuthorizedTransactionCreatedEvent.builder()
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

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
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(LoyaltyBankCreatedEvent event) {
        this.loyaltyBankId = event.getLoyaltyBankId();
        this.accountId = event.getAccountId();
        this.pending = event.getPending();
        this.earned = event.getEarned();
        this.authorized = event.getAuthorized();
        this.captured = event.getCaptured();
    }

    @EventSourcingHandler
    public void on(PendingTransactionCreatedEvent event) {
        this.pending += event.getPoints();
    }

    @EventSourcingHandler
    public void on(EarnedTransactionCreatedEvent event) {
        this.pending -= event.getPoints();
        this.earned += event.getPoints();
    }
}
