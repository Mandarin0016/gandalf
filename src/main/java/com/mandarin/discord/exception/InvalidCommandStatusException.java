package com.mandarin.discord.exception;

import static com.mandarin.discord.exception.ExceptionCode.INVALID_COMMAND_STATUS;

public class InvalidCommandStatusException extends DefaultException {

    public InvalidCommandStatusException() {
        super(INVALID_COMMAND_STATUS);
    }

    public InvalidCommandStatusException(Throwable cause) {
        super(INVALID_COMMAND_STATUS, cause);
    }
}
