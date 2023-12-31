package model;

import javafx.util.Pair;
import model.enums.*;
import model.pieces.*;

import java.util.ArrayList;
import java.util.List;

import static model.enums.Castling.*;
import static model.enums.CheckStatus.*;
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

    private final List<Piece> whiteCapturedPieces;
    private final List<Piece> blackCapturedPieces;

    private static final Pair<String, String> INVALID_FEN = new Pair<>("", "");
    private static final byte INVALID_CASTLE = 0b11111;
    private static final byte WHITE_KING_CASTLE_MASK = 0b1000;
    private static final byte WHITE_QUEEN_CASTLE_MASK = 0b0100;
    private static final byte BLACK_KING_CASTLE_MASK = 0b0010;
    private static final byte BLACK_QUEEN_CASTLE_MASK = 0b0001;
    private static final byte CASTLE_AVAIL = 0b1;
    private static final Pair<Integer, Integer> INVALID_TARGET = new Pair<>(-3,-3);
    private static final Pair<Integer, Integer> NO_TARGET = new Pair<>(-2,-2);
    private static final Pair<Integer, Integer> NO_CHECKS = new Pair<>(-4,-4);

    public ChessBoard() {
        chessBoard = new Piece[8][8];
//        blackKingPos = new ArrayList<>();
//        whiteKingPos = new ArrayList<>();
        whiteCapturedPieces = new ArrayList<>();
        blackCapturedPieces = new ArrayList<>();

        threefoldRep = new SimpleThreefoldRepetition();
        FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
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
        target = EnPassantCheck(fields[3], activeColor);
        if(target == INVALID_TARGET) {
            return false;
        }
        if(target != NO_TARGET) {
            if(activeColor == WHITE && !isEqualPiece(BLACK, PAWN, tempChessBoard[target.getValue() + 1][target.getKey()])) {
                return false;
            } else if(activeColor == BLACK && !isEqualPiece(WHITE, PAWN, tempChessBoard[target.getValue() - 1][target.getKey()])) {
                return false;
            }
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
            } else if(type.matches("[rnbqkpRNBQKP]")) {
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
                } else if((line==0 || line==7)&&type.matches("[pP]")) {
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
        switch (type) {
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
                    if(((WHITE_KING_CASTLE_MASK & castle) >> 3) == CASTLE_AVAIL) {
                        return INVALID_CASTLE;
                    }
                    castle += 0b1000;
                    break;
                case "Q":
                    if(((WHITE_QUEEN_CASTLE_MASK & castle) >> 2) == CASTLE_AVAIL) {
                        return INVALID_CASTLE;
                    }
                    castle += 0b0100;
                    break;
                case "k":
                    if(((BLACK_KING_CASTLE_MASK & castle) >> 1) == CASTLE_AVAIL) {
                        return INVALID_CASTLE;
                    }
                    castle += 0b0010;
                    break;
                case "q":
                    if((BLACK_QUEEN_CASTLE_MASK & castle) == CASTLE_AVAIL) {
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

    private Pair<Integer, Integer> EnPassantCheck(String fen, Color activeColor) {
        if(fen.equals("-")) {
            return NO_TARGET;
        } else if(fen.length() != 2 || Character.isUpperCase(fen.charAt(0))) {
            return INVALID_TARGET;
        }

        try {
            int col = Character.getNumericValue(fen.charAt(0));
            int row = Character.getNumericValue(fen.charAt(1));
            if((activeColor == BLACK && row != 3) || (activeColor == WHITE && row != 6) || col < Character.getNumericValue('a') ||
                    col > Character.getNumericValue('h')) {
                return INVALID_TARGET;
            }
            return new Pair<>(ChessColNotationToArrayPos(col),ChessRowNotationToArrayPos(row));
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
        int x = pos.getKey() + dir.getKey();
        int y = pos.getValue() + dir.getValue();

        while(x > -1 && x < 8 && y > - 1 && y < 8) {
            Piece piece = getPositionStatus(x, y, board);
            if(dir.getKey() == 0 || dir.getValue() == 0) {
                if(isEqualPiece(attackColor, ROOK, piece) || isEqualPiece(attackColor, QUEEN, piece)) {
                    return new Pair<>(x, y);
                }
            } else {
                if(isEqualPiece(attackColor, BISHOP, piece) || isEqualPiece(attackColor, QUEEN, piece)) {
                    return new Pair<>(x, y);
                }
            }
            if(piece != null) {
                return NO_CHECKS;
            }
            x += dir.getKey();
            y += dir.getValue();
        }

        return NO_CHECKS;
    }

    private Pair<Integer, Integer> checkKnightChecks(Color attackColor, Pair<Integer, Integer> pos, Piece[][] board) {
        int x = pos.getKey();
        int y = pos.getValue();
        Pair<Integer, Integer> checkPos;

        for(KnightDirections dir : KnightDirections.values()) {
            checkPos = new Pair<>(x + dir.dir.getKey(), y + dir.dir.getValue());
            if(containsPiece(attackColor, KNIGHT, checkPos.getKey(), checkPos.getValue(), board)) {
                return checkPos;
            }
        }
        return NO_CHECKS;
    }

    private Pair<Integer, Integer> checkKingChecks(Color attackColor, Pair<Integer, Integer> pos, Piece[][] board) {
        int x = pos.getKey();
        int y = pos.getValue();
        Pair<Integer, Integer> checkPos;

        for(Direction dir : Direction.values()) {
            checkPos = new Pair<>(x + dir.dir.getKey(), y + dir.dir.getValue());

            if(containsPiece(attackColor, KING, checkPos.getKey(), checkPos.getValue(), board)) {
                return checkPos;
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

        if(containsPiece(attackColor, PAWN, x - 1, y + 1, board)) {
            return new Pair<>(x - 1, y + 1);
        } else if(containsPiece(attackColor, PAWN, x + 1, y + 1, board)) {
            return new Pair<>(x + 1, y + 1);
        }

        return NO_CHECKS;
    }

    private Pair<Integer, Integer> checkBlackPawnChecks(Color attackColor, Pair<Integer, Integer> pos, Piece[][] board) {
        int x = pos.getKey();
        int y = pos.getValue();

        if(containsPiece(attackColor, PAWN, x - 1, y - 1, board)) {
            return new Pair<>(x - 1, y - 1);
        } else if(containsPiece(attackColor, PAWN, x + 1, y - 1, board)) {
            return new Pair<>(x + 1, y - 1);
        }

        return NO_CHECKS;
    }

    private boolean isEqualPiece(Color color, PieceName name, Piece piece) {
        return piece != null && piece.getName() == name && piece.getColor() == color;
    }

    private boolean containsPiece(Color attackColor, PieceName name, int x, int y, Piece[][] board) {
        if(x < 0 || y < 0 || x > 7 || y > 7) {
            return false;
        }
        Piece piece = getPositionStatus(x, y, board);
        return isEqualPiece(attackColor, name, piece);
    }

    private void CastlingSetter(byte castle) {
        this.whiteKingSideCastle = ((WHITE_KING_CASTLE_MASK & castle) >> 3) == CASTLE_AVAIL;
        this.whiteQueenSideCastle = ((WHITE_QUEEN_CASTLE_MASK & castle) >> 2) == CASTLE_AVAIL;
        this.blackKingSideCastle = ((BLACK_KING_CASTLE_MASK & castle) >> 1) == CASTLE_AVAIL;
        this.blackQueenSideCastle = (BLACK_QUEEN_CASTLE_MASK & castle) == CASTLE_AVAIL;
    }

    private void KingPosSetter() {
        for(int i = 0; i < chessBoard.length; i++) {
            for(int j = 0; j < chessBoard[0].length; j++) {
                AddKing(chessBoard[i][j], j, i);
            }
        }
    }

    private void AddKing(Piece piece, int x, int y) {
        if (piece != null && piece.getName() == KING) {
            if (piece.getColor() == WHITE) {
//              whiteKingPos.add(new Pair<>(x, y));
                this.whiteKingPos = new Pair<>(x, y);
            } else {
//              blackKingPos.add(new Pair<>(x, y));
                this.blackKingPos = new Pair<>(x, y);
            }
        }
    }

    @Override
    public Piece getPositionStatus(int x, int y) {
        if(x < 0 || x > 7 || y < 0 || y > 7) {
            return null;
        }
        return chessBoard[y][x];
    }

    private Piece getPositionStatus(int x, int y, Piece[][] board) {
        if(x < 0 || x > 7 || y < 0 || y > 7) {
            return null;
        }
        return board[y][x];
    }

    @Override
    public CheckStatus FindCheckStatus() {
        CheckStatus status;
        if(halfmoveClock >= 100 || threefoldRep.isThreefoldRepetition()) {
            return DRAW;
        }
//        List<Pair<Integer, Integer>> kingPos = activeColor == WHITE ? whiteKingPos : blackKingPos;
//        if(kingPos.size() == 1) {
//            return FindCheckSingle(kingPos.get(0));
//        }
//        return FindCheckMulti(kingPos);
        status = FindCheckSingle(activeColor == WHITE ? whiteKingPos : blackKingPos, activeColor == WHITE ? BLACK : WHITE);

        if(status == NOT_IN_CHECK && IsStalemate()) {
            return STALEMATE;
        }

        return status;
    }

    private boolean IsStalemate() {
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                Piece piece = getPositionStatus(x, y);
                if(piece != null && piece.getColor() == activeColor && !FindValidMoves(x,y).isEmpty()) {
                    return InsufficientPiecesForCheckmate();
                }
            }
        }
        return true;
    }

    private boolean InsufficientPiecesForCheckmate() {
        List<Piece> pieces = GetActivePieces();
        int blackBishopKnightCount = 0;
        int whiteBishopKnightCount = 0;

        for(Piece piece : pieces) {
            if(piece.getName() == QUEEN || piece.getName() == ROOK) {
                return false;
            }
            if(piece.getName() == BISHOP || piece.getName() == KNIGHT) {
                if(piece.getColor() == WHITE) {
                    whiteBishopKnightCount++;
                } else {
                    blackBishopKnightCount++;
                }
            }
        }


        return whiteBishopKnightCount < 2 && blackBishopKnightCount == 0 ||
                whiteBishopKnightCount == 0 && blackBishopKnightCount < 2;
    }

    private List<Piece> GetActivePieces() {
        List<Piece> pieces = new ArrayList<>();
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                Piece piece = getPositionStatus(x, y);
                if(piece != null) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    private CheckStatus FindCheckSingle(Pair<Integer, Integer> pos, Color attackColor) {
        if(InCheck(attackColor, pos, this.chessBoard) != NO_CHECKS) {
            if(HasKingMoves(pos, attackColor) || CanBlockOrCaptureCheckingPiece(pos, attackColor)) {
                return CheckStatus.CHECK;
            }
            return CheckStatus.CHECKMATE;
        }
        return NOT_IN_CHECK;
    }

//    private CheckStatus FindCheckMulti(List<Pair<Integer, Integer>> kingPos) {
//        return CheckStatus.NOT_IN_CHECK;
//    }

    private boolean HasKingMoves(Pair<Integer, Integer> pos, Color attackColor) {
        int x = pos.getKey();
        int y = pos.getValue();

        for(Direction dir: Direction.values()) {
            int endX = x + dir.dir.getKey();
            int endY = y + dir.dir.getValue();
            if(IsValidMove(x, y, endX, endY, attackColor, new Pair<>(endX, endY))) {
                return true;
            }
        }
        return false;
    }

    private Piece[][] MoveCheck(int initX, int initY, int endX, int endY) {
        Piece[][] board = new Piece[8][8];
        CopyBoard(this.chessBoard, board);
        Piece temp = getPositionStatus(initX, initY, board);
        if(temp != null && temp.getName() == PAWN && enPassantPos.getKey() == endX && enPassantPos.getValue() == endY) {
            if(activeColor == WHITE) {
                board[endY + 1][endX] = null;
            } else {
                board[endY - 1][endX] = null;
            }
        }
        board[initY][initX] = null;
        board[endY][endX] = temp;
        return board;
    }

    private boolean IsValidMove(int initX, int initY, int endX, int endY, Color attackColor, Pair<Integer, Integer> pos) {
        if(initX < 0 || initY < 0 || endX < 0 || endY < 0 || initX > 7 || initY > 7 || endX > 7 || endY > 7) {
            return false;
        }
        Piece[][] board = MoveCheck(initX, initY, endX, endY);
        Piece piece = getPositionStatus(endX, endY);
        if(piece != null && piece.getColor() != attackColor) {
            return false;
        }
        return InCheck(attackColor, pos, board) == NO_CHECKS;
    }

    private void CopyBoard(Piece[][] src, Piece[][] dst) {
        for(int i = 0; i < 8; i++) {
            System.arraycopy(src[i], 0, dst[i], 0, 8);
        }
    }

    private boolean CanBlockOrCaptureCheckingPiece(Pair<Integer, Integer> pos, Color attackColor) {
        boolean canBlock = true;
        Pair<Integer, Integer> checkPos;
        for(Direction dir : Direction.values()) {
            checkPos = checkDir(attackColor, pos, dir.dir, this.chessBoard);
            if((checkPos != NO_CHECKS)) {
                if((FindValidBlockMoves(attackColor, pos, dir.dir, checkPos) ||
                        FindValidCaptureMoves(attackColor, pos, checkPos))) {
                    if(!canBlock) {
                            return false;
                    }
                    canBlock = false;
                } else {
                        return false;
                    }
                }
            }

        for(KnightDirections knightDir : KnightDirections.values()) {
            int knightPosX = pos.getKey() + knightDir.dir.getKey();
            int knightPosY = pos.getValue() + knightDir.dir.getValue();

            if(containsPiece(attackColor, KNIGHT, knightPosX, knightPosY, this.chessBoard)) {
                if(FindValidCaptureMoves(attackColor, pos, new Pair<>(knightPosX, knightPosY))) {
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
        if(checkPos != NO_CHECKS && !(FindValidCaptureMoves(attackColor, pos, checkPos) && canBlock)) {
            return false;
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
                    if(IsValidMove(check.getKey(), check.getValue(), x, y, attackColor, pos)) {
                        return true;
                    }
                }
            }

                for(KnightDirections knightDir : KnightDirections.values()) {
                    int knightPosX = checkPos.getKey() + knightDir.dir.getKey();
                    int knightPosY = checkPos.getValue() + knightDir.dir.getValue();
                    if (containsPiece(attackColor, KNIGHT, knightPosX, knightPosY, this.chessBoard) &&
                            IsValidMove(knightPosX, knightPosY, x, y, attackColor, pos)) {
                        return true;
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
            check = checkDir(activeColor, checkPos, direct.dir, this.chessBoard);
            if(check != NO_CHECKS && getPositionStatus(check.getKey(), check.getValue(), this.chessBoard).getName() != KING) {
                if(IsValidMove(check.getKey(), check.getValue(), checkPos.getKey(), checkPos.getValue(), attackColor, pos)) {
                    return true;
                }
            }
        }

        for(KnightDirections knightDir : KnightDirections.values()) {
            int knightPosX = checkPos.getKey() + knightDir.dir.getKey();
            int knightPosY = checkPos.getValue() + knightDir.dir.getValue();
            if(containsPiece(activeColor, KNIGHT, knightPosX, knightPosY, this.chessBoard) &&
                    IsValidMove(knightPosX, knightPosY, checkPos.getKey(), checkPos.getValue(), attackColor, pos)) {
                return true;
            }
        }

        check = checkPawnChecks(activeColor, checkPos, this.chessBoard);
        if(check != NO_CHECKS) {
            return IsValidMove(check.getKey(), check.getValue(), checkPos.getKey(), checkPos.getValue(), attackColor, pos);
        }

        return false;
    }

    @Override
    public boolean MovePiece(int initX, int initY, int endX, int endY) {
        return MovePiece(initX, initY, endX, endY, null);
    }

    @Override
    public boolean MovePiece(int initX, int initY, int endX, int endY, PieceName promoName) {
        Piece piece = getPositionStatus(initX, initY);
        CheckStatus status = FindCheckStatus();
        if(piece == null || piece.getColor() != activeColor ||
                status == CHECKMATE || status == STALEMATE || status == DRAW ||
                (initX == endX && initY == endY)) {
            return false;
        }

        MoveClassification validMove;
        switch(piece.getName()) {
            case KING:
                validMove = KingMove(initX, initY, endX, endY, status);
                break;
            case QUEEN:
                validMove = QueenMove(initX, initY, endX, endY);
                break;
            case BISHOP:
                validMove = BishopMove(initX, initY, endX, endY);
                break;
            case KNIGHT:
                validMove = KnightMove(initX, initY, endX, endY);
                break;
            case ROOK:
                validMove = RookMove(initX, initY, endX, endY);
                break;
            case PAWN:
                if(activeColor == WHITE) {
                    validMove = WhitePawnMove(initX, initY, endX, endY);
                } else {
                    validMove = BlackPawnMove(initX, initY, endX, endY);
                }
                break;
            default:
                return false;
        }
        if(!validMove.validMove) {
            return false;
        }
        Move(initX, initY, endX, endY);
        this.enPassantPos = validMove.enPassantTarget;

        if(piece.getName() == PAWN && ((activeColor == WHITE && endY == 0) || (activeColor == BLACK && endY == 7))) {
            PawnPromotion(endX, endY, promoName);
        }

        if(validMove.enPassantCapture != NO_TARGET) {
            int x = validMove.enPassantCapture.getKey();
            int y = validMove.enPassantCapture.getValue();
            this.chessBoard[y][x] = null;
        }

        if(validMove.pieceCaptured != null) {
            if(activeColor == WHITE) {
                whiteCapturedPieces.add(validMove.pieceCaptured);
            } else {
                blackCapturedPieces.add(validMove.pieceCaptured);
            }
        } else if(validMove.castle != NO_CASTLE) {
            InvalidateCastling(validMove.castle, endX, endY);
        }

        if(piece.getName() == KING) {
            if(activeColor == WHITE) {
                whiteKingPos = new Pair<>(endX, endY);
            } else {
                blackKingPos = new Pair<>(endX, endY);
            }
        }
        if(piece.getName() != PAWN || validMove.pieceCaptured == null) {
            halfmoveClock++;
        } else {
            halfmoveClock = 0;
        }

        if(this.activeColor == BLACK) {
            fullmoveNumber++;
        }

        threefoldRep.LogMove(piece.getName(), initX ,initY, endX, endY);
        this.activeColor = getAttackColor();
        return true;
    }

    private void PawnPromotion(int endX, int endY, PieceName name) {
        if(endY == 0) {
            switch(name) {
                case QUEEN:
                    this.chessBoard[endY][endX] = createPiece("Q");
                    break;
                case ROOK:
                    this.chessBoard[endY][endX] = createPiece("R");
                    break;
                case BISHOP:
                    this.chessBoard[endY][endX] = createPiece("B");
                    break;
                case KNIGHT:
                    this.chessBoard[endY][endX] = createPiece("N");
                    break;
                default:
            }
        } else {
            switch(name) {
                case QUEEN:
                    this.chessBoard[endY][endX] = createPiece("q");
                    break;
                case ROOK:
                    this.chessBoard[endY][endX] = createPiece("r");
                    break;
                case BISHOP:
                    this.chessBoard[endY][endX] = createPiece("b");
                    break;
                case KNIGHT:
                    this.chessBoard[endY][endX] = createPiece("n");
                    break;
                default:
            }
        }
    }

    private MoveClassification KingMove(int initX, int initY, int endX, int endY, CheckStatus status) {
        int validX = Math.abs(initX - endX);
        int validY = Math.abs(initY - endY);
        if((validX == 1 || validX == 0) && (validY == 1 || validY == 0)) {
            if (getPositionStatus(endX, endY) != null && getPositionStatus(endX, endY).getColor() == activeColor) {
                return new MoveClassification(false);
            }

            if(IsValidMove(initX, initY, endX, endY, getAttackColor(), new Pair<>(endX, endY))) {
                return new MoveClassification(getPositionStatus(endX,endY), KING_MOVED);
            }
        } else if(initY == endY && Math.abs(initX - endX) == 2) {
            if((activeColor == WHITE && initX == 4 && initY == 7) ||
                    (activeColor == BLACK && initX == 4 && initY == 0)) {
                if(status == CheckStatus.CHECK) {
                    return new MoveClassification(false);
                }
                return Castling(initX, initY, endX, endY);
            }
        }
        return new MoveClassification(false);
    }

    private MoveClassification Castling(int initX, int initY, int endX, int endY) {
            if(endX == 6 && (activeColor == WHITE ? whiteKingSideCastle : blackKingSideCastle)) {
                if(IsValidMove(initX, initY, endX, endY, getAttackColor(), new Pair<>(endX, endY)) &&
                        IsValidMove(initX, initY, endX - 1, endY, getAttackColor(), new Pair<>(endX - 1, endY))) {
                    return new MoveClassification(activeColor == WHITE ? WHITE_KING_SIDE : BLACK_KING_SIDE);
                }
            } else if(activeColor == WHITE ? whiteQueenSideCastle : blackQueenSideCastle) {
                if (getPositionStatus(endX - 1, endY) != null) {
                    return new MoveClassification(false);
                }

                if(IsValidMove(initX, initY, endX, endY, getAttackColor(), new Pair<>(endX, endY)) &&
                        IsValidMove(initX, initY, endX + 1, endY, getAttackColor(), new Pair<>(endX + 1, endY))) {
                    return new MoveClassification(activeColor == WHITE ? WHITE_QUEEN_SIDE : BLACK_QUEEN_SIDE);
                }
            }
        return new MoveClassification(false);
    }

    private MoveClassification QueenMove(int initX, int initY, int endX, int endY) {
        if(initX == endX || initY == endY) {
            return RookMove(initX, initY, endX, endY);
        } else if(Math.abs(initX - endX) == Math.abs(initY - endY)) {
            return BishopMove(initX, initY, endX, endY);
        }
        return new MoveClassification(false);
    }

    private MoveClassification BishopMove(int initX, int initY, int endX, int endY) {
        if(Math.abs(initX - endX) == Math.abs(initY - endY)) {
            Direction dir = BishopMoveDirection(initX, initY, endX, endY);
            for(int i = 1; i < Math.abs(initX - endX); i++) {
                int x = initX + (dir.dir.getKey()*i);
                int y = initY + (dir.dir.getValue()*i);
                if(getPositionStatus(x, y) != null) {
                    return new MoveClassification(false);
                }
            }
            if (getPositionStatus(endX, endY) != null && getPositionStatus(endX, endY).getColor() == activeColor) {
                return new MoveClassification(false);
            }

            if(IsValidMove(initX, initY, endX, endY, getAttackColor(), getKingPos())) {
                return new MoveClassification(getPositionStatus(endX,endY));
            }
        }
        return new MoveClassification(false);
    }

    private Direction BishopMoveDirection(int initX, int initY, int endX, int endY) {
        if(initX - endX < 0) {
            if(initY - endY < 0) {
                return Direction.DOWN_RIGHT_DIAGONAL;
            }
            return Direction.UP_RIGHT_DIAGONAL;
        } else {
            if(initY - endY < 0) {
                return Direction.DOWN_LEFT_DIAGONAL;
            }
            return Direction.UP_LEFT_DIAGONAL;
        }
    }

    private MoveClassification KnightMove(int initX, int initY, int endX, int endY) {
        if((Math.abs(initX - endX) == 2 && (Math.abs(initY - endY) == 1)) ||
                (Math.abs(initX - endX) == 1 && Math.abs(initY - endY) == 2)) {

            if (getPositionStatus(endX, endY) != null && getPositionStatus(endX, endY).getColor() == activeColor) {
                return new MoveClassification(false);
            }

            if(IsValidMove(initX, initY, endX, endY, getAttackColor(), getKingPos())) {
                return new MoveClassification(getPositionStatus(endX, endY));
            }
        }
        return new MoveClassification(false);
    }

    private MoveClassification RookMove(int initX, int initY, int endX, int endY) {
        if (initX == endX) {
            if (initY < endY) {
                for (int i = initY + 1; i < endY; i++) {
                    if (getPositionStatus(endX, i) != null) {
                        return new MoveClassification(false);
                    }
                }
            } else {
                for (int i = initY - 1; i > endY; i--) {
                    if (getPositionStatus(endX, i) != null) {
                        return new MoveClassification(false);
                    }
                }
            }

        } else if (initY == endY) {
            if (initX < endX) {
                for (int i = initX + 1; i < endX; i++) {
                    if (getPositionStatus(i, endY) != null) {
                        return new MoveClassification(false);
                    }
                }
            } else {
                for (int i = initX - 1; i > endX; i--) {
                    if (getPositionStatus(i, endY) != null) {
                        return new MoveClassification(false);
                    }
                }
            }
        } else {
            return new MoveClassification(false);
        }

        if(getPositionStatus(endX, endY) != null && getPositionStatus(endX, endY).getColor() == activeColor) {
            return new MoveClassification(false);
        }

        if(IsValidMove(initX, initY, endX, endY, getAttackColor(), getKingPos())) {
            if(activeColor == WHITE) {
                if(initX == 0 && initY == 7) {
                    return new MoveClassification(getPositionStatus(endX, endY), QUEEN_ROOK_MOVED);
                } else if(initX == 7 && initY == 7) {
                   return new MoveClassification(getPositionStatus(endX, endY), KING_ROOK_MOVED);
                }
            } else {
                if(initX == 0 && initY == 0) {
                    return new MoveClassification(getPositionStatus(endX, endY), QUEEN_ROOK_MOVED);
                } else if(initX == 7 && initY == 0){
                    return new MoveClassification(getPositionStatus(endX, endY), KING_ROOK_MOVED);
                }
            }
            return new MoveClassification(getPositionStatus(endX, endY));
        }

        return new MoveClassification(false);
    }

    private void InvalidateCastling(Castling castle, int endX, int endY) {
        switch(castle) {

            case BLACK_KING_SIDE:
            case WHITE_KING_SIDE:
                Move(endX + 1, endY, endX - 1, endY);
                if(activeColor == WHITE) {
                    whiteKingSideCastle = false;
                    whiteQueenSideCastle = false;
                } else {
                    blackKingSideCastle = false;
                    blackQueenSideCastle = false;
                }
                break;
            case BLACK_QUEEN_SIDE:
            case WHITE_QUEEN_SIDE:
                Move(endX - 2, endY, endX + 1, endY);
                if(activeColor == WHITE) {
                    whiteKingSideCastle = false;
                    whiteQueenSideCastle = false;
                } else {
                    blackKingSideCastle = false;
                    blackQueenSideCastle = false;
                }
                break;
            case KING_MOVED:
                if(activeColor == WHITE) {
                    whiteKingSideCastle = false;
                    whiteQueenSideCastle = false;
                } else {
                    blackKingSideCastle = false;
                    blackQueenSideCastle = false;
                }
                break;
            case KING_ROOK_MOVED:
                if(activeColor == WHITE) {
                    whiteKingSideCastle = false;
                } else {
                    blackKingSideCastle = false;
                }
                break;
            case QUEEN_ROOK_MOVED:
                if(activeColor == WHITE) {
                    whiteQueenSideCastle = false;
                } else {
                    blackQueenSideCastle = false;
                }
                break;
        }
    }

    private MoveClassification WhitePawnMove(int initX, int initY, int endX, int endY) {
        if(initX == endX) {
            if((initY - 1) == endY) { //check blocks for single move, set no enpassant
                if(getPositionStatus(endX, endY) != null || !IsValidMove(initX, initY, endX, endY, BLACK, whiteKingPos)) {
                    return new MoveClassification(false);
                }
                return new MoveClassification(true);
            } else if(((initY - 2) == endY) && initY == 6) { //check blocks for double move and set enpassant
                if(getPositionStatus(endX, endY + 1) != null || getPositionStatus(endX, endY) != null ||
                        !IsValidMove(initX, initY, endX, endY, BLACK, whiteKingPos)) {
                    return new MoveClassification(false);
                }
                return new MoveClassification(new Pair<>(endX, endY + 1));
            }
        } else if((initY - 1) == endY) {
            if((initX - 1) == endX || (initX + 1) == endX) { //check for valid capture on left or right or enpassant
                if(getPositionStatus(endX, endY) == null) {
                    if(enPassantPos.getKey() == endX && enPassantPos.getValue() == endY &&
                    IsValidMove(initX, initY, endX, endY, BLACK, whiteKingPos)) {
                        return new MoveClassification(new Pair<>(endX, endY + 1), getPositionStatus(endX, endY + 1));
                    }
                } else if(getPositionStatus(endX, endY).getColor() == BLACK && IsValidMove(initX, initY, endX, endY, BLACK, whiteKingPos)) {
                    return new MoveClassification(getPositionStatus(endX, endY));
                }
            }
        }
        return new MoveClassification(false);
    }

    private MoveClassification BlackPawnMove(int initX, int initY, int endX, int endY) {
        if(initX == endX) {
            if((initY + 1) == endY) { //check blocks for single move, set no enpassant, set turn
                if(getPositionStatus(endX, endY) != null || !IsValidMove(initX, initY, endX, endY, WHITE, blackKingPos)) {
                    return new MoveClassification(false);
                }
                return new MoveClassification(true);
            } else if(((initY + 2) == endY) && initY == 1) { //check blocks for double move and set enpassant
                if(getPositionStatus(endX, endY - 1) != null || getPositionStatus(endX, endY) != null ||
                        !IsValidMove(initX, initY, endX, endY, WHITE, blackKingPos)) {
                    return new MoveClassification(false);
                }
                return new MoveClassification(new Pair<>(endX, endY - 1));
            }
        } else if((initY + 1) == endY) {
            if((initX - 1) == endX || (initX + 1) == endX) { //check for valid capture on left or right or enpassant
                if(getPositionStatus(endX, endY) == null) {
                    if(enPassantPos.getKey() == endX && enPassantPos.getValue() == endY && IsValidMove(initX, initY, endX, endY, WHITE, blackKingPos)) {
                        return new MoveClassification(new Pair<>(endX, endY - 1), getPositionStatus(endX, endY - 1));
                    }
                } else if(getPositionStatus(endX, endY).getColor() == WHITE && IsValidMove(initX, initY, endX, endY, WHITE, blackKingPos)) {
                    return new MoveClassification(getPositionStatus(endX, endY));
                }
            }
        }
        return new MoveClassification(false);
    }

    private void Move(int initX, int initY, int endX, int endY) {
        Piece temp = this.chessBoard[initY][initX];
        this.chessBoard[initY][initX] = null;
        this.chessBoard[endY][endX] = temp;
    }

    private Color getAttackColor() {
        return activeColor == WHITE ? BLACK : WHITE;
    }

    private Pair<Integer, Integer> getKingPos() {
        return activeColor == WHITE ? whiteKingPos : blackKingPos;
    }
    @Override
    public List<Pair<Integer, Integer>> FindValidMoves(int x, int y) {
        List<Pair<Integer, Integer>> list = new ArrayList<>();
        Piece piece = getPositionStatus(x, y);
        if(piece == null || piece.getColor() != activeColor) {
            return list;
        }

        switch(piece.getName()) {
            case PAWN:
                return PawnValidMoves(x, y);
            case KING:
                return KingValidMoves(x, y);
            case QUEEN:
                return QueenValidMoves(x, y);
            case BISHOP:
                return BishopValidMoves(x, y);
            case KNIGHT:
                return KnightValidMoves(x, y);
            case ROOK:
                return RookValidMoves(x, y);
            default:
                return list;
        }
    }

    @Override
    public Piece[][] getBoard() {
        return this.chessBoard;
    }

    @Override
    public Color getActiveColor() {
        return this.activeColor;
    }

    @Override
    public int getTurnNumber() {
        return this.fullmoveNumber;
    }

    private List<Pair<Integer, Integer>> PawnValidMoves(int x, int y) {
        List<Pair<Integer, Integer>> list = new ArrayList<>();

        if(activeColor == WHITE) {
            for(int i = -1; i < 2; i++) {
                if(WhitePawnMove(x, y, x + i, y - 1).validMove) {
                    list.add(new Pair<>(x + i, y - 1));
                }
            }
            if(WhitePawnMove(x, y, x, y - 2).validMove) {
                list.add(new Pair<>(x, y - 2));
            }
        } else {
            for(int i = -1; i < 2; i++) {
                if(BlackPawnMove(x, y, x + i, y + 1).validMove) {
                    list.add(new Pair<>(x + i, y + 1));
                }
            }
            if(BlackPawnMove(x, y, x, y + 2).validMove) {
                list.add(new Pair<>(x, y + 2));
            }
        }
        return list;
    }

    private List<Pair<Integer, Integer>> KingValidMoves(int x, int y) {
        List<Pair<Integer, Integer>> list = new ArrayList<>();
        int dx;
        int dy;
        //FindCheckMulti();
        CheckStatus status = FindCheckSingle(new Pair<>(x, y), getAttackColor());
        for(Direction dir: Direction.values()) {
            dx = dir.dir.getKey();
            dy = dir.dir.getValue();
            if(KingMove(x, y, x + dx, y + dy, status).validMove) {
                list.add(new Pair<>(x + dx, y + dy));
            }
        }

        if(status == NOT_IN_CHECK) {
            if(KingMove(x, y, x - 2, y, status).validMove) {
                list.add(new Pair<>(x - 2, y));
            }
            if(KingMove(x, y, x + 2, y, status).validMove) {
                list.add(new Pair<>(x + 2, y));
            }
        }

        return list;
    }

    private List<Pair<Integer, Integer>> QueenValidMoves(int x, int y) {
        List<Pair<Integer, Integer>> list = new ArrayList<>();
        list.addAll(RookValidMoves(x, y));
        list.addAll(BishopValidMoves(x, y));
        return list;
    }

    private List<Pair<Integer, Integer>> BishopValidMoves(int x, int y) {
        List<Pair<Integer, Integer>> list = new ArrayList<>();
        list.addAll(BishopDiagonalValidMoveCheck(x, y, Direction.UP_RIGHT_DIAGONAL));
        list.addAll(BishopDiagonalValidMoveCheck(x, y, Direction.UP_LEFT_DIAGONAL));
        list.addAll(BishopDiagonalValidMoveCheck(x, y, Direction.DOWN_RIGHT_DIAGONAL));
        list.addAll(BishopDiagonalValidMoveCheck(x, y, Direction.DOWN_LEFT_DIAGONAL));

        return list;
    }

    private List<Pair<Integer, Integer>> BishopDiagonalValidMoveCheck(int x, int y, Direction dir) {
        List<Pair<Integer, Integer>> list = new ArrayList<>();
        int dx = dir.dir.getKey();
        int dy = dir.dir.getValue();
        int endX = x + dx;
        int endY = y + dy;
        while(endX > -1 && endX < 8 && endY > -1 && endY < 8) {
            if(BishopMove(x, y, endX, endY).validMove) {
                list.add(new Pair<>(endX, endY));
            }
            endX += dx;
            endY += dy;
        }

        return list;
    }

    private List<Pair<Integer, Integer>> RookValidMoves(int x, int y) {
        List<Pair<Integer, Integer>> list = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            if(RookMove(x,y,i,y).validMove && x != i) {
                list.add(new Pair<>(i, y));
            }
            if(RookMove(x,y,x,i).validMove && y != i) {
                list.add(new Pair<>(x, i));
            }
        }
        return list;
    }

    private List<Pair<Integer, Integer>> KnightValidMoves(int x, int y) {
        List<Pair<Integer, Integer>> list = new ArrayList<>();
        int dx;
        int dy;
        for(KnightDirections knightDir: KnightDirections.values()) {
            dx = knightDir.dir.getKey();
            dy = knightDir.dir.getValue();
            if(KnightMove(x, y, x + dx, y + dy).validMove) {
                list.add(new Pair<>(x + dx, y + dy));
            }
        }

        return list;
    }

    private static class MoveClassification {
        private boolean validMove;
        private Pair<Integer, Integer> enPassantTarget;
        private Pair<Integer, Integer> enPassantCapture;
        private Castling castle;
        private Piece pieceCaptured;

        private MoveClassification(boolean valid, Pair<Integer, Integer> target,
                                   Pair<Integer, Integer> enPassantCapture, Piece pieceCapture, Castling castle) {
            this.validMove = valid;
            this.enPassantTarget = target;
            this.enPassantCapture = enPassantCapture;
            this.pieceCaptured = pieceCapture;
            this.castle = castle;
        }
        private MoveClassification(boolean valid) {
            this(valid, NO_TARGET, NO_TARGET, null, NO_CASTLE);
        }

        private MoveClassification(Piece piece) {
            this(true, NO_TARGET, NO_TARGET, piece, NO_CASTLE);
        }

        private MoveClassification(Pair<Integer, Integer> target) {
            this(true, target, NO_TARGET, null, NO_CASTLE);
        }

        private MoveClassification(Pair<Integer, Integer> enPassantCapture, Piece piece) {
            this(true, NO_TARGET, enPassantCapture, piece, NO_CASTLE);
        }

        private MoveClassification(Castling castle) {
            this(true, NO_TARGET, NO_TARGET, null, castle);
        }

        private MoveClassification(Piece pieceCaptured, Castling castle) {
            this(true, NO_TARGET, NO_TARGET, pieceCaptured, castle);
        }
    }
}
