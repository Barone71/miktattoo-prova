package com.miktattooink.service;

import com.miktattooink.dto.AvailabilitySlotResponse;
import com.miktattooink.dto.BookingRequest;
import com.miktattooink.dto.BookingResponse;
import com.miktattooink.exception.BookingNotFoundException;
import com.miktattooink.exception.SlotAlreadyBookedException;
import com.miktattooink.exception.SlotNotFoundException;
import com.miktattooink.model.AvailabilitySlot;
import com.miktattooink.model.Booking;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookingService {

    private final Map<String, AvailabilitySlot> slots = new LinkedHashMap<>();
    private final Map<Long, Booking> bookings = new LinkedHashMap<>();
    private final AtomicLong bookingSequence = new AtomicLong(1);

    public BookingService() {
        seedAvailability();
    }

    public List<AvailabilitySlotResponse> findAllAvailability() {
        return slots.values()
                .stream()
                .sorted(Comparator.comparing(AvailabilitySlot::getDate).thenComparing(AvailabilitySlot::getStartTime))
                .map(this::toSlotResponse)
                .toList();
    }

    public List<BookingResponse> findAllBookings() {
        return bookings.values()
                .stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
                .map(this::toBookingResponse)
                .toList();
    }

    public BookingResponse createBooking(BookingRequest request) {
        AvailabilitySlot slot = slots.get(request.slotId());

        if (slot == null) {
            throw new SlotNotFoundException(request.slotId());
        }

        if (!slot.isAvailable()) {
            throw new SlotAlreadyBookedException(request.slotId());
        }

        slot.markAsBooked();

        Booking booking = new Booking(
                bookingSequence.getAndIncrement(),
                slot.getId(),
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                request.name().trim(),
                request.email().trim(),
                request.phone().trim(),
                request.tattooIdea().trim(),
                request.placement().trim(),
                request.approximateSize().trim(),
                LocalDateTime.now()
        );

        bookings.put(booking.getId(), booking);
        return toBookingResponse(booking);
    }

    public void deleteBooking(Long bookingId) {
        Booking removed = bookings.remove(bookingId);

        if (removed == null) {
            throw new BookingNotFoundException(bookingId);
        }

        AvailabilitySlot slot = slots.get(removed.getSlotId());
        if (slot != null) {
            slot.markAsAvailable();
        }
    }

    private void seedAvailability() {
        List<LocalDate> dates = List.of(
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(8)
        );

        List<LocalTime> times = List.of(
                LocalTime.of(10, 0),
                LocalTime.of(11, 30),
                LocalTime.of(15, 0),
                LocalTime.of(17, 30)
        );

        int counter = 1;
        for (LocalDate date : dates) {
            for (LocalTime start : times) {
                String id = "slot-" + counter++;
                slots.put(id, new AvailabilitySlot(id, date, start, start.plusMinutes(45), true));
            }
        }

        AvailabilitySlot occupied = slots.get("slot-3");
        if (occupied != null) {
            occupied.markAsBooked();
        }
    }

    private AvailabilitySlotResponse toSlotResponse(AvailabilitySlot slot) {
        return new AvailabilitySlotResponse(
                slot.getId(),
                slot.getDate().toString(),
                slot.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                slot.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                slot.isAvailable()
        );
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getSlotId(),
                booking.getDate().toString(),
                booking.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                booking.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                booking.getName(),
                booking.getEmail(),
                booking.getPhone(),
                booking.getTattooIdea(),
                booking.getPlacement(),
                booking.getApproximateSize(),
                booking.getCreatedAt().toString()
        );
    }
}
