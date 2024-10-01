package loyalty.service.core.exceptions;

public class ExcessiveVoidPointsException extends IllegalStateException {
    public ExcessiveVoidPointsException() {
        super("Cannot void more points than authorized on a redemption");
    }
}
