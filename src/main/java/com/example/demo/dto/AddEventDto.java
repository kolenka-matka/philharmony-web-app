package com.example.demo.dto;

import com.example.demo.models.enums.EventType;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class AddEventDto {

    // Поля БЕЗ аннотаций (как в примере!)
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private EventType eventType;
    private String imageUrl;
    private Integer availableSeats;
    private String hallId;

    // Аннотации на ГЕТТЕРАХ (как в примере!)

    @NotEmpty(message = "Название события не должно быть пустым!")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов!")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NotEmpty(message = "Описание не должно быть пустым!")
    @Size(min = 10, max = 500, message = "Описание должно быть от 10 до 500 символов!")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull(message = "Дата и время обязательны!")
    @Future(message = "Дата должна быть в будущем!")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @NotNull(message = "Тип события обязателен!")
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @NotEmpty(message = "URL изображения обязателен!")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NotNull(message = "Количество мест обязательно!")
    @Min(value = 1, message = "Количество мест должно быть больше 0!")
    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    // Внимание: В примере companyName, у вас hallId
    @NotNull(message = "Зал обязателен!")
    public String getHallId() {
        return hallId;
    }

    public void setHallId(String hallId) {
        this.hallId = hallId;
    }
}