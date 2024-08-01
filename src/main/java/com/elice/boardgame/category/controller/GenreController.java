package com.elice.boardgame.category.controller;

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
    public ResponseEntity<List<Genre>> getAllGenres() {
        return ResponseEntity.ok(genreService.findAll());
    }

    @GetMapping("/{GenreId}")
    public ResponseEntity<Genre> getGenreById(@PathVariable("GenreId") Long id) {
        return ResponseEntity.ok(genreService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Genre> getGenreByName(@PathVariable String name) {
        return ResponseEntity.ok(genreService.findByGenreName(name));
    }

    @PostMapping
    public void createGenre(@RequestBody String genreName) {
        System.out.println(genreName);
        genreService.save(genreName);
    }

    @PutMapping("/{GenreId}")
    public ResponseEntity<Genre> updateGenre(@PathVariable("GenreId") Long id,
        @RequestBody Map<String, String> request) {
        String newGenreName = request.get("name");
        return ResponseEntity.ok(genreService.update(id, newGenreName));
    }

    @DeleteMapping("/{GenreId}")
    public void deleteGenre(@PathVariable("GenreId") Long id) {
        genreService.deleteById(id);
    }
}

