package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountFirstNameChangedEvent extends AbstractAccountEvent {

    private String oldFirstName;
    private String newFirstName;
}
