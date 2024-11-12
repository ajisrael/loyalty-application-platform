package loyalty.service.command.aggregates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.command.commands.EndAccountAndLoyaltyBankCreationCommand;
import loyalty.service.command.commands.StartAccountAndLoyaltyBankCreationCommand;
import loyalty.service.command.utils.LogHelper;
import loyalty.service.core.events.AccountAndLoyaltyBankCreationEndedEvent;
import loyalty.service.core.events.AccountAndLoyaltyBankCreationStartedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate
@NoArgsConstructor
@Getter
public class SagaOrchestratorAggregate {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaOrchestratorAggregate.class);

    @AggregateIdentifier
    private String requestId;

    @CommandHandler
    public SagaOrchestratorAggregate(StartAccountAndLoyaltyBankCreationCommand command) {
        AccountAndLoyaltyBankCreationStartedEvent event = AccountAndLoyaltyBankCreationStartedEvent.builder()
                .requestId(command.getRequestId())
                .accountId(command.getAccountId())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .loyaltyBankId(command.getLoyaltyBankId())
                .businessId(command.getBusinessId())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    void handle(EndAccountAndLoyaltyBankCreationCommand command) {
        AccountAndLoyaltyBankCreationEndedEvent event = AccountAndLoyaltyBankCreationEndedEvent.builder()
                .requestId(command.getRequestId())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(AccountAndLoyaltyBankCreationStartedEvent event) {
        this.requestId = event.getRequestId();
        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(AccountAndLoyaltyBankCreationEndedEvent event) {
        AggregateLifecycle.markDeleted();
        LogHelper.logEventProcessed(LOGGER, event);
    }
}
