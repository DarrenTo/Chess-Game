package controller;

import javafx.util.Pair;

public class ChessBoard implements IChessBoard{
    protected Piece[][] chessBoard;
    private boolean enPassantAvailable;
    private Pair<Integer, Integer> enPassantPos;
    private int halfmoveClock;
    private int fullmoveNumber;
    private Piece prevMoveWhitePiece;
    private Piece prevMoveBlackPiece;
    private Pair<Integer, Integer> prevMoveWhitePos;
    private Pair<Integer, Integer> prevMoveBlackPos;
    private Pair<Integer, Integer> blackKingPos;
    private Pair<Integer, Integer> whiteKingPos;

    public ChessBoard() {
        enPassantAvailable = false;
        chessBoard = new Piece[8][8];
    }

    @Override
    public void FENSetup(String fen) {

    }

    @Override
    public Piece getPositionStatus(int x, int y) {
        return null;
    }

    @Override
    public CheckStatus FindCheck() {
        return null;
    }

    @Override
    public boolean MovePiece(Piece piece, int x, int y) {
        return false;
    }
}
