package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BusinessUpdatedEvent extends AbstractEvent {

    private String businessId;
    private String businessName;
}
