package loyalty.service.command.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import loyalty.service.core.validation.ProjectionId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loyalty_bank_lookup")
public class LoyaltyBankLookupEntity {

    @Id
    @ProjectionId(message = "LoyaltyBankId should be valid")
    @NotNull(message = "LoyaltyBankId cannot be null")
    @Column(name = "loyalty_bank_id", unique = true)
    private String loyaltyBankId;

    @ProjectionId(message = "AccountId should be valid")
    @NotNull(message = "AccountId cannot be null")
    @Column(name = "account_id")
    private String accountId;

    @ProjectionId(message = "BusinessId should be valid")
    @NotNull(message = "BusinessId cannot be null")
    @Column(name = "business_id")
    private String businessId;
}
