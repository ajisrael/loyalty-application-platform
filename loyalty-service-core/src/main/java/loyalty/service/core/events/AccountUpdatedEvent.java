package loyalty.service.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountUpdatedEvent {

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
}
