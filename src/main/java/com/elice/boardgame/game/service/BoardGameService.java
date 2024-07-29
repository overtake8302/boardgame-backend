package com.elice.boardgame.game.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.GameGenreId;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.repository.GameGenreRepository;
import com.elice.boardgame.category.service.GenreService;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameLike;
import com.elice.boardgame.game.entity.GameLikePK;
import com.elice.boardgame.game.entity.GameProfilePic;
import com.elice.boardgame.game.exception.GameDeleteFailException;
import com.elice.boardgame.game.exception.GameNotFoundException;
import com.elice.boardgame.game.exception.GamePostException;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.game.repository.GameLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
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

    @Transactional
    public BoardGame create(BoardGame newBoardGame, List<Long> genreIds) {

        BoardGame savedBoardGame = boardGameRepository.save(newBoardGame);

        List<GameGenre> genres = new ArrayList<>();

        for (Long id : genreIds) {
           Optional<Genre> genre = genreService.findById(id);
           if (genre.isPresent()) {
               GameGenre gameGenre = new GameGenre();
               gameGenre.setBoardGame(savedBoardGame);
               gameGenre.setGenre(genre.get());
               GameGenreId gameGenreId = new GameGenreId();
               gameGenreId.setGameId(savedBoardGame.getGameId());
               gameGenreId.setGenreId(genre.get().getGenreId());
               gameGenre.setId(gameGenreId);
               gameGenre = gameGenreRepository.save(gameGenre);
               genres.add(gameGenre);
           } else {
               throw new GamePostException();
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
            Optional<Genre> genre = genreService.findById(id);
            if (genre.isPresent()) {
                GameGenre gameGenre = new GameGenre();
                gameGenre.setBoardGame(savedBoardGame);
                gameGenre.setGenre(genre.get());
                GameGenreId gameGenreId = new GameGenreId();
                gameGenreId.setGameId(savedBoardGame.getGameId());
                gameGenreId.setGenreId(genre.get().getGenreId());
                gameGenre.setId(gameGenreId);
                gameGenre = gameGenreRepository.save(gameGenre);
                genres.add(gameGenre);
            } else {
                throw new GamePostException();
            }
        }

        savedBoardGame.setGameGenres(genres);
        savedBoardGame = boardGameRepository.save(savedBoardGame);

        return savedBoardGame;
    }

    public BoardGame findGameByGameId(Long gameId) {

        BoardGame foundGame = boardGameRepository.findByGameIdAndDeletedAtIsNull(gameId);

        if (foundGame == null) {
            throw new GameNotFoundException();
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
            targetGame.setDeletedAt(LocalDateTime.now());
            boardGameRepository.save(targetGame);

        } catch (Exception e) {
            throw new GameDeleteFailException();
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
            Optional<Genre> genre = genreService.findById(id);
            if (genre.isPresent()) {
                GameGenre gameGenre = new GameGenre();
                gameGenre.setBoardGame(target);
                gameGenre.setGenre(genre.get());
                GameGenreId gameGenreId = new GameGenreId();
                gameGenreId.setGameId(target.getGameId());
                gameGenreId.setGenreId(genre.get().getGenreId());
                gameGenre.setId(gameGenreId);
                gameGenre = gameGenreRepository.save(gameGenre);
                genres.add(gameGenre);
            } else {
                throw new GamePostException();
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
            Optional<Genre> genre = genreService.findById(id);
            if (genre.isPresent()) {
                GameGenre gameGenre = new GameGenre();
                gameGenre.setBoardGame(target);
                gameGenre.setGenre(genre.get());
                GameGenreId gameGenreId = new GameGenreId();
                gameGenreId.setGameId(target.getGameId());
                gameGenreId.setGenreId(genre.get().getGenreId());
                gameGenre.setId(gameGenreId);
                gameGenre = gameGenreRepository.save(gameGenre);
                genres.add(gameGenre);
            } else {
                throw new GamePostException();
            }
        }

        target.setGameGenres(genres);

        boardGameRepository.save(target);

        return target;
    }


    public List<BoardGame> findGameByName(String keyword) {

        List<BoardGame> foundGames = boardGameRepository.findByNameContaining(keyword);

        if (foundGames == null || foundGames.isEmpty()) {
            throw new GameNotFoundException();
        }

        return foundGames;
    }

    public boolean clickLike(Long gameId) {

        BoardGame targetGame = boardGameRepository.findByGameIdAndDeletedAtIsNull(gameId);
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
}
