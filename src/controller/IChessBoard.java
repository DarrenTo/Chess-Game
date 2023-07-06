package controller;

public interface IChessBoard {

    /**
     * Looks for checks or checkmate on the king for the moving side
     * e.g. White's turn to move, they are the 'moving side'
     * @return CheckStatus
     *
     * If Checkmate the game should end
     */
    CheckStatus FindCheck();

    /**
     * Moves a piece if valid
     *
     * @param piece The piece to be moved
     * @param x The desired x position after being moved
     * @param y The desired y position after being moved
     *
     * @return boolean
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
     */
    boolean MovePiece(Piece piece, int x, int y);

    /**
     * Get the piece at a specific position
     *
     * @param x The x position
     * @param y The y position
     *
     * @return The piece at the x,y position or null if nothing is there
     */
    Piece getPositionStatus(int x, int y);
}
