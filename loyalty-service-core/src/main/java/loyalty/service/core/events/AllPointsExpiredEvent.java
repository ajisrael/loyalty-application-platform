package loyalty.service.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllPointsExpiredEvent {
    private String loyaltyBankId;
    private String accountId;
    private int pendingPointsRemoved;
    private int authorizedPointsVoided;
    private int availablePointsCaptured;
}
