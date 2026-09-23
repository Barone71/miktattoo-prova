package com.miktattooink;

import com.miktattooink.dto.AdminSlotResponse;
import com.miktattooink.dto.BookingRequest;
import com.miktattooink.dto.BookingResponse;
import com.miktattooink.dto.SlotRequest;
import com.miktattooink.exception.SlotAlreadyBookedException;
import com.miktattooink.service.BookingService;
import com.miktattooink.service.SlotService;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Online il database è PostgreSQL: qui avviamo un PostgreSQL vero (portatile)
 * per verificare che le migrazioni Flyway e le entity funzionino anche lì, non solo su H2.
 */
@SpringBootTest
@ActiveProfiles("test")
class PostgresCompatibilityTest {

    private static EmbeddedPostgres postgres;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) throws IOException {
        postgres = EmbeddedPostgres.builder().start();
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
    }

    @AfterAll
    static void stopPostgres() throws IOException {
        if (postgres != null) {
            postgres.close();
        }
    }

    @Autowired
    private SlotService slotService;

    @Autowired
    private BookingService bookingService;

    @Test
    void schemaAndBookingFlowWorkOnPostgres() {
        LocalDate day = LocalDate.now().plusDays(70);
        AdminSlotResponse slot = slotService.createSlot(new SlotRequest(day, LocalTime.of(11, 0), LocalTime.of(12, 0)));

        BookingResponse booking = bookingService.createBooking(new BookingRequest(
                slot.id(), "Luca Neri", "luca@example.com", "+39 333 5555555",
                "Teschio blackwork", "Polpaccio", "15 cm"
        ));

        assertThat(booking.startTime()).isEqualTo("11:00");
        // Il vincolo unico del database blocca il doppione anche su PostgreSQL
        assertThatThrownBy(() -> bookingService.createBooking(new BookingRequest(
                slot.id(), "Altro", "altro@example.com", "1", "x", "x", "x"
        ))).isInstanceOf(SlotAlreadyBookedException.class);

        bookingService.deleteBooking(booking.id());
        assertThat(slotService.findUpcomingSlots())
                .anyMatch(s -> s.id().equals(slot.id()) && !s.booked());
    }
}
