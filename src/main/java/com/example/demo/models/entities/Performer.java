package com.example.demo.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "performers")
public class Performer extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Genre getGenre() { return genre; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setGenre(Genre genre) { this.genre = genre; }
}
