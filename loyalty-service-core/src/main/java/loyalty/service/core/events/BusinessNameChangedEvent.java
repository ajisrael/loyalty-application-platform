package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BusinessNameChangedEvent extends AbstractBusinessEvent {

    private String oldBusinessName;
    private String newBusinessName;
}
