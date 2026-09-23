package com.miktattooink.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false)
    private AvailabilitySlot slot;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(name = "tattoo_idea", nullable = false, length = 1000)
    private String tattooIdea;

    @Column(nullable = false, length = 120)
    private String placement;

    @Column(name = "approximate_size", nullable = false, length = 120)
    private String approximateSize;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Booking() {
        // richiesto da JPA
    }

    public Booking(
            AvailabilitySlot slot,
            String name,
            String email,
            String phone,
            String tattooIdea,
            String placement,
            String approximateSize,
            LocalDateTime createdAt
    ) {
        this.slot = slot;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.tattooIdea = tattooIdea;
        this.placement = placement;
        this.approximateSize = approximateSize;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public AvailabilitySlot getSlot() {
        return slot;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getTattooIdea() {
        return tattooIdea;
    }

    public String getPlacement() {
        return placement;
    }

    public String getApproximateSize() {
        return approximateSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
