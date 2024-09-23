package loyalty.service.query.querymodels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountQueryModel {

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
}
