package loyalty.service.command.aggregates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.DeleteAccountCommand;
import loyalty.service.command.commands.UpdateAccountCommand;
import loyalty.service.command.commands.rollbacks.RollbackAccountCreationCommand;
import loyalty.service.command.utils.LogHelper;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountDeletedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aggregate
@NoArgsConstructor
@Getter
public class AccountAggregate {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountAggregate.class);

    @AggregateIdentifier
    private String accountId;
    private String firstName;
    private String lastName;
    private String email;

    @CommandHandler
    public AccountAggregate(CreateAccountCommand command) {
        AccountCreatedEvent event = AccountCreatedEvent.builder()
                .requestId(command.getRequestId())
                .accountId(command.getAccountId())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void updateAccount(UpdateAccountCommand command) {
        AccountUpdatedEvent event = AccountUpdatedEvent.builder()
                .requestId(command.getRequestId())
                .accountId(command.getAccountId())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void deleteAccount(DeleteAccountCommand command) {
        AccountDeletedEvent event = AccountDeletedEvent.builder()
                .requestId(command.getRequestId())
                .accountId(command.getAccountId())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void rollbackAccount(RollbackAccountCreationCommand command) {
        AccountDeletedEvent event = AccountDeletedEvent.builder()
                .requestId(command.getRequestId())
                .accountId(command.getAccountId())
                .build();

        MetaData metaData = MetaData.with("reason", "rollback");

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event, metaData);
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.email = event.getEmail();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(AccountUpdatedEvent event) {
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.email = event.getEmail();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(AccountDeletedEvent event) {
        AggregateLifecycle.markDeleted();

        LogHelper.logEventProcessed(LOGGER, event);
    }
}
