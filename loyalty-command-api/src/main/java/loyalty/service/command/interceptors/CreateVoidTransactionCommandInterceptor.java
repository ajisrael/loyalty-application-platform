package loyalty.service.command.interceptors;

import lombok.RequiredArgsConstructor;
import loyalty.service.command.commands.transactions.CreateVoidTransactionCommand;
import loyalty.service.command.data.entities.RedemptionTrackerEntity;
import loyalty.service.command.data.repositories.RedemptionTrackerRepository;
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

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.*;

@Component
@RequiredArgsConstructor
public class CreateVoidTransactionCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateVoidTransactionCommandInterceptor.class);

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

                if (redemptionTrackerEntity == null) {
                    LOGGER.info(
                            Markers.append(REQUEST_ID, command.getRequestId()),
                            PAYMENT_ID_NOT_FOUND_CANCELLING_COMMAND, paymentId, commandName
                    );

                    throw new PaymentIdNotFoundException(paymentId);
                }

                if (redemptionTrackerEntity.getAuthorizedPoints() < command.getPoints()) {
                    Marker marker = MarkerGenerator.generateMarker(redemptionTrackerEntity);
                    marker.add(Markers.append(REQUEST_ID, command.getRequestId()));
                    marker.add(Markers.append("pointsToVoid", command.getPoints()));
                    LOGGER.info(marker, "Attempting to void more points than authorized, cancelling command");

                    throw new ExcessiveVoidPointsException();
                }
            }

            return genericCommand;
        };
    }
}
