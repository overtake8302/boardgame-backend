package com.elice.boardgame.game.repository;

import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.QGameGenre;
import com.elice.boardgame.game.dto.GameProfilePicResponseDto;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CustomBoardGameRepositoryImpl implements CustomBoardGameRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BoardGame> findBoardGamesWithFilters(List<String> playTimes, List<String> playNums,
        List<String> ageLimits, List<String> prices, List<String> genres) {
        QBoardGame qBoardGame = QBoardGame.boardGame;
        QGameGenre qGameGenre = QGameGenre.gameGenre;
        BooleanBuilder builder = new BooleanBuilder();

        if (playTimes != null && !playTimes.isEmpty()) {
            BooleanExpression playTimeExpression = qBoardGame.playTime.stringValue().in(playTimes);
            builder.and(playTimeExpression);
        }
        if (playNums != null && !playNums.isEmpty()) {
            BooleanExpression playNumExpression = qBoardGame.playNum.stringValue().in(playNums);
            builder.and(playNumExpression);
        }
        if (ageLimits != null && !ageLimits.isEmpty()) {
            BooleanExpression ageLimitExpression = qBoardGame.ageLimit.stringValue().in(ageLimits);
            builder.and(ageLimitExpression);
        }
        if (prices != null && !prices.isEmpty()) {
            BooleanBuilder priceBuilder = new BooleanBuilder();
            for (String price : prices) {
                switch (price) {
                    case "3만원 이하":
                        priceBuilder.or(qBoardGame.price.loe(30000));
                        break;
                    case "3~5만원":
                        priceBuilder.or(qBoardGame.price.between(30001, 50000));
                        break;
                    case "5~10만원":
                        priceBuilder.or(qBoardGame.price.between(50001, 100000));
                        break;
                    case "10만원 이상":
                        priceBuilder.or(qBoardGame.price.goe(100001));
                        break;
                }
            }
            builder.and(priceBuilder);
        }
        if (genres != null && !genres.isEmpty()) {
            builder.and(qBoardGame.gameId.in(
                queryFactory.select(qGameGenre.id.gameId)
                    .from(qGameGenre)
                    .where(qGameGenre.genre.genre.in(genres))
            ));
        }

        return queryFactory.selectFrom(qBoardGame)
            .where(builder)
            .fetch();
    }

    @Override
    public List<BoardGame> findByGenres(List<Long> genreIds, Long userId) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameGenre gameGenre = QGameGenre.gameGenre;
        QGameLike gameLike = QGameLike.gameLike;

        // 각 genreId를 확인하는 조건 추가
        BooleanExpression predicate = gameGenre.genre.genreId.in(genreIds);

        // 좋아요를 누른 게임 ID 가져오기
        List<Long> likedGameIds = queryFactory.select(gameLike.boardGame.gameId)
            .from(gameLike)
            .where(gameLike.user.id.eq(userId))
            .fetch();

        // game_id를 그룹화하고 count(genre_id)로 정렬
        List<Long> boardGameIds = queryFactory
            .select(gameGenre.boardGame.gameId)
            .from(gameGenre)
            .where(predicate)
            .groupBy(gameGenre.boardGame.gameId)
            .orderBy(gameGenre.genre.genreId.count().desc())
            .fetch();

        // 보드게임 ID로 보드게임을 조회하며 좋아요를 누른 게임은 제외
        return queryFactory
            .selectFrom(boardGame)
            .where(boardGame.gameId.in(boardGameIds) //서브쿼리 사용해서 좋아요 누른 게임 제외
                .and(boardGame.gameId.notIn(likedGameIds)))
            .fetch();
    }
    
     public Page<GameResponseDto> findAllOrderByAverageRateDesc(Pageable pageable) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameRate gameRate = QGameRate.gameRate;
        QGameVisitor gameVisitor = QGameVisitor.gameVisitor;
        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;
        QGameGenre gameGenre = QGameGenre.gameGenre;

        List<Tuple> results = queryFactory
                .select(boardGame, gameRate.rate.avg(), gameVisitor.id.count())
                .from(boardGame)
                .leftJoin(boardGame.gameRates, gameRate)
                .leftJoin(boardGame.gameVisitors, gameVisitor)
                .leftJoin(boardGame.gameGenres, gameGenre) // 장르 조인
                .where(boardGame.deletedDate.isNull())
                .groupBy(boardGame)
                .orderBy(gameRate.rate.avg().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<GameResponseDto> gameResponseDtos = new ArrayList<>();
        for (Tuple tuple : results) {
            BoardGame boardGameEntity = tuple.get(boardGame);
            Double averageRate = tuple.get(gameRate.rate.avg());
            Long views = tuple.get(gameVisitor.id.count());

            // 프로필 사진 수집
            List<String> picAddresses = boardGameEntity.getGameProfilePics().stream()
                    .map(GameProfilePic::getPicAddress)
                    .collect(Collectors.toList());

            // 장르 수집
            List<GameGenre> genres = queryFactory
                    .select(gameGenre)
                    .from(gameGenre)
                    .where(gameGenre.boardGame.eq(boardGameEntity))
                    .fetch();

            GameResponseDto dto = new GameResponseDto();
            dto.setGameId(boardGameEntity.getGameId());
            dto.setName(boardGameEntity.getName());
            dto.setPlayTime(boardGameEntity.getPlayTime().getLabel());
            dto.setPlayNum(boardGameEntity.getPlayNum().getLabel());
            dto.setAgeLimit(boardGameEntity.getAgeLimit().getLabel());
            dto.setPrice(boardGameEntity.getPrice());
            dto.setDesigner(boardGameEntity.getDesigner());
            dto.setArtwork(boardGameEntity.getArtwork());
            dto.setReleaseDate(boardGameEntity.getReleaseDate());
            dto.setDifficulty(boardGameEntity.getDifficulty().getLabel());
            dto.setPublisher(boardGameEntity.getPublisher());
            dto.setYoutubeLink(boardGameEntity.getYoutubeLink());
            dto.setLikeCount(boardGameEntity.getGameLikes().size());
            dto.setGameGenres(genres);
            dto.setAverageRate(averageRate);
            dto.setViews(views);

            GameProfilePicResponseDto gameProfilePicResponseDto = new GameProfilePicResponseDto(picAddresses);
            dto.setGameProfilePics(gameProfilePicResponseDto);

            gameResponseDtos.add(dto);
        }

        return new PageImpl<>(gameResponseDtos, pageable, gameResponseDtos.size());
    }








    @Override
    public GameResponseDto getGameResponseDtoByGameIdAndDeletedDateIsNull(Long gameId) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameRate gameRate = QGameRate.gameRate;
        QGameVisitor gameVisitor = QGameVisitor.gameVisitor;
        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;
        QGameGenre gameGenre = QGameGenre.gameGenre;

        Tuple result = queryFactory
                .select(boardGame, gameRate.rate.avg(), gameVisitor.id.count())
                .from(boardGame)
                .leftJoin(boardGame.gameRates, gameRate)
                .leftJoin(boardGame.gameVisitors, gameVisitor)
                .leftJoin(boardGame.gameProfilePics, gameProfilePic) // 프로필 사진 조인
                .leftJoin(boardGame.gameGenres, gameGenre) // 장르 조인
                .where(boardGame.deletedDate.isNull()
                        .and(boardGame.gameId.eq(gameId)))
                .groupBy(boardGame)
                .orderBy(gameRate.rate.avg().desc())
                .fetchOne();

        if (result == null) {
            return null;
        }

        BoardGame boardGameEntity = result.get(boardGame);
        Double averageRate = result.get(gameRate.rate.avg());
        Long views = result.get(gameVisitor.id.count());

        // 프로필 사진 수집
        List<String> picAddresses = boardGameEntity.getGameProfilePics().stream()
                .map(GameProfilePic::getPicAddress)
                .collect(Collectors.toList());

        // 장르 수집
        List<GameGenre> genres = queryFactory
                .select(gameGenre)
                .from(gameGenre)
                .where(gameGenre.boardGame.eq(boardGameEntity))
                .fetch();

        GameResponseDto dto = new GameResponseDto();
        dto.setGameId(boardGameEntity.getGameId());
        dto.setName(boardGameEntity.getName());
        dto.setPlayTime(boardGameEntity.getPlayTime().getLabel());
        dto.setPlayNum(boardGameEntity.getPlayNum().getLabel());
        dto.setAgeLimit(boardGameEntity.getAgeLimit().getLabel());
        dto.setPrice(boardGameEntity.getPrice());
        dto.setDesigner(boardGameEntity.getDesigner());
        dto.setArtwork(boardGameEntity.getArtwork());
        dto.setReleaseDate(boardGameEntity.getReleaseDate());
        dto.setDifficulty(boardGameEntity.getDifficulty().getLabel());
        dto.setPublisher(boardGameEntity.getPublisher());
        dto.setYoutubeLink(boardGameEntity.getYoutubeLink());
        dto.setLikeCount(boardGameEntity.getGameLikes().size());
        dto.setGameGenres(genres);
        dto.setAverageRate(averageRate);
        dto.setViews(views);

        GameProfilePicResponseDto gameProfilePicResponseDto = new GameProfilePicResponseDto(picAddresses);
        dto.setGameProfilePics(gameProfilePicResponseDto);

        return dto;
    }


    @Override
    public List<GameResponseDto> findByNameContainingAndDeletedDateIsNull(String keyword) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameVisitor gameVisitor = QGameVisitor.gameVisitor;
        QGameRate gameRate = QGameRate.gameRate;
        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;
        QGameGenre gameGenre = QGameGenre.gameGenre;

        List<Tuple> results = queryFactory
                .select(boardGame, gameRate.rate.avg(), gameVisitor.id.count())
                .from(boardGame)
                .leftJoin(boardGame.gameRates, gameRate)
                .leftJoin(boardGame.gameVisitors, gameVisitor)
                .leftJoin(boardGame.gameGenres, gameGenre) // 장르 조인
                .where(boardGame.name.contains(keyword).and(boardGame.deletedDate.isNull()))
                .groupBy(boardGame)
                .fetch();

        List<GameResponseDto> gameResponseDtos = new ArrayList<>();
        for (Tuple tuple : results) {
            BoardGame boardGameEntity = tuple.get(boardGame);
            Double averageRate = tuple.get(gameRate.rate.avg());
            Long views = tuple.get(gameVisitor.id.count());

            // 프로필 사진 수집
            List<String> picAddresses = boardGameEntity.getGameProfilePics().stream()
                    .map(GameProfilePic::getPicAddress)
                    .collect(Collectors.toList());

            // 장르 수집
            List<GameGenre> genres = queryFactory
                    .select(gameGenre)
                    .from(gameGenre)
                    .where(gameGenre.boardGame.eq(boardGameEntity))
                    .fetch();

            GameResponseDto dto = new GameResponseDto();
            dto.setGameId(boardGameEntity.getGameId());
            dto.setName(boardGameEntity.getName());
            dto.setPlayTime(boardGameEntity.getPlayTime().getLabel());
            dto.setPlayNum(boardGameEntity.getPlayNum().getLabel());
            dto.setAgeLimit(boardGameEntity.getAgeLimit().getLabel());
            dto.setPrice(boardGameEntity.getPrice());
            dto.setDesigner(boardGameEntity.getDesigner());
            dto.setArtwork(boardGameEntity.getArtwork());
            dto.setReleaseDate(boardGameEntity.getReleaseDate());
            dto.setDifficulty(boardGameEntity.getDifficulty().getLabel());
            dto.setPublisher(boardGameEntity.getPublisher());
            dto.setYoutubeLink(boardGameEntity.getYoutubeLink());
            dto.setLikeCount(boardGameEntity.getGameLikes().size());
            dto.setGameGenres(genres);
            dto.setAverageRate(averageRate);
            dto.setViews(views);

            GameProfilePicResponseDto gameProfilePicResponseDto = new GameProfilePicResponseDto(picAddresses);
            dto.setGameProfilePics(gameProfilePicResponseDto);

            gameResponseDtos.add(dto);
        }

        return gameResponseDtos;
    }



    public Page<GameResponseDto> findAllByDeletedDateIsNull(Pageable pageable) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameVisitor gameVisitor = QGameVisitor.gameVisitor;
        QGameRate gameRate = QGameRate.gameRate;
        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;
        QGameGenre gameGenre = QGameGenre.gameGenre;

        List<Tuple> results = queryFactory
                .select(boardGame, gameRate.rate.avg(), gameVisitor.id.count())
                .from(boardGame)
                .leftJoin(boardGame.gameRates, gameRate)
                .leftJoin(boardGame.gameVisitors, gameVisitor)
                .leftJoin(boardGame.gameProfilePics, gameProfilePic)
                .leftJoin(boardGame.gameGenres, gameGenre)
                .where(boardGame.deletedDate.isNull())
                .groupBy(boardGame)
