package com.mandarin.discord.exception;

import java.util.UUID;

public enum ExceptionCode {

    NO_SUCH_EVENT_LISTENER_EXCEPTION(
            UUID.fromString("6dd49863-fcb3-4d39-b903-58a5297d3b5e")
    );

    ExceptionCode(UUID code) {
        this.code = code;
    }

    private final UUID code;
}
