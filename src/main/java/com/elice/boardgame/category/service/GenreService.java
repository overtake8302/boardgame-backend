package com.elice.boardgame.category.service;

import com.elice.boardgame.category.entity.GenreEntity;
import com.elice.boardgame.category.repository.GenreRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    public List<GenreEntity> findAll() {
        return genreRepository.findAll();
    }

    public Optional<GenreEntity> findById(Long id) {
        return genreRepository.findById(id);
    }

    public Optional<GenreEntity> findByGenreName(String name) {
        return genreRepository.findByGenre(name);
    }

    public GenreEntity save(GenreEntity genre) {
        return genreRepository.save(genre);
    }

    public void deleteById(Long id) {
        genreRepository.deleteById(id);
    }
}
