package com.miktattooink.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookingRequest(
        @NotBlank(message = "Lo slot è obbligatorio")
        String slotId,

        @NotBlank(message = "Il nome è obbligatorio")
        @Size(max = 80, message = "Il nome non può superare 80 caratteri")
        String name,

        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Inserisci un'email valida")
        String email,

        @NotBlank(message = "Il telefono è obbligatorio")
        @Size(max = 30, message = "Il telefono non può superare 30 caratteri")
        String phone,

        @NotBlank(message = "Descrivi la tua idea di tatuaggio")
        @Size(max = 1000, message = "L'idea non può superare 1000 caratteri")
        String tattooIdea,

        @NotBlank(message = "La zona del corpo è obbligatoria")
        @Size(max = 120, message = "La zona del corpo non può superare 120 caratteri")
        String placement,

        @NotBlank(message = "La dimensione indicativa è obbligatoria")
        @Size(max = 120, message = "La dimensione indicativa non può superare 120 caratteri")
        String approximateSize
) {
}
