package com.example.demo.services;

import com.example.demo.models.entities.Genre;
import java.util.List;

public interface GenreService {
    List<Genre> getAllGenres();
    Genre getGenreById(String id);
    Genre saveGenre(Genre genre);
    void deleteGenre(String id);
}