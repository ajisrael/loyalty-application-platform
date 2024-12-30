package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AllPointsExpiredEvent extends AbstractLoyaltyBankEvent {

    private String accountId;
    private String businessId;
    private int pendingPointsRemoved;
    private int authorizedPointsVoided;
    private int pointsExpired;
}
