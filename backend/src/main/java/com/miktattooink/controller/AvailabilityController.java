package com.miktattooink.controller;

import com.miktattooink.dto.AvailabilitySlotResponse;
import com.miktattooink.service.BookingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final BookingService bookingService;

    public AvailabilityController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<AvailabilitySlotResponse> findAllAvailability() {
        return bookingService.findAllAvailability();
    }
}
