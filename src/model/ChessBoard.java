package model;

import javafx.util.Pair;
import model.enums.*;
import model.pieces.*;

import java.util.List;

import static model.enums.Color.*;
import static model.enums.PieceName.*;

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
    private final IThreefoldRepetition threefoldRep;

//    private final List<Pair<Integer, Integer>> blackKingPos;
//    private final List<Pair<Integer, Integer>> whiteKingPos;
    private Pair<Integer, Integer> blackKingPos;
    private Pair<Integer, Integer> whiteKingPos;

    private static final Pair<String, String> INVALID_FEN = new Pair<>("", "");
    private static final byte INVALID_CASTLE = 0b11111;
    private static final byte WHITE_KING_CASTLE_MASK = 0b1000;
    private static final byte WHITE_QUEEN_CASTLE_MASK = 0b0100;
    private static final byte BLACK_KING_CASTLE_MASK = 0b0010;
    private static final byte BLACK_QUEEN_CASTLE_MASK = 0b0001;
    private static final Pair<Integer, Integer> INVALID_TARGET = new Pair<>(-3,-3);
    private static final Pair<Integer, Integer> NO_TARGET = new Pair<>(-2,-2);
    private static final Pair<Integer, Integer> NO_CHECKS = new Pair<>(-4,-4);
    private static final int EMPTY_SQUARE = -1;
    private static final int NOT_IN_CHECK = -2;
    private static final int IN_CHECK = -3;

    public ChessBoard() {
        chessBoard = new Piece[8][8];
//        blackKingPos = new ArrayList<>();
//        whiteKingPos = new ArrayList<>();
        threefoldRep = new SimpleThreefoldRepetition();
    }

    @Override
    public boolean FENSetup(String fen) {
        Piece[][] tempChessBoard;
        Color activeColor;
        Pair<Integer, Integer> target;
        byte castlingRights;
        int halfmoveClock;
        int fullmoveNumber;
        String[] fields = FENFieldSeparator(fen);
        if(fields == null) {
            return false;
        }

        tempChessBoard = PiecePlacementCheck(fields[0]);
        if(tempChessBoard == null) {
            return false;
        }
        activeColor = ActiveColorCheck(fields[1]);
        if(activeColor == null) {
            return false;
        }
        castlingRights = CastlingRightsCheck(fields[2]);
        if(castlingRights == INVALID_CASTLE) {
            return false;
        }
        target = EnPassantCheck(fields[3]);
        if(target == INVALID_TARGET) {
            return false;
        }
        halfmoveClock = HalfMoveCheck(fields[4]);
        if(halfmoveClock == -1) {
            return false;
        }
        fullmoveNumber = FullMoveCheck(fields[5]);
        if(fullmoveNumber == -1) {
            return false;
        }

        if (IllegalKingCaptureCheck(activeColor, tempChessBoard)) {
            return false;
        }

        this.chessBoard = tempChessBoard;
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
        boolean SingleBlackKing = false;
        boolean SingleWhiteKing = false;
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
                if(type.matches("k")) {
                    if(!SingleBlackKing) {
                        SingleBlackKing = true;
                    } else {
                        return null;
                    }
                } else if(type.matches("K")) {
                    if(!SingleWhiteKing) {
                        SingleWhiteKing = true;
                    } else {
                        return null;
                    }
                } else if((line==0 || line==7)&&type.matches("p|P")) {
                    return null;
                }

                board[line][square] = createPiece(type);
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

        if(line == 7 && index == fen.length() && SingleBlackKing && SingleWhiteKing) {
            return board;
        }

        return null;
    }

    private Piece createPiece(String type) {
        switch(type) {
            case "p":
                return new Pawn(BLACK);
            case "P":
                return new Pawn(WHITE);
            case "r":
                return new Rook(BLACK);
            case "R":
                return new Rook(WHITE);
            case "n":
                return new Knight(BLACK);
            case "N":
                return new Knight(WHITE);
            case "b":
                return new Bishop(BLACK);
            case "B":
                return new Bishop(WHITE);
            case "q":
                return new Queen(BLACK);
            case "Q":
                return new Queen(WHITE);
            case "k":
                return new King(BLACK);
            case "K":
                return new King(WHITE);
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
                    if(piece.getName() == KING && piece.getColor() != activeColor) {
                        if(InCheck(activeColor, new Pair<>(j, i), board) != NO_CHECKS) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Pair<Integer, Integer> InCheck(Color color, Pair<Integer, Integer> pos, Piece[][] board) {
        Pair<Integer, Integer> check;
        for(Direction dir : Direction.values()) {
            check = checkDir(color, pos, dir.dir, board);
            if(check != NO_CHECKS) {
                return check;
            }
        }
        check = checkKnightChecks(color, pos, board);
        if(check != NO_CHECKS) {
            return check;
        }
        check = checkKingChecks(color, pos, board);
        if(check != NO_CHECKS) {
            return check;
        }
        return checkPawnChecks(color, pos, board);
    }

    private Pair<Integer, Integer> checkDir(Color attackColor, Pair<Integer, Integer> pos, Pair<Integer, Integer> dir, Piece[][] board) {
        int x = pos.getKey();
        int y = pos.getValue();
        int status = 0;
        while(status != NOT_IN_CHECK && status != IN_CHECK) {
            x += dir.getKey();
            y += dir.getValue();
            if(dir.getKey() == 0 || dir.getValue() == 0) {
                status = hasXorQueen(attackColor, new Pair<>(x, y), ROOK, board);
            } else {
                status = hasXorQueen(attackColor, new Pair<>(x, y), BISHOP, board);
            }
        }

        if(status == NOT_IN_CHECK) {
            return NO_CHECKS;
        }

        return new Pair<>(x, y);
    }

    private Pair<Integer, Integer> checkKnightChecks(Color attackColor, Pair<Integer, Integer> pos, Piece[][] board) {
        int x = pos.getKey();
        int y = pos.getValue();

        for(KnightDirections dir : KnightDirections.values()) {
            if(hasKnight(attackColor, new Pair<>(x + dir.dir.getKey(), y + dir.dir.getValue()), board)) {
                return new Pair<>(x + dir.dir.getKey(), y + dir.dir.getValue());
            }
        }

        return NO_CHECKS;
    }

    private Pair<Integer, Integer> checkKingChecks(Color attackColor, Pair<Integer, Integer> pos, Piece[][] board) {
        int x = pos.getKey();
        int y = pos.getValue();

        for(Direction dir : Direction.values()) {
            if(hasKing(attackColor, new Pair<>(x + dir.dir.getKey(), y + dir.dir.getValue()), board)) {
                return new Pair<>(x + dir.dir.getKey(), y + dir.dir.getValue());
            }
        }
        return NO_CHECKS;
    }

    private Pair<Integer, Integer> checkPawnChecks(Color attackColor, Pair<Integer, Integer> pos, Piece[][] board) {
        return attackColor == WHITE ? checkWhitePawnChecks(attackColor, pos, board) : checkBlackPawnChecks(attackColor, pos, board);
    }

    private Pair<Integer, Integer> checkWhitePawnChecks(Color attackColor, Pair<Integer, Integer> pos, Piece[][] board) {
        int x = pos.getKey();
        int y = pos.getValue();
        if(hasPawn(attackColor, new Pair<>(x - 1, y + 1), board)) {
            return new Pair<>(x - 1, y + 1);
        } else if(hasPawn(attackColor, new Pair<>(x + 1, y + 1), board)) {
            return new Pair<>(x + 1, y + 1);
        }
        return NO_CHECKS;
    }

    private Pair<Integer, Integer> checkBlackPawnChecks(Color attackColor, Pair<Integer, Integer> pos, Piece[][] board) {
        int x = pos.getKey();
        int y = pos.getValue();
        if(hasPawn(attackColor, new Pair<>(x - 1, y - 1), board)) {
            return new Pair<>(x - 1, y - 1);
        } else if(hasPawn(attackColor, new Pair<>(x + 1, y - 1), board)) {
            return new Pair<>(x + 1, y - 1);
        }
        return NO_CHECKS;
    }

    private boolean hasKnight(Color attackColor, Pair<Integer, Integer> pos, Piece[][] board) {
        if(pos.getKey() < 0 || pos.getKey() > 7 || pos.getValue() < 0 || pos.getValue() > 7) {
            return false;
        }
            Piece piece = getPositionStatus(pos.getKey(), pos.getValue(), board);
            return piece != null && (piece.getName() == KNIGHT && piece.getColor() == attackColor);
    }

    private int hasXorQueen(Color attackColor, Pair<Integer, Integer> pos, PieceName x, Piece[][] board) {
        if(pos.getKey() < 0 || pos.getKey() > 7 || pos.getValue() < 0 || pos.getValue() > 7) {
            return NOT_IN_CHECK;
        }
        Piece piece = getPositionStatus(pos.getKey(), pos.getValue(), board);
        if(piece == null) {
            return EMPTY_SQUARE;
        }
        if(piece.getColor() == attackColor && (piece.getName() == x || piece.getName() == QUEEN)) {
            return IN_CHECK;
        }
        return NOT_IN_CHECK;
    }

    private boolean hasPawn(Color attackColor, Pair<Integer, Integer> pos,  Piece[][] board) {
        if(pos.getKey() < 0 || pos.getKey() > 7 || pos.getValue() < 0 || pos.getValue() > 7) {
            return false;
        }
        Piece piece = getPositionStatus(pos.getKey(), pos.getValue(), board);
        return piece != null && (piece.getName() == PAWN && piece.getColor() == attackColor);
    }

    private boolean hasKing(Color attackColor, Pair<Integer, Integer> pos, Piece[][] board) {
        if(pos.getKey() < 0 || pos.getKey() > 7 || pos.getValue() < 0 || pos.getValue() > 7) {
            return false;
        }
        Piece piece = getPositionStatus(pos.getKey(), pos.getValue(), board);
        return piece != null && (piece.getName() == KING && piece.getColor() == attackColor);
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
                AddKing(chessBoard[i][j], j, i);
            }
        }
    }

    private void AddKing(Piece piece, int x, int y) {
        if(piece != null) {
            if(piece.getName() == KING) {
                if(piece.getColor() == WHITE) {
//                    whiteKingPos.add(new Pair<>(x, y));
                    this.whiteKingPos = new Pair<>(x, y);
                } else {
//                    blackKingPos.add(new Pair<>(x, y));
                    this.blackKingPos = new Pair<>(x, y);
                }
            }
        }
    }

    @Override
    public Piece getPositionStatus(int x, int y) {
        return chessBoard[y][x];
    }

    private Piece getPositionStatus(int x, int y, Piece[][] board) {
        return board[y][x];
    }

    @Override
    public CheckStatus FindCheckStatus() {
//        List<Pair<Integer, Integer>> kingPos = activeColor == WHITE ? whiteKingPos : blackKingPos;
//        if(kingPos.size() == 1) {
//            return FindCheckSingle(kingPos.get(0));
//        }
//        return FindCheckMulti(kingPos);
        return FindCheckSingle(activeColor == WHITE ? whiteKingPos : blackKingPos, activeColor == WHITE ? BLACK : WHITE);
    }

    private CheckStatus FindCheckSingle(Pair<Integer, Integer> pos, Color attackColor) {
        if(InCheck(attackColor, pos, this.chessBoard) != NO_CHECKS) {
            if(HasKingMoves(pos, attackColor) || CanBlockOrCaptureCheckingPiece(pos, attackColor)) {
                return CheckStatus.CHECK;
            }
            return CheckStatus.CHECKMATE;
        }
        return CheckStatus.NOT_IN_CHECK;
    }

    private CheckStatus FindCheckMulti(List<Pair<Integer, Integer>> kingPos) {
        return CheckStatus.NOT_IN_CHECK;
    }

    private boolean HasKingMoves(Pair<Integer, Integer> pos, Color attackColor) {
        int x = pos.getKey();
        int y = pos.getValue();

        return (x > 0 && InCheck(attackColor, new Pair<>(x - 1, y), Move(x, y, x - 1, y)) == NO_CHECKS && getPositionStatus(x - 1, y) == null) ||
                (x > 0 && y > 0 && InCheck(attackColor, new Pair<>(x - 1, y - 1), Move(x, y, x - 1, y - 1)) == NO_CHECKS && getPositionStatus(x - 1, y - 1) == null) ||
                (y > 0 && InCheck(attackColor, new Pair<>(x, y - 1), Move(x, y, x, y - 1)) == NO_CHECKS && getPositionStatus(x, y - 1) == null) ||
                (x < 7 && y > 0 && InCheck(attackColor, new Pair<>(x + 1, y - 1), Move(x, y, x + 1, y - 1)) == NO_CHECKS && getPositionStatus(x + 1, y - 1) == null) ||
                (x < 7 && InCheck(attackColor, new Pair<>(x + 1, y), Move(x, y, x + 1, y)) == NO_CHECKS && getPositionStatus(x + 1, y) == null) ||
                (x < 7 && y < 7 && InCheck(attackColor, new Pair<>(x + 1, y + 1), Move(x, y, x + 1, y + 1)) == NO_CHECKS && getPositionStatus(x + 1, y + 1) == null) ||
                (y < 7 && InCheck(attackColor, new Pair<>(x, y + 1), Move(x, y, x, y + 1)) == NO_CHECKS && getPositionStatus(x, y + 1) == null) ||
                (x > 0 && y < 7 && InCheck(attackColor, new Pair<>(x - 1, y + 1), Move(x, y, x - 1, y + 1)) == NO_CHECKS && getPositionStatus(x - 1, y + 1) == null);
    }

    private Piece[][] Move(int initX, int initY, int endX, int endY) {
        Piece[][] board = new Piece[8][8];
        CopyBoard(this.chessBoard, board);
        Piece temp = getPositionStatus(initX, initY, board);
        board[initY][initX] = null;
        board[endY][endX] = temp;
        return board;
    }

    private void CopyBoard(Piece[][] src, Piece[][] dst) {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                dst[i][j] = src[i][j];
            }
        }
    }

    private boolean CanBlockOrCaptureCheckingPiece(Pair<Integer, Integer> pos, Color attackColor) {
        boolean canBlock = true;
        Pair<Integer, Integer> checkPos;
        for(Direction dir : Direction.values()) {
            checkPos = checkDir(attackColor, pos, dir.dir, this.chessBoard);
            if((checkPos != NO_CHECKS)) {
                if((FindValidBlockMoves(attackColor, pos, dir.dir, checkPos) || FindValidCaptureMoves(attackColor, pos, checkPos))) {
                    if(!canBlock) {
                            return false;
                    }
                    canBlock = false;
                } else {
                        return false;
                    }
                }
            }



        for(KnightDirections knightdir : KnightDirections.values()) {
            if (hasKnight(attackColor, new Pair<>(pos.getKey() + knightdir.dir.getKey(), pos.getValue() + knightdir.dir.getValue()), this.chessBoard)) {
                if(FindValidCaptureMoves(attackColor, pos, new Pair<>(pos.getKey() + knightdir.dir.getKey(), pos.getValue() + knightdir.dir.getValue()))) {
                    if (!canBlock) {
                        return false;
                    }
                    canBlock = false;
                } else {
                    return false;
                }
            }
        }

        checkPos = checkPawnChecks(attackColor, pos, this.chessBoard);
        if(checkPos != NO_CHECKS) {
            if(FindValidCaptureMoves(attackColor, pos, checkPos)) {
                if(!canBlock) {
                    return false;
                }
                canBlock = false;
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean FindValidBlockMoves(Color attackColor, Pair<Integer, Integer> pos, Pair<Integer, Integer> dir, Pair<Integer, Integer> checkPos) {
        int x = pos.getKey() + dir.getKey();
        int y = pos.getValue() + dir.getValue();

        while(x != checkPos.getKey() && y != checkPos.getValue()) {
            Pair<Integer, Integer> check;
            for(Direction direct : Direction.values()) {
                check = checkDir(activeColor, new Pair<>(x, y), direct.dir, this.chessBoard);
                if(check != NO_CHECKS) {
                    Piece[][] board = Move(check.getKey(), check.getValue(), x, y);
                    if(InCheck(attackColor, pos, board) == NO_CHECKS) {
                        return true;
                    }
                }
            }

            for(KnightDirections knightdir : KnightDirections.values()) {
                if(hasKnight(activeColor, new Pair<>(x + knightdir.dir.getKey(), y + knightdir.dir.getValue()), this.chessBoard)) {
                    Piece[][] board = Move(x + knightdir.dir.getKey(), y + knightdir.dir.getValue(), x, y);
                    if(InCheck(attackColor, pos, board) == NO_CHECKS) {
                        return true;
                    }
                }
            }
            x += dir.getKey();
            y += dir.getValue();
        }
        return false;
    }

    private boolean FindValidCaptureMoves(Color attackColor, Pair<Integer, Integer> pos, Pair<Integer, Integer> checkPos) {
        Pair<Integer, Integer> check;
        for(Direction direct : Direction.values()) {
            check = checkDir(activeColor, new Pair<>(checkPos.getKey(), checkPos.getValue()), direct.dir, this.chessBoard);
            if(check != NO_CHECKS) {
                Piece[][] board = Move(check.getKey(), check.getValue(), checkPos.getKey(), checkPos.getValue());
                if(InCheck(attackColor, pos, board) == NO_CHECKS) {
                    return true;
                }
            }
        }

        for(KnightDirections knightdir : KnightDirections.values()) {
            if(hasKnight(activeColor, new Pair<>(checkPos.getKey() + knightdir.dir.getKey(), checkPos.getValue() + knightdir.dir.getValue()), this.chessBoard)) {
                Piece[][] board = Move(checkPos.getKey() + knightdir.dir.getKey(), checkPos.getValue() + knightdir.dir.getValue(), checkPos.getKey(), checkPos.getValue());
                if(InCheck(attackColor, pos, board) == NO_CHECKS) {
                    return true;
                }
            }
        }

        check = checkPawnChecks(activeColor, new Pair<>(checkPos.getKey(), checkPos.getValue()), this.chessBoard);
        if(check != NO_CHECKS) {
            Piece[][] board = Move(check.getKey(), check.getValue(), checkPos.getKey(), checkPos.getValue());
            if(InCheck(attackColor, pos, board) == NO_CHECKS) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean MovePiece(int initX, int initY, int endX, int endY) {
        return false;
    }

    @Override
    public List<Pair<Integer, Integer>> FindValidMoves(int x, int y) {
        return null;
    }
}
