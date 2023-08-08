package model.enums;

import javafx.util.Pair;

public enum Direction {
    LEFT(new Pair<>(-1, 0)),
    RIGHT(new Pair<>(1, 0)),
    UP(new Pair<>(0, -1)),
    DOWN(new Pair<>(0, 1)),
    UP_LEFT_DIAGONAL(new Pair<>(-1, -1)),
    UP_RIGHT_DIAGONAL(new Pair<>(1, -1)),
    DOWN_LEFT_DIAGONAL(new Pair<>(-1, 1)),
    DOWN_RIGHT_DIAGONAL(new Pair<>(1, 1));


    public final Pair<Integer, Integer> dir;
    Direction(Pair<Integer, Integer> dir) {
        this.dir = dir;
    }
}
