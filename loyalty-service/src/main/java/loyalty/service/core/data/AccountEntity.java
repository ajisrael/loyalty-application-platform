package loyalty.service.core.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode
@Document(collection = "accounts")
public class AccountEntity {

    @Id
    private String accountId;
    private String firstName;
    private String lastName;
    @Indexed(unique = true)
    private String email;
}
