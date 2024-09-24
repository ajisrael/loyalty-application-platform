package loyalty.service.core.errorhandling;

import loyalty.service.core.exceptions.*;
import org.axonframework.eventsourcing.AggregateDeletedException;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Date;

@ControllerAdvice
public class LoyaltyServiceErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyServiceErrorHandler.class);

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException exception, WebRequest webRequest) {
        BindingResult bindingResult = exception.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
        ErrorMessage errorResponse = new ErrorMessage(new Date(), errorMessage);
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccountNotFoundException.class})
    public ResponseEntity<Object> handleAccountNotFoundException(AccountNotFoundException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {LoyaltyBankNotFoundException.class})
    public ResponseEntity<Object> handleLoyaltyBankNotFoundException(LoyaltyBankNotFoundException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {NoLoyaltyBanksForAccountFoundException.class})
    public ResponseEntity<Object> handleLoyaltyBankWithAccountIdNotFoundException(NoLoyaltyBanksForAccountFoundException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {AggregateDeletedException.class})
    public ResponseEntity<Object> handleAggregateDeletedException(AggregateDeletedException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {AggregateNotFoundException.class})
    public ResponseEntity<Object> handleAggregateNotFoundException(AggregateNotFoundException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {NoResourceFoundException.class})
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {EmailExistsForAccountException.class})
    public ResponseEntity<Object> handleEmailExistsForAccountException(EmailExistsForAccountException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccountExistsWithLoyaltyBankException.class})
    public ResponseEntity<Object> handleAccountExistsWithLoyaltyBankException(AccountExistsWithLoyaltyBankException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UnsatisfiedServletRequestParameterException.class})
    public ResponseEntity<Object> handleUnsatisfiedServletRequestParameterException(
            UnsatisfiedServletRequestParameterException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalLoyaltyBankStateException.class})
    public ResponseEntity<Object> handleIllegalLoyaltyBankStateException(IllegalLoyaltyBankStateException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {InsufficientPointsException.class})
    public ResponseEntity<Object> handleInsufficientPointsException(InsufficientPointsException exception, WebRequest webRequest) {
        ErrorMessage errorResponse = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherExceptions(Exception exception, WebRequest webRequest) {
        LOGGER.error(exception.getClass().toString());
        ErrorMessage errorMessage = new ErrorMessage(new Date(), exception.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
