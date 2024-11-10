package loyalty.service.core.exceptions;

public class InsufficientPointsException extends IllegalStateException {
    public InsufficientPointsException() {
        // TODO: move this string to a constant
        super("Insufficient points for operation");
    }
}
