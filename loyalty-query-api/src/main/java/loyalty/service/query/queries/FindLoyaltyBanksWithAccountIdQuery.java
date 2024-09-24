package loyalty.service.query.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindLoyaltyBanksWithAccountIdQuery {

    private String accountId;
}
