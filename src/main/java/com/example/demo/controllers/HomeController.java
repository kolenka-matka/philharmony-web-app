package com.example.demo.controllers;

import org.springframework.ui.Model;
import com.example.demo.dto.TopEventDto;
import com.example.demo.services.EventServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class HomeController {
    private final EventServiceImpl eventService;

    public HomeController(EventServiceImpl eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String home(Model model) {

        List<TopEventDto> topEvents = eventService.getTopEventsByBookings(3);
        model.addAttribute("topEvents", topEvents);

        return "index";
    }
}
