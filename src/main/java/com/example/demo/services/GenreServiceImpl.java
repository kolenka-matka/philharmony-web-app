package com.example.demo.services;

import com.example.demo.models.entities.Genre;
import com.example.demo.repositories.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Genre getGenreById(String id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Жанр не найден"));
    }

    @Override
    @Transactional
    public Genre saveGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    @Transactional
    public void deleteGenre(String id) {
        genreRepository.deleteById(id);
    }
}