package com.miktattooink.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Booking {
    private final Long id;
    private final String slotId;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String name;
    private final String email;
    private final String phone;
    private final String tattooIdea;
    private final String placement;
    private final String approximateSize;
    private final LocalDateTime createdAt;

    public Booking(
            Long id,
            String slotId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String name,
            String email,
            String phone,
            String tattooIdea,
            String placement,
            String approximateSize,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.slotId = slotId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.tattooIdea = tattooIdea;
        this.placement = placement;
        this.approximateSize = approximateSize;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getSlotId() {
        return slotId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getTattooIdea() {
        return tattooIdea;
    }

    public String getPlacement() {
        return placement;
    }

    public String getApproximateSize() {
        return approximateSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
