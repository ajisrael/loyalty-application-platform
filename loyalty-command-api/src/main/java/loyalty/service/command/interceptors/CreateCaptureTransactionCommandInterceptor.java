package loyalty.service.command.interceptors;

import loyalty.service.command.commands.transactions.CreateCapturedTransactionCommand;
import loyalty.service.command.data.entities.RedemptionTrackerEntity;
import loyalty.service.command.data.repositories.RedemptionTrackerRepository;
import loyalty.service.core.exceptions.ExcessiveCapturePointsException;
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

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.INTERCEPTED_COMMAND;
import static loyalty.service.core.constants.LogMessages.PAYMENT_ID_NOT_FOUND_CANCELLING_COMMAND;

@Component
public class CreateCaptureTransactionCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCaptureTransactionCommandInterceptor.class);

    private final RedemptionTrackerRepository redemptionTrackerRepository;

    public CreateCaptureTransactionCommandInterceptor(RedemptionTrackerRepository redemptionTrackerRepository) {
        this.redemptionTrackerRepository = redemptionTrackerRepository;
    }

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, genericCommand) -> {

            if (CreateCapturedTransactionCommand.class.equals(genericCommand.getPayloadType())) {
                CreateCapturedTransactionCommand command = (CreateCapturedTransactionCommand) genericCommand.getPayload();

                String commandName = command.getClass().getSimpleName();
                LOGGER.info(MarkerGenerator.generateMarker(command), INTERCEPTED_COMMAND, commandName);

                String paymentId = command.getPaymentId();
                RedemptionTrackerEntity redemptionTrackerEntity = redemptionTrackerRepository.findByPaymentId(paymentId);

                if (redemptionTrackerEntity == null) {
                    LOGGER.info(
                            Markers.append(REQUEST_ID, command.getRequestId()),
                            PAYMENT_ID_NOT_FOUND_CANCELLING_COMMAND, paymentId, commandName
                    );

                    throw new PaymentIdNotFoundException(paymentId);
                }

                if (redemptionTrackerEntity.getPointsAvailableForRedemption() < command.getPoints()) {
                    Marker marker = MarkerGenerator.generateMarker(redemptionTrackerEntity);
                    marker.add(Markers.append(REQUEST_ID, command.getRequestId()));
                    marker.add(Markers.append("pointsToCapture", command.getPoints()));
                    LOGGER.info(marker, "Attempting to capture more points than authorized, cancelling command");

                    throw new ExcessiveCapturePointsException();
                }
            }

            return genericCommand;
        };
    }
}
