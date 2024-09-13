package loyalty.service.command.commands;

import lombok.Builder;
import lombok.Getter;
import loyalty.service.core.validation.NonZeroPoints;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Builder
public class CreatePendingTransactionCommand {

    @TargetAggregateIdentifier
    private String loyaltyBankId;

    @NonZeroPoints
    private int points;
}
