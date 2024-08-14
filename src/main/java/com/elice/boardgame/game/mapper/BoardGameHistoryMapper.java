package com.elice.boardgame.game.mapper;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.BoardGameHistory;
import com.elice.boardgame.game.entity.GameGenreHistory;
import com.elice.boardgame.game.repository.GenreHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BoardGameHistoryMapper {

    private final GenreHistoryRepository historyRepository;

    public BoardGameHistory BoardGameToBoardGameHistory(BoardGame foundGame) {

        BoardGameHistory history = new BoardGameHistory();
        history.setCreatedAt(foundGame.getUpdatedAt());
        history.setYoutubeLink(foundGame.getYoutubeLink());
        history.setDifficulty(foundGame.getDifficulty());
        history.setPrice(foundGame.getPrice());
        history.setArtwork(foundGame.getArtwork());
        history.setPublisher(foundGame.getPublisher());
        history.setDesigner(foundGame.getDesigner());
        history.setAgeLimit(foundGame.getAgeLimit());
        history.setPlayNum(foundGame.getPlayNum());
        history.setPlayTime(foundGame.getPlayTime());
        history.setReleaseDate(foundGame.getReleaseDate());
        history.setName(foundGame.getName());
        history.setBoardGame(foundGame);
        List<GameGenreHistory> genreHistories = new ArrayList<>();
        List<GameGenre> genres = foundGame.getGameGenres();
        for (GameGenre gameGenre : genres) {
            GameGenreHistory genreHistory = new GameGenreHistory();
            genreHistory.setGenreId(gameGenre.getGenre().getGenreId());
            genreHistory.setGenre(gameGenre.getGenre().getGenre());
            genreHistories.add(genreHistory);
        }
        history.setGameGenres(genreHistories);
        User firstCreator = foundGame.getFirstCreator();
        User editBy = foundGame.getEditBy();
        if (firstCreator != null && firstCreator.getUsername() != null) {
            history.setCreator(firstCreator);
        }
        if (editBy != null && editBy.getUsername() != null) {
            history.setCreator(editBy);
        }
        return history;
    }
}
