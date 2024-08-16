package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.service.GenreService;
import com.elice.boardgame.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "모든 장르 조회", description = "모든 게임 장르를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 모든 장르 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping
    public CommonResponse<List<GenreDto>> getAllGenres() {
        List<GenreDto> genreDtos = genreService.findAll();
        return CommonResponse.<List<GenreDto>>builder().payload(genreDtos).message("").status(200).build();
    }

    @Operation(summary = "장르 ID로 조회", description = "주어진 ID로 특정 게임 장르를 조회합니다.", parameters = {
        @Parameter(name = "GenreId", description = "조회할 장르의 ID", required = true, schema = @Schema(type = "Long", example = "1"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 특정 장르 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "장르를 찾을 수 없음")
    })
    @GetMapping("/{GenreId}")
    public CommonResponse<GenreDto> getGenreById(@PathVariable("GenreId") Long id) {
        GenreDto genreDto = genreService.findById(id);
        return CommonResponse.<GenreDto>builder().payload(genreDto).message("").status(200).build();
    }

    @Operation(summary = "장르 이름으로 조회", description = "주어진 이름으로 특정 게임 장르를 조회합니다.", parameters = {
        @Parameter(name = "name", description = "조회할 장르의 이름", required = true, schema = @Schema(type = "String", example = "액션"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 특정 장르 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "장르를 찾을 수 없음")
    })
    @GetMapping("/name/{name}")
    public CommonResponse<GenreDto> getGenreByName(@PathVariable String name) {
        GenreDto genreDto = genreService.findByGenreName(name);
        return CommonResponse.<GenreDto>builder().payload(genreDto).message("").status(200).build();
    }

    @Operation(summary = "장르 생성", description = "새로운 게임 장르를 생성합니다.", parameters = {
        @Parameter(name = "genreId", description = "생성할 장르의 ID", required = true, schema = @Schema(type = "Long", example = "1")),
        @Parameter(name = "genre", description = "생성할 장르의 이름", required = true, schema = @Schema(type = "String", example = "가족"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 장르 생성됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public void createGenre(@RequestBody GenreDto genreDto) {
        genreService.save(genreDto);
    }

    @Operation(summary = "장르 수정", description = "주어진 ID로 특정 게임 장르를 수정합니다.", parameters = {
        @Parameter(name = "GenreId", description = "수정할 장르의 ID", required = true, schema = @Schema(type = "Long", example = "1")),
        @Parameter(name = "genre", description = "생성할 장르의 이름", required = true, schema = @Schema(type = "String", example = "가족"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 장르 수정됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "장르를 찾을 수 없음")
    })
    @PutMapping("/{GenreId}")
    public void updateGenre(@PathVariable("GenreId") Long id, @RequestBody GenreDto genreDto) {
        genreService.update(id, genreDto.getGenre());
    }

    @Operation(summary = "장르 삭제", description = "주어진 ID로 특정 게임 장르를 삭제합니다.", parameters = {
        @Parameter(name = "GenreId", description = "삭제할 장르의 ID", required = true, schema = @Schema(type = "Long", example = "1"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 장르 삭제됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "장르를 찾을 수 없음")
    })
    @DeleteMapping("/{GenreId}")
    public void deleteGenre(@PathVariable("GenreId") Long id) {
        genreService.deleteById(id);
    }
}

