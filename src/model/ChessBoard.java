package model;

import javafx.util.Pair;
import model.enums.CheckStatus;
import model.enums.Color;
import model.pieces.*;

import java.util.ArrayList;
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
    private static final int INVALID_FEN = -1;

    public ChessBoard() {
        enPassantAvailable = false;
        chessBoard = new Piece[8][8];
        blackKingPos = new ArrayList<>();
        whiteKingPos = new ArrayList<>();
    }

    @Override
    public boolean FENSetup(String fen) {
        int index;
        int len;
        if(fen.isEmpty()) {
            return false;
        }
        len = fen.length();
        index = PiecePlacement(fen, len);
        if(index == INVALID_FEN) {
            return false;
        }
        return true;
    }

    private int PiecePlacement(String fen, int len) {
        int index;
        int line = 0;
        int square = 0;
        for(index = 0; index < len; index++) {
            String type = Character.toString(fen.charAt(index));

            if(square == 8) {
                if(type.equals("/") && line < 7) {
                    line++;
                    square = 0;
                } else if(line==7) {
                    if(type.equals(" ")) {
                        return index+1;
                    }
                    return INVALID_FEN;
                } else {
                    return INVALID_FEN;
                }
            } else if(type.matches("r|n|b|q|k|p|R|N|B|Q|K|P")) {
                if((line==0 || line==7)&&type.matches("p|P")) {
                    return INVALID_FEN;
                }
                chessBoard[line][square] = createPiece(type, line, square);
                if(chessBoard[line][square] == null) {
                    return INVALID_FEN;
                }
                square++;
            } else if(type.matches("[1-8]")) {
                square+= Integer.parseInt(type);
            }  else {
                return INVALID_FEN;
            }
        }

        return INVALID_FEN;
    }

    private Piece createPiece(String type, int x, int y) {
        switch(type) {
            case "p":
                return new Pawn(x, y, this, Color.BLACK);
            case "P":
                return new Pawn(x, y, this, Color.WHITE);
            case "r":
                return new Rook(x, y, this, Color.BLACK);
            case "R":
                return new Rook(x, y, this, Color.WHITE);
            case "n":
                return new Knight(x, y, this, Color.BLACK);
            case "N":
                return new Knight(x, y, this, Color.WHITE);
            case "b":
                return new Bishop(x, y, this, Color.BLACK);
            case "B":
                return new Bishop(x, y, this, Color.WHITE);
            case "q":
                return new Queen(x, y, this, Color.BLACK);
            case "Q":
                return new Queen(x, y, this, Color.WHITE);
            case "k":
                blackKingPos.add(new Pair<>(x,y));
                return new King(x, y, this, Color.BLACK);
            case "K":
                whiteKingPos.add(new Pair<>(x,y));
                return new King(x, y, this, Color.WHITE);
            default:
                return null;
        }
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
