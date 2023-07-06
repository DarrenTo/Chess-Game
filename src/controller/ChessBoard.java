package controller;

import javafx.util.Pair;

public class ChessBoard implements IChessBoard{
    private static final int NOT_IN_CHECK = 0;
    private static final int IN_CHECK = 1;
    private static final int CHECKMATE = 2;
    protected Piece[][] chessBoard;
    private boolean enPassantAvailable;
    private Pair<Integer, Integer> enPassantPos;

    private Pair<Integer, Integer> blackKing;
    private Pair<Integer, Integer> whiteKing;

    public ChessBoard() {
        enPassantAvailable = false;
        chessBoard = new Piece[8][8];
    }

    @Override
    public CheckStatus FindCheck() {
        return null;
    }

    @Override
    public boolean MovePiece(Piece piece, int x, int y) {
        return false;
    }

    @Override
    public Piece getPositionStatus(int x, int y) {
        return null;
    }

    public boolean isEnPassantAvailable() {
        return enPassantAvailable;
    }

    public Pair<Integer, Integer> getEnPassantPos() {
        return enPassantPos;
    }

    public Pair<Integer, Integer> getBlackKing() {
        return blackKing;
    }

    public Pair<Integer, Integer> getWhiteKing() {
        return whiteKing;
    }

    public void setEnPassantAvailable(boolean enPassantAvailable) {
        this.enPassantAvailable = enPassantAvailable;
    }

    public void setEnPassantPos(Pair<Integer, Integer> enPassantPos) {
        this.enPassantPos = enPassantPos;
    }

    public void setBlackKing(Pair<Integer, Integer> blackKing) {
        this.blackKing = blackKing;
    }

    public void setWhiteKing(Pair<Integer, Integer> whiteKing) {
        this.whiteKing = whiteKing;
    }
}
