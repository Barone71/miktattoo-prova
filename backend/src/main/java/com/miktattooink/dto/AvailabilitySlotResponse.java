package com.miktattooink.dto;

public record AvailabilitySlotResponse(
        String id,
        String date,
        String startTime,
        String endTime,
        boolean available
) {
}
