package loyalty.service.command.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import loyalty.service.core.validation.ProjectionId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_lookup")
public class AccountLookupEntity {

    @Id
    @Column(name = "account_id", unique = true)
    @NotNull(message = "AccountId cannot be null")
    @ProjectionId
    private String accountId;

    @Column(name = "email", unique = true)
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
}
