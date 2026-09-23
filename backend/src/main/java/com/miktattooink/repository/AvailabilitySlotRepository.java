package com.miktattooink.repository;

import com.miktattooink.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByDateGreaterThanEqualOrderByDateAscStartTimeAsc(LocalDate from);

    List<AvailabilitySlot> findByDate(LocalDate date);

    boolean existsByDateGreaterThanEqual(LocalDate from);
}
