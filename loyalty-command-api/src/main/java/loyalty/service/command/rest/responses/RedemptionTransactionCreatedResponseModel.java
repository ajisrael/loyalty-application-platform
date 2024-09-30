package loyalty.service.command.rest.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RedemptionTransactionCreatedResponseModel {

    private String requestId;
    private String paymentId;
}
