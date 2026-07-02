package com.miktattooink.controller;

import com.miktattooink.dto.ProfileResponse;
import com.miktattooink.service.WorkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final WorkService workService;

    public ProfileController(WorkService workService) {
        this.workService = workService;
    }

    @GetMapping
    public ProfileResponse getProfile() {
        return workService.getProfile();
    }
}
