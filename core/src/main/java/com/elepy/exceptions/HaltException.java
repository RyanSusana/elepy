package com.elepy.exceptions;

public class HaltException extends ElepyException {
    public HaltException() {
        super("Halt!", 403);
    }

    public static void halt() {
        throw new HaltException();
    }
}
