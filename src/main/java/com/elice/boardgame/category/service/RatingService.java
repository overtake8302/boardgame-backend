package com.elice.boardgame.category.service;

import com.elice.boardgame.category.DTO.BoardGameRateDto;
import com.elice.boardgame.category.DTO.RatingCountDto;
import com.elice.boardgame.game.dto.GameProfilePicResponseDto;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameProfilePic;
import com.elice.boardgame.game.repository.GameRateRepository;
import com.elice.boardgame.game.service.GameProfilePicService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final GameRateRepository gameRateRepository;

    private final GameProfilePicService gameProfilePicService;

    public List<RatingCountDto> getRatingCounts(Long userId) {
        return gameRateRepository.countRatingsByUserId(userId);
    }

    public RatingCountDto getGamesByRating(Long userId, Double rate) {
        return gameRateRepository.findRatingCountByUserIdAndRate(userId, rate);
    }


    //dto 변환 로직
    private GameResponseDto convertToGameResponseDto(BoardGameRateDto boardGameRateDTO) {
        BoardGame boardGame = boardGameRateDTO.getBoardGame();
        GameResponseDto gameResponseDto = new GameResponseDto();

        gameResponseDto.setGameId(boardGame.getGameId());
        gameResponseDto.setName(boardGame.getName());
        gameResponseDto.setPlayTime(boardGame.getPlayTime().toString());
        gameResponseDto.setPlayNum(boardGame.getPlayNum().toString());
        gameResponseDto.setAgeLimit(boardGame.getAgeLimit().toString());
        gameResponseDto.setPrice(boardGame.getPrice());
        gameResponseDto.setDesigner(boardGame.getDesigner());
        gameResponseDto.setArtwork(boardGame.getArtwork());
        gameResponseDto.setReleaseDate(boardGame.getReleaseDate());
        gameResponseDto.setDifficulty(boardGame.getDifficulty().toString());
        gameResponseDto.setPublisher(boardGame.getPublisher());
        gameResponseDto.setYoutubeLink(boardGame.getYoutubeLink());
        gameResponseDto.setLikeCount(boardGame.getGameLikes().size());
        gameResponseDto.setAverageRate(boardGameRateDTO.getRate());
        gameResponseDto.setGameGenres(boardGame.getGameGenres());
        gameResponseDto.setViews(boardGame.getViews());

        // GameProfilePicResponseDto를 생성하는 로직
        List<GameProfilePic> gameProfilePics = boardGame.getGameProfilePics();
        List<String> picAddresses = gameProfilePics.stream()
            .map(GameProfilePic::getPicAddress)
            .collect(Collectors.toList());
//        GameProfilePicResponseDto gameProfilePicResponseDto = new GameProfilePicResponseDto(picAddresses);
        gameResponseDto.setGameProfilePics(picAddresses);

        return gameResponseDto;
    }


}
