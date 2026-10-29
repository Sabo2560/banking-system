package io.github.sabo2560.bank.exception;

public class ConfigException extends RuntimeException{

    public ConfigException(String message){
        super(message);
    }
    public ConfigException(String message, Throwable cause){
        super(message,cause);
    }



}
