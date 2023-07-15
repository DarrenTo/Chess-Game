package model.pieces;

import javafx.util.Pair;
import model.enums.Color;
import model.enums.PieceName;

import java.util.List;

public interface Piece {
    /**
     * Returns color of piece
      * @return Color
     */
    Color getColor();

    /**
     * Returns the name of the piece
     * @return PieceName
     */
    PieceName getName();
}