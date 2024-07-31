package com.elice.boardgame.category.service;

import com.elice.boardgame.ExceptionHandler.BoardGameNotFoundException;
import com.elice.boardgame.category.entity.LiveView;
import com.elice.boardgame.category.entity.LiveViewRanking;
import com.elice.boardgame.category.repository.LiveViewRankingRepository;
import com.elice.boardgame.category.repository.LiveViewRepository;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.repository.BoardGameRepository;
import java.time.LocalDate;
import java.time.Period;
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

    private final LiveViewRepository liveViewRepository;

    private final LiveViewRankingRepository liveViewRankingRepository;

    public void addViewScore(BoardGame game, String ipAddress) {
        Optional<LiveView> optionalEntity = liveViewRepository.findByGame(game);

        LiveView liveView = optionalEntity.orElse(new LiveView());

        if (liveView.getIpAddress() != null && liveView.getIpAddress().equals(ipAddress)) {
            return;
        }
        LocalDate now = LocalDate.now();

        liveView.setGame(game);
        liveView.setCreatedDate(now.atStartOfDay());
        liveView.setViewScore(8L);
        liveView.setIpAddress(ipAddress);

        liveViewRepository.save(liveView);
    }


    public void updateLiveViewScore() {
        LocalDate currentDate = LocalDate.now();

        List<LiveView> dataList = liveViewRepository.findAll();

        for (LiveView data : dataList) {
            LocalDate date = LocalDate.from(data.getCreatedDate());
            Period period = Period.between(date, currentDate);

            int days = period.getDays();

            if (days <= 7) {
                data.setViewScore(8L);
            } else if (days <= 14) {
                data.setViewScore(4L);
            } else if (days <= 21) {
                data.setViewScore(2L);
            } else if (days <= 30) {
                liveViewRepository.delete(data);
            } else {
                data.setViewScore(1L);
            }

            liveViewRepository.save(data);
        }
    }

    public void updateRanking() {
        List<LiveView> liveViews = liveViewRepository.findAll();

        Map<BoardGame, Long> viewCounts = liveViews.stream()
            .collect(Collectors.groupingBy(LiveView::getGame,
                Collectors.summingLong(LiveView::getViewScore)));

        viewCounts.forEach((game, sumScore) -> {
            Optional<LiveViewRanking> optionalEntity = liveViewRankingRepository.findByGame(game);
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

    public List<BoardGame> getLiveViewRanking() {
        List<LiveViewRanking> liveViews = liveViewRankingRepository.findAllByOrderBySumScoreDesc();
        List<BoardGame> boardGames = new ArrayList<>();

        for (LiveViewRanking liveView : liveViews) {
            BoardGame boardGame = liveView.getGame();
            if (boardGame == null) {
                throw new BoardGameNotFoundException("해당 보드게임이 존재하지 않습니다.");
            }
            boardGames.add(boardGame);
        }

        return boardGames;
    }
}
