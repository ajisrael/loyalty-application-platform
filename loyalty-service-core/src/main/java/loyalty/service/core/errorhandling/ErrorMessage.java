package loyalty.service.core.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ErrorMessage {

    private final Date timestamp;
    private final String message;
}