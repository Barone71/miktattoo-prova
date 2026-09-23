package com.miktattooink.service;

import com.miktattooink.dto.AdminSlotResponse;
import com.miktattooink.dto.SlotRequest;
import com.miktattooink.exception.InvalidSlotException;
import com.miktattooink.exception.SlotHasBookingException;
import com.miktattooink.exception.SlotNotFoundException;
import com.miktattooink.model.AvailabilitySlot;
import com.miktattooink.model.Booking;
import com.miktattooink.repository.AvailabilitySlotRepository;
import com.miktattooink.repository.BookingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Gestione degli orari dal pannello admin: il tatuatore aggiunge e toglie disponibilità.
 */
@Service
public class SlotService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final AvailabilitySlotRepository slotRepository;
    private final BookingRepository bookingRepository;

    public SlotService(AvailabilitySlotRepository slotRepository, BookingRepository bookingRepository) {
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminSlotResponse> findUpcomingSlots() {
        LocalDate today = LocalDate.now();
        Map<Long, Booking> bookingBySlot = bookingRepository.findBySlotDateGreaterThanEqual(today)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getSlot().getId(), Function.identity()));

        return slotRepository.findByDateGreaterThanEqualOrderByDateAscStartTimeAsc(today)
                .stream()
                .map(slot -> toResponse(slot, bookingBySlot.get(slot.getId())))
                .toList();
    }

    @Transactional
    public AdminSlotResponse createSlot(SlotRequest request) {
        if (request.date().isBefore(LocalDate.now())) {
            throw new InvalidSlotException("Non puoi aggiungere orari in un giorno passato.");
        }
        if (!request.endTime().isAfter(request.startTime())) {
            throw new InvalidSlotException("L'orario di fine deve essere dopo quello di inizio.");
        }

        boolean overlaps = slotRepository.findByDate(request.date())
                .stream()
                .anyMatch(existing -> request.startTime().isBefore(existing.getEndTime())
                        && existing.getStartTime().isBefore(request.endTime()));
        if (overlaps) {
            throw new InvalidSlotException("Questo orario si sovrappone a uno già presente in quel giorno.");
        }

        try {
            AvailabilitySlot saved = slotRepository.saveAndFlush(
                    new AvailabilitySlot(request.date(), request.startTime(), request.endTime())
            );
            return toResponse(saved, null);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidSlotException("Esiste già un orario con questo inizio in quel giorno.");
        }
    }

    @Transactional
    public void deleteSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException(String.valueOf(slotId)));

        if (bookingRepository.existsBySlotId(slotId)) {
            throw new SlotHasBookingException();
        }

        try {
            slotRepository.delete(slot);
            slotRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            // Un cliente ha prenotato proprio in questo istante.
            throw new SlotHasBookingException();
        }
    }

    /**
     * Rende un giorno non disponibile: toglie tutti gli orari liberi.
     * Gli orari già prenotati restano, per non perdere le prenotazioni.
     */
    @Transactional
    public int clearFreeSlots(LocalDate date) {
        Set<Long> bookedSlotIds = bookingRepository.findBookedSlotIds();

        List<AvailabilitySlot> freeSlots = slotRepository.findByDate(date)
                .stream()
                .filter(slot -> !bookedSlotIds.contains(slot.getId()))
                .toList();

        slotRepository.deleteAll(freeSlots);
        return freeSlots.size();
    }

    private AdminSlotResponse toResponse(AvailabilitySlot slot, Booking booking) {
        return new AdminSlotResponse(
                String.valueOf(slot.getId()),
                slot.getDate().toString(),
                slot.getStartTime().format(TIME_FORMAT),
                slot.getEndTime().format(TIME_FORMAT),
                booking != null,
                booking != null ? booking.getId() : null,
                booking != null ? booking.getName() : null
        );
    }
}
