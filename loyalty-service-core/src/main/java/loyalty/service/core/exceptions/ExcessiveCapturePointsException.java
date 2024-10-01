package loyalty.service.core.exceptions;

public class ExcessiveCapturePointsException extends IllegalStateException {
    public ExcessiveCapturePointsException() {
        super("Cannot capture more points than authorized on a redemption");
    }
}
