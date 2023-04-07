package com.mandarin.discord.exception;


/**
 * Generic Exception
 */
public abstract class DefaultException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    protected DefaultException(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    protected DefaultException(ExceptionCode exceptionCode, Throwable cause) {
        super(cause);
        this.exceptionCode = exceptionCode;
    }

    protected ExceptionCode getExceptionCode() {
        return this.exceptionCode;
    }
}
