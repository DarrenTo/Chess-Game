package model;

import javafx.util.Pair;
import model.enums.PieceName;

import java.util.ArrayList;
import java.util.List;

import static model.enums.PieceName.PAWN;

public class SimpleThreefoldRepetition implements IThreefoldRepetition{
    List<Pair<Integer, Integer>> list1;
    List<Pair<Integer, Integer>> list2;

    // only checks for a consecutive threefold position repetition
    public SimpleThreefoldRepetition() {
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
    }

    @Override
    public boolean isThreefoldRepetition() {
        return list1.size() == 5 && list2.size() == 5;
    }

    @Override
    public void LogMove(PieceName name, int initX, int initY, int endX, int endY) {
        if (isThreefoldRepetition()) {
            return;
        }
        if(name == PAWN) {
            list1.clear();
            list2.clear();
        } else if (list1.isEmpty() || list2.isEmpty()) {
            if (list1.size() > list2.size()) {
                list2.add(new Pair<>(initX, initY));
                list2.add(new Pair<>(endX, endY));
            } else {
                list1.add(new Pair<>(initX, initY));
                list1.add(new Pair<>(endX, endY));
            }
        } else if (isSamePieceMoving(initX, initY)) {
            list1.clear();
            list2.clear();
            list1.add(new Pair<>(initX, initY));
            list1.add(new Pair<>(endX, endY));
        } else {
            if(list1.size() > list2.size()) {
                list2.add(new Pair<>(endX, endY));
            } else {
                list1.add(new Pair<>(endX, endY));
            }
            if(!isRepetitive()) {
                list1.remove(0);
                list2.remove(0);
            }
        }
    }

    private boolean isSamePieceMoving(int initX, int initY) {
        return list1.size() > list2.size() ? !list2.get(list2.size() - 1).equals(new Pair<>(initX, initY)) :
                !list1.get(list1.size() - 1).equals(new Pair<>(initX, initY));
    }

    private boolean isRepetitive() {
        if(list1.size() >= 3 && !list1.get(0).equals(list1.get(2))) {
            return false;
        }
        if(list1.size() >= 5 && !list1.get(0).equals(list1.get(4))) {
            return false;
        }
        if(list2.size() >= 3 && !list2.get(0).equals(list2.get(2))) {
            return false;
        }
        if(list2.size() >= 5 && !list2.get(0).equals(list2.get(4))) {
            return false;
        }

        return true;
    }
}
