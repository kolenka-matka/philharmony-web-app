package com.example.demo.dto;

public class TopEventDto {
    private String title;
    private Integer totalBookedSeats; // общее количество забронированных билетов
    private String imageUrl;
    private int position; // место в топе (1, 2, 3...)

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalBookedSeats() {
        return totalBookedSeats;
    }

    public void setTotalBookedSeats(Integer totalBookedSeats) {
        this.totalBookedSeats = totalBookedSeats;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public TopEventDto(String title, Integer totalBookedSeats, String imageUrl, int position) {
        this.title = title;
        this.totalBookedSeats = totalBookedSeats;
        this.imageUrl = imageUrl;
        this.position = position;
    }
}