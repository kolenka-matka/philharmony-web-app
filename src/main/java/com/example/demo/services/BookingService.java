package com.example.demo.services;

import com.example.demo.dto.BookingCreateDto;
import com.example.demo.dto.BookingViewDto;

import java.security.Principal;
import java.util.List;


public interface BookingService {

    void createBooking(String eventTitle,
                       BookingCreateDto bookingCreateDto,
                       Principal principal);

    List<BookingViewDto> getUserBookings(Principal principal);

    BookingViewDto getBookingById(String id, Principal principal);

    void cancelBooking(String bookingId, Principal principal);
}
