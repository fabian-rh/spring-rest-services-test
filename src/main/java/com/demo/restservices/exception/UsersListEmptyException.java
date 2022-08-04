package com.demo.restservices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsersListEmptyException extends RuntimeException {
    public UsersListEmptyException (String message){
        super(message);
    }
}
