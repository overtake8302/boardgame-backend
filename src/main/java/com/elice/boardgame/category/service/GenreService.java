package com.elice.boardgame.category.service;

import com.elice.boardgame.ExceptionHandler.GenreAlreadyExistsException;
import com.elice.boardgame.ExceptionHandler.GenreNotFoundException;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.repository.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> findAll() {
        List<Genre> genres = genreRepository.findAll();
        if (genres.isEmpty()) {
            throw new GenreNotFoundException("찾는 장르가 없습니다.");
        }
        return genres;
    }

    public Genre findById(Long id) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new GenreNotFoundException("찾는 장르가 없습니다."));
        return genre;
    }

    public Genre findByGenreName(String name) {
        return genreRepository.findByGenre(name)
            .orElseThrow(() -> new GenreNotFoundException("찾는 장르가 없습니다."));
    }

    public Genre save(String name) {
        if (genreRepository.findByGenre(name).isPresent()) {
            throw new GenreAlreadyExistsException("이미 존재하는 장르명입니다.");
        }
        Genre genre = new Genre();
        genre.setGenre(name);
        return genreRepository.save(genre);
    }


    public Genre update(Long id, String newGenreName) {
        Genre updatedGenre = findById(id);
        updatedGenre.setGenre(newGenreName);
        return genreRepository.save(updatedGenre);
    }

    public void deleteById(Long id) {
        findById(id);
        genreRepository.deleteById(id);
    }
}
