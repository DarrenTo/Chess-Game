package model;

import javafx.util.Pair;
import model.enums.CheckStatus;
import model.enums.Color;
import model.enums.PieceName;
import model.pieces.*;

import java.util.ArrayList;
import java.util.List;

import static model.enums.Color.BLACK;
import static model.enums.Color.WHITE;
import static model.enums.PieceName.KING;

public class ChessBoard implements IChessBoard{
    protected Piece[][] chessBoard;
    private Color activeColor;
    private boolean blackKingSideCastle;
    private boolean blackQueenSideCastle;
    private boolean whiteKingSideCastle;
    private boolean whiteQueenSideCastle;
    private Pair<Integer, Integer> enPassantPos;
    private int halfmoveClock;
    private int fullmoveNumber;
    private IThreefoldRepetition threefoldRep;
    private List<Pair<Integer, Integer>> blackKingPos;
    private List<Pair<Integer, Integer>> whiteKingPos;
    private static final Pair<String, String> INVALID_FEN = new Pair<>("", "");
    private static final byte INVALID_CASTLE = 0b11111;
    private static final byte WHITE_KING_CASTLE_MASK = 0b1000;
    private static final byte WHITE_QUEEN_CASTLE_MASK = 0b0100;
    private static final byte BLACK_KING_CASTLE_MASK = 0b0010;
    private static final byte BLACK_QUEEN_CASTLE_MASK = 0b0001;
    private static final Pair<Integer, Integer> INVALID_TARGET = new Pair<>(-1,-1);
    private static final Pair<Integer, Integer> NO_TARGET = new Pair<>(-2,-2);

    public ChessBoard() {
        chessBoard = new Piece[8][8];
        blackKingPos = new ArrayList<>();
        whiteKingPos = new ArrayList<>();
        threefoldRep = new SimpleThreefoldRepetition();
    }

    @Override
    public boolean FENSetup(String fen) {
        Piece[][] chessBoard;
        Color activeColor;
        Pair<Integer, Integer> target;
        byte castlingRights;
        int halfmoveClock;
        int fullmoveNumber;
        Pair<String, String> fenField;
        String[] fields = FENFieldSeparator(fen);
        if(fields == null) {
            return false;
        }

        chessBoard = PiecePlacementCheck(fields[0]);
        activeColor = ActiveColorCheck(fields[1]);
        castlingRights = CastlingRightsCheck(fields[2]);
        target = EnPassantCheck(fields[3]);
        halfmoveClock = HalfMoveCheck(fields[4]);
        fullmoveNumber = FullMoveCheck(fields[5]);
        if (chessBoard == null || activeColor == null || castlingRights == INVALID_CASTLE ||
                target == INVALID_TARGET || halfmoveClock == -1 || fullmoveNumber == -1 ||
        IllegalKingCaptureCheck(activeColor, chessBoard)) {
            return false;
        }

        this.chessBoard = chessBoard;
        this.activeColor = activeColor;
        CastlingSetter(castlingRights);
        this.enPassantPos = target;
        this.halfmoveClock = halfmoveClock;
        this.fullmoveNumber = fullmoveNumber;
        KingPosSetter();
        return true;
    }

    private String[] FENFieldSeparator(String fen) {
        String[] fields = new String[6];
        Pair<String, String> fenField = new Pair<>("", fen);

        for(int i = 0; i < 5; i++) {
            fenField = FENSplitter(fenField.getValue());
            if(fenField == INVALID_FEN) {
                return null;
            }
            fields[i] = fenField.getKey();
        }
        fields[5] = fenField.getValue();
        return fields;
    }
    private Pair<String, String> FENSplitter(String fen) {
        if(fen.isEmpty()) {
            return INVALID_FEN;
        }
        int index = fen.indexOf(" ");
        if(index == -1 || index == (fen.length() - 1)) {
            return INVALID_FEN;
        }
        return new Pair<>(fen.substring(0, index), fen.substring(index + 1));
    }

    private Piece[][] PiecePlacementCheck(String fen) {
        Piece[][] board = new Piece[8][8];
        int index;
        int line = 0;
        int square = 0;
        for(index = 0; index < fen.length(); index++) {
            String type = Character.toString(fen.charAt(index));

            if(square == 8) {
                if(type.equals("/") && line < 7) {
                    line++;
                    square = 0;
                } else {
                    return null;
                }
            } else if(type.matches("r|n|b|q|k|p|R|N|B|Q|K|P")) {
                if((line==0 || line==7)&&type.matches("p|P")) {
                    return null;
                }
                board[line][square] = createPiece(type, line, square);
                if(board[line][square] == null) {
                    return null;
                }
                square++;
            } else if(type.matches("[1-8]")) {
                square+= Integer.parseInt(type);
            }  else {
                return null;
            }
        }

        if(line == 7 && index == fen.length()) {
            return board;
        }

        return null;
    }

    private Piece createPiece(String type, int x, int y) {
        switch(type) {
            case "p":
                return new Pawn(x, y, this, BLACK);
            case "P":
                return new Pawn(x, y, this, WHITE);
            case "r":
                return new Rook(x, y, this, BLACK);
            case "R":
                return new Rook(x, y, this, WHITE);
            case "n":
                return new Knight(x, y, this, BLACK);
            case "N":
                return new Knight(x, y, this, WHITE);
            case "b":
                return new Bishop(x, y, this, BLACK);
            case "B":
                return new Bishop(x, y, this, WHITE);
            case "q":
                return new Queen(x, y, this, BLACK);
            case "Q":
                return new Queen(x, y, this, WHITE);
            case "k":
                return new King(x, y, this, BLACK);
            case "K":
                return new King(x, y, this, WHITE);
            default:
                return null;
        }
    }

