package com.miktattooink.dto;

public record AdminSlotResponse(
        String id,
        String date,
        String startTime,
        String endTime,
        boolean booked,
        Long bookingId,
        String bookedBy
) {
}
