package com.example.exception;

public class UsernameNotFoundException extends RuntimeException{
    UsernameNotFoundException(){
        super();
    }

    UsernameNotFoundException(String message){
        super(message);
    }

}
