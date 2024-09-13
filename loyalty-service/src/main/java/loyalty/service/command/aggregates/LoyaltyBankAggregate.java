package loyalty.service.command.aggregates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.commands.CreatePendingTransactionCommand;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.PendingTransactionCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
@Getter
public class LoyaltyBankAggregate {

    @AggregateIdentifier
    private String loyaltyBankId;
    private String accountId;
    private int pending;
    private int earned;
    private int reserved;
    private int redeemed;


    @CommandHandler
    public LoyaltyBankAggregate(CreateLoyaltyBankCommand command) {
        LoyaltyBankCreatedEvent event = LoyaltyBankCreatedEvent.builder()
                .loyaltyBankId(command.getLoyaltyBankId())
                .accountId(command.getAccountId())
                .pending(0)
                .earned(0)
                .reserved(0)
                .redeemed(0)
                .build();

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void on(CreatePendingTransactionCommand command) {
        if (this.pending + command.getPoints() < 0) {
            throw new IllegalStateException("Pending balance cannot be negative");
        }

        PendingTransactionCreatedEvent event = PendingTransactionCreatedEvent.builder()
                .loyaltyBankId(command.getLoyaltyBankId())
                .points(command.getPoints())
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PendingTransactionCreatedEvent event) {
        this.pending += event.getPoints();
    }

    @EventSourcingHandler
    public void on(LoyaltyBankCreatedEvent event) {
        this.loyaltyBankId = event.getLoyaltyBankId();
        this.accountId = event.getAccountId();
        this.pending = event.getPending();
        this.earned = event.getEarned();
        this.reserved = event.getReserved();
        this.redeemed = event.getRedeemed();
    }
}
