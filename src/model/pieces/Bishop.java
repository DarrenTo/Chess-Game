package model.pieces;

import javafx.util.Pair;
import model.ChessBoard;
import model.IChessBoard;
import model.enums.Color;
import model.enums.PieceName;
import model.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

import static model.enums.PieceName.BISHOP;

public class Bishop implements Piece {
    static PieceName name = BISHOP;
    Color color;

    public Bishop(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public PieceName getName() {
        return name;
    }
}
