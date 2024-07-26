package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.entity.GenreEntity;
import com.elice.boardgame.category.service.GenreService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<GenreEntity> getAllGenres() {
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreEntity> getGenreById(@PathVariable Long id) {
        Optional<GenreEntity> genre = genreService.findById(id);
        if (genre.isPresent()) {
            return ResponseEntity.ok(genre.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<GenreEntity> getGenreByName(@PathVariable String name) {
        Optional<GenreEntity> genre = genreService.findByGenreName(name);
        if (genre.isPresent()) {
            return ResponseEntity.ok(genre.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public GenreEntity createGenre(@RequestBody String genreName) {
        GenreEntity genre = new GenreEntity();
        genre.setGenre(genreName);
        return genreService.save(genre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreEntity> updateGenre(@PathVariable Long id, @RequestBody String newGenreName) {
        Optional<GenreEntity> genre = genreService.findById(id);
        if (genre.isPresent()) {
            GenreEntity updatedGenre = genre.get();
            updatedGenre.setGenre(newGenreName);
            genreService.save(updatedGenre);
            return ResponseEntity.ok(updatedGenre);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        if (genreService.findById(id).isPresent()) {
            genreService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
