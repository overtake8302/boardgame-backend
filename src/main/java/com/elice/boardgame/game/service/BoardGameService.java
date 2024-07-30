package com.elice.boardgame.game.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.GameGenreId;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.repository.GameGenreRepository;
import com.elice.boardgame.category.service.GenreService;
import com.elice.boardgame.enumeration.GameRateResponseMessages;
import com.elice.boardgame.game.dto.GameRatePostDto;
import com.elice.boardgame.game.dto.GameRateResponseDto;
import com.elice.boardgame.game.entity.*;
import com.elice.boardgame.ExceptionHandler.GameErrorMessages;
import com.elice.boardgame.ExceptionHandler.GameRootException;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.game.repository.GameLikeRepository;
import com.elice.boardgame.game.repository.GameRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardGameService {

    private final BoardGameRepository boardGameRepository;
    private final GameProfilePicService gameProfilePicService;
    private final GenreService genreService;
    private final GameGenreRepository gameGenreRepository;
    private final GameLikeRepository gameLikeRepository;
    private final UserRepository userRepository;
    private final GameRateRepository gameRateRepository;

    @Transactional
    public BoardGame create(BoardGame newBoardGame, List<Long> genreIds) {

        BoardGame savedBoardGame = boardGameRepository.save(newBoardGame);

        List<GameGenre> genres = new ArrayList<>();

        for (Long id : genreIds) {
            Genre genre = genreService.findById(id);
            if (genre != null) {
                GameGenre gameGenre = new GameGenre();
                gameGenre.setBoardGame(savedBoardGame);
                gameGenre.setGenre(genre);
                GameGenreId gameGenreId = new GameGenreId();
                gameGenreId.setGameId(savedBoardGame.getGameId());
                gameGenreId.setGenreId(genre.getGenreId());
                gameGenre.setId(gameGenreId);
                gameGenre = gameGenreRepository.save(gameGenre);
                genres.add(gameGenre);
            } else {
                throw new GameRootException(GameErrorMessages.GAME_POST_ERROR);
            }
        }

        savedBoardGame.setGameGenres(genres);
        savedBoardGame = boardGameRepository.save(savedBoardGame);

        return savedBoardGame;
    }

    @Transactional
    public BoardGame create(BoardGame newBoardGame, List<MultipartFile> files, List<Long> genreIds) throws IOException {

        List<GameProfilePic> pics = new ArrayList<>();

        for (MultipartFile file : files) {
            GameProfilePic pic = gameProfilePicService.save(file);
            pics.add(pic);
        }

        newBoardGame.setGameProfilePics(pics);

        BoardGame savedBoardGame = boardGameRepository.save(newBoardGame);

        List<GameGenre> genres = new ArrayList<>();

        for (Long id : genreIds) {
            Genre genre = genreService.findById(id);
            if (genre != null) {
                GameGenre gameGenre = new GameGenre();
                gameGenre.setBoardGame(savedBoardGame);
                gameGenre.setGenre(genre);
                GameGenreId gameGenreId = new GameGenreId();
                gameGenreId.setGameId(savedBoardGame.getGameId());
                gameGenreId.setGenreId(genre.getGenreId());
                gameGenre.setId(gameGenreId);
                gameGenre = gameGenreRepository.save(gameGenre);
                genres.add(gameGenre);
            } else {
                throw new GameRootException(GameErrorMessages.GAME_POST_ERROR);
            }
        }

        savedBoardGame.setGameGenres(genres);
        savedBoardGame = boardGameRepository.save(savedBoardGame);

        return savedBoardGame;
    }

    public BoardGame findGameByGameId(Long gameId) {

        BoardGame foundGame = boardGameRepository.findByGameIdAndDeletedDateIsNull(gameId);

        if (foundGame == null) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND);
        }

        return foundGame;
    }

    @Transactional
    public void deleteGameByGameId(Long gameId) {

        BoardGame targetGame = findGameByGameId(gameId);
        List<GameProfilePic> targetPics = targetGame.getGameProfilePics();

        try {
            if (!targetPics.isEmpty()) {
                for (GameProfilePic pic : targetPics) {
                    gameProfilePicService.deleteByFileName(pic);
                }
            }

            List<GameGenre> targetGenres = targetGame.getGameGenres();

            for (GameGenre targetGenre : targetGenres) {
                gameGenreRepository.delete(targetGenre);
            }

            targetGame.setGameProfilePics(Collections.emptyList());
            targetGame.setDeletedDate(LocalDateTime.now());
            boardGameRepository.save(targetGame);

        } catch (Exception e) {
            throw new GameRootException(GameErrorMessages.GAME_DELETE_FAIL);
        }
    }

    @Transactional
    public BoardGame editWithoutPics(BoardGame target, List<Long> genreIds) {

        gameProfilePicService.deleteFiles(target.getGameProfilePics());
        target.setGameProfilePics(Collections.emptyList());

        List<GameGenre> oldGenres = target.getGameGenres();

        for (GameGenre oldGenre : oldGenres) {
            gameGenreRepository.delete(oldGenre);
        }

        List<GameGenre> genres = new ArrayList<>();

        for (Long id : genreIds) {
            Genre genre = genreService.findById(id);
            if (genre != null) {
                GameGenre gameGenre = new GameGenre();
                gameGenre.setBoardGame(target);
                gameGenre.setGenre(genre);
                GameGenreId gameGenreId = new GameGenreId();
                gameGenreId.setGameId(target.getGameId());
                gameGenreId.setGenreId(genre.getGenreId());
                gameGenre.setId(gameGenreId);
                gameGenre = gameGenreRepository.save(gameGenre);
                genres.add(gameGenre);
            } else {
                throw new GameRootException(GameErrorMessages.GAME_POST_ERROR);
            }
        }

        target.setGameGenres(genres);

        BoardGame updatedGame = boardGameRepository.save(target);

        return updatedGame;
    }

    @Transactional
    public BoardGame editWithPics(BoardGame target, List<MultipartFile> files, List<Long> genreIds) throws IOException {

        gameProfilePicService.deleteFiles(target.getGameProfilePics());
        target.setGameProfilePics(Collections.emptyList());

        List<GameProfilePic> pics = new ArrayList<>();

        for (MultipartFile file : files) {
            GameProfilePic pic = gameProfilePicService.save(file);
            pics.add(pic);
        }

        target.setGameProfilePics(pics);

        List<GameGenre> oldGenres = target.getGameGenres();

        for (GameGenre oldGenre : oldGenres) {
            gameGenreRepository.delete(oldGenre);
        }

        List<GameGenre> genres = new ArrayList<>();

        for (Long id : genreIds) {
            Genre genre = genreService.findById(id);
            if (genre != null) {
                GameGenre gameGenre = new GameGenre();
                gameGenre.setBoardGame(target);
                gameGenre.setGenre(genre);
                GameGenreId gameGenreId = new GameGenreId();
                gameGenreId.setGameId(target.getGameId());
                gameGenreId.setGenreId(genre.getGenreId());
                gameGenre.setId(gameGenreId);
                gameGenre = gameGenreRepository.save(gameGenre);
                genres.add(gameGenre);
            } else {
                throw new GameRootException(GameErrorMessages.GAME_POST_ERROR);
            }
        }

        target.setGameGenres(genres);

        boardGameRepository.save(target);

        return target;
    }


    public List<BoardGame> findGameByName(String keyword) {

        List<BoardGame> foundGames = boardGameRepository.findByNameContaining(keyword);

        if (foundGames == null || foundGames.isEmpty()) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND);
        }

        return foundGames;
    }

    public boolean clickLike(Long gameId) {

        BoardGame targetGame = boardGameRepository.findByGameIdAndDeletedDateIsNull(gameId);
        User currentUser = getCurrentUser();
        GameLikePK gameLikePK = new GameLikePK(currentUser.getId(), gameId);

//        boolean like = gameLikeRepository.existsByGameLikePK(gameLikePK);
        Optional<GameLike> target = gameLikeRepository.findById(gameLikePK);

        if (target.isPresent()) {
            gameLikeRepository.delete(target.get());
            return true;
        }

        GameLike gameLike = new GameLike(gameLikePK, targetGame, currentUser);
        gameLikeRepository.save(gameLike);

        return false;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUserName);
        return currentUser;
    }

    public GameRateResponseDto clickGameRate(Long gameId, GameRatePostDto gameRatePostDto) {

        BoardGame foundGame = findGameByGameId(gameId);
        User currentUser = getCurrentUser();
        GameRate foundGameRate = gameRateRepository.findByUserIdAndBoardGameGameId(gameId, currentUser.getId());

        if (foundGameRate != null) {
            foundGameRate.setRate(gameRatePostDto.getRate());
            gameRateRepository.save(foundGameRate);

            return new GameRateResponseDto(GameRateResponseMessages.EDITED.getMessage());
        }

        GameRate newGameRate = new GameRate();
        newGameRate.setBoardGame(foundGame);
        newGameRate.setUser(currentUser);
        newGameRate.setRate(gameRatePostDto.getRate());
        gameRateRepository.save(newGameRate);

        return new GameRateResponseDto(GameRateResponseMessages.REGISTERED.getMessage());
    }
}
