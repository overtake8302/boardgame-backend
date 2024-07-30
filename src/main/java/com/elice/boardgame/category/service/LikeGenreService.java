package com.elice.boardgame.category.service;

import com.elice.boardgame.ExceptionHandler.GenreNotFoundException;
import com.elice.boardgame.ExceptionHandler.MemberNotFoundException;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.entity.LikeGenre;
import com.elice.boardgame.category.entity.LikeGenreId;
import com.elice.boardgame.category.repository.GameGenreRepository;
import com.elice.boardgame.category.repository.GenreRepository;
import com.elice.boardgame.category.repository.LikeGenreRepository;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.repository.GameLikeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeGenreService {

    private final LikeGenreRepository likeGenreRepository;

    private final GenreRepository genreRepository;

    private final UserRepository userRepository;

    private final GameGenreRepository gameGenreRepository;

    private final GameLikeRepository gameLikeRepository;

    public void addLikeGenreScore(LikeGenreId id) {
        Optional<LikeGenre> optionalEntity = likeGenreRepository.findById(id);
        if (optionalEntity.isPresent()) {
            LikeGenre likeGenre = optionalEntity.orElseThrow();
            long score = likeGenre.getScore() + 2;
            likeGenre.setScore(score);

            likeGenreRepository.save(likeGenre);
        } else {
            LikeGenre newEntity = new LikeGenre();
            newEntity.setId(id);
            newEntity.setScore(2L);

            User user = userRepository.findById(id.getUserId())
                .orElseThrow(() -> new MemberNotFoundException("찾는 멤버가 없습니다."));
            Genre genre = genreRepository.findById(id.getGenreId())
                .orElseThrow(() -> new GenreNotFoundException("찾는 장르가 없습니다."));

            newEntity.setUser(user);
            newEntity.setGenre(genre);

            likeGenreRepository.save(newEntity);
        }
    }

    public void subtractLikeGenreScore(LikeGenreId id) {
        Optional<LikeGenre> optionalEntity = likeGenreRepository.findById(id);
        LikeGenre likeGenre = optionalEntity.orElseThrow();
        long score = likeGenre.getScore() - 2;
        likeGenre.setScore(score);

        likeGenreRepository.save(likeGenre);
    }

    public void addRateGenreScore(LikeGenreId id, int rating) {
        if (rating > 3) {
            Optional<LikeGenre> optionalEntity = likeGenreRepository.findById(id);
            if (optionalEntity.isPresent()) {
                long delta = 0L;
                if (rating == 5) {
                    delta = 2L;
                } else if (rating == 4) {
                    delta = 1L;
                }
                LikeGenre likeGenre = optionalEntity.orElseThrow();
                long score = likeGenre.getScore() + delta;
                likeGenre.setScore(score);
                likeGenreRepository.save(likeGenre);
            } else {
                LikeGenre newEntity = new LikeGenre();
                newEntity.setId(id);
                if (rating == 5) {
                    newEntity.setScore(2L);
                } else if (rating == 4) {
                    newEntity.setScore(1L);
                }
                User user = userRepository.findById(id.getUserId())
                    .orElseThrow(() -> new RuntimeException("Member not found"));
                Genre genre = genreRepository.findById(id.getGenreId())
                    .orElseThrow(() -> new RuntimeException("Genre not found"));

                newEntity.setUser(user);
                newEntity.setGenre(genre);
                likeGenreRepository.save(newEntity);
            }
        }
    }

    public void subtractRateGenreScore(LikeGenreId id, int rating) {
        if (rating > 3) {
            long delta = 0L;
            if (rating == 5) {
                delta = 2L;
            } else if (rating == 4) {
                delta = 1L;
            }
            Optional<LikeGenre> optionalEntity = likeGenreRepository.findById(id);
            LikeGenre likeGenre = optionalEntity.orElseThrow();
            Long score = likeGenre.getScore() - delta;
            likeGenre.setScore(score);

            likeGenreRepository.save(likeGenre);
        }
    }

    public List<String> getTop3GenreIds(Long userId) {
        List<LikeGenre> likeGenres = likeGenreRepository.findByUserIdOrderByScoreDesc(userId);

        return likeGenres.stream()
            .limit(3)
            .map(likeGenre -> likeGenre.getGenre().getGenre())
            .collect(Collectors.toList());
    }

    public List<BoardGame> getGenreGame(Long userId) {
        List<LikeGenre> likeGenres = likeGenreRepository.findByUserIdOrderByScoreDesc(userId);
        List<BoardGame> boardGames = new ArrayList<>();

        for (LikeGenre likeGenre : likeGenres) {
            Optional<List<GameGenre>> gameGenresOpt = gameGenreRepository.findByGenre(
                likeGenre.getGenre());
            if (gameGenresOpt.isPresent()) {
                List<GameGenre> gameGenres = gameGenresOpt.get();
                for (GameGenre gameGenre : gameGenres) {
                    boardGames.add(gameGenre.getBoardGame());
                    if (boardGames.size() >= 5) {
                        return boardGames;
                    }
                }
            }
        }
        return boardGames;
    }

    public List<BoardGame> getGames(Long userId) {
        return gameLikeRepository.findLikedGamesByUserId(userId);
    }
}