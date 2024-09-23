package loyalty.service.core.exceptions;

public class InsufficientPointsException extends IllegalStateException {
    public InsufficientPointsException() {
        super("Insufficient points for operation");
    }
}
