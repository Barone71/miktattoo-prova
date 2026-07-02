package com.miktattooink.dto;

public record BookingResponse(
        Long id,
        String slotId,
        String date,
        String startTime,
        String endTime,
        String name,
        String email,
        String phone,
        String tattooIdea,
        String placement,
        String approximateSize,
        String createdAt
) {
}
