package loyalty.service.core.events.loyalty.bank;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.loyalty.bank.AbstractLoyaltyBankEvent;

@Getter
@SuperBuilder
public class AllPointsExpiredEvent extends AbstractLoyaltyBankEvent {

    private String accountId;
    private String businessId;
    private int pendingPointsRemoved;
    private int authorizedPointsVoided;
    private int pointsExpired;
}
