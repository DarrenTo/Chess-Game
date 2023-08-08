package model.enums;

import javafx.util.Pair;
public enum KnightDirections {
    UP1_LEFT2(new Pair<>(-2, -1)),
    UP2_LEFT1(new Pair<>(-1, -2)),
    UP2_RIGHT1(new Pair<>(1, -2)),
    UP1_RIGHT2(new Pair<>(2, -1)),
    DOWN1_RIGHT2(new Pair<>(2, 1)),
    DOWN2_RIGHT1(new Pair<>(1, 2)),
    DOWN2_LEFT1(new Pair<>(-1, 2)),
    DOWN1_LEFT2(new Pair<>(-2, 1));

    public final Pair<Integer, Integer> dir;

    KnightDirections(Pair<Integer, Integer> dir) {
        this.dir = dir;
    }
}
