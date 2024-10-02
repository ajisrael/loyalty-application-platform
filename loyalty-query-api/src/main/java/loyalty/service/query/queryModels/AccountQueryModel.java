package loyalty.service.query.queryModels;

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
