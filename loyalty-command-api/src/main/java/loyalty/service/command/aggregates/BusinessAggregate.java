package loyalty.service.command.aggregates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.command.commands.*;
import loyalty.service.command.utils.LogHelper;
import loyalty.service.core.events.business.BusinessDeletedEvent;
import loyalty.service.core.events.business.BusinessEnrolledEvent;
import loyalty.service.core.events.business.BusinessNameChangedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aggregate
@NoArgsConstructor
@Getter
public class BusinessAggregate {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessAggregate.class);

    @AggregateIdentifier
    private String businessId;
    private String businessName;

    @CommandHandler
    public BusinessAggregate(CreateBusinessCommand command) {
        BusinessEnrolledEvent event = BusinessEnrolledEvent.builder()
                .requestId(command.getRequestId())
                .businessId(command.getBusinessId())
                .businessName(command.getBusinessName())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void updateBusiness(UpdateBusinessCommand command) {
        BusinessNameChangedEvent event = BusinessNameChangedEvent.builder()
                .requestId(command.getRequestId())
                .businessId(command.getBusinessId())
                .oldBusinessName(this.businessName)
                .newBusinessName(command.getBusinessName())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void deleteBusiness(DeleteBusinessCommand command) {
        BusinessDeletedEvent event = BusinessDeletedEvent.builder()
                .requestId(command.getRequestId())
                .businessId(command.getBusinessId())
                .build();

        LogHelper.logCommandIssuingEvent(LOGGER, command, event);

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(BusinessEnrolledEvent event) {
        this.businessId = event.getBusinessId();
        this.businessName = event.getBusinessName();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(BusinessNameChangedEvent event) {
        this.businessName = event.getNewBusinessName();

        LogHelper.logEventProcessed(LOGGER, event);
    }

    @EventSourcingHandler
    public void on(BusinessDeletedEvent event) {
        AggregateLifecycle.markDeleted();

        LogHelper.logEventProcessed(LOGGER, event);
    }
}
