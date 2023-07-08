package controller;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static controller.PieceName.BISHOP;
import static controller.PieceName.PAWN;

public class Bishop implements Piece{

    static PieceName name = BISHOP;
    ChessBoard board;
    Color color;
    Pair<Integer, Integer> position;
    public Bishop(int x, int y, ChessBoard board, Color color) {
        this.board = board;
        this.position = new Pair<>(x, y);
        this.color = color;
    }

    @Override
    public boolean Move(int x, int y) {
        return false;
    }

    @Override
    public List<Pair<Integer, Integer>> FindValidMoves() {
        return new ArrayList<>();
    }

    public Color getColor() {
        return color;
    }

    public Pair<Integer, Integer> getPosition() {
        return position;
    }

    public PieceName getName() {
        return name;
    }
}
