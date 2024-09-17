package loyalty.service.command.commands;

import lombok.Builder;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Builder
public class CreateCapturedTransactionCommand {

    @TargetAggregateIdentifier
    private String loyaltyBankId;
    private int points;
}
