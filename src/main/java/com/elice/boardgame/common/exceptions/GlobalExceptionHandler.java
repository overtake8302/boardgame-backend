package com.elice.boardgame.common.exceptions;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.social.exception.SocialAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Internal Server Error");
        responseBody.put("message", ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<CommonResponse<String>> handleGenreNotFoundException(GenreNotFoundException ex) {
        CommonResponse<String> response = CommonResponse.<String>builder()
            .payload(null)
            .message(ex.getMessage())
            .status(HttpStatus.NOT_FOUND.value())
            .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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

    @ExceptionHandler(SocialAlreadyExistsException.class)
    public ResponseEntity<CommonResponse<Void>> handleSocialAlreadyExistsException(SocialAlreadyExistsException ex) {
        CommonResponse<Void> response = CommonResponse.<Void>builder()
            .payload(null)
            .message(ex.getMessage())
            .status(HttpStatus.BAD_REQUEST.value())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
