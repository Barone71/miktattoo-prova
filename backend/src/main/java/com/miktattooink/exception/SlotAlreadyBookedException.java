package com.miktattooink.exception;

public class SlotAlreadyBookedException extends RuntimeException {
    public SlotAlreadyBookedException(String slotId) {
        super("Questo orario è appena stato prenotato da qualcun altro: scegline un altro.");
    }
}
