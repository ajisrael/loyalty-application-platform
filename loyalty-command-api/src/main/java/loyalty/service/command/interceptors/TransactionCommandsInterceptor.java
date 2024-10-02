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

            if (CreateVoidTransactionCommand.class.equals(genericCommand.getPayloadType())) {
                CreateVoidTransactionCommand command = (CreateVoidTransactionCommand) genericCommand.getPayload();

                String commandName = command.getClass().getSimpleName();
                LOGGER.info(MarkerGenerator.generateMarker(command), INTERCEPTED_COMMAND, commandName);

                String paymentId = command.getPaymentId();
                RedemptionTrackerEntity redemptionTrackerEntity = redemptionTrackerRepository.findByPaymentId(paymentId);

                throwExceptionIfRedemptionDoesNotExist(redemptionTrackerEntity, command.getRequestId(), paymentId, commandName);

                if (redemptionTrackerEntity.getAuthorizedPoints() < command.getPoints()) {
                    Marker marker = MarkerGenerator.generateMarker(redemptionTrackerEntity);
                    marker.add(Markers.append(REQUEST_ID, command.getRequestId()));
                    marker.add(Markers.append(REQUESTED_POINTS, command.getPoints()));
                    LOGGER.info(marker, EXCESSIVE_POINTS_REQUEST_CANCELLING_COMMAND, VOID);

                    throw new ExcessiveVoidPointsException();
                }
            }

            if (CreateCapturedTransactionCommand.class.equals(genericCommand.getPayloadType())) {
                CreateCapturedTransactionCommand command = (CreateCapturedTransactionCommand) genericCommand.getPayload();

                String commandName = command.getClass().getSimpleName();
                LOGGER.info(MarkerGenerator.generateMarker(command), INTERCEPTED_COMMAND, commandName);

                String paymentId = command.getPaymentId();
                RedemptionTrackerEntity redemptionTrackerEntity = redemptionTrackerRepository.findByPaymentId(paymentId);

                throwExceptionIfRedemptionDoesNotExist(redemptionTrackerEntity, command.getRequestId(), paymentId, commandName);

                if (redemptionTrackerEntity.getPointsAvailableForRedemption() < command.getPoints()) {
                    Marker marker = MarkerGenerator.generateMarker(redemptionTrackerEntity);
                    marker.add(Markers.append(REQUEST_ID, command.getRequestId()));
                    marker.add(Markers.append(REQUESTED_POINTS, command.getPoints()));
                    LOGGER.info(marker, EXCESSIVE_POINTS_REQUEST_CANCELLING_COMMAND, CAPTURE);

                    throw new ExcessiveCapturePointsException();
                }
            }

            return genericCommand;
        };
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
}
