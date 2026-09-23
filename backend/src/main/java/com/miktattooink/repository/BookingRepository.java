package com.miktattooink.repository;

import com.miktattooink.model.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = "slot")
    List<Booking> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "slot")
    List<Booking> findBySlotDateGreaterThanEqual(LocalDate from);

    boolean existsBySlotId(Long slotId);

    @Query("select b.slot.id from Booking b")
    Set<Long> findBookedSlotIds();
}
