package com.miktattooink.service;

import com.miktattooink.dto.ProfileResponse;
import com.miktattooink.dto.TattooWorkResponse;
import com.miktattooink.model.TattooWork;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkService {

    private final List<TattooWork> works = List.of(
            new TattooWork(1L, "Snake & Staff", "Blackwork", "/images/work-1.jpg", "Serpente avvolto su un bastone, dal polso alla mano, con scritta in arabo."),
            new TattooWork(2L, "Chef", "Lettering", "/images/work-2.jpg", "Lettering sulla nuca con tratto deciso e scritto a mano."),
            new TattooWork(3L, "Happy Puppy", "Animali", "/images/work-3.jpg", "Ritratto realistico di un cucciolo sull'avambraccio."),
            new TattooWork(4L, "Eye", "Realistic", "/images/work-4.jpg", "Occhio realistico sul polpaccio, primo passo di un progetto più ampio."),
            new TattooWork(5L, "Lion", "Animali", "/images/work-5.jpg", "Leone black & grey sul braccio, con criniera ricca di dettagli."),
            new TattooWork(6L, "Laughing Girl", "Realistic", "/images/work-6.jpg", "Ritratto femminile con trucco da clown e vetro infranto."),
            new TattooWork(7L, "Max", "Animali", "/images/work-7.jpg", "Ritratto di cane con il nome in corsivo."),
            new TattooWork(8L, "Crown of Thorns", "Realistic", "/images/work-8.jpg", "Volto di Cristo con corona di spine sull'avambraccio.")
    );

    public List<TattooWorkResponse> findAllWorks() {
        return works.stream()
                .map(this::toResponse)
                .toList();
    }

    public ProfileResponse getProfile() {
        return new ProfileResponse(
                "Mik Tattoo Ink",
                "/images/mik-profile.jpg",
                List.of(
                        "Ogni tatuaggio parte da una storia. Prima della macchinetta viene l’ascolto: chi sei, cosa ti rappresenta, cosa vuoi portare con te per sempre. Da lì nasce un disegno pensato solo per te, che non troverai sulla pelle di nessun altro.",
                        "Nello studio di Vico del Gargano il lavoro è fatto di nero pieno, linee sicure e sfumature curate. Niente fretta e niente compromessi: ogni progetto viene seguito passo dopo passo, dal primo schizzo all’ultimo ritocco."
                ),
                "info@miktattooink.it",
                "+39 333 000 0000",
                "Vico del Gargano, Italia"
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
