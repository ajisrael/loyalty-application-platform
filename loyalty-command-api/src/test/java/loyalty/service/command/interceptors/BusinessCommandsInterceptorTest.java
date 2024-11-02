package loyalty.service.command.interceptors;

import loyalty.service.command.data.repositories.BusinessLookupRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BusinessCommandsInterceptorTest {

    @Mock
    private BusinessLookupRepository businessLookupRepository;

    @InjectMocks
    BusinessCommandsInterceptor businessCommandsInterceptor;



}