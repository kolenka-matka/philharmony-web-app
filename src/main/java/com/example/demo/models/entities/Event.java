package com.example.demo.models.entities;

import com.example.demo.models.enums.EventType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType = EventType.CONCERT;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Column(nullable = false)
    private Integer availableSeats;

    @ManyToMany
    @JoinTable(
            name = "events_performers",
            joinColumns = @JoinColumn(name="event_id"),
            inverseJoinColumns = @JoinColumn(name="performer_id")
    )
    private List<Performer> performers = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<Booking> bookings = new ArrayList<>();

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getDateTime() { return dateTime; }
    public Hall getHall() { return hall; }
    public Integer getAvailableSeats() { return availableSeats; }
    public List<Performer> getPerformers() { return performers; }
    public List<Booking> getBookings() { return bookings; }
    public String getImageUrl() {return imageUrl;}

    public EventType getEventType() { return eventType; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setHall(Hall hall) { this.hall = hall; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
    public void setPerformers(List<Performer> performers) { this.performers = performers; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

}
