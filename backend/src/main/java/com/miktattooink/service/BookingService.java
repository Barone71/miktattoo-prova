package com.miktattooink.service;

import com.miktattooink.dto.AvailabilitySlotResponse;
import com.miktattooink.dto.BookingRequest;
import com.miktattooink.dto.BookingResponse;
import com.miktattooink.exception.BookingNotFoundException;
import com.miktattooink.exception.SlotAlreadyBookedException;
import com.miktattooink.exception.SlotNotFoundException;
import com.miktattooink.model.AvailabilitySlot;
import com.miktattooink.model.Booking;
import com.miktattooink.repository.AvailabilitySlotRepository;
import com.miktattooink.repository.BookingRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class BookingService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final AvailabilitySlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BookingService(
            AvailabilitySlotRepository slotRepository,
            BookingRepository bookingRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlotResponse> findAllAvailability() {
        Set<Long> bookedSlotIds = bookingRepository.findBookedSlotIds();

        return slotRepository.findByDateGreaterThanEqualOrderByDateAscStartTimeAsc(LocalDate.now())
                .stream()
                .filter(this::isUpcoming)
                .map(slot -> toSlotResponse(slot, !bookedSlotIds.contains(slot.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toBookingResponse)
                .toList();
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        AvailabilitySlot slot = findBookableSlot(request.slotId());

        if (bookingRepository.existsBySlotId(slot.getId())) {
            throw new SlotAlreadyBookedException(request.slotId());
        }

        Booking booking = new Booking(
                slot,
                request.name().trim(),
                request.email().trim(),
                request.phone().trim(),
                request.tattooIdea().trim(),
                request.placement().trim(),
                request.approximateSize().trim(),
                LocalDateTime.now()
        );

        BookingResponse response;
        try {
            // saveAndFlush: se un altro cliente ha appena preso lo stesso orario,
            // il vincolo unico del database scatta qui e non al commit.
            response = toBookingResponse(bookingRepository.saveAndFlush(booking));
        } catch (DataIntegrityViolationException ex) {
            throw new SlotAlreadyBookedException(request.slotId());
        }

        // La notifica email parte solo a salvataggio completato (vedi BookingNotificationService).
        eventPublisher.publishEvent(new BookingCreatedEvent(response));
        return response;
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        // Cancellando la prenotazione l'orario torna automaticamente libero.
        bookingRepository.delete(booking);
    }

    private AvailabilitySlot findBookableSlot(String rawSlotId) {
        Long slotId;
        try {
            slotId = Long.valueOf(rawSlotId);
        } catch (NumberFormatException ex) {
            throw new SlotNotFoundException(rawSlotId);
        }

        return slotRepository.findById(slotId)
                .filter(this::isUpcoming)
                .orElseThrow(() -> new SlotNotFoundException(rawSlotId));
    }

    private boolean isUpcoming(AvailabilitySlot slot) {
        LocalDate today = LocalDate.now();
        return slot.getDate().isAfter(today)
                || (slot.getDate().isEqual(today) && slot.getStartTime().isAfter(LocalTime.now()));
    }

    private AvailabilitySlotResponse toSlotResponse(AvailabilitySlot slot, boolean available) {
        return new AvailabilitySlotResponse(
                String.valueOf(slot.getId()),
                slot.getDate().toString(),
                slot.getStartTime().format(TIME_FORMAT),
                slot.getEndTime().format(TIME_FORMAT),
                available
        );
    }

    private BookingResponse toBookingResponse(Booking booking) {
        AvailabilitySlot slot = booking.getSlot();
        return new BookingResponse(
                booking.getId(),
                String.valueOf(slot.getId()),
                slot.getDate().toString(),
                slot.getStartTime().format(TIME_FORMAT),
                slot.getEndTime().format(TIME_FORMAT),
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
