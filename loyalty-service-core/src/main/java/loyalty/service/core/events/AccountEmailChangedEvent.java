package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountEmailChangedEvent extends AbstractAccountEvent {

    private String oldEmail;
    private String newEmail;
}