//                .orderBy()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<GameResponseDto> gameResponseDtos = new ArrayList<>();
        for (Tuple tuple : results) {
            BoardGame boardGameEntity = tuple.get(boardGame);
            Double averageRate = tuple.get(gameRate.rate.avg());
            Long views = tuple.get(gameVisitor.id.count());

            // 프로필 사진 수집
            List<String> picAddresses = boardGameEntity.getGameProfilePics().stream()
                    .map(GameProfilePic::getPicAddress)
                    .collect(Collectors.toList());

            // 장르 수집
            List<GameGenre> genres = queryFactory
                    .select(gameGenre)
                    .from(gameGenre)
                    .where(gameGenre.boardGame.eq(boardGameEntity))
                    .fetch();

            GameResponseDto dto = new GameResponseDto();
            dto.setGameId(boardGameEntity.getGameId());
            dto.setName(boardGameEntity.getName());
            dto.setPlayTime(boardGameEntity.getPlayTime().getLabel());
            dto.setPlayNum(boardGameEntity.getPlayNum().getLabel());
            dto.setAgeLimit(boardGameEntity.getAgeLimit().getLabel());
            dto.setPrice(boardGameEntity.getPrice());
            dto.setDesigner(boardGameEntity.getDesigner());
            dto.setArtwork(boardGameEntity.getArtwork());
            dto.setReleaseDate(boardGameEntity.getReleaseDate());
            dto.setDifficulty(boardGameEntity.getDifficulty().getLabel());
            dto.setPublisher(boardGameEntity.getPublisher());
            dto.setYoutubeLink(boardGameEntity.getYoutubeLink());
            dto.setLikeCount(boardGameEntity.getGameLikes().size());
            dto.setGameGenres(genres);
            dto.setAverageRate(averageRate);
            dto.setViews(views);

            GameProfilePicResponseDto gameProfilePicResponseDto = new GameProfilePicResponseDto(picAddresses);
            dto.setGameProfilePics(gameProfilePicResponseDto);

            gameResponseDtos.add(dto);
        }

        return new PageImpl<>(gameResponseDtos, pageable, gameResponseDtos.size());
    }








    @Override
    public List<GameResponseDto> findByGameGenresGenreGenre(String genre, Pageable pageable) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameGenre gameGenre = QGameGenre.gameGenre;
        QGameVisitor gameVisitor = QGameVisitor.gameVisitor;
        QGameRate gameRate = QGameRate.gameRate;
        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;

        List<Tuple> results = queryFactory
                .select(boardGame, gameRate.rate.avg(), gameVisitor.id.count())
                .from(boardGame)
                .leftJoin(boardGame.gameRates, gameRate)
                .leftJoin(boardGame.gameVisitors, gameVisitor)
                .leftJoin(boardGame.gameProfilePics, gameProfilePic) // 프로필 사진 조인
                .leftJoin(boardGame.gameGenres, gameGenre) // 장르 조인
                .where(gameGenre.genre.genre.eq(genre).and(boardGame.deletedDate.isNull()))
                .groupBy(boardGame)
                .orderBy(gameRate.rate.avg().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<GameResponseDto> gameResponseDtos = new ArrayList<>();
        for (Tuple tuple : results) {
            BoardGame boardGameEntity = tuple.get(boardGame);
            Double averageRate = tuple.get(gameRate.rate.avg());
            Long views = tuple.get(gameVisitor.id.count());

            // 프로필 사진 수집
            List<String> picAddresses = boardGameEntity.getGameProfilePics().stream()
                    .map(GameProfilePic::getPicAddress)
                    .collect(Collectors.toList());

            // 장르 수집
            List<GameGenre> genres = queryFactory
                    .select(gameGenre)
                    .from(gameGenre)
                    .where(gameGenre.boardGame.eq(boardGameEntity))
                    .fetch();

            GameResponseDto dto = new GameResponseDto();
            dto.setGameId(boardGameEntity.getGameId());
            dto.setName(boardGameEntity.getName());
            dto.setPlayTime(boardGameEntity.getPlayTime().getLabel());
            dto.setPlayNum(boardGameEntity.getPlayNum().getLabel());
            dto.setAgeLimit(boardGameEntity.getAgeLimit().getLabel());
            dto.setPrice(boardGameEntity.getPrice());
            dto.setDesigner(boardGameEntity.getDesigner());
            dto.setArtwork(boardGameEntity.getArtwork());
            dto.setReleaseDate(boardGameEntity.getReleaseDate());
            dto.setDifficulty(boardGameEntity.getDifficulty().getLabel());
            dto.setPublisher(boardGameEntity.getPublisher());
            dto.setYoutubeLink(boardGameEntity.getYoutubeLink());
            dto.setLikeCount(boardGameEntity.getGameLikes().size());
            dto.setGameGenres(genres);
            dto.setAverageRate(averageRate);
            dto.setViews(views);

            GameProfilePicResponseDto gameProfilePicResponseDto = new GameProfilePicResponseDto(picAddresses);
            dto.setGameProfilePics(gameProfilePicResponseDto);

            gameResponseDtos.add(dto);
        }

        return gameResponseDtos;
    }
}
