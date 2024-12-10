package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountUpdatedEvent extends AbstractEvent {
    // TODO: Adjust event so it shows old and new state when issued

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
}
