package com.elice.boardgame.game.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.auth.service.AuthService;
import com.elice.boardgame.category.DTO.GenreDto;
import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.GameGenreId;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.mapper.GenreMapper;
import com.elice.boardgame.category.repository.GameGenreRepository;
import com.elice.boardgame.category.service.GenreService;
import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.common.enums.GameRateResponseMessages;
import com.elice.boardgame.common.exceptions.GameErrorMessages;
import com.elice.boardgame.common.exceptions.GameRootException;
import com.elice.boardgame.game.dto.*;
import com.elice.boardgame.game.entity.*;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.game.repository.GameLikeRepository;
import com.elice.boardgame.game.repository.GameRateRepository;
import com.elice.boardgame.game.repository.GameVisitorRepository;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Comment;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.repository.CommentRepository;
import com.elice.boardgame.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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
    private final AuthService authService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public GameResponseDto create(GamePostDto gamePostDto, List<MultipartFile> files) throws IOException {

        User currentUser = authService.getCurrentUser();
        BoardGame newBoardGame = boardGameMapper.gamePostDtoToBoardGame(gamePostDto);
        newBoardGame.setFirstCreator(currentUser);
        BoardGame savedBoardGame = boardGameRepository.save(newBoardGame);

        if (files != null && !files.isEmpty()) {

            List<GameProfilePic> pics = new ArrayList<>();

            for (MultipartFile file : files) {
                GameProfilePic pic = gameProfilePicService.save(file, savedBoardGame);
                pics.add(pic);
            }

            savedBoardGame.setGameProfilePics(pics);
        }

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

        return findGameByGameId(savedBoardGame.getGameId());
    }

    public GameResponseDto findGameByGameId(Long gameId) {

        GameResponseDto foundGame = boardGameRepository.getGameResponseDtoByGameIdAndDeletedDateIsNull(gameId);

        if (foundGame == null) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return foundGame;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public void deleteGameByGameId(Long gameId) {

        User currentUser = authService.getCurrentUser();
        BoardGame targetGame = boardGameRepository.findByGameIdAndDeletedDateIsNull(gameId);

        if (targetGame == null) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND,HttpStatus.NOT_FOUND);
        }

        if (currentUser != null && targetGame.getFirstCreator() != null) {
            if (!currentUser.getRole().equals("ROLE_ADMIN") && !(targetGame.getFirstCreator().getId().equals(currentUser.getId()))) {
                throw new GameRootException(GameErrorMessages.ACCESS_DENIED, HttpStatus.FORBIDDEN);
            }
        }

        List<GameProfilePic> targetPics = targetGame.getGameProfilePics();

        try {
            if (!targetPics.isEmpty()) {
                gameProfilePicService.deleteImages(targetPics, gameId);
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
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public GameResponseDto editGame(GamePutDto gamePutDto, List<MultipartFile> files) throws IOException {

        BoardGame foundGame = boardGameRepository.findByGameIdAndDeletedDateIsNull(gamePutDto.getGameId());;
        BoardGame target = boardGameMapper.boardGameUpdateMapper(foundGame, gamePutDto);

        gameProfilePicService.deleteImages(target.getGameProfilePics(), foundGame.getGameId());
        target.setGameProfilePics(Collections.emptyList());

        if (files != null && !files.isEmpty()) {

            List<GameProfilePic> pics = new ArrayList<>();

            for (MultipartFile file : files) {
                GameProfilePic pic = gameProfilePicService.save(file, target);
                pics.add(pic);
            }

            target.setGameProfilePics(pics);
        }

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


    public Page<GameResponseDto> findGameByName(String keyword, Pageable pageable) {

        Page<GameResponseDto> foundGames = boardGameRepository.findByNameContainingAndDeletedDateIsNull(keyword, pageable);

        if (foundGames == null || foundGames.isEmpty()) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return foundGames;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ClickLikeResponseDto clickLike(Long gameId) {

        BoardGame targetGame = boardGameRepository.findByGameIdAndDeletedDateIsNull(gameId);
        User currentUser = authService.getCurrentUser();
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

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public GameRateResponseDto clickGameRate(Long gameId, GameRatePostDto gameRatePostDto) {

        BoardGame foundGame = boardGameRepository.findByGameIdAndDeletedDateIsNull(gameId);
        User currentUser = authService.getCurrentUser();
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

    public Page<GameResponseDto> findAll(Pageable pageable, Enums.GameListSortOption sortBy) {

        return boardGameRepository.findAllByDeletedDateIsNull(pageable, sortBy);
    }

    @Transactional
    public void incrementViewCount(String visitorId, Long gameId) {

        gameVisitorRepository.insertIgnore(visitorId, gameId);
    }

    public List<GameResponseDto> findGamesByGenreAndSort(String genre, Enums.GameListSortOption sort) {

        return boardGameRepository.findByGameGenresGenreGenre(genre, sort);

    }

    public List<PostDto> getTop10Posts(Long gameId, Enums.Category category) {
        List<Post> posts = postRepository.findTop10ByBoardGameGameIdAndCategoryOrderByIdDesc(gameId, category);
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            PostDto postDto = new PostDto();
            postDto.setCategory(post.getCategory());
            postDto.setTitle(post.getTitle());
            postDto.setContent(post.getContent());
            postDtos.add(postDto);
        }
        return postDtos;
    }

}
