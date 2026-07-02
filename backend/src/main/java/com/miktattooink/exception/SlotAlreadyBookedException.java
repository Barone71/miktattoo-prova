package com.miktattooink.exception;

public class SlotAlreadyBookedException extends RuntimeException {
    public SlotAlreadyBookedException(String slotId) {
        super("Lo slot selezionato non è più disponibile: " + slotId);
    }
}
