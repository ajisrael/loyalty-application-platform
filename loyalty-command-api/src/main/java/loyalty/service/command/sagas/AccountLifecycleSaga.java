package loyalty.service.command.sagas;

import loyalty.service.command.commands.DeleteAccountCommand;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static loyalty.service.core.utils.MarkerGenerator.generateMarker;

@Saga
public class AccountLifecycleSaga {
    @Autowired
    private transient CommandGateway commandGateway;
    private String deadlineId = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountLifecycleSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "accountId")
    public void on(AccountCreatedEvent event, @Autowired DeadlineManager deadlineManager) {
        String accountId = event.getAccountId();

        deadlineId = deadlineManager.schedule(
                Duration.ofSeconds(20),
                "delete-account-deadline",
                event
        );

        Marker marker = generateMarker(event);
        marker.add(Markers.append("deadlineId", deadlineId));

        LOGGER.info(marker, "Deadline {} created for {}", deadlineId, accountId);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "accountId")
    public void on(AccountUpdatedEvent event, @Autowired DeadlineManager deadlineManager) {
        deadlineManager.cancelSchedule("delete-account-deadline", deadlineId);

        Marker marker = generateMarker(event);
        marker.add(Markers.append("deadlineId", deadlineId));

        LOGGER.info(marker, "Deadline {} cancelled for {}", deadlineId, event.getAccountId());
    }

    @EndSaga
    @DeadlineHandler(deadlineName = "delete-account-deadline")
    public void on(AccountCreatedEvent event) {
        DeleteAccountCommand command = DeleteAccountCommand.builder()
                .requestId(event.getRequestId())
                .accountId(event.getAccountId())
                .build();

        commandGateway.send(command);

        Marker marker = generateMarker(event);
        marker.add(Markers.append("deadlineId", deadlineId));

        LOGGER.info(marker, "Deadline {} handled for account {}", deadlineId, event.getAccountId());
    }
}
