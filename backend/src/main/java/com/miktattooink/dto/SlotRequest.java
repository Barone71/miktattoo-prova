package com.miktattooink.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record SlotRequest(
        @NotNull(message = "La data è obbligatoria")
        LocalDate date,

        @NotNull(message = "L'orario di inizio è obbligatorio")
        LocalTime startTime,

        @NotNull(message = "L'orario di fine è obbligatorio")
        LocalTime endTime
) {
}
