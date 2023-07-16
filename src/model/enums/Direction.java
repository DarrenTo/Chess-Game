package model.enums;

import javafx.util.Pair;

public enum Direction {
    LEFT(new Pair<>(-1, 0), new Pair<>(0, -1)),
    RIGHT(new Pair<>(1, 0), new Pair<>(7, -1)),
    UP(new Pair<>(0, -1), new Pair<>(-1, 0)),
    DOWN(new Pair<>(0, 1), new Pair<>(-1, 7)),
    UP_LEFT_DIAGONAL(new Pair<>(-1, -1), new Pair<>(0, 0)),
    UP_RIGHT_DIAGONAL(new Pair<>(1, -1), new Pair<>(7, 0)),
    DOWN_LEFT_DIAGONAL(new Pair<>(-1, 1), new Pair<>(0, 7)),
    DOWN_RIGHT_DIAGONAL(new Pair<>(1, 1), new Pair<>(7, 7));


    public final Pair<Integer, Integer> dir;
    public final Pair<Integer, Integer> limits;
    Direction(Pair<Integer, Integer> dir, Pair<Integer, Integer> limits) {
        this.dir = dir;
        this.limits = limits;
    }
}
