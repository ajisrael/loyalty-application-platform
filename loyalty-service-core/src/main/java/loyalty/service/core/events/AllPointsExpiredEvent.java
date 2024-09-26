package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AllPointsExpiredEvent extends AbstractEvent {

    private String loyaltyBankId;
    private String accountId;
    private int pendingPointsRemoved;
    private int authorizedPointsVoided;
    private int availablePointsCaptured;
}
