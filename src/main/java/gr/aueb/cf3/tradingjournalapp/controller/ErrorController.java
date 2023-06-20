package gr.aueb.cf3.tradingjournalapp.controller;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<?> handleExpiredOrWrongToken(Exception ex) {
        String message = ex.getMessage();
        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        String message = result.getFieldErrors().stream()
                .map((fieldError) -> {
                    String field = fieldError.getField();
                    return field + ": " + fieldError.getDefaultMessage();
                })
                .collect(Collectors.joining("\n"));
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
