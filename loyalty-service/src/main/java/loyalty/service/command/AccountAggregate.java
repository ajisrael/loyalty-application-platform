package loyalty.service.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.core.events.AccountCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Aggregate
@NoArgsConstructor
@Getter
public class AccountAggregate {

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

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.email = event.getEmail();
    }
}
