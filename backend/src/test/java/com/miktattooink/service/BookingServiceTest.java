package com.miktattooink.service;

import com.miktattooink.dto.AvailabilitySlotResponse;
import com.miktattooink.dto.BookingRequest;
import com.miktattooink.dto.BookingResponse;
import com.miktattooink.exception.SlotAlreadyBookedException;
import com.miktattooink.exception.SlotNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Test
    void shouldCreateBookingAndMarkSlotAsUnavailable() {
        String slotId = firstAvailableSlotId();

        BookingResponse response = bookingService.createBooking(validRequest(slotId));

        assertThat(response.id()).isNotNull();
        assertThat(response.slotId()).isEqualTo(slotId);
        assertThat(response.name()).isEqualTo("Mario Rossi");
        assertThat(isAvailable(slotId)).isFalse();
        assertThat(bookingService.findAllBookings()).extracting(BookingResponse::id).contains(response.id());
    }

    @Test
    void shouldNotBookAlreadyOccupiedSlot() {
        String slotId = firstAvailableSlotId();
        bookingService.createBooking(validRequest(slotId));

        assertThatThrownBy(() -> bookingService.createBooking(validRequest(slotId)))
                .isInstanceOf(SlotAlreadyBookedException.class);
    }

    @Test
    void deletingBookingFreesTheSlot() {
        String slotId = firstAvailableSlotId();
        BookingResponse booking = bookingService.createBooking(validRequest(slotId));

        bookingService.deleteBooking(booking.id());

        assertThat(isAvailable(slotId)).isTrue();
        assertThat(bookingService.findAllBookings()).isEmpty();
    }

    @Test
    void shouldRejectUnknownSlot() {
        assertThatThrownBy(() -> bookingService.createBooking(validRequest("999999")))
                .isInstanceOf(SlotNotFoundException.class);
        assertThatThrownBy(() -> bookingService.createBooking(validRequest("slot-1")))
                .isInstanceOf(SlotNotFoundException.class);
    }

    private String firstAvailableSlotId() {
        return bookingService.findAllAvailability()
                .stream()
                .filter(AvailabilitySlotResponse::available)
                .findFirst()
                .orElseThrow()
                .id();
    }

    private boolean isAvailable(String slotId) {
        return bookingService.findAllAvailability()
                .stream()
                .filter(slot -> slot.id().equals(slotId))
                .findFirst()
                .orElseThrow()
                .available();
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
