package loyalty.service.command.interceptors;

import loyalty.service.command.commands.*;
import loyalty.service.command.data.entities.BusinessLookupEntity;
import loyalty.service.command.data.repositories.BusinessLookupRepository;
import loyalty.service.core.exceptions.BusinessNotFoundException;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessCommandsInterceptorTest {

    @Mock
    private BusinessLookupRepository businessLookupRepository;

    @InjectMocks
    BusinessCommandsInterceptor businessCommandsInterceptor;


    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_NAME = "test business";


    @Test
    @DisplayName("Can handle valid EnrollBusinessCommand")
    void testHandle_whenEnrollBusinessCommandHandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        CreateBusinessCommand command = CreateBusinessCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .businessId(TEST_BUSINESS_ID)
                .businessName(TEST_BUSINESS_NAME)
                .build();

        // Act
        CommandMessage<?> result = businessCommandsInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
        verify(businessLookupRepository, times(0)).findByBusinessId(TEST_BUSINESS_ID);
    }

    @Test
    @DisplayName("Can handle valid UpdateBusinessCommand")
    void testHandle_whenUpdateBusinessCommandHandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        String newBusinessName = "new business name";
        UpdateBusinessCommand command = UpdateBusinessCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .businessId(TEST_BUSINESS_ID)
                .businessName(newBusinessName)
                .build();

        BusinessLookupEntity businessLookupEntity = new BusinessLookupEntity();
        businessLookupEntity.setBusinessId(TEST_BUSINESS_ID);

        when(businessLookupRepository.findByBusinessId(any(String.class))).thenReturn(businessLookupEntity);

        // Act
        CommandMessage<?> result = businessCommandsInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
        verify(businessLookupRepository, times(1)).findByBusinessId(TEST_BUSINESS_ID);
    }

    @Test
    @DisplayName("Can handle invalid UpdateBusinessCommand non existing business")
    void testHandle_whenUpdateBusinessCommandHandledAndBusinessDoesNotExist_shouldThrowException() {
        // Arrange
        UpdateBusinessCommand command = UpdateBusinessCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .businessId(TEST_BUSINESS_ID)
                .businessName(TEST_BUSINESS_NAME)
                .build();

        when(businessLookupRepository.findByBusinessId(any(String.class))).thenReturn(null);

        // Act & Assert
        assertThrows(BusinessNotFoundException.class, () -> {
            CommandMessage<?> result = businessCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw BusinessNotFoundException");

        // Assert
        verify(businessLookupRepository, times(1)).findByBusinessId(TEST_BUSINESS_ID);
    }

    @Test
    @DisplayName("Can handle valid DeleteBusinessCommand")
    void testHandle_whenDeleteBusinessCommandHandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        String newBusinessName = "new business name";
        DeleteBusinessCommand command = DeleteBusinessCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        BusinessLookupEntity businessLookupEntity = new BusinessLookupEntity();
        businessLookupEntity.setBusinessId(TEST_BUSINESS_ID);

        when(businessLookupRepository.findByBusinessId(any(String.class))).thenReturn(businessLookupEntity);

        // Act
        CommandMessage<?> result = businessCommandsInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
        verify(businessLookupRepository, times(1)).findByBusinessId(TEST_BUSINESS_ID);
    }

    @Test
    @DisplayName("Can handle invalid DeleteBusinessCommand non existing business")
    void testHandle_whenDeleteBusinessCommandHandledAndBusinessDoesNotExist_shouldThrowException() {
        // Arrange
        DeleteBusinessCommand command = DeleteBusinessCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        when(businessLookupRepository.findByBusinessId(any(String.class))).thenReturn(null);

        // Act & Assert
        assertThrows(BusinessNotFoundException.class, () -> {
            CommandMessage<?> result = businessCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw BusinessNotFoundException");

        // Assert
        verify(businessLookupRepository, times(1)).findByBusinessId(TEST_BUSINESS_ID);
    }
}