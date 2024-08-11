package com.elice.boardgame.game.mapper;

import com.elice.boardgame.game.dto.GamePostDto;
import com.elice.boardgame.game.dto.GamePutDto;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.game.repository.GameLikeRepository;
import com.elice.boardgame.game.repository.GameRateRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class BoardGameMapper {

    private final BoardGameRepository boardGameRepository;
    private final GameRateRepository gameRateRepository;
    private final GameProfilePicMapper gameProfilePicMapper;
    private final GameLikeRepository gameLikeRepository;

    public BoardGame gamePostDtoToBoardGame(GamePostDto dto) {

        BoardGame newBoardGame = new BoardGame();
        newBoardGame.setName(dto.getName());
        newBoardGame.setPlayTime(dto.getPlayTime());
        newBoardGame.setReleaseDate(dto.getReleaseDate());
        newBoardGame.setPlayNum(dto.getPlayNum());
        newBoardGame.setAgeLimit(dto.getAgeLimit());
        newBoardGame.setDesigner(dto.getDesigner());
        newBoardGame.setArtwork(dto.getArtwork());
        newBoardGame.setPublisher(dto.getPublisher());
        newBoardGame.setPrice(dto.getPrice());
        newBoardGame.setDifficulty(dto.getDifficulty());
        newBoardGame.setYoutubeLink(dto.getYoutubeLink());

        return newBoardGame;
    }

    public GameResponseDto boardGameToGameResponseDto(BoardGame boardGame) {

        return boardGameRepository.getGameResponseDtoByGameIdAndDeletedDateIsNull(boardGame.getGameId());

    }

    public BoardGame boardGameUpdateMapper(BoardGame target, GamePutDto gamePutDto) {

        target.setYoutubeLink(gamePutDto.getYoutubeLink());
        target.setDifficulty(gamePutDto.getDifficulty());
        target.setPrice(gamePutDto.getPrice());
        target.setArtwork(gamePutDto.getArtwork());
        target.setPublisher(gamePutDto.getPublisher());
        target.setDesigner(gamePutDto.getDesigner());
        target.setAgeLimit(gamePutDto.getAgeLimit());
        target.setPlayNum(gamePutDto.getPlayNum());
        target.setPlayTime(gamePutDto.getPlayTime());
        target.setReleaseDate(gamePutDto.getReleaseDate());
        target.setName(gamePutDto.getName());

        return target;
    }
}
