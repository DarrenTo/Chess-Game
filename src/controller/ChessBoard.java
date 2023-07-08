package controller;

import javafx.util.Pair;

import java.util.List;

public class ChessBoard implements IChessBoard{
    protected Piece[][] chessBoard;
    private boolean enPassantAvailable;
    private Pair<Integer, Integer> enPassantPos;
    private int halfmoveClock;
    private int fullmoveNumber;
    private IThreefoldRepetition threefoldRep;
    private List<Pair<Integer, Integer>> blackKingPos;
    private List<Pair<Integer, Integer>> whiteKingPos;

    public ChessBoard() {
        enPassantAvailable = false;
        chessBoard = new Piece[8][8];
    }

    @Override
    public boolean FENSetup(String fen) {
        return false;
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
