package model;

import javafx.util.Pair;
import model.enums.CheckStatus;
import model.enums.Color;
import model.enums.PieceName;
import model.pieces.Piece;

import java.util.List;

public interface IChessBoard {

    /**
     * Setup Chessboard according to FEN code
     * @param fen The FEN code
     *
     * https://www.chess.com/terms/fen-chess
     *
     *            Piece Placement
     *              Squares are represented beginning from 8th row (top row) and ending at the 1st row (bottom row)
     *              Each row starts from the 1st column (leftmost) and ending at the 8th column (rightmost)
     *
     *              - "/" denotes end of line
     *              - Lowercase letters denote black pieces (p,r,n,b,q,k)
     *              - Uppercase letters denote white pieces (P,R,N,B,Q,K)
     *              - Empty squares denoted by numbers 1-8 depending on the number of empty squares between two pieces
     *
     *            Active Color
     *              A single lowercase letter representing whose turn it is
     *
     *              - "w" indicates white's turn whereas "b" indicates black's turn
     *
     *            Castling Rights
     *              White castling privileges represented first followed by black castling privileges
     *
     *              - Uppercase letters denote white castling privileges ("K" for kingside/"Q" for queenside)
     *              - Lowercase letters denote black castling privileges ("k" for kingside/"q" for queenside)
     *              - "-" denotes no castling privileges on either side for either color
     *
     *            En Passant Targets
     *              If a pawn can be captured via en passant, the square behind said pawn is noted
     *              This uses chess notation (1-8 indicates row, a-h represents column)
     *              The absence of an enemy pawn threatening en passant does not affect this notation
     *
     *              - Chess notation represents square behind the pawn which is a target for en passant
     *                  - only row 3 and 6 are valid targets in accordance with traditional chess
     *              - "-" indicates no en passant targets present
     *
     *            HalfMove Clock
     *              Represents the moves both players have made since the last pawn advance or piece capture
     *              Used to enforce the 50-move draw rule
     *
     *             - Halfmove clock resets to 0 after a pawn move or piece capture
     *             - Game ends in a draw when the halfmove clock reaches 100
     *             - any number <0 or >100 is invalid
     *
     *            Fullmove number
     *              Represents the number of completed turn
     *
     *            - Incremented by 1 every time black moves
     *            - Any number >9999 (4 digits) is invalid to prevent excessive FEN lengths
     *
     *            Example FEN codes
     *
     *            rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 (standard starting position)
     *
     *            rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1 (after white moves pawn to e4)
     *
     *            rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2 (after black moves pawn to c5)
     *
     *            rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2 (after white moves knight to f3)
     *
     *            Note: Threefold repetition not represented with this notation
     *
     * @return boolean
     *
     * Should return true if fen is valid, otherwise false
     *
     */
    boolean FENSetup(String fen);

    /**
     * Get the piece at a specific position
     *
     * @param x The x position
     * @param y The y position
     *
     * @return The piece at the x,y position or null if nothing is there
     */
    Piece getPositionStatus(int x, int y);

    /**
     * Looks for checks or checkmate on the king for the moving side
     * e.g. White's turn to move, they are the 'moving side'
     * @return CheckStatus
     *
     * If Checkmate the game should end
     */
    CheckStatus FindCheckStatus();

    /**
     * Moves a piece if valid
     *
     * @param initX The initial X pos of a piece
     * @param initY The initial Y pos of a piece
     * @param endX  The x pos after move
     * @param endY The y pos after move
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
    boolean MovePiece(int initX, int initY, int endX, int endY);

    /**
     *
     * Move piece function for pawn promotion
     *
     * @param name Pawn promotion name
     * @return boolean
     */
    boolean MovePiece(int initX, int initY, int endX, int endY, PieceName name);

    /**
     * Find all valid moves
     *
     * @param x The x pos of a piece
     * @param y The y pos of a piece
     *
     * @return Pair<Integer, Integer>[]
     * The list should be empty if no valid moves
     */
    List<Pair<Integer, Integer>> FindValidMoves(int x, int y);


    /**
     *
     * @return chessboard array
     */
    Piece[][] getBoard();

    /**
     *
     * @return Active turn's color
     */
    Color getActiveColor();

}
