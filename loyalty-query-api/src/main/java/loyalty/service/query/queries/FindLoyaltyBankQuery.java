package loyalty.service.query.queries;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindLoyaltyBankQuery extends AbstractQuery {

    private String loyaltyBankId;
}
