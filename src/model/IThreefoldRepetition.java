package model;

import model.pieces.Piece;

public interface IThreefoldRepetition {

    /**
     * Returns whether a threefold repetition has occurred
     *
     * @return boolean
     */
    boolean isThreefoldRepetition();

    /**
     * Log a move to keep track of potential threefold repetitions
     *
     * @param piece The piece performing the move
     * @param x     The x position of piece after it is moved
     * @param y     The y position of piece after it is moved
     */
    void LogMove(Piece piece, int x, int y);
}
