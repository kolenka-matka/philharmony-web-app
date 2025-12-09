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
    private String comment;

    @Column(nullable = false)
    private int seatsCount;

    public User getUser() { return user; }
    public Event getEvent() { return event; }

    public void setUser(User user) { this.user = user; }
    public void setEvent(Event event) { this.event = event; }

    public String getComment() {return comment;}

    public void setComment(String comment) {this.comment = comment;}

    public int getSeatsCount() {return seatsCount;}

    public void setSeatsCount(int seatsCount) {this.seatsCount = seatsCount;}
}
