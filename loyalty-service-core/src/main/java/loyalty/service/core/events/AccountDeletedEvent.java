package loyalty.service.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountDeletedEvent {

    private String accountId;
}
