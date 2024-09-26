package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountDeletedEvent extends AbstractEvent {

    private String accountId;
}
