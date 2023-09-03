package model;

import model.enums.PieceName;
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
     * @param name      The name of the piece moved
     * @param initX     The x position of piece before it is moved
     * @param initY     The y position of piece before it is moved
     * @param endX      The x position of piece after it is moved
     * @param endY      The y position of piece after it is moved
     */
    void LogMove(PieceName name, int initX, int initY, int endX, int endY);
}
