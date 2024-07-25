package com.elice.boardgame.game.mapper;

import com.elice.boardgame.game.dto.GamePostDto;
import com.elice.boardgame.game.dto.GameProfilePicResponseDto;
import com.elice.boardgame.game.dto.GamePutDto;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.game.repository.GameRateRepository;
import com.elice.boardgame.game.service.BoardGameService;
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
    private final BoardGameService boardGameService;
    
    public BoardGame gamePostDtoToBoardGame(GamePostDto dto) {
        
        BoardGame newBoardGame = new BoardGame();
        newBoardGame.setName(dto.getName());
//        newBoardGame.setGameProfilePics(dto.getGameProfilePic());
        //장르도 추가하기
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

        GameResponseDto gameResponseDto = new GameResponseDto();
        gameResponseDto.setGameId(boardGame.getGameId());
        gameResponseDto.setName(boardGame.getName());
        //장르 추가하기
        gameResponseDto.setPlayTime(boardGame.getPlayTime());
        gameResponseDto.setReleaseDate(boardGame.getReleaseDate());
        gameResponseDto.setPlayNum(boardGame.getPlayNum());
        gameResponseDto.setAgeLimit(boardGame.getAgeLimit());
        gameResponseDto.setDesigner(boardGame.getDesigner());
        gameResponseDto.setArtwork(boardGame.getArtwork());
        gameResponseDto.setPublisher(boardGame.getPublisher());
        gameResponseDto.setPrice(boardGame.getPrice());
        GameProfilePicResponseDto gameProfilePicResponseDto = gameProfilePicMapper.gameProfilePicToDto(boardGame.getGameProfilePics());
        gameResponseDto.setGameProfilePics(gameProfilePicResponseDto);
        gameResponseDto.setLikeCount(boardGameRepository.countLikesByGameId(boardGame.getGameId()));
        gameResponseDto.setYoutubeLink(boardGame.getYoutubeLink());
        gameResponseDto.setAverageRate(gameRateRepository.findAverageRateByGameId(boardGame.getGameId()));
        gameResponseDto.setDifficulty(boardGame.getDifficulty());
        //댓글 후기 공략 질문 모임 중고 판매 기타등등

        return gameResponseDto;
    }

    public BoardGame gamePutDtoToBoardGame(GamePutDto gamePutDto) {

        BoardGame foundGame = boardGameService.findGameByGameId(gamePutDto.getGameId());

        foundGame.setYoutubeLink(gamePutDto.getYoutubeLink());
        foundGame.setDifficulty(gamePutDto.getDifficulty());
        foundGame.setPrice(gamePutDto.getPrice());
        foundGame.setArtwork(gamePutDto.getArtwork());
        foundGame.setPublisher(gamePutDto.getPublisher());
        foundGame.setDesigner(gamePutDto.getDesigner());
        foundGame.setAgeLimit(gamePutDto.getAgeLimit());
        foundGame.setPlayNum(gamePutDto.getPlayNum());
        foundGame.setPlayTime(gamePutDto.getPlayTime());
        foundGame.setReleaseDate(gamePutDto.getReleaseDate());
        foundGame.setName(gamePutDto.getName());

        return foundGame;

    }
}
