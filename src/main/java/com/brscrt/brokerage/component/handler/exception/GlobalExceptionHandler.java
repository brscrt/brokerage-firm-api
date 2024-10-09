package com.brscrt.brokerage.component.handler.exception;

import com.brscrt.brokerage.exception.checked.ApiException;
import com.brscrt.brokerage.exception.checked.NotFoundException;
import com.brscrt.brokerage.exception.unchecked.ApiRuntimeException;
import com.brscrt.brokerage.exception.unchecked.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String GENERIC_ERROR_MESSAGE = "An error occurred.";
    private static final String METHOD_PARAMETER_ERROR_MESSAGE = "Incorrect parameter.";
    private static final String METHOD_TYPE_ERROR_MESSAGE = "Incorrect method.";
    private static final String MISSING_BODY = "Missing Request Body.";
    private static final String INVALID_CREDENTIALS = "Invalid Credentials.";
    private static final String FORBIDDEN = "Access Forbidden.";

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception exception, WebRequest request) {
        return generateInternalServerResponse(exception, request);
    }

    @ExceptionHandler({InternalAuthenticationServiceException.class, BadCredentialsException.class})
    public final ResponseEntity<ErrorResponse> handleUnauthorized(Exception exception,
                                                                  WebRequest request) {
        return generateUnauthorizedResponse(exception, request);
    }

    @ExceptionHandler({AuthorizationDeniedException.class, UnauthorizedException.class})
    public final ResponseEntity<ErrorResponse> handleAuthorizationException(Exception exception, WebRequest request) {
        return generateForbiddenResponse(exception, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                    WebRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return generateResponse(request, HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchExceptions(
            MethodArgumentTypeMismatchException exception,
            WebRequest request) {
        logError(exception);

        return generateResponse(request, HttpStatus.BAD_REQUEST, METHOD_PARAMETER_ERROR_MESSAGE);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public final ResponseEntity<ErrorResponse> handleMissingServletRequestParameterExceptions(
            MissingServletRequestParameterException exception,
            WebRequest request) {
        logError(exception);

        return generateResponse(request, HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<ErrorResponse> handleHttpMessageNotReadableExceptions(
            HttpMessageNotReadableException exception,
            WebRequest request) {
        logError(exception);

        return generateResponse(request, HttpStatus.BAD_REQUEST, MISSING_BODY);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public final ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception,
            WebRequest request) {
        logError(exception);

        return generateResponse(request, HttpStatus.METHOD_NOT_ALLOWED, METHOD_TYPE_ERROR_MESSAGE);
    }

    @ExceptionHandler({NotFoundException.class, NoResourceFoundException.class})
    public final ResponseEntity<ErrorResponse> handleNotFoundException(Exception exception,
                                                                       WebRequest request) {
        return generateInformativeResponse(exception, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApiException.class)
    public final ResponseEntity<ErrorResponse> handleApiExceptions(ApiException exception,
                                                                   WebRequest request) {
        return generateInformativeResponse(exception, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiRuntimeException.class)
    public final ResponseEntity<ErrorResponse> handleApiRuntimeExceptions(ApiRuntimeException exception,
                                                                          WebRequest request) {
        return generateInformativeResponse(exception, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> generateInformativeResponse(Exception exception, WebRequest request,
                                                                      HttpStatus httpStatus) {
        log.error(exception.toString());

        return generateResponse(request, httpStatus, exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> generateInternalServerResponse(Exception exception, WebRequest request) {
        logError(exception);

        return generateResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, GENERIC_ERROR_MESSAGE);
    }

    private ResponseEntity<ErrorResponse> generateUnauthorizedResponse(Exception exception, WebRequest request) {
        logError(exception);

        return generateResponse(request, HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS);
    }

    private ResponseEntity<ErrorResponse> generateForbiddenResponse(Exception exception, WebRequest request) {
        logError(exception);

        return generateResponse(request, HttpStatus.FORBIDDEN, FORBIDDEN);
    }

    private ResponseEntity<ErrorResponse> generateResponse(WebRequest request,
                                                           HttpStatus httpStatus, String errorMessage) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), errorMessage,
                request.getDescription(false));

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    private void logError(Exception exception) {
        String errorClass = exception.getClass().getSimpleName();
        if (Objects.nonNull(exception.getCause())) {
            log.error("{}: {}. Cause: {}", errorClass, exception.getMessage(),
                    exception.getCause().getMessage());
        } else {
            log.error("{} occurred: {}.", errorClass, exception.getMessage());
        }
    }
}