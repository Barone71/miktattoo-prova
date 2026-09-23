package com.miktattooink.service;

import com.miktattooink.dto.BookingResponse;

/** Pubblicato quando una prenotazione viene salvata. */
public record BookingCreatedEvent(BookingResponse booking) {
}
