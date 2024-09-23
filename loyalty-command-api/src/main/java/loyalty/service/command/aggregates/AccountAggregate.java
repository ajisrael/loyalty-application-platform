package loyalty.service.command.aggregates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.commands.DeleteAccountCommand;
import loyalty.service.command.commands.UpdateAccountCommand;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountDeletedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Aggregate
@NoArgsConstructor
@Getter
public class AccountAggregate {

    @Autowired
    private CommandGateway commandGateway;

    @AggregateIdentifier
    private String accountId;
    private String firstName;
    private String lastName;
    private String email;

    @CommandHandler
    public AccountAggregate(CreateAccountCommand command) {
        AccountCreatedEvent event = AccountCreatedEvent.builder()
                .accountId(command.getAccountId())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .build();

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void updateAccount(UpdateAccountCommand command) {
        AccountUpdatedEvent event = AccountUpdatedEvent.builder()
                .accountId(command.getAccountId())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .build();

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void deleteAccount(DeleteAccountCommand command) {
        AccountDeletedEvent event = AccountDeletedEvent.builder()
                .accountId(command.getAccountId())
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.email = event.getEmail();
    }

    @EventSourcingHandler
    public void on(AccountUpdatedEvent event) {
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.email = event.getEmail();
    }

    @EventSourcingHandler
    public void on(AccountDeletedEvent event) {
        AggregateLifecycle.markDeleted();
    }
}
