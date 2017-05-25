package bip.error;

import bip.error.ServiceNotAvailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class UploadImageExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse illegalArgumentException(Exception ex) {
        return new ApiErrorResponse(400, 4000, ex.getMessage());
    }

    @ExceptionHandler(value = { NoHandlerFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse noHandlerFoundException(Exception ex) {
        return new ApiErrorResponse(404, 4041, ex.getMessage());
    }

    @ExceptionHandler(value = { Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse unknownException(Exception ex) {
        String stackTrace = ExceptionUtils.getStackTrace(ex);
        return new ApiErrorResponse(500, 5002, stackTrace);
    }

    @ExceptionHandler(value = { ServiceNotAvailableException.class })
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiErrorResponse serviceNotAvailableException(Exception ex) {
        return new ApiErrorResponse(503, 1000, ex.getMessage());
    }
    
    @ExceptionHandler(value = { CircuitBreakerOpenException.class })
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiErrorResponse circuitBreakerOpenException(Exception ex) {
        return new ApiErrorResponse(503, 1000, ex.getMessage());
    }
    
    
}