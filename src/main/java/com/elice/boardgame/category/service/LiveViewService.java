package com.elice.boardgame.category.service;

import com.elice.boardgame.common.exceptions.BoardGameNotFoundException;
import com.elice.boardgame.category.entity.LiveView;
import com.elice.boardgame.category.entity.LiveViewRanking;
import com.elice.boardgame.category.repository.LiveViewRankingRepository;
import com.elice.boardgame.category.repository.LiveViewRepository;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.repository.BoardGameRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LiveViewService {

    private final BoardGameRepository boardGameRepository;

    private final LiveViewRepository liveViewRepository;

    private final LiveViewRankingRepository liveViewRankingRepository;

    private final BoardGameMapper boardGameMapper;

    public void addViewScore(Long gameId, String ipAddress) {

        Optional<BoardGame> optionalBoardGame = boardGameRepository.findById(gameId);

        BoardGame game = optionalBoardGame.orElseThrow();

        List<LiveView> liveViews = liveViewRepository.findLiveViewsByGame(game);

        for (LiveView liveView: liveViews) {
            if (liveView.getIpAddress() != null && liveView.getIpAddress().equals(ipAddress)) {
                return;
            }
        }

        LiveView liveView = new LiveView();

        liveView.setGame(game);
        liveView.setViewScore(8L);
        liveView.setIpAddress(ipAddress);

        liveViewRepository.save(liveView);
    }

    public void updateLiveViewScore() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        List<LiveView> dataList = liveViewRepository.findAll();

        for (LiveView data : dataList) {
            LocalDateTime dateTime = data.getCreatedAt();
            Duration duration = Duration.between(dateTime, currentDateTime);
            long hours = duration.toHours();
            if (hours <= 12) {
                data.setViewScore(8L);
            } else if (hours <= 24) {
                data.setViewScore(6L);
            } else if (hours <= 48) {
                data.setViewScore(4L);
            } else if (hours <= 168) {
                data.setViewScore(2L);
            } else if (hours <= 336) {
                data.setViewScore(1L);
            } else {
                liveViewRepository.delete(data);
                continue;
            }

            liveViewRepository.save(data);
        }
    }

    public void updateRanking() {
        List<LiveView> liveViews = liveViewRepository.findAll();

        Map<BoardGame, Long> viewCounts = liveViews.stream()
            .filter(liveView -> liveView.getGame().getDeletedAt() == null)
            .collect(Collectors.groupingBy(LiveView::getGame,
                Collectors.summingLong(LiveView::getViewScore)));

        viewCounts.forEach((game, sumScore) -> {
            Optional<LiveViewRanking> optionalEntity = liveViewRankingRepository.findByGame(
                game);
            LiveViewRanking ranking;
            if (optionalEntity.isPresent()) {
                ranking = optionalEntity.get();
            } else {
                ranking = new LiveViewRanking();
                ranking.setGame(game);
            }

            ranking.setSumScore(sumScore);
            liveViewRankingRepository.save(ranking);
        });
    }

    public List<GameResponseDto> getLiveViewRanking() {
        List<LiveViewRanking> liveViews = liveViewRankingRepository.find();
        List<Long> boardGameIds = new ArrayList<>();

        for (LiveViewRanking liveView : liveViews) {
            Long gameId = liveView.getGame().getGameId();
            if (gameId == null) {
                throw new BoardGameNotFoundException("해당 보드게임이 존재하지 않습니다.");
            }
            boardGameIds.add(gameId);
        }

        List<GameResponseDto> dtos = boardGameMapper.boardGameIdsToGameResponseDto(boardGameIds);

        return dtos;
    }
}
