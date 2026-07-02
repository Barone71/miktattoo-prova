package com.miktattooink.controller;

import com.miktattooink.dto.TattooWorkResponse;
import com.miktattooink.service.WorkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/works")
public class WorkController {

    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @GetMapping
    public List<TattooWorkResponse> findAllWorks() {
        return workService.findAllWorks();
    }
}
