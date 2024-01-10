package br.com.dias.apiRest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomizedFileNotFoundException extends RuntimeException {

    public CustomizedFileNotFoundException(String message) {
        super(message);
    }

    public CustomizedFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
