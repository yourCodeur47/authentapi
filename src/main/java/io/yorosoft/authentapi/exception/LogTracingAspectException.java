package io.yorosoft.authentapi.exception;

public class LogTracingAspectException extends RuntimeException {
    public LogTracingAspectException(String message, Throwable cause) {
        super(message, cause);
    }
}
