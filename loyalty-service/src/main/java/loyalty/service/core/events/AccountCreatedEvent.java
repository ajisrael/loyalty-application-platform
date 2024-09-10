package loyalty.service.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountCreatedEvent {

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
}
