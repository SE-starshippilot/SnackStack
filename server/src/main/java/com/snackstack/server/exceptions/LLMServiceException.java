package com.snackstack.server.exceptions;

public class LLMServiceException extends RuntimeException {

  public LLMServiceException(String message) {
    super(message);
  }
}