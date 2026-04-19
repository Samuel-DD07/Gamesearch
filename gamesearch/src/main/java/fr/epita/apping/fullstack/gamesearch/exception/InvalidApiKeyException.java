package fr.epita.apping.fullstack.gamesearch.exception;

public class InvalidApiKeyException extends RuntimeException {

  public InvalidApiKeyException() {
    super("Invalid or missing API key");
  }
}
