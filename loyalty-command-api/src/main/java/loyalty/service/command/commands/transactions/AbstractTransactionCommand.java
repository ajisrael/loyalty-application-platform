package loyalty.service.command.commands.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.command.commands.AbstractCommand;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@SuperBuilder
public abstract class AbstractTransactionCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String loyaltyBankId;
    private int points;
}
