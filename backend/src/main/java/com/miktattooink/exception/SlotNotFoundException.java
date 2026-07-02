package com.miktattooink.exception;

public class SlotNotFoundException extends RuntimeException {
    public SlotNotFoundException(String slotId) {
        super("Slot non trovato: " + slotId);
    }
}
