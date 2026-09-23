package com.miktattooink.exception;

public class SlotNotFoundException extends RuntimeException {
    public SlotNotFoundException(String slotId) {
        super("Questo orario non è più disponibile: scegline un altro.");
    }
}
