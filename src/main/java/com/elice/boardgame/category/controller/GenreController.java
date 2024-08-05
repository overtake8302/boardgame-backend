package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.DTO.GenreDto;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.service.GenreService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genre")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreDto>> getAllGenres() {
        return ResponseEntity.ok(genreService.findAll());
    }

    @GetMapping("/{GenreId}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable("GenreId") Long id) {
        return ResponseEntity.ok(genreService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<GenreDto> getGenreByName(@PathVariable String name) {
        return ResponseEntity.ok(genreService.findByGenreName(name));
    }

    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@RequestBody GenreDto genreDto) {
        return ResponseEntity.ok(genreService.save(genreDto));
    }

    @PutMapping("/{GenreId}")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable("GenreId") Long id,
        @RequestBody GenreDto genreDto) {
        return ResponseEntity.ok(genreService.update(id, genreDto.getGenre()));
    }

    @DeleteMapping("/{GenreId}")
    public ResponseEntity<Void> deleteGenre(@PathVariable("GenreId") Long id) {
        genreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

