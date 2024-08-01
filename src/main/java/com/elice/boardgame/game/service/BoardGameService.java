package com.elice.boardgame.game.service;

import com.elice.boardgame.ExceptionHandler.GameErrorMessages;
import com.elice.boardgame.ExceptionHandler.GameRootException;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.category.DTO.GenreDto;
import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.GameGenreId;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.mapper.GenreMapper;
import com.elice.boardgame.category.repository.GameGenreRepository;
import com.elice.boardgame.category.service.GenreService;
import com.elice.boardgame.enums.GameRateResponseMessages;
import com.elice.boardgame.game.dto.*;
import com.elice.boardgame.game.entity.*;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.game.repository.GameLikeRepository;
import com.elice.boardgame.game.repository.GameRateRepository;
import com.elice.boardgame.game.repository.GameVisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final BoardGameMapper boardGameMapper;
    private final GameVisitorRepository gameVisitorRepository;
    private final GenreMapper genreMapper;

    @Transactional
    public GameResponseDto create(GamePostDto gamePostDto) {

        BoardGame newBoardGame = boardGameMapper.gamePostDtoToBoardGame(gamePostDto);
        BoardGame savedBoardGame = boardGameRepository.save(newBoardGame);

        List<GameGenre> genres = new ArrayList<>();

        for (Long id : gamePostDto.getGameGenreIds()) {
            GenreDto genreDto = genreService.findById(id);
            Genre genre = genreMapper.toEntity(genreDto);
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
                throw new GameRootException(GameErrorMessages.GAME_POST_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        savedBoardGame.setGameGenres(genres);
        savedBoardGame = boardGameRepository.save(savedBoardGame);

        return boardGameMapper.boardGameToGameResponseDto(savedBoardGame);

    }

    @Transactional
    public GameResponseDto create(GamePostDto gamePostDto, List<MultipartFile> files) throws IOException {

        BoardGame newBoardGame = boardGameMapper.gamePostDtoToBoardGame(gamePostDto);
        List<GameProfilePic> pics = new ArrayList<>();

        for (MultipartFile file : files) {
            GameProfilePic pic = gameProfilePicService.save(file);
            pics.add(pic);
        }

        newBoardGame.setGameProfilePics(pics);

        BoardGame savedBoardGame = boardGameRepository.save(newBoardGame);

        List<GameGenre> genres = new ArrayList<>();

        for (Long id : gamePostDto.getGameGenreIds()) {
            GenreDto genreDto = genreService.findById(id);
            Genre genre = genreMapper.toEntity(genreDto);
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
                throw new GameRootException(GameErrorMessages.GAME_POST_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        savedBoardGame.setGameGenres(genres);
        savedBoardGame = boardGameRepository.save(savedBoardGame);

        return boardGameMapper.boardGameToGameResponseDto(savedBoardGame);
    }

    public BoardGame findGameByGameId(Long gameId) {

        BoardGame foundGame = boardGameRepository.findByGameIdAndDeletedDateIsNull(gameId);

        if (foundGame == null) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
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
            throw new GameRootException(GameErrorMessages.GAME_DELETE_FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public GameResponseDto editWithoutPics(GamePutDto gamePutDto) {

        BoardGame foundGame = findGameByGameId(gamePutDto.getGameId());
        BoardGame target = boardGameMapper.boardGameUpdateMapper(foundGame, gamePutDto);

        gameProfilePicService.deleteFiles(target.getGameProfilePics());
        target.setGameProfilePics(Collections.emptyList());

        List<GameGenre> oldGenres = target.getGameGenres();

        for (GameGenre oldGenre : oldGenres) {
            gameGenreRepository.delete(oldGenre);
        }

        List<GameGenre> genres = new ArrayList<>();

        for (Long id : gamePutDto.getGameGenreIds()) {
            GenreDto genreDto = genreService.findById(id);
            Genre genre = genreMapper.toEntity(genreDto);
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
                throw new GameRootException(GameErrorMessages.GAME_POST_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        target.setGameGenres(genres);

        BoardGame updatedGame = boardGameRepository.save(target);

        return boardGameMapper.boardGameToGameResponseDto(updatedGame);
    }

    @Transactional
    public GameResponseDto editWithPics(GamePutDto gamePutDto, List<MultipartFile> files) throws IOException {

        BoardGame foundGame = findGameByGameId(gamePutDto.getGameId());
        BoardGame target = boardGameMapper.boardGameUpdateMapper(foundGame, gamePutDto);

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

        for (Long id : gamePutDto.getGameGenreIds()) {
            GenreDto genreDto = genreService.findById(id);
            Genre genre = genreMapper.toEntity(genreDto);
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
                throw new GameRootException(GameErrorMessages.GAME_POST_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        target.setGameGenres(genres);

        target = boardGameRepository.save(target);

        return boardGameMapper.boardGameToGameResponseDto(target);
    }


    public List<BoardGame> findGameByName(String keyword) {

        List<BoardGame> foundGames = boardGameRepository.findByNameContainingAndDeletedDateIsNull(keyword);

        if (foundGames == null || foundGames.isEmpty()) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return foundGames;
    }

    public ClickLikeResponseDto clickLike(Long gameId) {

        BoardGame targetGame = boardGameRepository.findByGameIdAndDeletedDateIsNull(gameId);
        User currentUser = getCurrentUser();
        GameLikePK gameLikePK = new GameLikePK(currentUser.getId(), gameId);

        Optional<GameLike> target = gameLikeRepository.findById(gameLikePK);

        ClickLikeResponseDto clickLikeResponseDto = new ClickLikeResponseDto();

        if (target.isPresent()) {
            gameLikeRepository.delete(target.get());
            clickLikeResponseDto.setMessages(ClickLikeResponseDto.ClickLikeResponseMessages.LIKE_REMOVED.getMessage());
        } else {
            GameLike gameLike = new GameLike(gameLikePK, targetGame, currentUser);
            gameLikeRepository.save(gameLike);
            clickLikeResponseDto.setMessages(ClickLikeResponseDto.ClickLikeResponseMessages.LIKE_ADDED.getMessage());
        }

        int likeCount = gameLikeRepository.countLikesByBoardGameGameId(gameId);
        clickLikeResponseDto.setLikeCount(likeCount);

        return clickLikeResponseDto;
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
        GameRate foundGameRate = gameRateRepository.findByUserIdAndBoardGameGameId(currentUser.getId(), gameId);

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

    public Page<GameResponseDto> findAll(Pageable pageable) {

        return boardGameRepository.findAllByDeletedDateIsNull(pageable)
            .map(boardGameMapper::boardGameToGameResponseDto);
    }

    public void incrementViewCount(String visitorId, Long gameId) {

        BoardGame boardGame = findGameByGameId(gameId);
        GameVisitor gameVisitor = gameVisitorRepository.findByVisitorIdAndBoardGameGameId(visitorId, gameId);

        if (gameVisitor == null) {
            GameVisitor newGameVisitor = new GameVisitor(visitorId, boardGame);
            gameVisitorRepository.save(newGameVisitor);
            int views = boardGame.getViews();
            views++;
            boardGame.setViews(views);
            boardGameRepository.save(boardGame);
        }
    }
}
