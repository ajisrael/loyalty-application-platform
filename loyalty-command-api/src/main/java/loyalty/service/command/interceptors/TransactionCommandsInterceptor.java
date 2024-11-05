package loyalty.service.command.interceptors;

import lombok.RequiredArgsConstructor;
import loyalty.service.command.commands.transactions.CreateCapturedTransactionCommand;
import loyalty.service.command.commands.transactions.CreateVoidTransactionCommand;
import loyalty.service.command.data.entities.RedemptionTrackerEntity;
import loyalty.service.command.data.repositories.RedemptionTrackerRepository;
import loyalty.service.core.exceptions.ExcessiveCapturePointsException;
import loyalty.service.core.exceptions.ExcessiveVoidPointsException;
import loyalty.service.core.exceptions.PaymentIdNotFoundException;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

import static loyalty.service.core.constants.DomainConstants.*;
import static loyalty.service.core.constants.LogMessages.*;

@Component
@RequiredArgsConstructor
public class TransactionCommandsInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionCommandsInterceptor.class);

    private final RedemptionTrackerRepository redemptionTrackerRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, genericCommand) -> {
            String commandName = genericCommand.getPayloadType().getSimpleName();
            LOGGER.debug(MarkerGenerator.generateMarker(genericCommand.getPayload()), INTERCEPTED_COMMAND, commandName);

            if (CreateVoidTransactionCommand.class.equals(genericCommand.getPayloadType())) {
                handleCreateVoidTransactionCommand((CreateVoidTransactionCommand) genericCommand.getPayload(), commandName);
            } else if (CreateCapturedTransactionCommand.class.equals(genericCommand.getPayloadType())) {
                handleCreateCapturedTransactionCommand((CreateCapturedTransactionCommand) genericCommand.getPayload(), commandName);
            }

            return genericCommand;
        };
    }

    private void handleCreateVoidTransactionCommand(CreateVoidTransactionCommand command, String commandName) {
        String requestId = command.getRequestId();
        String paymentId = command.getPaymentId();
        RedemptionTrackerEntity redemptionTrackerEntity = redemptionTrackerRepository.findByPaymentId(paymentId);

        throwExceptionIfRedemptionDoesNotExist(redemptionTrackerEntity, requestId, paymentId, commandName);
        throwExceptionIfAttemptingToVoidMorePointsThanAvailable(redemptionTrackerEntity, command.getPoints(), requestId, commandName);
    }

    private void handleCreateCapturedTransactionCommand(CreateCapturedTransactionCommand command, String commandName) {
        String requestId = command.getRequestId();
        String paymentId = command.getPaymentId();
        RedemptionTrackerEntity redemptionTrackerEntity = redemptionTrackerRepository.findByPaymentId(paymentId);

        throwExceptionIfRedemptionDoesNotExist(redemptionTrackerEntity, requestId, paymentId, commandName);
        throwExceptionIfAttemptingToCaptureMorePointsThanAvailable(redemptionTrackerEntity, command.getPoints(), requestId, commandName);
    }

    private void throwExceptionIfRedemptionDoesNotExist(RedemptionTrackerEntity redemptionTrackerEntity, String requestId, String paymentId, String commandName) {
        if (redemptionTrackerEntity == null) {
            LOGGER.info(
                    Markers.append(REQUEST_ID, requestId),
                    PAYMENT_ID_NOT_FOUND_CANCELLING_COMMAND, paymentId, commandName
            );

            throw new PaymentIdNotFoundException(paymentId);
        }
    }

    private void throwExceptionIfAttemptingToVoidMorePointsThanAvailable(RedemptionTrackerEntity redemptionTrackerEntity, int points, String requestId, String commandName) {
        if (redemptionTrackerEntity.getAuthorizedPoints() < points) {
            Marker marker = MarkerGenerator.generateMarker(redemptionTrackerEntity);
            marker.add(Markers.append(REQUEST_ID, requestId));
            marker.add(Markers.append(REQUESTED_POINTS, points));
            LOGGER.info(marker, EXCESSIVE_POINTS_REQUEST_CANCELLING_COMMAND, VOID, commandName);

            throw new ExcessiveVoidPointsException();
        }
    }

    private void throwExceptionIfAttemptingToCaptureMorePointsThanAvailable(RedemptionTrackerEntity redemptionTrackerEntity, int points, String requestId, String commandName) {
        if (redemptionTrackerEntity.getAuthorizedPoints() < points) {
            Marker marker = MarkerGenerator.generateMarker(redemptionTrackerEntity);
            marker.add(Markers.append(REQUEST_ID, requestId));
            marker.add(Markers.append(REQUESTED_POINTS, points));
            LOGGER.info(marker, EXCESSIVE_POINTS_REQUEST_CANCELLING_COMMAND, CAPTURE, commandName);

            throw new ExcessiveCapturePointsException();
        }
    }
}
