package com.example.demo.services;

import com.example.demo.dto.AddEventDto;
import com.example.demo.dto.ShowEventInfoDto;
import com.example.demo.dto.ShowDetailedEventInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventService {

    List<ShowEventInfoDto> allEvents();
    ShowDetailedEventInfoDto eventDetails(String eventTitle);
    void addEvent(AddEventDto dto);
    Page<ShowEventInfoDto> allEventsPaginated(Pageable pageable);
    List<ShowEventInfoDto> searchEvents(String search);

    @Transactional
    void deleteEvent(String eventTitle);
}