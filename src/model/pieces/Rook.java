package model.pieces;

import javafx.util.Pair;
import model.ChessBoard;
import model.IChessBoard;
import model.enums.Color;
import model.enums.PieceName;
import model.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

import static model.enums.PieceName.ROOK;

public class Rook implements Piece {
    static PieceName name = ROOK;
    Color color;

    public Rook(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public PieceName getName() {
        return name;
    }
}
