package com.elice.boardgame.category.service;

import com.elice.boardgame.common.exceptions.GenreAlreadyExistsException;
import com.elice.boardgame.common.exceptions.GenreNotFoundException;
import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.mapper.GenreMapper;
import com.elice.boardgame.category.repository.GenreRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public List<GenreDto> findAll() {
        List<Genre> genres = genreRepository.findAll();
        if (genres.isEmpty()) {
            throw new GenreNotFoundException("찾는 장르가 없습니다.");
        }
        return genres.stream()
            .map(genreMapper::toDto)
            .collect(Collectors.toList());
    }

    public GenreDto findById(Long id) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new GenreNotFoundException("찾는 장르가 없습니다."));
        return genreMapper.toDto(genre);
    }

    public GenreDto findByGenreName(String name) {
        Genre genre = genreRepository.findByGenre(name)
            .orElseThrow(() -> new GenreNotFoundException("찾는 장르가 없습니다."));
        return genreMapper.toDto(genre);
    }

    public GenreDto save(GenreDto genreDto) {
        if (genreRepository.findByGenre(genreDto.getGenre()).isPresent()) {
            throw new GenreAlreadyExistsException("이미 존재하는 장르명입니다.");
        }
        Genre genre = genreMapper.toEntity(genreDto);
        genreRepository.save(genre);
        return genreMapper.toDto(genre);
    }

    public GenreDto update(Long id, String newGenreName) {
        Genre updatedGenre = genreRepository.findById(id)
            .orElseThrow(() -> new GenreNotFoundException("찾는 장르가 없습니다."));
        updatedGenre.setGenre(newGenreName);
        genreRepository.save(updatedGenre);
        return genreMapper.toDto(updatedGenre);
    }

    public void deleteById(Long id) {
        genreRepository.deleteById(id);
    }
}
