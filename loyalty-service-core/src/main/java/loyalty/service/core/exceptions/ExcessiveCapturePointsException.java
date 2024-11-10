package loyalty.service.core.exceptions;

public class ExcessiveCapturePointsException extends IllegalStateException {
    public ExcessiveCapturePointsException() {
        // TODO: move this string to a constant
        super("Cannot capture more points than authorized on a redemption");
    }
}
