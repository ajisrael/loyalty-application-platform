package loyalty.service.command.aggregates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
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
