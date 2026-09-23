package com.miktattooink.exception;

public class SlotHasBookingException extends RuntimeException {
    public SlotHasBookingException() {
        super("Questo orario ha una prenotazione: cancella prima la prenotazione.");
    }
}
