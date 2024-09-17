package loyalty.service.command.commands.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@SuperBuilder
public abstract class AbstractTransactionCommand {

    @TargetAggregateIdentifier
    private String loyaltyBankId;
    private int points;
}
