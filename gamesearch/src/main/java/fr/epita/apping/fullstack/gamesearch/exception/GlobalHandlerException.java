package fr.epita.apping.fullstack.gamesearch.exception;

import fr.epita.apping.fullstack.gamesearch.presentation.api.response.ErrorResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalHandlerException {

  @ExceptionHandler(GameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleGameNotFound(GameNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(404, "Not Found", ex.getMessage()));
  }

  @ExceptionHandler(InvalidApiKeyException.class)
  public ResponseEntity<ErrorResponse> handleInvalidApiKey(InvalidApiKeyException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse(401, "Unauthorized", ex.getMessage()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse(401, "Unauthorized", "Invalid username or password"));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(403, "Forbidden", "Access denied"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    List<String> details =
        ex.getBindingResult().getFieldErrors().stream()
            .map(field -> field.getField() + ": " + field.getDefaultMessage())
            .toList();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "Bad Request", "Validation failed", details));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String message = "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'";
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "Bad Request", message));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "Bad Request", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred"));
  }
}
