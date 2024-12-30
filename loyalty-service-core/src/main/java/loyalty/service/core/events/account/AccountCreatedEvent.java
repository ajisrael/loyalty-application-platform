package loyalty.service.core.events.account;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.account.AbstractAccountEvent;

@Getter
@SuperBuilder
public class AccountCreatedEvent extends AbstractAccountEvent {

    private String firstName;
    private String lastName;
    private String email;
}