    private Color ActiveColorCheck(String fen) {
        if(fen.equals("w")) {
            return WHITE;
        } else if(fen.equals("b")) {
            return BLACK;
        }
        return null;
    }

    private byte CastlingRightsCheck(String fen) {
        byte castle = 0b000;
        if(fen.equals("-")) {
            return 0b0000;
        }

        for(int i = 0; i < fen.length(); i++) {
            String type = Character.toString(fen.charAt(i));

            switch(type) {
                case "K":
                    if((WHITE_KING_CASTLE_MASK & castle) != 0b0) {
                        return INVALID_CASTLE;
                    }
                    castle += 0b1000;
                    break;
                case "Q":
                    if((WHITE_QUEEN_CASTLE_MASK & castle) != 0b0) {
                        return INVALID_CASTLE;
                    }
                    castle += 0b0100;
                    break;
                case "k":
                    if((BLACK_KING_CASTLE_MASK & castle) != 0b0) {
                        return INVALID_CASTLE;
                    }
                    castle += 0b0010;
                    break;
                case "q":
                    if((BLACK_QUEEN_CASTLE_MASK & castle) != 0b0) {
                        return INVALID_CASTLE;
                    }
                    castle += 0b0001;
                    break;
                default:
                    return INVALID_CASTLE;
            }
        }

        return castle;
    }

    private Pair<Integer, Integer> EnPassantCheck(String fen) {
        if(fen.equals("-")) {
            return NO_TARGET;
        } else if(fen.length() != 2 || Character.isUpperCase(fen.charAt(0))) {
            return INVALID_TARGET;
        }

        try {
            int col = Character.getNumericValue(fen.charAt(0));
            int row = Character.getNumericValue(fen.charAt(1));
            if((row != 3 && row != 6) || col < Character.getNumericValue('a') ||
                    col > Character.getNumericValue('h')) {
                return INVALID_TARGET;
            }
            return new Pair<>(ChessRowNotationToArrayPos(row),ChessColNotationToArrayPos(col));
        } catch(IndexOutOfBoundsException e) {
            return INVALID_TARGET;
        }
    }

    private int ChessRowNotationToArrayPos(int n) {
        return Math.abs(n - 8);
    }

    private int ChessColNotationToArrayPos(int n) {
        return n - Character.getNumericValue('a');
    }

    private int HalfMoveCheck(String fen) {
        try {
            int halfmove = Integer.parseInt(fen);
            if (halfmove < 0 || halfmove > 100) {
                return -1;
            }
            return halfmove;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int FullMoveCheck(String fen) {
        try {
            int fullmove = Integer.parseInt(fen);
            if (fullmove < 0 || fullmove > 9999) {
                return -1;
            }
            return fullmove;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean IllegalKingCaptureCheck(Color activeColor, Piece[][] board) {
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[0].length; j++) {
                Piece piece = board[i][j];
                if(piece != null) {
                    if(piece.getName() == KING && piece.getColor() == activeColor) {
                        if(checkDirectionalCapture(activeColor, new Pair<>(i, j))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean checkDirectionalCapture(Color color, Pair<Integer, Integer> pos) {
        if(checkLeft(color, pos) || checkUpperLeftDiagonal(color, pos) || checkUp(color, pos) ||
        checkUpperRightDiagonal(color, pos) || checkRight(color, pos) || checkLowerRightDiagonal(color, pos) ||
        checkDown(color, pos) || checkLowerLeftDiagonal(color, pos)) {
            return true;
        }
        return false;
    }

    private boolean checkLeft(Color color, Pair<Integer, Integer> pos) {
        return true;
    }

    private boolean checkUpperLeftDiagonal(Color color, Pair<Integer, Integer> pos) {
        return true;
    }

    private boolean checkUp(Color color, Pair<Integer, Integer> pos) {
        return true;
    }

    private boolean checkUpperRightDiagonal(Color color, Pair<Integer, Integer> pos) {
        return true;
    }

    private boolean checkRight(Color color, Pair<Integer, Integer> pos) {
        return true;
    }

    private boolean checkLowerRightDiagonal(Color color, Pair<Integer, Integer> pos) {
        return true;
    }

    private boolean checkDown(Color color, Pair<Integer, Integer> pos) {
        return true;
    }

    private boolean checkLowerLeftDiagonal(Color color, Pair<Integer, Integer> pos) {
        return true;
    }

    private void CastlingSetter(byte castle) {
        if((WHITE_KING_CASTLE_MASK & castle) != 0b0) {
            this.whiteKingSideCastle = true;
        }
        if((WHITE_QUEEN_CASTLE_MASK & castle) != 0b0) {
            this.whiteQueenSideCastle = true;
        }
        if((BLACK_KING_CASTLE_MASK & castle) != 0b0) {
            this.blackKingSideCastle = true;
        }
        if((BLACK_QUEEN_CASTLE_MASK & castle) != 0b0) {
            this.blackQueenSideCastle = true;
        }
    }

    private void KingPosSetter() {
        for(int i = 0; i < chessBoard.length; i++) {
            for(int j = 0; j < chessBoard[0].length; j++) {
                Piece piece = chessBoard[i][j];
                if(piece != null) {
                    if(piece.getName() == KING) {
                        if(piece.getColor() == WHITE) {
                            whiteKingPos.add(new Pair<>(i, j));
                        } else {
                            blackKingPos.add(new Pair<>(i, j));
                        }
                    }
                }
            }
        }
    }

    @Override
    public Piece getPositionStatus(int x, int y) {
        return chessBoard[x][y];
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
