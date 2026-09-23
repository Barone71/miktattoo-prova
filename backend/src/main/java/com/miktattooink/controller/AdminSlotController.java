package com.miktattooink.controller;

import com.miktattooink.dto.AdminSlotResponse;
import com.miktattooink.dto.SlotRequest;
import com.miktattooink.service.SlotService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminSlotController {

    private final SlotService slotService;

    public AdminSlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping("/slots")
    public List<AdminSlotResponse> findUpcomingSlots() {
        return slotService.findUpcomingSlots();
    }

    @PostMapping("/slots")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminSlotResponse createSlot(@Valid @RequestBody SlotRequest request) {
        return slotService.createSlot(request);
    }

    @DeleteMapping("/slots/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSlot(@PathVariable Long id) {
        slotService.deleteSlot(id);
    }

    @DeleteMapping("/days/{date}/slots")
    public Map<String, Integer> clearFreeSlots(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Map.of("removed", slotService.clearFreeSlots(date));
    }
}
