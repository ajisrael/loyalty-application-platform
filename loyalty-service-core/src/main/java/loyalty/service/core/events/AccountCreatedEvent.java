package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountCreatedEvent extends AbstractEvent {

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
}
