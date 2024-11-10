package loyalty.service.core.exceptions;

public class ExcessiveVoidPointsException extends IllegalStateException {
    public ExcessiveVoidPointsException() {
        // TODO: move this string to a constant
        super("Cannot void more points than authorized on a redemption");
    }
}
