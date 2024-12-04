package loyalty.service.command.service;

import loyalty.service.command.commands.transactions.CreateExpirePointsTransactionCommand;
import loyalty.service.command.data.entities.TransactionEntity;
import loyalty.service.command.data.repositories.TransactionRepository;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.SENDING_COMMAND_FOR_LOYALTY_BANK;

@Component
public class PointExpirationService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PointExpirationService.class);
    private final TransactionRepository transactionRepository;
    private final CommandGateway commandGateway;

    public PointExpirationService(TransactionRepository transactionRepository, @Lazy CommandGateway commandGateway) {
        this.transactionRepository = transactionRepository;
        this.commandGateway = commandGateway;
    }

    public void expireTransactionsBeforeDate(Instant expirationDate, String requestId) {
        List<TransactionEntity> transactions = transactionRepository.findAllByTimestampBefore(expirationDate);

        Marker marker = Markers.append(REQUEST_ID, requestId);

        if (transactions.isEmpty()) {
            LOGGER.info(marker, "0 transactions found, no expiration required");
            return;
        }

        LOGGER.info(marker, "{} transactions found, beginning expiration process", transactions.size());

        transactions.forEach(transaction -> {
            CreateExpirePointsTransactionCommand command = CreateExpirePointsTransactionCommand.builder()
                    .requestId(requestId)
                    .loyaltyBankId(transaction.getLoyaltyBankId())
                    .targetTransactionId(transaction.getTransactionId())
                    .points(transaction.getPoints())
                    .build();

            LOGGER.info(
                    MarkerGenerator.generateMarker(command),
                    SENDING_COMMAND_FOR_LOYALTY_BANK, command.getClass().getSimpleName(), command.getLoyaltyBankId()
            );

            commandGateway.send(command);
        });
    }
}
