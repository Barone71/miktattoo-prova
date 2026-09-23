package com.miktattooink.service;

import com.miktattooink.dto.BookingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Avvisa il tatuatore via email a ogni nuova prenotazione.
 * Parte solo dopo che la prenotazione è davvero salvata, e in background:
 * se l'invio fallisce la prenotazione resta valida e l'errore finisce nel log.
 */
@Service
public class BookingNotificationService {

    private static final Logger log = LoggerFactory.getLogger(BookingNotificationService.class);
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ITALIAN);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String notifyEmail;
    private final String fromEmail;

    public BookingNotificationService(
            ObjectProvider<JavaMailSender> mailSender,
            @Value("${app.notification.email}") String notifyEmail,
            @Value("${app.mail.from}") String fromEmail
    ) {
        this.mailSender = mailSender;
        this.notifyEmail = notifyEmail;
        this.fromEmail = fromEmail;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingCreated(BookingCreatedEvent event) {
        BookingResponse booking = event.booking();
        String subject = buildSubject(booking);
        String body = buildBody(booking);

        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || notifyEmail.isBlank()) {
            log.info("Email non configurata (MAIL_HOST o NOTIFY_EMAIL mancanti). Notifica non inviata:\n{}\n\n{}", subject, body);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notifyEmail);
        message.setFrom(fromEmail.isBlank() ? notifyEmail : fromEmail);
        // "Rispondi" nell'email scrive direttamente al cliente.
        message.setReplyTo(booking.email());
        message.setSubject(subject);
        message.setText(body);

        try {
            sender.send(message);
            log.info("Notifica prenotazione {} inviata a {}", booking.id(), notifyEmail);
        } catch (MailException ex) {
            log.error("Invio notifica prenotazione {} fallito", booking.id(), ex);
        }
    }

    String buildSubject(BookingResponse booking) {
        // Il nome arriva dal form: niente a capo nell'oggetto dell'email.
        String name = booking.name().replaceAll("[\\r\\n]+", " ");
        return "Nuova prenotazione: " + name + " – " + formatDate(booking.date()) + " ore " + booking.startTime();
    }

    String buildBody(BookingResponse booking) {
        return """
                Hai una nuova prenotazione dal sito Mik Tattoo Ink.

                QUANDO
                %s, dalle %s alle %s

                CLIENTE
                Nome: %s
                Email: %s
                Telefono: %s

                TATUAGGIO
                Idea: %s
                Zona del corpo: %s
                Dimensione: %s

                Rispondi a questa email per scrivere direttamente al cliente.
                Puoi vedere e gestire tutte le prenotazioni dal pannello admin del sito (/admin).
                """.formatted(
                formatDate(booking.date()),
                booking.startTime(),
                booking.endTime(),
                booking.name(),
                booking.email(),
                booking.phone(),
                booking.tattooIdea(),
                booking.placement(),
                booking.approximateSize()
        );
    }

    private String formatDate(String isoDate) {
        return LocalDate.parse(isoDate).format(DATE_FORMAT);
    }
}
