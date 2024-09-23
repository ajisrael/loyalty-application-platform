package loyalty.service.core.data.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode
@Document(collection = "loyalty_banks")
public class LoyaltyBankEntity {

    @Id
    private String loyaltyBankId;
    @Indexed(unique = true)
    private String accountId;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;
}
