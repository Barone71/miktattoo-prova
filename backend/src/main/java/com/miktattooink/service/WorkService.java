package com.miktattooink.service;

import com.miktattooink.dto.ProfileResponse;
import com.miktattooink.dto.SocialLinkResponse;
import com.miktattooink.dto.TattooWorkResponse;
import com.miktattooink.model.TattooWork;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkService {

    private final List<TattooWork> works = List.of(
            new TattooWork(1L, "Blackwork Snake", "Blackwork", "/images/tattoo-1.png", "Serpente blackwork con linee forti e contrasto pieno."),
            new TattooWork(2L, "Fine Line Angel", "Fine Line", "/images/tattoo-2.png", "Figura angelica minimale con linee sottili."),
            new TattooWork(3L, "Realistic Eye", "Realistic", "/images/tattoo-3.png", "Studio realistico su occhio e texture organiche."),
            new TattooWork(4L, "Minimal Script", "Lettering", "/images/tattoo-4.png", "Lettering essenziale con composizione pulita."),
            new TattooWork(5L, "Dark Rose", "Black & Grey", "/images/tattoo-5.png", "Rosa scura con ombre morbide e profondità."),
            new TattooWork(6L, "Custom Ritual", "Custom", "/images/tattoo-6.png", "Progetto custom con simboli, equilibrio e narrativa personale.")
    );

    public List<TattooWorkResponse> findAllWorks() {
        return works.stream()
                .map(this::toResponse)
                .toList();
    }

    public ProfileResponse getProfile() {
        return new ProfileResponse(
                "Mik Tattoo Ink",
                "/images/mik-profile.png",
                List.of(
                        "Mik Tattoo Ink nasce da una visione semplice: trasformare un’idea in un segno che resti. Ogni tatuaggio viene progettato ascoltando la persona, il suo stile, la sua storia e il significato che vuole portare sulla pelle.",
                        "Lo studio lavora con un’estetica pulita, essenziale e decisa. Linee precise, contrasti forti, composizioni equilibrate e massima cura in ogni fase: dalla consulenza iniziale fino alla realizzazione finale."
                ),
                "info@miktattooink.it",
                "+39 333 000 0000",
                "Napoli, Italia",
                List.of(
                        new SocialLinkResponse("Instagram", "https://instagram.com"),
                        new SocialLinkResponse("TikTok", "https://tiktok.com"),
                        new SocialLinkResponse("Facebook", "https://facebook.com")
                )
        );
    }

    private TattooWorkResponse toResponse(TattooWork work) {
        return new TattooWorkResponse(
                work.getId(),
                work.getTitle(),
                work.getStyle(),
                work.getImageUrl(),
                work.getDescription()
        );
    }
}
