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
    @ProjectionId(message = "AccountId should be valid")
    @NotNull(message = "AccountId cannot be null")
    @Column(name = "account_id", unique = true)
    private String accountId;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    @Column(name = "email", unique = true)
    private String email;
}
