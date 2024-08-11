package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.service.GenreService;
import com.elice.boardgame.common.dto.CommonResponse;
import java.util.List;
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
    public CommonResponse<List<GenreDto>> getAllGenres() {
        List<GenreDto> genreDtos = genreService.findAll();
        return CommonResponse.<List<GenreDto>>builder()
            .payload(genreDtos)
            .message("")
            .status(200)
            .build();
    }

    @GetMapping("/{GenreId}")
    public CommonResponse<GenreDto> getGenreById(@PathVariable("GenreId") Long id) {
        GenreDto genreDto = genreService.findById(id);
        return CommonResponse.<GenreDto>builder()
            .payload(genreDto)
            .message("")
            .status(200)
            .build();
    }

    @GetMapping("/name/{name}")
    public CommonResponse<GenreDto> getGenreByName(@PathVariable String name) {
        GenreDto genreDto = genreService.findByGenreName(name);
        return CommonResponse.<GenreDto>builder()
            .payload(genreDto)
            .message("")
            .status(200)
            .build();
    }

    @PostMapping
    public void createGenre(@RequestBody GenreDto genreDto) {
        genreService.save(genreDto);
    }

    @PutMapping("/{GenreId}")
    public void updateGenre(@PathVariable("GenreId") Long id,
        @RequestBody GenreDto genreDto) {
        genreService.update(id, genreDto.getGenre());
    }

    @DeleteMapping("/{GenreId}")
    public void deleteGenre(@PathVariable("GenreId") Long id) {
        genreService.deleteById(id);
    }
}

