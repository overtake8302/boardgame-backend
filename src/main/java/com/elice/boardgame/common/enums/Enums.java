package com.elice.boardgame.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Enums {
    public enum Category {
        FREE("자유 게시판"),
        REVIEW("후기 게시판"),
        USED("중고 거래 게시판"),
        MEETING("모임 게시판"),
        TACTICS("전략 게시판");

        private final String label;

        Category(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum PlayTime {
        SHORT("30분 이하"),
        MEDIUM("30분 ~ 1시간"),
        LONG("1시간 이상");

        private final String label;

        PlayTime(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum PlayNum {
        ONE_PLAYER("1인용"),
        TWO_PLAYERS("2인용"),
        THREE_PLAYERS("3인용"),
        FOUR_PLAYERS("4인용"),
        FIVE_PLUS_PLAYERS("5인 이상");

        private final String label;

        PlayNum(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum AgeLimit {
        AGE_ALL("전체 이용가"),
        AGE_12_PLUS("12세 이용가"),
        AGE_15_PLUS("15세 이용가"),
        AGE_18_PLUS("청소년 이용 불가");

        private final String label;

        AgeLimit(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum Difficulty {
        EASY("쉬움"),
        MEDIUM("보통"),
        HARD("어려움");

        private final String label;

        Difficulty(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum GameListSortOption {
        GAME_ID("gameId"),
        DIFFICULTY("difficulty"),
        AVERAGE_RATE("averageRate"),
        VIEWS("views"),
        LIKES("likes");

        private final String sortBy;

        public static GameListSortOption fromString(String sortBy) {
            for (GameListSortOption option : GameListSortOption.values()) {
                if (option.sortBy.equalsIgnoreCase(sortBy)) {
                    return option;
                }
            }
            throw new IllegalArgumentException("Unknown sort option: " + sortBy);
        }
    }
}