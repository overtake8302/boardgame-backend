package com.elice.boardgame.category.mapper;

import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreDto toDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setGenreId(genre.getGenreId());
        dto.setGenre(genre.getGenre());
        return dto;
    }

    public Genre toEntity(GenreDto genreDto) {
        Genre genre = new Genre();
        genre.setGenre(genreDto.getGenre());
        genre.setGenreId(genreDto.getGenreId());
        return genre;
    }
}
