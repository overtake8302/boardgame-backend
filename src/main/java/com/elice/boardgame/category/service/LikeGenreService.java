package com.elice.boardgame.category.service;

import com.elice.boardgame.common.exceptions.BoardGameNotFoundException;
import com.elice.boardgame.common.exceptions.GenreNotFoundException;
import com.elice.boardgame.common.exceptions.MemberNotFoundException;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.entity.LikeGenre;
import com.elice.boardgame.category.entity.LikeGenreId;
import com.elice.boardgame.category.repository.GameGenreRepository;
import com.elice.boardgame.category.repository.LikeGenreRepository;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameLike;
import com.elice.boardgame.game.entity.GameLikePK;
import com.elice.boardgame.game.entity.GameRate;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.game.repository.GameLikeRepository;
import com.elice.boardgame.game.repository.GameRateRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeGenreService {

    private final LikeGenreRepository likeGenreRepository;

    private final GameRateRepository gameRateRepository;

    private final UserRepository userRepository;

    private final GameGenreRepository gameGenreRepository;

    private final GameLikeRepository gameLikeRepository;

    private final BoardGameRepository boardGameRepository;

    private final BoardGameMapper boardGameMapper;

    @Transactional
    public void likeGenreScore(Long userId, Long gameId) {
        GameLikePK gameLikePK = new GameLikePK();
        gameLikePK.setUserId(userId);
        gameLikePK.setGameId(gameId);

        BoardGame boardGame = boardGameRepository.findById(gameId)
            .orElseThrow(() -> new BoardGameNotFoundException("해당 게임이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException("해당 유저가 존재하지 않습니다."));

        List<Genre> genres = gameGenreRepository.findGenresByBoardGame(boardGame)
            .orElseThrow(() -> new GenreNotFoundException("해당 장르가 존재하지 않습니다."));

        // gameLike에 존재하지 않는다면
        if (!gameLikeRepository.existsByGameLikePK(gameLikePK)) {
            GameLike gameLike = new GameLike();
            gameLike.setGameLikePK(gameLikePK);
            gameLike.setBoardGame(boardGame);
            gameLike.setUser(user);
            gameLikeRepository.save(gameLike);

            updateLikeGenre(user, genres, 2L);
        }
        // gameLike에 존재한다면 취소
        else {
            gameLikeRepository.deleteByGameLikePK(gameLikePK);
            updateLikeGenre(user, genres, -2L);
        }
    }


    @Transactional
    public void genreRatingScore(Long userId, Long gameId, Double rating) {
        Long updateCount = 0L;
        if (rating == 5) {
            updateCount = 2L;
        } else if (rating == 4){
            updateCount = 1L;
        }

        BoardGame boardGame = boardGameRepository.findById(gameId)
            .orElseThrow(() -> new BoardGameNotFoundException("해당 게임이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException("해당 유저가 존재하지 않습니다."));

        List<Genre> genres = gameGenreRepository.findGenresByBoardGame(boardGame)
            .orElseThrow(() -> new GenreNotFoundException("해당 장르가 존재하지 않습니다."));

        Optional<GameRate> optionalEntity = gameRateRepository.findByUserAndBoardGame(user,
            boardGame);
        // gameRate에 존재하지 않는다면
        if (!optionalEntity.isPresent()) {
            GameRate gameRate = new GameRate();
            gameRate.setBoardGame(boardGame);
            gameRate.setUser(user);
            gameRate.setRate(rating);

            gameRateRepository.save(gameRate);

            updateLikeGenre(user, genres, updateCount);
        }
        // gameRate에 존재한다면 취소
        else {
            GameRate gameRate = optionalEntity.orElseThrow();
            gameRateRepository.deleteByUserAndBoardGame(user, boardGame);
            Double originRate = gameRate.getRate();
            if (!Objects.equals(originRate, rating)) {
                updateCount = (long) (rating - originRate);
            }
            else {
                updateCount *= -1;
            }
            updateLikeGenre(user, genres, updateCount);
        }
    }

    public void updateLikeGenre(User user, List<Genre> genres, Long updateCount) {
        for (Genre genre : genres) {
            Optional<LikeGenre> optionalEntity = likeGenreRepository.findByLikeGenreId(user.getId(),
                genre.getGenreId());

            if (optionalEntity.isPresent()) {
                LikeGenre likeGenre = optionalEntity.orElseThrow();
                likeGenre.setScore(likeGenre.getScore() + updateCount);
                likeGenreRepository.save(likeGenre);
            } else {
                LikeGenre newEntity = new LikeGenre();
                LikeGenreId likeGenreId = new LikeGenreId();
                likeGenreId.setUserId(user.getId());
                likeGenreId.setGenreId(genre.getGenreId());
                newEntity.setId(likeGenreId);
                newEntity.setUser(user);
                newEntity.setGenre(genre);
                newEntity.setScore(updateCount);

                likeGenreRepository.save(newEntity);
            }
        }
    }

    public List<GenreDto> getGenres(Long userId) {
        List<LikeGenre> likeGenres = likeGenreRepository.findByUserIdOrderByScoreDesc(userId);

        return likeGenres.stream()
            .map(likeGenre -> {
                GenreDto genreDto = new GenreDto();
                genreDto.setGenreId(likeGenre.getGenre().getGenreId());
                genreDto.setGenre(likeGenre.getGenre().getGenre());
                return genreDto;
            })
            .collect(Collectors.toList());
    }


    public Page<GameResponseDto> getLikeGames(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BoardGame> boardGames = gameLikeRepository.findLikedGamesByUserId(userId, pageable);

        return boardGames.map(boardGameMapper::boardGameToGameResponseDto);
    }

    public Page<GameResponseDto> getRateGames(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BoardGame> boardGames = gameRateRepository.findByUserId(userId, pageable);

        return boardGames.map(boardGameMapper::boardGameToGameResponseDto);
    }

    public Page<GameResponseDto> getGenreGame(Long userId, int page, int size) {
        List<LikeGenre> likeGenres = likeGenreRepository.findByUserIdOrderByScoreDesc(userId);
        List<Genre> genres = likeGenres.stream()
            .map(LikeGenre::getGenre)
            .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        Page<BoardGame> boardGames = gameGenreRepository.findBoardGamesByGenresOrderByRatingDesc(genres, userId, pageable);

        return boardGames.map(boardGameMapper::boardGameToGameResponseDto);
    }

    public Page<GameResponseDto> getGamesFromLikeGenre(Long userId, int page, int size) {
        List<LikeGenre> likeGenres = likeGenreRepository.findByUserIdOrderByScoreDesc(userId);
        List<Long> topLikeGenreIds = likeGenres.stream()
            .limit(3)
            .map(likeGenre -> likeGenre.getGenre().getGenreId())
            .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        Page<BoardGame> boardGames = boardGameRepository.findByGenres(topLikeGenreIds, userId, pageable);

        return boardGames.map(boardGameMapper::boardGameToGameResponseDto);
    }
}