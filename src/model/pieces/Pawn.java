package model.pieces;

import javafx.util.Pair;
import model.ChessBoard;
import model.enums.Color;
import model.enums.PieceName;
import model.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

import static model.enums.PieceName.PAWN;

public class Pawn implements Piece {

    static PieceName name = PAWN;

    ChessBoard board;
    Color color;
    Pair<Integer, Integer> position;
    public Pawn(int x, int y, ChessBoard board, Color color) {
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
