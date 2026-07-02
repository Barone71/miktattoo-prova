package com.miktattooink.service;

import com.miktattooink.dto.BookingRequest;
import com.miktattooink.dto.BookingResponse;
import com.miktattooink.exception.SlotAlreadyBookedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingServiceTest {

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService();
    }

    @Test
    void shouldCreateBookingAndMarkSlotAsUnavailable() {
        BookingRequest request = validRequest("slot-1");

        BookingResponse response = bookingService.createBooking(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.slotId()).isEqualTo("slot-1");
        assertThat(response.name()).isEqualTo("Mario Rossi");

        boolean slotAvailable = bookingService.findAllAvailability()
                .stream()
                .filter(slot -> slot.id().equals("slot-1"))
                .findFirst()
                .orElseThrow()
                .available();

        assertThat(slotAvailable).isFalse();
    }

    @Test
    void shouldNotBookAlreadyOccupiedSlot() {
        BookingRequest request = validRequest("slot-3");

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(SlotAlreadyBookedException.class);
    }

    private BookingRequest validRequest(String slotId) {
        return new BookingRequest(
                slotId,
                "Mario Rossi",
                "mario@email.it",
                "+39 333 0000000",
                "Serpente blackwork sull'avambraccio",
                "Avambraccio",
                "12 cm"
        );
    }
}
