package loyalty.service.command.commands.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreateExpirePointsTransactionCommand extends AbstractTransactionCommand  {
    private String transactionId;
}
