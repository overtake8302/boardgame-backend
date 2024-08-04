package com.elice.boardgame.game.repository;

public interface CustomGameVisitorRepository {

    void insertIgnore(String visitorId, Long gameId);
}
