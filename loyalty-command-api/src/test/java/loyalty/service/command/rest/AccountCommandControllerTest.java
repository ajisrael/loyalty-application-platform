package loyalty.service.command.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.rest.requests.CreateAccountRequestModel;
import loyalty.service.command.rest.responses.AccountCreatedResponseModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = AccountCommandController.class)
@ExtendWith(MockitoExtension.class)
class AccountCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CommandGateway commandGateway;

    @MockBean
    EventGateway eventGateway;

    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();

    @Test
    @DisplayName("Can create an account")
    void testCreateAccount_whenCorrectAccountDetailsProvided_shouldReturnAccountIdAndRequestId() throws Exception {
        // Arrange
        CreateAccountRequestModel createAccountRequestModel = new CreateAccountRequestModel(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(createAccountRequestModel));

        when(commandGateway.sendAndWait(any(CreateAccountCommand.class))).thenReturn(TEST_ACCOUNT_ID);

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        AccountCreatedResponseModel accountCreatedResponseModel = convertResponseToAccountCreatedResponseModel(mvcResult);

        // Assert
        assertNotNull(accountCreatedResponseModel.getRequestId(), "RequestId should not be null");
        assertDoesNotThrow(() -> UUID.fromString(accountCreatedResponseModel.getRequestId()), "RequestId should be a valid UUID");
        assertEquals(TEST_ACCOUNT_ID, accountCreatedResponseModel.getAccountId(), "AccountIds should match");
    }


    @Test
    @DisplayName("Can update information on an existing account")
    void testUpdateAccount_whenCorrectAccountDetailsProvided_shouldReturnAccountIdAndRequestId() throws Exception {
        // Arrange
        CreateAccountRequestModel createAccountRequestModel = new CreateAccountRequestModel(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(createAccountRequestModel));

        when(commandGateway.sendAndWait(any(CreateAccountCommand.class))).thenReturn(TEST_ACCOUNT_ID);

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        AccountCreatedResponseModel accountCreatedResponseModel = convertResponseToAccountCreatedResponseModel(mvcResult);

        // Assert
        assertNotNull(accountCreatedResponseModel.getRequestId(), "RequestId should not be null");
        assertDoesNotThrow(() -> UUID.fromString(accountCreatedResponseModel.getRequestId()), "RequestId should be a valid UUID");
        assertEquals(TEST_ACCOUNT_ID, accountCreatedResponseModel.getAccountId(), "AccountIds should match");
    }

    private AccountCreatedResponseModel convertResponseToAccountCreatedResponseModel(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBodyAsString);
        String requestId = jsonNode.get("requestId").asText();
        String accountId = jsonNode.get("accountId").asText();

        AccountCreatedResponseModel accountCreatedResponseModel = AccountCreatedResponseModel.builder()
                .requestId(requestId)
                .accountId(accountId)
                .build();

        return accountCreatedResponseModel;
    }
}