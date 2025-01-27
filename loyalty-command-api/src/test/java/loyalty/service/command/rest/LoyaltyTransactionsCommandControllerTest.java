package loyalty.service.command.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import loyalty.service.command.commands.AbstractCommand;
import loyalty.service.command.commands.transactions.*;
import loyalty.service.command.rest.requests.CreateLoyaltyRedemptionTransactionRequestModel;
import loyalty.service.command.rest.requests.CreateLoyaltyTransactionRequestModel;
import loyalty.service.command.rest.responses.RedemptionTransactionCreatedResponseModel;
import loyalty.service.command.rest.responses.TransactionCreatedResponseModel;
import loyalty.service.core.exceptions.ExcessiveCapturePointsException;
import loyalty.service.core.exceptions.ExcessiveVoidPointsException;
import loyalty.service.core.exceptions.LoyaltyBankNotFoundException;
import loyalty.service.core.exceptions.PaymentIdNotFoundException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoyaltyTransactionsCommandController.class)
@ExtendWith(MockitoExtension.class)
class LoyaltyTransactionsCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommandGateway commandGateway;

    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final int TEST_POINTS = 100;

    // Test for creating a pending transaction
    @Test
    @DisplayName("Can create a pending transaction")
    void testCreatePendingTransaction_whenCorrectDetailsProvided_shouldReturnRequestId() throws Exception {
        // Arrange
        CreateLoyaltyTransactionRequestModel request = new CreateLoyaltyTransactionRequestModel(TEST_LOYALTY_BANK_ID, TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        TransactionCreatedResponseModel responseModel = convertResponseToTransactionCreatedResponseModel(mvcResult);

        // Assert
        assertNotNull(responseModel.getRequestId(), "RequestId should not be null");
        assertDoesNotThrow(() -> UUID.fromString(responseModel.getRequestId()), "RequestId should be a valid UUID");
    }

    // Test for creating an earned transaction
    @Test
    @DisplayName("Can create an earned transaction")
    void testCreateEarnedTransaction_whenCorrectDetailsProvided_shouldReturnRequestId() throws Exception {
        // Arrange
        CreateLoyaltyTransactionRequestModel request = new CreateLoyaltyTransactionRequestModel(TEST_LOYALTY_BANK_ID, TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction/earn")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        TransactionCreatedResponseModel responseModel = convertResponseToTransactionCreatedResponseModel(mvcResult);

        // Assert
        assertNotNull(responseModel.getRequestId(), "RequestId should not be null");
        assertDoesNotThrow(() -> UUID.fromString(responseModel.getRequestId()), "RequestId should be a valid UUID");
    }

    // Test for creating an awarded transaction
    @Test
    @DisplayName("Can create an awarded transaction")
    void testCreateAwardedTransaction_whenCorrectDetailsProvided_shouldReturnRequestId() throws Exception {
        // Arrange
        CreateLoyaltyTransactionRequestModel request = new CreateLoyaltyTransactionRequestModel(TEST_LOYALTY_BANK_ID, TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction/award")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        TransactionCreatedResponseModel responseModel = convertResponseToTransactionCreatedResponseModel(mvcResult);

        // Assert
        assertNotNull(responseModel.getRequestId(), "RequestId should not be null");
        assertDoesNotThrow(() -> UUID.fromString(responseModel.getRequestId()), "RequestId should be a valid UUID");
    }

    // Test for creating an authorize transaction
    @Test
    @DisplayName("Can create an authorize transaction")
    void testCreateAuthorizeTransaction_whenCorrectDetailsProvided_shouldReturnRequestIdAndPaymentId() throws Exception {
        // Arrange
        CreateLoyaltyTransactionRequestModel request = new CreateLoyaltyTransactionRequestModel(TEST_LOYALTY_BANK_ID, TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        RedemptionTransactionCreatedResponseModel responseModel = convertResponseToRedemptionTransactionCreatedResponseModel(mvcResult);

        // Assert
        assertNotNull(responseModel.getRequestId(), "RequestId should not be null");
        assertDoesNotThrow(() -> UUID.fromString(responseModel.getRequestId()), "RequestId should be a valid UUID");
        assertNotNull(responseModel.getPaymentId(), "PaymentId should not be null");
        assertDoesNotThrow(() -> UUID.fromString(responseModel.getPaymentId()), "PaymentId should be a valid UUID");
    }

    // Test for creating a void transaction
    @Test
    @DisplayName("Can create a void transaction")
    void testCreateVoidTransaction_whenCorrectDetailsProvided_shouldReturnRequestId() throws Exception {
        // Arrange
        CreateLoyaltyRedemptionTransactionRequestModel request = new CreateLoyaltyRedemptionTransactionRequestModel(TEST_LOYALTY_BANK_ID, UUID.randomUUID().toString(), TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction/void")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        TransactionCreatedResponseModel responseModel = convertResponseToTransactionCreatedResponseModel(mvcResult);

        // Assert
        assertNotNull(responseModel.getRequestId(), "RequestId should not be null");
        assertDoesNotThrow(() -> UUID.fromString(responseModel.getRequestId()), "RequestId should be a valid UUID");
    }

    // Test for creating a capture transaction
    @Test
    @DisplayName("Can create a capture transaction")
    void testCreateCaptureTransaction_whenCorrectDetailsProvided_shouldReturnRequestId() throws Exception {
        // Arrange
        CreateLoyaltyRedemptionTransactionRequestModel request = new CreateLoyaltyRedemptionTransactionRequestModel(TEST_LOYALTY_BANK_ID, UUID.randomUUID().toString(), TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        TransactionCreatedResponseModel responseModel = convertResponseToTransactionCreatedResponseModel(mvcResult);

        // Assert
        assertNotNull(responseModel.getRequestId(), "RequestId should not be null");
        assertDoesNotThrow(() -> UUID.fromString(responseModel.getRequestId()), "RequestId should be a valid UUID");
    }

    // Edge case: Validation failure
    @Test
    @DisplayName("Validation failure should return 400 Bad Request")
    void testValidationFailure_shouldReturn400BadRequest() throws Exception {
        // Arrange
        CreateLoyaltyTransactionRequestModel invalidRequest = new CreateLoyaltyTransactionRequestModel(null, TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidRequest));

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    // Edge case: Excessive void points exception
    @Test
    @DisplayName("Excessive void points should return 422 Unprocessable Entity")
    void testExcessiveVoidPoints_shouldReturn422UnprocessableEntity() throws Exception {
        // Arrange
        CreateLoyaltyRedemptionTransactionRequestModel request = new CreateLoyaltyRedemptionTransactionRequestModel(TEST_LOYALTY_BANK_ID, UUID.randomUUID().toString(), TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction/void")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        doThrow(new ExcessiveVoidPointsException()).when(commandGateway).sendAndWait(any(CreateVoidTransactionCommand.class));

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnprocessableEntity());
    }

    // Edge case: Excessive capture points exception
    @Test
    @DisplayName("Excessive capture points should return 422 Unprocessable Entity")
    void testExcessiveCapturePoints_shouldReturn422UnprocessableEntity() throws Exception {
        // Arrange
        CreateLoyaltyRedemptionTransactionRequestModel request = new CreateLoyaltyRedemptionTransactionRequestModel(TEST_LOYALTY_BANK_ID, UUID.randomUUID().toString(), TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        doThrow(new ExcessiveCapturePointsException()).when(commandGateway).sendAndWait(any(CreateCapturedTransactionCommand.class));

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnprocessableEntity());
    }

    // Edge case: Payment ID not found exception
    @Test
    @DisplayName("Payment ID not found should return 404 Not Found")
    void testPaymentIdNotFound_shouldReturn404NotFound() throws Exception {
        // Arrange
        CreateLoyaltyRedemptionTransactionRequestModel request = new CreateLoyaltyRedemptionTransactionRequestModel(TEST_LOYALTY_BANK_ID, UUID.randomUUID().toString(), TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction/void")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        doThrow(new PaymentIdNotFoundException(request.getPaymentId())).when(commandGateway).sendAndWait(any(CreateVoidTransactionCommand.class));

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    // Edge case: LoyaltyBank not found exception for redemption endpoint
    @ParameterizedTest
    @MethodSource(value = "loyaltyTransactionCommandControllerRedemptionTestPaths")
    @DisplayName("LoyaltyBank not found should return 404 Not Found")
    void testLoyaltyBankNotFoundForRedemption_shouldReturn404NotFound(String path) throws Exception {
        // Arrange
        CreateLoyaltyRedemptionTransactionRequestModel request = new CreateLoyaltyRedemptionTransactionRequestModel(TEST_LOYALTY_BANK_ID, UUID.randomUUID().toString(), TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        doThrow(new LoyaltyBankNotFoundException(request.getLoyaltyBankId())).when(commandGateway).sendAndWait(any(AbstractCommand.class));

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    // Edge case: LoyaltyBank not found exception for transaction endpoint
    @ParameterizedTest
    @MethodSource(value = "loyaltyTransactionCommandControllerTransactionTestPaths")
    @DisplayName("LoyaltyBank not found should return 404 Not Found")
    void testLoyaltyBankNotFoundForTransaction_shouldReturn404NotFound(String path) throws Exception {
        // Arrange
        CreateLoyaltyTransactionRequestModel request = new CreateLoyaltyTransactionRequestModel(TEST_LOYALTY_BANK_ID, TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        doThrow(new LoyaltyBankNotFoundException(request.getLoyaltyBankId())).when(commandGateway).sendAndWait(any(AbstractCommand.class));

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    // Edge case: Internal server error redemption endpoint
    @ParameterizedTest
    @MethodSource(value = "loyaltyTransactionCommandControllerRedemptionTestPaths")
    @DisplayName("Internal server error should return 500 on redemption paths")
    void testInternalServerErrorOnRedemption_shouldReturn500InternalServerError(String path) throws Exception {
        // Arrange
        CreateLoyaltyRedemptionTransactionRequestModel request = new CreateLoyaltyRedemptionTransactionRequestModel(TEST_LOYALTY_BANK_ID, UUID.randomUUID().toString(), TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        doThrow(new RuntimeException("Internal server error")).when(commandGateway).sendAndWait(any(AbstractCommand.class));

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isInternalServerError());
    }

    // Edge case: Internal server error on transaction endpoint
    @ParameterizedTest
    @MethodSource(value = "loyaltyTransactionCommandControllerTransactionTestPaths")
    @DisplayName("Internal server error should return 500 on transaction paths")
    void testInternalServerErrorOnTransaction_shouldReturn500InternalServerError(String path) throws Exception {
        // Arrange
        CreateLoyaltyTransactionRequestModel request = new CreateLoyaltyTransactionRequestModel(TEST_LOYALTY_BANK_ID, TEST_POINTS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        doThrow(new RuntimeException("Internal server error")).when(commandGateway).sendAndWait(any(AbstractCommand.class));

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isInternalServerError());
    }

    private TransactionCreatedResponseModel convertResponseToTransactionCreatedResponseModel(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBodyAsString);
        String requestId = jsonNode.get("requestId").asText();
        return TransactionCreatedResponseModel.builder().requestId(requestId).build();
    }

    private RedemptionTransactionCreatedResponseModel convertResponseToRedemptionTransactionCreatedResponseModel(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBodyAsString);
        String requestId = jsonNode.get("requestId").asText();
        String paymentId = jsonNode.get("paymentId").asText();
        return RedemptionTransactionCreatedResponseModel.builder().requestId(requestId).paymentId(paymentId).build();
    }

    private static Stream<Arguments> loyaltyTransactionCommandControllerRedemptionTestPaths() {
        return Stream.of(
                Arguments.arguments("/transaction/void"),
                Arguments.arguments("/transaction/capture")
        );
    }

    private static Stream<Arguments> loyaltyTransactionCommandControllerTransactionTestPaths() {
        return Stream.of(
                Arguments.arguments("/transaction/pending"),
                Arguments.arguments("/transaction/earn"),
                Arguments.arguments("/transaction/award"),
                Arguments.arguments("/transaction/authorize")
        );
    }
}