package com.miktattooink.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super("Prenotazione non trovata: " + id);
    }
}
