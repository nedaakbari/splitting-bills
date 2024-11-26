package ir.splitwise.splitbills.controller;

import ir.splitwise.splitbills.exceptions.DuplicateDataException;
import ir.splitwise.splitbills.exceptions.InvalidDataException;
import ir.splitwise.splitbills.exceptions.UserNotFoundException;
import ir.splitwise.splitbills.models.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorInfo> handleInvalidDataException(MethodArgumentNotValidException e) {
        String message = getMessage(e);
        return new ResponseEntity<>(new ErrorInfo(message), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ErrorInfo> handleException(HttpMediaTypeNotSupportedException e) {
        return new ResponseEntity<>(new ErrorInfo("بدنه نمیتواند خالی باشد"), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserNotFoundException.class, InvalidDataException.class, DuplicateDataException.class})
    public ResponseEntity<ErrorInfo> handleInvalidDataException(Exception e) {
        return new ResponseEntity<>(new ErrorInfo(e.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.error(exception.getMessage(), exception.getStackTrace());
        return new ResponseEntity<>("an error happened: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getMessage(MethodArgumentNotValidException ex) {
        var exceptions = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
                .toList();

        return StringUtils.join(exceptions, " ,");
    }
}
