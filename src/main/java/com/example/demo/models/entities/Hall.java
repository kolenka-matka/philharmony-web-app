package com.example.demo.models.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "halls")
public class Hall extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "hall")
    private List<Event> events = new ArrayList<>();

    public String getName() { return name; }
    public String getAddress() { return address; }
    public Integer getCapacity() { return capacity; }

    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
