package fr.epita.apping.fullstack.gamesearch.presentation.api.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    int status, String error, String message, LocalDateTime timestamp, List<String> details) {
  public ErrorResponse(int status, String error, String message) {
    this(status, error, message, LocalDateTime.now(), List.of());
  }

  public ErrorResponse(int status, String error, String message, List<String> details) {
    this(status, error, message, LocalDateTime.now(), details);
  }
}
