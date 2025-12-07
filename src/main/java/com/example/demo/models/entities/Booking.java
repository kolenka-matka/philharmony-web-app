package com.example.demo.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false)
    private String seats;
    public User getUser() { return user; }
    public Event getEvent() { return event; }
    public String getSeats() { return seats; }

    public void setUser(User user) { this.user = user; }
    public void setEvent(Event event) { this.event = event; }
    public void setSeats(String seats) { this.seats = seats; }
}
