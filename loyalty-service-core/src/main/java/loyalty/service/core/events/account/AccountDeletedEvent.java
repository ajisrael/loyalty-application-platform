package loyalty.service.core.events.account;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountDeletedEvent extends AbstractAccountEvent {
}
