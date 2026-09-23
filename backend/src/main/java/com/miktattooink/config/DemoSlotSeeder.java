package com.miktattooink.config;

import com.miktattooink.model.AvailabilitySlot;
import com.miktattooink.repository.AvailabilitySlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Finché gli orari non si gestiscono dal pannello admin, all'avvio crea degli orari
 * di esempio se nel database non ce n'è nessuno futuro.
 * Si disattiva con app.demo.seed-slots=false.
 */
@Component
@ConditionalOnProperty(name = "app.demo.seed-slots", havingValue = "true")
public class DemoSlotSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoSlotSeeder.class);

    private static final List<Integer> DAYS_FROM_TODAY = List.of(2, 3, 5, 7, 8);
    private static final List<LocalTime> START_TIMES = List.of(
            LocalTime.of(10, 0),
            LocalTime.of(11, 30),
            LocalTime.of(15, 0),
            LocalTime.of(17, 30)
    );
    private static final int SLOT_MINUTES = 45;

    private final AvailabilitySlotRepository slotRepository;

    public DemoSlotSeeder(AvailabilitySlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        LocalDate today = LocalDate.now();
        if (slotRepository.existsByDateGreaterThanEqual(today)) {
            return;
        }

        List<AvailabilitySlot> slots = new ArrayList<>();
        for (int days : DAYS_FROM_TODAY) {
            for (LocalTime start : START_TIMES) {
                slots.add(new AvailabilitySlot(today.plusDays(days), start, start.plusMinutes(SLOT_MINUTES)));
            }
        }

        slotRepository.saveAll(slots);
        log.info("Creati {} orari di esempio", slots.size());
    }
}
