package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountLastNameChangedEvent extends AbstractAccountEvent {

    private String oldLastName;
    private String newLastName;
}
