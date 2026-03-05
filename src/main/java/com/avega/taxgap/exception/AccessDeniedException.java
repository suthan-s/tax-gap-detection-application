package com.avega.taxgap.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends RuntimeException {

  private UserRequestException userRequestException;

  public AccessDeniedException(String message, UserRequestException errorException){
    super(message);
    this.userRequestException = errorException;
  }
}
