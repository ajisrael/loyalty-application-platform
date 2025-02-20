package loyalty.service.command.aggregates;

import loyalty.service.command.commands.DeleteBusinessCommand;
import loyalty.service.command.commands.CreateBusinessCommand;
import loyalty.service.command.commands.UpdateBusinessCommand;
import loyalty.service.core.events.business.BusinessDeletedEvent;
import loyalty.service.core.events.business.BusinessEnrolledEvent;
import loyalty.service.core.events.business.BusinessNameChangedEvent;
import org.axonframework.eventsourcing.eventstore.EventStoreException;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BusinessAggregateTest {

    private FixtureConfiguration<BusinessAggregate> fixture;

    private static final String TEST_REQUEST_ID = "test-request-id";
    private static final String TEST_BUSINESS_ID = "test-business-id";
    private static final String TEST_BUSINESS_NAME = "test-business-name";
    private static final String TEST_NEW_BUSINESS_NAME = "test-new-business-name";

    private static final CreateBusinessCommand CREATE_BUSINESS_COMMAND = CreateBusinessCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .businessId(TEST_BUSINESS_ID)
            .businessName(TEST_BUSINESS_NAME)
            .build();

    private static final BusinessEnrolledEvent businessEnrolledEvent = BusinessEnrolledEvent.builder()
            .requestId(CREATE_BUSINESS_COMMAND.getRequestId())
            .businessId(CREATE_BUSINESS_COMMAND.getBusinessId())
            .businessName(CREATE_BUSINESS_COMMAND.getBusinessName())
            .build();

    private static final UpdateBusinessCommand updateBusinessCommand = UpdateBusinessCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .businessId(TEST_BUSINESS_ID)
            .businessName(TEST_NEW_BUSINESS_NAME)
            .build();

    private static final BusinessNameChangedEvent BUSINESS_NAME_CHANGED_EVENT = BusinessNameChangedEvent.builder()
            .requestId(updateBusinessCommand.getRequestId())
            .businessId(updateBusinessCommand.getBusinessId())
            .oldBusinessName(TEST_BUSINESS_NAME)
            .newBusinessName(updateBusinessCommand.getBusinessName())
            .build();

    private static final DeleteBusinessCommand deleteBusinessCommand = DeleteBusinessCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .businessId(TEST_BUSINESS_ID)
            .build();

    private static final BusinessDeletedEvent businessDeletedEvent = BusinessDeletedEvent.builder()
            .requestId(deleteBusinessCommand.getRequestId())
            .businessId(deleteBusinessCommand.getBusinessId())
            .build();

    @BeforeEach
    void setup() {
        fixture = new AggregateTestFixture<>(BusinessAggregate.class);
    }

    @Test
    @DisplayName("EnrollBusinessCommand results in BusinessEnrolledEvent")
    void testBusinessAggregate_whenEnrollBusinessCommandHandledWithNoPriorActivity_ShouldIssueBusinessEnrolledEvent() {
        // Arrange & Act & Assert
        fixture.givenNoPriorActivity()
                .when(CREATE_BUSINESS_COMMAND)
                .expectEvents(businessEnrolledEvent)
                .expectState(state -> {
                    assertEquals(businessEnrolledEvent.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(businessEnrolledEvent.getBusinessName(), state.getBusinessName(), "BusinessNames should match");
                });
    }

    @Test
    @DisplayName("Cannot enroll an existing business")
    void testBusinessAggregate_whenCreateBusinessCommandHandledWithPriorActivity_ShouldThrowException() {
        fixture.given(businessEnrolledEvent)
                .when(CREATE_BUSINESS_COMMAND)
                .expectException(EventStoreException.class);
    }

    @Test
    @DisplayName("UpdateBusinessCommand results in BusinessNameChangedEvent")
    void testUpdateBusiness_whenUpdateBusinessCommandHandled_ShouldIssueBusinessUpdatedEvent() {
        // Arrange & Act & Assert
        fixture.given(businessEnrolledEvent)
                .when(updateBusinessCommand)
                .expectEvents(BUSINESS_NAME_CHANGED_EVENT)
                .expectState(state -> {
                    assertEquals(BUSINESS_NAME_CHANGED_EVENT.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(BUSINESS_NAME_CHANGED_EVENT.getNewBusinessName(), state.getBusinessName(), "New BusinessNames should match");
                    assertEquals(BUSINESS_NAME_CHANGED_EVENT.getOldBusinessName(), businessEnrolledEvent.getBusinessName(), "Old BusinessNames should match");
                });
    }

    @Test
    @DisplayName("Cannot update a business that hasn't been enrolled")
    void testUpdateBusiness_whenUpdateBusinessCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(updateBusinessCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("DeleteBusinessCommand results in BusinessDeletedEvent")
    void testDeleteBusiness_whenDeleteBusinessCommandHandled_ShouldIssueBusinessDeletedEvent() {
        // Arrange & Act & Assert
        fixture.given(businessEnrolledEvent)
                .when(deleteBusinessCommand)
                .expectEvents(businessDeletedEvent)
                .expectMarkedDeleted();
    }

    @Test
    @DisplayName("Cannot delete an business that hasn't been created")
    void testDeleteBusiness_whenDeleteBusinessCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(deleteBusinessCommand)
                .expectException(AggregateNotFoundException.class);
    }
}