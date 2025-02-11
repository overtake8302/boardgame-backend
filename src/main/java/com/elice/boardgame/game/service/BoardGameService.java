package com.elice.boardgame.game.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.auth.service.AuthService;
import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.GameGenreId;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.mapper.GenreMapper;
import com.elice.boardgame.category.repository.GameGenreRepository;
import com.elice.boardgame.category.service.GenreService;
import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.common.enums.GameRateResponseMessages;
import com.elice.boardgame.common.exceptions.GameErrorMessages;
import com.elice.boardgame.common.exceptions.GameRootException;
import com.elice.boardgame.common.exceptions.UserErrorMessages;
import com.elice.boardgame.common.exceptions.UserException;
import com.elice.boardgame.game.dto.*;
import com.elice.boardgame.game.entity.*;
import com.elice.boardgame.game.mapper.BoardGameHistoryMapper;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.repository.*;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.repository.CommentRepository;
import com.elice.boardgame.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private final BoardGameHistoryRepository boardGameHistoryRepository;
    private final BoardGameHistoryMapper historyMapper;
    private final GameProfilePicRepository gameProfilePicRepository;
    private final GenreHistoryRepository genreHistoryRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public GameResponseDto create(GamePostDto gamePostDto, List<MultipartFile> files, User user) throws IOException {

        if (user == null) {
            throw new GameRootException(GameErrorMessages.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        BoardGame newBoardGame = boardGameMapper.gamePostDtoToBoardGame(gamePostDto);
        newBoardGame.setFirstCreator(user);
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

        return findGameByGameId(savedBoardGame.getGameId(), false, false, null);
    }

    public GameResponseDto findGameByGameId(Long gameId, boolean wantComments, boolean wantPosts, String category) {

        GameResponseDto foundGame = boardGameRepository.getGameResponseDtoByGameIdAndDeletedAtIsNull(gameId);

        if (foundGame == null || foundGame.getGameId() == null) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if (wantComments) {
            List<GameCommentDto> comments = findComentsByGameId(gameId);
            foundGame.setComments(comments);
        }

        if (wantPosts && category != null) {
            List<PostsByGame> posts = getTop10Posts(gameId,category);
            foundGame.setPosts(posts);
        }

        return foundGame;
    }

    @Transactional
    public void deleteGameByGameId(Long gameId) {

        User currentUser = authService.getCurrentUser();
        BoardGame targetGame = boardGameRepository.findByGameIdAndDeletedAtIsNull(gameId);

        if (targetGame == null) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND,HttpStatus.NOT_FOUND);
        }

        if (targetGame.getFirstCreator() != null) {
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
            targetGame.setDeletedAt(LocalDateTime.now());
            boardGameRepository.save(targetGame);

        } catch (Exception e) {
            throw new GameRootException(GameErrorMessages.GAME_DELETE_FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public GameResponseDto editGame(GamePutDto gamePutDto, List<MultipartFile> files) throws IOException {

        BoardGame foundGame = boardGameRepository.findByGameIdAndDeletedAtIsNull(gamePutDto.getGameId());

        if (foundGame == null) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

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

    @Transactional
    public synchronized GameResponseDto editGameV2(GamePutDto gamePutDto, List<MultipartFile> files, User user) throws IOException {

        if (user == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        BoardGame foundGame = boardGameRepository.findByGameIdAndDeletedAtIsNull(gamePutDto.getGameId());

        if (foundGame == null) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        BoardGameHistory history = historyMapper.BoardGameToBoardGameHistory(foundGame);
        history = boardGameHistoryRepository.save(history);

        List<GameGenreHistory> genreHistory = history.getGameGenres();
        for (GameGenreHistory gameGenreHistory : genreHistory) {
            gameGenreHistory.setGameHistory(history);
            genreHistoryRepository.save(gameGenreHistory);
        }

        List<GameGenre> oldGenres = foundGame.getGameGenres();
        Iterator<GameGenre> genreIterator = oldGenres.iterator();
        while (genreIterator.hasNext()) {
            GameGenre oldGenre = genreIterator.next();
            gameGenreRepository.delete(oldGenre);
        }

        BoardGame target = boardGameMapper.boardGameUpdateMapper(foundGame, gamePutDto);
        target.setEditBy(user);

        List<GameProfilePic> oldPics = target.getGameProfilePics();
        if (oldPics != null && !oldPics.isEmpty()) {
            for (GameProfilePic pic : oldPics) {
                if (pic.getIsActive()) {
                    pic.setGameHistory(history);
                    gameProfilePicRepository.save(pic);
                }
            }
        }

        List<GameProfilePic> newPics = new ArrayList<>();

        if (gamePutDto.getGameProfilePics() != null && !gamePutDto.getGameProfilePics().isEmpty()) {
            List<String> newPicUrl = gamePutDto.getGameProfilePics();

            for (GameProfilePic oldPic : oldPics) {
                for (String url : newPicUrl) {
                    if (oldPic.getPicAddress().equals(url)) {
                        newPics.add(oldPic);
                        oldPic.setIsActive(true);
                        break;
                    } else {
                        oldPic.setIsActive(false);
                    }
                }
                gameProfilePicRepository.save(oldPic);
            }
        }

        if (files != null && !files.isEmpty()) {

            if (gamePutDto.getGameProfilePics() == null || gamePutDto.getGameProfilePics().isEmpty()) {
                for (GameProfilePic oldPic : oldPics) {
                    oldPic.setIsActive(false);
                    gameProfilePicRepository.save(oldPic);
                    }

            }

            for (MultipartFile file : files) {
                GameProfilePic pic = gameProfilePicService.save(file, target);
                newPics.add(pic);
            }
        }

        if ((files == null || files.isEmpty()) && (gamePutDto.getGameProfilePics() == null || gamePutDto.getGameProfilePics().isEmpty())) {
            List<GameProfilePic> removeTargetPic = target.getGameProfilePics();
            Iterator<GameProfilePic> picIterator = removeTargetPic.iterator();
            while (picIterator.hasNext()) {
                GameProfilePic pic = picIterator.next();
                if (pic.getIsActive()) {
                    pic.setIsActive(false);
                    picIterator.remove();
                }
            }
        }

        target.setGameProfilePics(newPics);

        List<GameGenre> genres = new ArrayList<>();

        if (gamePutDto.getGameGenreIds() != null && !gamePutDto.getGameGenreIds().isEmpty()) {
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
        }

        target.setGameGenres(genres);

        target = boardGameRepository.save(target);

        return boardGameMapper.boardGameToGameResponseDto(target);
    }



    public Page<GameResponseDto> findGameByName(String keyword, Pageable pageable) {

        Page<GameResponseDto> foundGames = boardGameRepository.findByNameContainingAndDeletedAtIsNull(keyword, pageable);

        if (foundGames == null || foundGames.isEmpty()) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return foundGames;
    }

    public ClickLikeResponseDto clickLike(Long gameId, User user) {

        BoardGame targetGame = boardGameRepository.findByGameIdAndDeletedAtIsNull(gameId);
        GameLikePK gameLikePK = new GameLikePK(user.getId(), gameId);

        Optional<GameLike> target = gameLikeRepository.findById(gameLikePK);

        ClickLikeResponseDto clickLikeResponseDto = new ClickLikeResponseDto();

        if (target.isPresent()) {
            gameLikeRepository.delete(target.get());
            clickLikeResponseDto.setMessages(ClickLikeResponseDto.ClickLikeResponseMessages.LIKE_REMOVED.getMessage());
        } else {
            GameLike gameLike = new GameLike(gameLikePK, targetGame, user);
            gameLikeRepository.save(gameLike);
            clickLikeResponseDto.setMessages(ClickLikeResponseDto.ClickLikeResponseMessages.LIKE_ADDED.getMessage());
        }

        int likeCount = gameLikeRepository.countLikesByBoardGameGameId(gameId);
        clickLikeResponseDto.setLikeCount(likeCount);

        return clickLikeResponseDto;
    }

    public GameRateResponseDto clickGameRate(Long gameId, GameRatePostDto gameRatePostDto, User user) {

        BoardGame foundGame = boardGameRepository.findByGameIdAndDeletedAtIsNull(gameId);
        GameRate foundGameRate = gameRateRepository.findByUserIdAndBoardGameGameId(user.getId(), gameId);

        if (foundGameRate != null) {
            foundGameRate.setRate(gameRatePostDto.getRate());
            gameRateRepository.save(foundGameRate);

            return new GameRateResponseDto(GameRateResponseMessages.EDITED.getMessage());
        }

        GameRate newGameRate = new GameRate();
        newGameRate.setBoardGame(foundGame);
        newGameRate.setUser(user);
        newGameRate.setRate(gameRatePostDto.getRate());
        gameRateRepository.save(newGameRate);

        return new GameRateResponseDto(GameRateResponseMessages.REGISTERED.getMessage());
    }

    public Page<GameListResponseDto> findAll(Pageable pageable, Enums.GameListSortOption sortBy, String keyword) {

        if (keyword != null && !keyword.isEmpty()) {
            return boardGameRepository.findByNameContainingAndDeletedAtIsNull(pageable, sortBy, keyword);
        }

        return boardGameRepository.findAllByDeletedAtIsNull(pageable, sortBy);
    }

    @Transactional
    public void incrementViewCount(String visitorId, Long gameId) {

        gameVisitorRepository.insertIgnore(visitorId, gameId);
    }

    public List<HomeGamesResponseDto> findGamesByGenreAndSort(Enums.GameListSortOption sort, String genre) {

        return boardGameRepository.findByGameGenresGenreGenre(sort, genre);

    }

    public List<PostsByGame> getTop10Posts(Long gameId, String category) {
        List<PostsByGame> posts = postRepository.findTop10ByBoardGameGameIdAndCategoryAndDeletedAtIsNullOrderByIdDesc(gameId, category);

        return posts;
    }

    public Page<SearchResponse> searchByKeyword(String keyword, Pageable pageable) {
        return boardGameRepository.searchByKeyword(keyword, pageable);
    }
    public Page<GameResponseDto> getGamesLikedByUser(Long userId, Pageable pageable) {
        return boardGameRepository.findGamesLikedByUserId(userId, pageable);
    }

    public List<GameCommentDto> findComentsByGameId(Long gameId) {
        return boardGameRepository.findComentsByGameId(gameId);
    }

    public Boolean checkFirstCreatorOrAdmin(Long gameId, User user) {

        BoardGame foundGame = boardGameRepository.findByGameIdAndDeletedAtIsNull(gameId);
        if (user == null) {
            return false;
        }
        if (foundGame.getFirstCreator() == null || user.getId().equals(foundGame.getFirstCreator().getId()) || user.getRole().equals("ROLE_ADMIN") ) {
            return true;
        }
        return false;
    }

    public Page<GameHistoriesResponseDto> getGameHistoriesByGameId(Pageable pageable,Long gameId) {

        GameResponseDto game = findGameByGameId(gameId, false, false, null);
        if (game == null || game.getGameId() == null) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Page<GameHistoriesResponseDto> dtos = boardGameHistoryRepository.findHistoriesByGameId(pageable,gameId);

        if (dtos == null || dtos.isEmpty()) {
            throw new GameRootException(GameErrorMessages.NO_HISTORIES, HttpStatus.NOT_FOUND);
        }

        return dtos;
    }

    public GameHistoryResponseDto getGameHistory(Long historyId) {


        GameHistoryResponseDto dto = boardGameHistoryRepository.findHistoryByHistoryId(historyId);

        if (dto == null || dto.getGameHistoryId() == null) {
            throw new GameRootException(GameErrorMessages.NO_HISTORY, HttpStatus.NOT_FOUND);
        }

        if (findGameByGameId(dto.getGameId(),false,false, null).getGameId() == null) {
            throw new GameRootException(GameErrorMessages.GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return dto;
    }
}
