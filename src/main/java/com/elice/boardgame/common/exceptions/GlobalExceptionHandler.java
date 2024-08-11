package com.elice.boardgame.common.exceptions;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Internal Server Error");
        responseBody.put("message", ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Not Found");
        responseBody.put("message", ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Error Occurred");
        responseBody.put("message", ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GameRootException.class)
    public ResponseEntity<GameErrorResponse> handleGamePostException(GameRootException gameRootException) {

        GameErrorResponse errorResponse = new GameErrorResponse(gameRootException.getErrorMessage().getErrorCode(), gameRootException.getErrorMessage().getErrorMessage());

        return new ResponseEntity<>(errorResponse, gameRootException.getHttpStatus());
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<UserErrorResponse> handleUserException(UserException userException) {

        UserErrorResponse errorResponse = new UserErrorResponse(userException.getUserErrorMessages().getErrorCode(), userException.getUserErrorMessages().getErrorMessage());

        return new ResponseEntity<>(errorResponse, userException.getHttpStatus());
    }
}
