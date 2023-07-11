package model.pieces;

import javafx.util.Pair;
import model.enums.Color;
import model.enums.PieceName;

import java.util.List;

public interface Piece {
    /**
     * Move if valid
     *
     * @param x The desired x position after being moved
     * @param y The desired y position after being moved
     *
     * True indicates a valid processed move
     * False indicates an invalid unprocessed move
     *
     * A move is only valid if:
     * The king is not in check after the move
     * The piece does not move over other pieces unless it is a knight
     * The piece does not capture its own piece
     *
     * The board and piece should be updated upon a successful move
     * @return boolean
     */
    boolean Move(int x, int y);


    /**
     * Find all valid moves
     *
     * @return Pair<Integer, Integer>[]
     * The list should be empty if no valid moves
     */
     List<Pair<Integer, Integer>> FindValidMoves();

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