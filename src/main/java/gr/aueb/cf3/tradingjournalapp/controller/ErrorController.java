package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.TradeNotFoundException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.TradeUserCorrelationException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UsernameAlreadyExistsException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.HibernateQueryException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler({UsernameAlreadyExistsException.class, EmailAlreadyExistsException.class})
    public ResponseEntity<?> handleExistingUsernameOrEmail(Exception ex) {
        String message = ex.getMessage();
        ex.printStackTrace();
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({TradeUserCorrelationException.class, TradeNotFoundException.class})
    public ResponseEntity<?> handleTradeExceptions(TradeUserCorrelationException ex) {
        String message = ex.getMessage();
        ex.printStackTrace();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UsernameNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<?> handleUsernameNotFound(Exception ex) {
        String message = ex.getMessage();
        ex.printStackTrace();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HibernateQueryException.class})
    public ResponseEntity<?> handleDatabaseException(HibernateQueryException ex) {
        String message = ex.getMessage();
        ex.printStackTrace();
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({JwtException.class})
    public ResponseEntity<?> handleJwtException(JwtException ex) {
        String message = ex.getMessage();
        ex.printStackTrace();
        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
