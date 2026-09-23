package com.miktattooink.service;

import com.miktattooink.dto.AdminSlotResponse;
import com.miktattooink.dto.BookingRequest;
import com.miktattooink.dto.SlotRequest;
import com.miktattooink.exception.InvalidSlotException;
import com.miktattooink.exception.SlotHasBookingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SlotServiceTest {

    // Un giorno lontano, senza gli orari di esempio creati all'avvio dei test.
    private static final LocalDate DAY = LocalDate.now().plusDays(40);

    @Autowired
    private SlotService slotService;

    @Autowired
    private BookingService bookingService;

    @Test
    void adminCanAddSlotAndCustomersSeeIt() {
        AdminSlotResponse slot = slotService.createSlot(slot(DAY, "14:00", "15:00"));

        assertThat(slot.booked()).isFalse();
        assertThat(bookingService.findAllAvailability())
                .anyMatch(s -> s.id().equals(slot.id()) && s.available());
    }

    @Test
    void rejectsInvalidSlots() {
        slotService.createSlot(slot(DAY, "14:00", "15:00"));

        assertThatThrownBy(() -> slotService.createSlot(slot(DAY, "14:30", "15:30")))
                .isInstanceOf(InvalidSlotException.class);
        assertThatThrownBy(() -> slotService.createSlot(slot(DAY, "16:00", "15:00")))
                .isInstanceOf(InvalidSlotException.class);
        assertThatThrownBy(() -> slotService.createSlot(slot(LocalDate.now().minusDays(1), "10:00", "11:00")))
                .isInstanceOf(InvalidSlotException.class);
    }

    @Test
    void adjacentSlotsAreAllowed() {
        slotService.createSlot(slot(DAY, "14:00", "15:00"));

        AdminSlotResponse next = slotService.createSlot(slot(DAY, "15:00", "16:00"));

        assertThat(next.id()).isNotNull();
    }

    @Test
    void bookedSlotCannotBeDeleted() {
        AdminSlotResponse slot = slotService.createSlot(slot(DAY, "14:00", "15:00"));
        Long bookingId = bookingService.createBooking(bookingFor(slot.id())).id();

        assertThatThrownBy(() -> slotService.deleteSlot(Long.valueOf(slot.id())))
                .isInstanceOf(SlotHasBookingException.class);
        assertThat(slotService.findUpcomingSlots())
                .anyMatch(s -> s.id().equals(slot.id()) && s.booked()
                        && "Anna Bianchi".equals(s.bookedBy()) && bookingId.equals(s.bookingId()));
    }

    @Test
    void cancellingTheBookingFromTheSlotFreesIt() {
        AdminSlotResponse slot = slotService.createSlot(slot(DAY, "14:00", "15:00"));
        bookingService.createBooking(bookingFor(slot.id()));
        Long bookingId = slotService.findUpcomingSlots().stream()
                .filter(s -> s.id().equals(slot.id()))
                .findFirst().orElseThrow()
                .bookingId();

        bookingService.deleteBooking(bookingId);

        assertThat(slotService.findUpcomingSlots())
                .anyMatch(s -> s.id().equals(slot.id()) && !s.booked() && s.bookingId() == null);
    }

    @Test
    void clearingADayKeepsBookedSlots() {
        AdminSlotResponse booked = slotService.createSlot(slot(DAY, "10:00", "11:00"));
        slotService.createSlot(slot(DAY, "12:00", "13:00"));
        slotService.createSlot(slot(DAY, "15:00", "16:00"));
        bookingService.createBooking(bookingFor(booked.id()));

        int removed = slotService.clearFreeSlots(DAY);

        assertThat(removed).isEqualTo(2);
        assertThat(slotService.findUpcomingSlots())
                .filteredOn(s -> s.date().equals(DAY.toString()))
                .extracting(AdminSlotResponse::id)
                .containsExactly(booked.id());
    }

    private SlotRequest slot(LocalDate date, String start, String end) {
        return new SlotRequest(date, LocalTime.parse(start), LocalTime.parse(end));
    }

    private BookingRequest bookingFor(String slotId) {
        return new BookingRequest(slotId, "Anna Bianchi", "anna@email.it", "+39 333 1111111",
                "Rosa fine line", "Polso", "5 cm");
    }
}
