package com.miktattooink.service;

import com.miktattooink.dto.AdminSlotResponse;
import com.miktattooink.dto.BookingRequest;
import com.miktattooink.dto.SlotRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        // Database dedicato: questo test salva davvero (niente rollback) per far partire la notifica.
        "spring.datasource.url=jdbc:h2:mem:notificationdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1",
        "app.notification.email=tatuatore@example.com",
        "app.mail.from=noreply@example.com"
})
@ActiveProfiles("test")
class BookingNotificationTest {

    @MockitoBean
    private JavaMailSender mailSender;

    @Autowired
    private SlotService slotService;

    @Autowired
    private BookingService bookingService;

    @Test
    void tattooistReceivesAnEmailForEveryNewBooking() {
        LocalDate day = LocalDate.now().plusDays(50);
        AdminSlotResponse slot = slotService.createSlot(new SlotRequest(day, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        bookingService.createBooking(new BookingRequest(
                slot.id(), "Giulia Verdi", "giulia@example.com", "+39 333 2222222",
                "Rondine old school", "Clavicola", "10 cm"
        ));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, timeout(5000)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("tatuatore@example.com");
        assertThat(message.getFrom()).isEqualTo("noreply@example.com");
        assertThat(message.getReplyTo()).isEqualTo("giulia@example.com");
        assertThat(message.getSubject()).contains("Giulia Verdi").contains("16:00");
        assertThat(message.getText())
                .contains("Rondine old school")
                .contains("+39 333 2222222")
                .contains("16:00")
                .contains("17:00");
    }
}
