package loyalty.service.command.commands;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.logstash.logback.marker.Markers;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.slf4j.Marker;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class CreateAccountCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String accountId;
    private String firstName;
    private String lastName;
    private String email;

    public void validate() {
        throwExceptionIfParameterIsNullOrBlank(this.getAccountId(), ACCOUNT_ID_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getFirstName(), FIRST_NAME_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getLastName(), LAST_NAME_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getEmail(), EMAIL_CANNOT_BE_EMPTY);
    }

//    public Marker getLoggingMarker() {
//        Marker marker = Markers.append("requestId", this.getRequestId());
//        marker.add(Markers.append("accountId", this.accountId));
//        marker.add(Markers.append("firstName", this.firstName));
//        marker.add(Markers.append("lastName", this.lastName));
//        marker.add(Markers.append("email", this.email));
//        return marker;
//    }
}
