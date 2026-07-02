package com.miktattooink.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailabilitySlot {
    private final String id;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private boolean available;

    public AvailabilitySlot(String id, LocalDate date, LocalTime startTime, LocalTime endTime, boolean available) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public String getId() {
        return id;
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

    public boolean isAvailable() {
        return available;
    }

    public void markAsBooked() {
        this.available = false;
    }

    public void markAsAvailable() {
        this.available = true;
    }
}
