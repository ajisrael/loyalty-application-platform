package loyalty.service.core.events.account;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.account.AbstractAccountEvent;

@Getter
@SuperBuilder
public class AccountFirstNameChangedEvent extends AbstractAccountEvent {

    private String oldFirstName;
    private String newFirstName;
}
