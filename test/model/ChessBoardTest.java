package model;

import model.enums.CheckStatus;
import model.pieces.Knight;
import model.pieces.*;
import org.junit.jupiter.api.*;

import static model.enums.CheckStatus.*;
import static model.enums.Color.BLACK;
import static model.enums.Color.WHITE;
import static org.junit.jupiter.api.Assertions.*;

public class ChessBoardTest {
    private IChessBoard board;

    @BeforeAll
    static void beforeAll() {
        System.out.println("BeforeAll");
    }

    @BeforeEach
    void beforeEach() {
        board = new ChessBoard();
    }

    @Nested
    @DisplayName("FENSetup")
    class FENSetupTest {

        @Nested
        @DisplayName("Piece Placement")
        class PiecePlacementTest {
            @Test
            @DisplayName("Invalid Character m")
            void InvalidChar() {
                assertFalse(board.FENSetup("mnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid Character T")
            void InvalidChar2() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppTpp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid Character 9")
            void InvalidChar3() {
                assertFalse(board.FENSetup("rn9qkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid Character 0")
            void InvalidChar4() {
                assertFalse(board.FENSetup("r0bqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Less than 8")
            void ShortLine() {
                assertFalse(board.FENSetup("rnbqkr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Less than 8 with number")
            void ShortLine2() {
                assertFalse(board.FENSetup("rnbqkbnr/5p/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("More than 8")
            void LongLine() {
                assertFalse(board.FENSetup("rnbqkbnrR/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("More than 8 with number")
            void LongLine2() {
                assertFalse(board.FENSetup("rnbqkbn3/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Missing slash")
            void MissingSlash() {
                assertFalse(board.FENSetup("rnbqkbnrpppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Less than 8 rows")
            void NotEnoughRows() {
                assertFalse(board.FENSetup("rnbqkbnr/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("More than 8 rows")
            void TooManyRows() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Valid, lots of pawns")
            void LotsOfPawns() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/pppppppp/8/8/PPPPPPPP/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid pawn in first row")
            void InvalidPawn() {
                assertFalse(board.FENSetup("ppbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
                assertFalse(board.FENSetup("PPbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid pawn in eighth row")
            void InvalidPawn2() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/PPBQKBNR w KQkq - 0 1"));
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/ppBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Valid pawn in middle rows")
            void ValidPawn() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/pp6/8/PP6/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("No White King")
            void InvalidNoKing() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1BNR w kq - 0 1"));
            }

            @Test
            @DisplayName("No Black King")
            void InvalidNoKing2() {
                assertFalse(board.FENSetup("rnbq1bnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1"));
            }

            @Test
            @DisplayName("Multi White Kings")
            void MultiKing() {
                assertFalse(board.FENSetup("rnbq1bnr/pppppppp/8/8/8/4K3/PPPPPPPP/RNBQKBNR w KQ - 0 1"));
            }

            @Test
            @DisplayName("Multi Black Kings")
            void MultiKing2() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/3k4/8/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1"));
            }

        }

        @Nested
        @DisplayName("Active Color")
        class ActiveColorTest {
            @Test
            @DisplayName("Active Color w")
            void ValidColor() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Active Color b")
            void ValidColor2() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid Color r")
            void InvalidColor() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR r KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid Color -")
            void InvalidColor2() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR - KQkq - 0 1"));
            }
        }

        @Nested
        @DisplayName("Castling Rights")
        class CastlingRightsTest {
            @Test
            @DisplayName("Invalid Character p")
            void InvalidCharCastle() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w Kpkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid Character R")
            void InvalidCharCastle2() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KkR - 0 1"));
            }

            @Test
            @DisplayName("Invalid Character B")
            void InvalidCharCastle3() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w B - 0 1"));
            }

            @Test
            @DisplayName("Invalid Character Combo")
            void InvalidCharCastle4() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w Kp- - 0 1"));
            }

            @Test
            @DisplayName("Valid Single Castle")
            void ValidCharCastle() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w q - 0 1"));
            }

            @Test
            @DisplayName("Valid Castle Combo")
            void ValidCharCastle2() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qK - 0 1"));
            }

            @Test
            @DisplayName("Valid Castle Combo2")
            void ValidCharCastle3() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qKk - 0 1"));
            }

            @Test
            @DisplayName("Valid No Castle")
            void ValidCharCastle4() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"));
            }
        }

        @Nested
        @DisplayName("En Passant Target")
        class EnPassantTest {

            @Test
            @DisplayName("Valid target a3")
            void ValidEnPassant() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/P7/8/1PPPPPPP/RNBQKBNR b KQkq a3 0 1"));
            }

            @Test
            @DisplayName("Valid target h6")
            void ValidEnPassant2() {
                assertTrue(board.FENSetup("rnbqkbnr/ppppppp1/8/7p/8/8/PPPPPPPP/RNBQKBNR w KQkq h6 0 1"));
            }
            @Test
            @DisplayName("Valid target a3")
            void InvalidEnPassant9() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq a3 0 1"));
            }

            @Test
            @DisplayName("Invalid target h3")
            void ValidEnPassant8() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq h3 0 1"));
            }

            @Test
            @DisplayName("Invalid target a6")
            void InvalidEnPassant7() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq a6 0 1"));
            }

            @Test
            @DisplayName("Invalid target h6")
            void InvalidEnPassant6() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq h6 0 1"));
            }

            @Test
            @DisplayName("Invalid target a0")
            void InvalidEnPassant() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq a0 0 1"));
            }

            @Test
            @DisplayName("Invalid target a9")
            void InvalidEnPassant2() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq a9 0 1"));
            }

            @Test
            @DisplayName("Invalid target i3")
            void InvalidEnPassant3() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq i3 0 1"));
            }

            @Test
            @DisplayName("Invalid target i6")
            void InvalidEnPassant4() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq i6 0 1"));
            }

            @Test
            @DisplayName("Invalid target a5")
            void InvalidEnPassant5() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq a5 0 1"));
            }
        }

        @Nested
        @DisplayName("Halfmove Clock")
        class HalfmoveTest {
            @Test
            @DisplayName("Valid halfmove 1")
            void ValidHalfmove() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1 1"));
            }

            @Test
            @DisplayName("Valid halfmove 100")
            void ValidHalfmove2() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 100 1"));
            }

            @Test
            @DisplayName("Valid halfmove 99")
            void ValidHalfmove3() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 99 1"));
            }

            @Test
            @DisplayName("Invalid halfmove 101")
            void InvalidHalfmove() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 101 1"));
            }

            @Test
            @DisplayName("Invalid halfmove -1")
            void InvalidHalfmove2() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - -1 1"));
            }

        }

        @Nested
        @DisplayName("Fullmove number")
        class FullmoveTest {
            @Test
            @DisplayName("Valid fullmove 2")
            void ValidFullmove() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 2"));
            }

            @Test
            @DisplayName("Valid fullmove 9999")
            void ValidFullmove2() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 9999"));
            }

            @Test
            @DisplayName("Invalid fullmove 10000")
            void InvalidFullmove() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 10000"));
            }

            @Test
            @DisplayName("Invalid fullmove -1")
            void InvalidFullmove2() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -1"));
            }
        }

        @Nested
        @DisplayName("Miscellaneous")
        class MiscTest {
            @Test
            @DisplayName("Invalid space at front")
            void InvalidMisc() {
                assertFalse(board.FENSetup(" rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid missing first space")
            void InvalidMisc2() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNRw KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid missing second space")
            void InvalidMisc3() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR wKQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid missing third space")
            void InvalidMisc4() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq- 0 1"));
            }

            @Test
            @DisplayName("Invalid missing fourth space")
            void InvalidMisc5() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -0 1"));
            }

            @Test
            @DisplayName("Invalid missing fifth space")
            void InvalidMisc6() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 01"));
            }

            @Test
            @DisplayName("Invalid ending space")
            void InvalidMisc7() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 "));
            }

            @Test
            @DisplayName("Invalid white first move capture king; knight")
            void InvalidMisc8() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/5N2/8/8/8/PPPPPPPP/RNBQKB1R w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid black first move capture king; knight")
            void InvalidMisc9() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/5n2/PPPPPPPP/RNBQKBNR b KQkq - 0 1"));
            }

            @Test
            @DisplayName("Valid black first move king in check; knight")
            void ValidMisc() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/5N2/8/8/8/PPPPPPPP/RNBQKB1R b KQkq - 0 1"));
            }

            @Test
            @DisplayName("Valid white first move king in check; knight")
            void ValidMisc2() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/5n2/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid white first move capture king; bishop")
            void InvalidMisc10() {
                assertFalse(board.FENSetup("rnbqkbnr/ppppp1pp/5p2/7B/8/8/PPPP1PPP/RNBQK1NR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid black first move capture king; bishop")
            void InvalidMisc11() {
                assertFalse(board.FENSetup("rnbqk1nr/pppppppp/8/8/7b/5P2/PPPPP1PP/RNBQKBNR b KQkq - 0 1"));
            }

            @Test
            @DisplayName("Valid black first move king in check; bishop")
            void ValidMisc3() {
                assertTrue(board.FENSetup("rnbqkbnr/ppppp1pp/5p2/7B/8/8/PPPP1PPP/RNBQK1NR b KQkq - 0 1"));
            }

            @Test
            @DisplayName("Valid white first move king in check; bishop")
            void ValidMisc4() {
                assertTrue(board.FENSetup("rnbqk1nr/pppppppp/8/8/7b/5P2/PPPPP1PP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid white first move capture king; pawn")
            void InvalidMisc12() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppPpp/8/8/8/8/PPPPP1PP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid black first move capture king; pawn")
            void InvalidMisc13() {
                assertFalse(board.FENSetup("rnbqkbnr/ppppp1pp/8/8/8/8/PPPPPpPP/RNBQKBNR b KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid Kings beside each other")
            void InvalidMisc14() {
                assertFalse(board.FENSetup("rnbq1bnr/pppppppp/8/3k4/4K3/8/PPPPPPPP/RNBQ1BNR w - - 0 1"));
            }

            @Test
            @DisplayName("Invalid Kings beside each other")
            void InvalidMisc15() {
                assertFalse(board.FENSetup("rnbq1bnr/pppppppp/8/3k4/4K3/8/PPPPPPPP/RNBQ1BNR b - - 0 1"));
            }

            @Test
            @DisplayName("Valid black first move king in check; pawn")
            void ValidMisc5() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppPpp/8/8/8/8/PPPPP1PP/RNBQKBNR b KQkq - 0 1"));
            }

            @Test
            @DisplayName("Valid white first move king in check; pawn")
            void ValidMisc6() {
                assertTrue(board.FENSetup("rnbqkbnr/ppppp1pp/8/8/8/8/PPPPPpPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Short FEN; in piece placement")
            void ShortFEN1() {
                assertFalse(board.FENSetup("rnbqkbnr/ppppppp"));
            }

            @Test
            @DisplayName("Short FEN; in piece placement with slash")
            void ShortFEN2() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/"));
            }

            @Test
            @DisplayName("Short FEN; only piece placement")
            void ShortFEN3() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/pp6/8/PP6/8/PPPPPPPP/RNBQKBNR "));
            }

            @Test
            @DisplayName("Short FEN; with color")
            void ShortFEN4() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w "));
            }

            @Test
            @DisplayName("Short FEN; with castling")
            void ShortFEN5() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq "));
            }

            @Test
            @DisplayName("Short FEN; with target")
            void ShortFEN6() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - "));
            }

            @Test
            @DisplayName("Short FEN; with halfmove")
            void ShortFEN7() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 "));
            }

            @Test
            @DisplayName("Invalid empty FEN")
            void InvalidEmpty() {
                assertFalse(board.FENSetup(""));
            }

        }
    }

    @Nested
    @DisplayName("getPositionStatus")
    class PositionStatusTest {
        @Test
        @DisplayName("Standard Board")
        void StandardTest() {
            board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            assertEqualPieces(board.getPositionStatus(0, 0), new Rook(BLACK));
            assertEqualPieces(board.getPositionStatus(1, 0), new Knight(BLACK));
            assertEqualPieces(board.getPositionStatus(2, 0), new Bishop(BLACK));
            assertEqualPieces(board.getPositionStatus(3, 0), new Queen(BLACK));
            assertEqualPieces(board.getPositionStatus(4, 0), new King(BLACK));
            assertEqualPieces(board.getPositionStatus(5, 0), new Bishop(BLACK));
            assertEqualPieces(board.getPositionStatus(6, 0), new Knight(BLACK));
            assertEqualPieces(board.getPositionStatus(7, 0), new Rook(BLACK));

            for(int i = 0; i < 8; i++) {
                assertEqualPieces(board.getPositionStatus(i, 1), new Pawn(BLACK));
                assertEqualPieces(board.getPositionStatus(i, 6), new Pawn(WHITE));
            }

            assertEqualPieces(board.getPositionStatus(0, 7), new Rook(WHITE));
            assertEqualPieces(board.getPositionStatus(1, 7), new Knight(WHITE));
            assertEqualPieces(board.getPositionStatus(2, 7), new Bishop(WHITE));
            assertEqualPieces(board.getPositionStatus(3, 7), new Queen(WHITE));
            assertEqualPieces(board.getPositionStatus(4, 7), new King(WHITE));
            assertEqualPieces(board.getPositionStatus(5, 7), new Bishop(WHITE));
            assertEqualPieces(board.getPositionStatus(6, 7), new Knight(WHITE));
            assertEqualPieces(board.getPositionStatus(7, 7), new Rook(WHITE));

            for(int i = 2; i < 6; i++) {
                for(int j = 0; j < 8; j++) {
                    assertNull(board.getPositionStatus(j, i));
                }
            }
        }

        @Test

        @DisplayName("Early Game Position")
        void EarlyGameTest() {
            board.FENSetup("r1bqk2r/ppp1bppp/2n1pn2/8/2BP4/2N1BN2/PPP2PPP/R2Q1RK1 w kq - 0 1");
            assertEqualPieces(board.getPositionStatus(0,0), new Rook(BLACK));
            assertNull(board.getPositionStatus(1,0));
            assertEqualPieces(board.getPositionStatus(2, 0), new Bishop(BLACK));
            assertEqualPieces(board.getPositionStatus(3, 0), new Queen(BLACK));
            assertEqualPieces(board.getPositionStatus(4, 0), new King(BLACK));
            assertNull(board.getPositionStatus(5, 0));
            assertNull(board.getPositionStatus(6, 0));
            assertEqualPieces(board.getPositionStatus(7, 0), new Rook(BLACK));

            assertEqualPieces(board.getPositionStatus(0, 1), new Pawn(BLACK));
            assertEqualPieces(board.getPositionStatus(1, 1), new Pawn(BLACK));
            assertEqualPieces(board.getPositionStatus(2, 1), new Pawn(BLACK));
            assertNull(board.getPositionStatus(3,1));
            assertEqualPieces(board.getPositionStatus(4, 1), new Bishop(BLACK));
            assertEqualPieces(board.getPositionStatus(5, 1), new Pawn(BLACK));
            assertEqualPieces(board.getPositionStatus(6, 1), new Pawn(BLACK));
            assertEqualPieces(board.getPositionStatus(7, 1), new Pawn(BLACK));

            assertNull(board.getPositionStatus(0, 2));
            assertNull(board.getPositionStatus(1, 2));
            assertEqualPieces(board.getPositionStatus(2, 2), new Knight(BLACK));
            assertNull(board.getPositionStatus(3, 2));
            assertEqualPieces(board.getPositionStatus(4,2), new Pawn(BLACK));
            assertEqualPieces(board.getPositionStatus(5, 2), new Knight(BLACK));
            assertNull(board.getPositionStatus(6, 2));
            assertNull(board.getPositionStatus(7, 2));

            for(int i = 0; i < 8; i++) {
                assertNull(board.getPositionStatus(i, 3));
            }
            for(int i = 0; i < 8; i++) {
                if(i == 2) {
                    assertEqualPieces(board.getPositionStatus(i, 4), new Bishop(WHITE));
                } else if(i == 3) {
                    assertEqualPieces(board.getPositionStatus(i, 4), new Pawn(WHITE));
                } else {
                    assertNull(board.getPositionStatus(i, 4));
                }
            }

            assertNull(board.getPositionStatus(0, 5));
            assertNull(board.getPositionStatus(1, 5));
            assertEqualPieces(board.getPositionStatus(2, 5), new Knight(WHITE));
            assertNull(board.getPositionStatus(3, 5));
            assertEqualPieces(board.getPositionStatus(4,5), new Bishop(WHITE));
            assertEqualPieces(board.getPositionStatus(5, 5), new Knight(WHITE));
            assertNull(board.getPositionStatus(6, 5));
            assertNull(board.getPositionStatus(7, 5));

            assertEqualPieces(board.getPositionStatus(0, 6), new Pawn(WHITE));
            assertEqualPieces(board.getPositionStatus(1, 6), new Pawn(WHITE));
            assertEqualPieces(board.getPositionStatus(2, 6), new Pawn(WHITE));
            assertNull(board.getPositionStatus(3,6));
            assertNull(board.getPositionStatus(4, 6));
            assertEqualPieces(board.getPositionStatus(5, 6), new Pawn(WHITE));
            assertEqualPieces(board.getPositionStatus(6, 6), new Pawn(WHITE));
            assertEqualPieces(board.getPositionStatus(7, 6), new Pawn(WHITE));

            assertEqualPieces(board.getPositionStatus(0,7), new Rook(WHITE));
            assertNull(board.getPositionStatus(1,7));
            assertNull(board.getPositionStatus(2, 7));
            assertEqualPieces(board.getPositionStatus(3, 7), new Queen(WHITE));
            assertNull(board.getPositionStatus(4, 7));
            assertEqualPieces(board.getPositionStatus(5, 7), new Rook(WHITE));
            assertEqualPieces(board.getPositionStatus(6, 7), new King(WHITE));
            assertNull(board.getPositionStatus(7, 7));
        }
    }

    @Nested
    @DisplayName("FindCheckStatus")
    class FindCheckStatusTest {

        @Nested
        @DisplayName("Single King")
        class SingleKingTest {

            @Test
            @DisplayName("No Check")
            void NoCheck() {
                board.FENSetup("rnbqk2r/pppp1ppp/5n2/4p3/1bB1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 1");
                assertNoCheck();
            }

            @Test
            @DisplayName("Check; Capture Only Option")
            void CheckOnlyCapture() {
                board.FENSetup("rnbqkb1r/pppppppp/8/8/8/5n2/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
                assertCheck();
            }

            @Test
            @DisplayName("Check; Move Only Option")
            void CheckMoveOnlyOption() {
                board.FENSetup("rnb1kb1r/pppp1ppp/4p3/8/3P3q/3n1P2/PPP1P1PP/RNBQKBNR w KQkq - 0 1");
                assertCheck();
            }

            @Test
            @DisplayName("Check; Move Only Option; Block option pinned")
            void CheckMoveOnlyOption2() {
                board.FENSetup("rn1qkbn1/ppppppp1/8/3Q1P2/4P1b1/1B6/PPPP2P1/RNBK2Nr w q - 0 1");
                assertCheck();
            }

            @Test
            @DisplayName("Checkmate; Classic Bishop Queen; Black")
            void CheckmateBishopQueen() {
                board.FENSetup("rn2k1nr/pppp1pb1/6p1/3Qp3/4P1b1/1B6/PPPPq1PP/RNB1K2R w q - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Classic Bishop Queen; White")
            void CheckmateBishopQueen2() {
                board.FENSetup("rnb1k2r/ppp1Qppp/3p4/2b1p3/1q2P2B/2NP4/PPP2PPP/R3K1NR b KQkq - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Bishop Queen Corner")
            void CheckmateBishopQueen3() {
                board.FENSetup("8/6k1/8/8/3b4/8/1q6/K7 w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Ladder mate")
            void CheckmateLadder() {
                board.FENSetup("8/6k1/8/8/8/8/4r3/K4r2 w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Double Rook")
            void CheckmateDoubleRook() {
                board.FENSetup("8/6k1/8/8/8/8/1rr5/RK1R4 w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Knight Smothered Mate")
            void CheckmateSmothered() {
                board.FENSetup("5k2/8/8/8/8/8/5nPP/6RK w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Bishop Knight")
            void CheckmateBishopKnight() {
                board.FENSetup("8/8/8/8/8/5bkn/8/7K w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Double Bishop")
            void CheckmateDoubleBishop() {
                board.FENSetup("6k1/8/8/8/3b4/5b2/7P/7K w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Bishop Rook")
            void CheckmateBishopRook() {
                board.FENSetup("4k3/8/8/8/3b4/8/7P/6rK w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Knight Queen")
            void CheckmateKnightQueen() {
                board.FENSetup("3k4/8/8/8/5n2/8/6q1/6K1 w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Triple Knight")
            void CheckmateTripleKnight() {
                board.FENSetup("8/8/8/7k/8/4nnn1/8/7K w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Double Pawn")
            void CheckmateDoublePawn() {
                board.FENSetup("8/8/8/8/8/4k3/4pp2/4K3 w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Pawn Queen")
            void CheckmatePawnQueen() {
                board.FENSetup("3k4/8/8/8/8/5p2/6q1/6K1 w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; King Queen")
            void CheckmateKingQueen() {
                board.FENSetup("8/8/8/8/8/5k2/6q1/6K1 w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Block option pinned")
            void CheckmateBlockPinned() {
                board.FENSetup("rnb1k1n1/pppp1p2/6p1/3QpP2/4P1qb/1B6/PPPP2P1/RNBK2Nr w q - 0 1");
                assertCheckmate();
            }
            @Test
            @DisplayName("Checkmate; Double Check; Both can be captured")
            void CheckmateDoubleCheck() {
                board.FENSetup("rnb1kb1r/pppp1ppp/4p3/8/5P1q/3n1N2/PPPPP1PP/RNBQKB1R w KQkq - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Double Check; One can be blocked")
            void CheckmateDoubleCheck2() {
                board.FENSetup("rnb1kb1r/pppp1ppp/4p3/8/7q/3n1P2/PPPPP1PP/RNBQKBNR w KQkq - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Back rank")
            void CheckmateBackRank() {
                board.FENSetup("5k2/8/8/8/8/8/5PPP/r5K1 w - - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Misc")
            void CheckmateMisc() {
                board.FENSetup("rn2k2r/ppp1pppp/3pbn2/4b3/3K1Pq1/2PPP3/PP4PP/RNBQ1BNR w kq - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; No Castling in Check")
            void CheckmateMisc2() {
                board.FENSetup("6k1/8/8/8/8/8/4PPPP/RNq1K2R w KQ - 0 1");
                assertCheckmate();
            }

            @Test
            @DisplayName("Checkmate; Anastasia's mate")
            void CheckmateMisc3() {
                board.FENSetup("1k6/8/8/8/7r/8/4nPPK/8 w - - 0 1");
                assertCheckmate();
            }
        }

        @Nested
        @DisplayName("Multi King Test")
        class MultiKingTest {

        }
    }

    @Nested
    @DisplayName("MovePiece")
    class MovePieceTest {

        @Test
        @DisplayName("Null space")
        void NullSpace() {
            board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            assertFalse(board.MovePiece(0,4,0,5));
        }

        @Test
        @DisplayName("Wrong Color")
        void WrongColor() {
            board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            assertFalse(board.MovePiece(0,1, 0,2));
        }

        @Test
        @DisplayName("Valid Pawn Move")
        void PawnMove() {
            board.FENSetup("rnbqkbnr/pppp1ppp/8/4p3/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
            assertValidMove(4, 5, 4, 4);
        }

        @Test
        @DisplayName("Valid Double Pawn Move")
        void PawnMove2() {
            board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            assertValidMove(1,6,1,4);
        }

        @Test
        @DisplayName("Valid Pawn Capture; Left")
        void PawnCapture() {
            board.FENSetup("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
            assertValidMove(4, 4, 3, 3);
        }

        @Test
        @DisplayName("Valid Pawn Capture; Right")
        void PawnCapture2() {
            board.FENSetup("rnbqkbnr/pppp1ppp/8/4p3/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1");
            assertValidMove(3, 4, 4, 3);
        }

        @Test
        @DisplayName("Valid En Passant; Right")
        void PawnEnPassant() {
            board.FENSetup("rnbqkbnr/pppp1ppp/8/3Pp3/8/8/PPP1PPPP/RNBQKBNR w KQkq e6 0 1");
            assertValidMove(3, 3, 4, 2);
            assertEqualPieces(board.getPositionStatus(4, 3), null);
        }

        @Test
        @DisplayName("Valid En Passant; Left")
        void PawnEnPassant2() {
            board.FENSetup("rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 1");
            assertValidMove(4, 3, 3, 2);
            assertEqualPieces(board.getPositionStatus(3,3), null);
        }

        @Test
        @DisplayName("Invalid Pawn Move")
        void InvalidPawnMove() {
            board.FENSetup("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
            assertFalse(board.MovePiece(4, 4, 4, 3));
        }

        @Test
        @DisplayName("Invalid Double Pawn Move")
        void InvalidPawnMove2() {
            board.FENSetup("rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
            assertFalse(board.MovePiece(4, 5, 4, 3));
        }

        @Test
        @DisplayName("Invalid Double Pawn Move")
        void InvalidPawnMove3() {
            board.FENSetup("rnbqkbnr/pppp1ppp/8/8/3Pp3/2N5/PPP1PPPP/R1BQKBNR w KQkq - 0 1");
            assertFalse(board.MovePiece(4, 6, 4, 4));
        }

        @Test
        @DisplayName("Invalid Pawn Capture")
        void InvalidPawnMove4() {
            board.FENSetup("rnb1kbnr/ppp1pppp/4q3/3p4/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 1");
            assertFalse(board.MovePiece(4, 4, 3, 3));
        }

        @Test
        @DisplayName("Invalid En Passant into Check")
        void InvalidPawnMove8() {
            board.FENSetup("rn1qkbnr/ppp1pppp/2b5/3pP3/8/5K2/PPPP1PPP/RNBQ1BNR w kq d6 0 1");
            assertFalse(board.MovePiece(4,3,3,3));
        }

        @Test
        @DisplayName("Invalid En Passant")
        void InvalidPawnMove5() {
            board.FENSetup("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
            assertValidMove(4, 4, 4, 3);
            assertValidMove(2, 1, 2, 3);
            assertFalse(board.MovePiece(4, 3, 3, 2));
        }

        @Test
        @DisplayName("Invalid Pawn Move")
        void InvalidPawnMove6() {
            board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            assertFalse(board.MovePiece(0, 6, 1, 5));
        }

        @Test
        @DisplayName("Invalid Pawn Move")
        void InvalidPawnMove7() {
            board.FENSetup("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
            assertFalse(board.MovePiece(4, 4, 4, 5));
        }

        @Test
        @DisplayName("Valid Pawn Move; Black")
        void BlackPawnMove() {
            board.FENSetup("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1");
            assertValidMove(0, 1, 0, 2);
        }

        @Test
        @DisplayName("Valid Pawn Double Move; Black")
        void BlackPawnMove2() {
            board.FENSetup("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1");
            assertValidMove(0, 1, 0, 3);
        }

        @Test
        @DisplayName("Valid Pawn En Passant; Black")
        void BlackPawnMove3 () {
            board.FENSetup("rnbqkbnr/ppp1pppp/8/8/3pP3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
            assertValidMove(3, 4, 4, 5);
            assertEqualPieces(board.getPositionStatus(4,4), null);
        }

        @Test
        @DisplayName("Invalid Pawn Move; Black")
        void InvalidBlackPawnMove() {
            board.FENSetup("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1");
            assertFalse(board.MovePiece(4, 3, 4, 4));
        }

        @Test
        @DisplayName("Invalid Pawn Capture; Black")
        void InvalidBlackPawnCapture() {
            board.FENSetup("rnbqkbnr/pppp1ppp/8/4p3/3P4/4Q3/PPP1PPPP/RNB1KBNR b KQkq - 0 1");
            assertFalse(board.MovePiece(4, 3, 3, 4));
        }

        @Test
        @DisplayName("Valid Rook Move")
        void RookMove() {
            board.FENSetup("rnbqkbnr/ppppppp1/8/7p/P7/8/1PPPPPPP/RNBQKBNR w KQkq - 0 1");
            assertValidMove(0,7, 0, 5);
            assertValidMove(7,0,7,2);
            assertValidMove(0,5,3,5);
            assertValidMove(7,2,5,2);
        }

        @Test
        @DisplayName("Valid Rook Capture")
        void RookCapture() {
            board.FENSetup("rnbqkbnr/1pppppp1/8/p7/7P/8/1PPPPPP1/RNBQKBNR w Kkq - 0 1");
            assertValidMove(0,7,0,3);
            assertValidMove(7,0,7,4);
        }

        @Test
        @DisplayName("Invalid Rook Move")
        void InvalidRookMove() {
            board.FENSetup("rnbqkbn1/ppp1ppp1/3p3r/7p/P7/R3P3/1PPP1PPP/1NBQKBNR w Kq - 0 1");
            assertFalse(board.MovePiece(0,5, 0,4));
            assertFalse(board.MovePiece(0,5, 0,3));
            assertFalse(board.MovePiece(0,5, 4,5));
            assertFalse(board.MovePiece(0,5, 5,5));
            assertFalse(board.MovePiece(0,5,2,3));
            assertValidMove(0,5,0,7);
            assertFalse(board.MovePiece(7, 2, 7, 3));
            assertFalse(board.MovePiece(7, 2, 7, 4));
            assertFalse(board.MovePiece(7,2,3,2));
            assertFalse(board.MovePiece(7,2,2,2));
        }

        @Test
        @DisplayName("Invalid Rook Capture")
        void InvalidRookCapture() {
            board.FENSetup("rnbk4/ppp1pppp/4q3/N2r4/4R2n/3Q4/1PPP1PPP/2B1KBNR w K - 0 1");
            assertFalse(board.MovePiece(4,4,7,4));
            assertValidMove(4, 4, 4, 2);
            assertFalse(board.MovePiece(3, 3, 0, 3));
            assertValidMove(3, 3, 3, 5);
        }

        @Test
        @DisplayName("Invalid Rook Capture")
        void InvalidRookCapture2() {
            board.FENSetup("rnbqk1n1/pppr1ppp/4p3/1B6/1b6/4P3/PPPR1PPP/1NBQK1NR w Kq - 0 1");
            assertFalse(board.MovePiece(3, 6, 3, 5));
            assertValidMove(2, 6, 2, 5);
            assertFalse(board.MovePiece(3,1,3,2));
            assertValidMove(2,1,2,2);
        }

        @Test
        @DisplayName("Valid Knight Move")
        void ValidKnightMove() {
            board.FENSetup("rnbqkbnr/p1pppppp/8/1p6/6P1/8/PPPPPP1P/RNBQKBNR w KQkq - 0 1");
            assertValidMove(1,7,2,5);
            assertValidMove(6, 0, 5, 2);
            assertValidMove(2,5,1,3);
            assertValidMove(5,2,6,4);
        }

        @Test
        @DisplayName("Invalid Knight Move")
        void InvalidKnightMove() {
            board.FENSetup("r1bqk1nr/1ppn1ppp/p2p4/1B2p3/1b2P3/P2P4/1PPN1PPP/R1BQK1NR w KQkq - 0 1");
            assertFalse(board.MovePiece(3,6,5,5));
            assertValidMove(0,5,1,4);
            assertFalse(board.MovePiece(3,1,5,2));
            assertValidMove(0,2, 1,3);
            assertFalse(board.MovePiece(3,6,4,4));
            assertValidMove(3, 6, 1, 5);
            assertFalse(board.MovePiece(3,1, 4,3));
            assertValidMove(3, 1, 1, 2);
        }

        @Test
        @DisplayName("Valid Bishop Move")
        void ValidBishopMove() {
            board.FENSetup("rnbqkbnr/ppp1pppp/3p4/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
            assertValidMove(5, 7, 3, 5);
            assertValidMove(2, 0, 4, 2);
            assertValidMove(3, 5, 7, 1);
            assertValidMove(4, 2, 0,6);
        }

        @Test
        @DisplayName("Invalid Bishop Move")
        void InvalidBishopMove() {
            board.FENSetup("rn1k1bnr/p2bpppp/4q3/1p3P2/2p3P1/3Q4/PPPPB2P/RNB1K1NR w KQ - 0 1");
            assertFalse(board.MovePiece(4, 6, 5, 5));
            assertValidMove(5, 3,4,2);
            assertFalse(board.MovePiece(3, 1, 2, 2));
            assertValidMove(2, 4, 3, 5);
            assertFalse(board.MovePiece(4, 6, 1, 3));
            assertFalse(board.MovePiece(4, 6, 4, 4));
            assertFalse(board.MovePiece(4, 6, 6, 4));
            assertFalse(board.MovePiece(4, 6, 7, 3));
            assertValidMove(4, 6, 3, 7);
            assertFalse(board.MovePiece(3, 1, 6, 4));
            assertFalse(board.MovePiece(3, 1, 3, 3));
            assertFalse(board.MovePiece(3, 1, 1, 3));
            assertFalse(board.MovePiece(3, 1, 0, 4));
            assertValidMove(3, 1, 4, 0);
        }

        @Test
        @DisplayName("Valid Queen Move")
        void ValidQueenMove() {
            board.FENSetup("rnbqkbnr/pp1ppppp/2p5/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
            assertValidMove(3,7,7,3);
            assertValidMove(3, 0, 0, 3);
            assertValidMove(7,3,7,1);
            assertValidMove(0,3,0,6);
        }

        @Test
        @DisplayName("Invalid Queen Move")
        void InvalidQueenMove() {
            board.FENSetup("1nbk1bnr/p3pppp/1p1qr3/5P2/P1p5/3RQ1P1/1PPP3P/1NB1KBNR w K - 0 1");
            assertFalse(board.MovePiece(4, 5, 5, 5));
            assertValidMove(5, 3, 4, 2);
            assertFalse(board.MovePiece(3, 2, 2, 2));
            assertValidMove(2, 4, 3, 5);
            assertFalse(board.MovePiece(4, 5, 4, 2));
            assertFalse(board.MovePiece(4, 5, 4, 1));
            assertFalse(board.MovePiece(4, 5, 0, 1));
            assertFalse(board.MovePiece(4, 5, 3, 3));
            assertValidMove(4, 5, 5, 5);
            assertFalse(board.MovePiece(3, 2, 3, 5));
            assertFalse(board.MovePiece(3, 2, 3, 6));
            assertFalse(board.MovePiece(3, 2, 7, 6));
            assertFalse(board.MovePiece(3, 2, 4, 4));
            assertValidMove(3, 2, 4, 2);
        }

        @Test
        @DisplayName("Valid King Move")
        void ValidKingMove() {
            board.FENSetup("rnb2bnr/ppppkppp/2P5/8/4p3/8/PPPKPPPP/RNB2BNR w - - 0 1");
            assertValidMove(3,6,4,5);
            assertValidMove(4,1,3,2);
            assertValidMove(4,5,4,4);
            assertValidMove(3,2,2,2);
        }

        @Test
        @DisplayName("Invalid King Move")
        void InvalidKingMove() {
            board.FENSetup("r3k2r/ppp2ppp/3pp3/1b4B1/8/4P3/PPPP1PPP/R3K2R w - - 0 1");
            assertFalse(board.MovePiece(4,7,4,6));
            assertValidMove(3,6,3,5);
            assertFalse(board.MovePiece(4,0,4,1));
            assertValidMove(5,1,5,2);
            assertFalse(board.MovePiece(4,7,6, 7));
            assertFalse(board.MovePiece(4,7,2,7));
            assertValidMove(4,7,5,7);
            assertFalse(board.MovePiece(4,0,6,0));
            assertFalse(board.MovePiece(4,0,2,0));
            assertValidMove(4,0,5,0);
        }

        @Test
        @DisplayName("Kingside Castle")
        void KingSideCastle() {
            board.FENSetup("r3k2r/ppp1pppp/3p4/1b4B1/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");
            assertValidMove(4,7,6,7);
            assertEqualPieces(board.getPositionStatus(5,7),new Rook(WHITE));
            assertValidMove(4,0,6,0);
            assertEqualPieces(board.getPositionStatus(5,0),new Rook(BLACK));
        }

        @Test
        @DisplayName("Queenside Castle")
        void QueenSideCastle() {
            board.FENSetup("r3k2r/ppp1pppp/3p4/1b4B1/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");
            assertValidMove(4,7,2,7);
            assertEqualPieces(board.getPositionStatus(3,7),new Rook(WHITE));
            assertValidMove(4,0,2,0);
            assertEqualPieces(board.getPositionStatus(3,0),new Rook(BLACK));
        }

        @Test
        @DisplayName("Invalid castling through pieces")
        void InvalidCastle() {
            board.FENSetup("r1n1kn1r/ppp1pppp/3p4/1b4B1/8/8/PPPPPPPP/RN2K1NR w KQkq - 0 1");
            assertFalse(board.MovePiece(4,7,6,7));
            assertFalse(board.MovePiece(4,7,2,7));
            assertValidMove(4,7,5,7);
            assertFalse(board.MovePiece(4,0,6,0));
            assertFalse(board.MovePiece(4,0,2,0));
            assertValidMove(4,0,3,0);
        }

        @Test
        @DisplayName("Invalid castling through check")
        void InvalidCastle2() {
            board.FENSetup("r3k2r/p2pp2p/8/2Q2Q2/2q2q2/8/P2PP2P/R3K2R w KQkq - 0 1");
            assertFalse(board.MovePiece(4,7,6,7));
            assertFalse(board.MovePiece(4,7,2,7));
            assertValidMove(7,6,7,5);
            assertFalse(board.MovePiece(4,0,6,0));
            assertFalse(board.MovePiece(4,0,2,0));
            assertValidMove(7,1,7,2);

            assertValidMove(5,3,6,3);
            assertValidMove(5,4,6,4);
            assertValidMove(2,3,1,3);
            assertValidMove(2,4,1,4);

            assertFalse(board.MovePiece(4,7,6,7));
            assertValidMove(4,7,3,7);
            assertFalse(board.MovePiece(4,0,6,0));
            assertValidMove(4,0,2,0);

            board.FENSetup("r3k2r/p2ppp2/8/7Q/7q/8/P2PPP2/R3K2R w KQkq - 0 1");
            assertValidMove(4,7,6,7);
            assertValidMove(4,0,6,0);
        }

        @Test
        @DisplayName("Invalid castling when checked")
        void InvalidCastle3() {
            board.FENSetup("r3k2r/p2pp3/8/6Q1/7q/8/P2PP3/R3K2R w KQkq - 0 1");
            assertFalse(board.MovePiece(4,7,6,7));
            assertFalse(board.MovePiece(4,7,2,7));
            assertValidMove(4,7,3,7);
            assertValidMove(7,4,6,4);
            assertValidMove(6,3,7,3);
            assertFalse(board.MovePiece(4,0,6,0));
            assertFalse(board.MovePiece(4,0,2,0));
            assertValidMove(4,0,3,0);
        }

        @Test
        @DisplayName("Invalid castle after moving king")
        void InvalidCastle4() {
            board.FENSetup("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");
            assertValidMove(4,7,5,7);
            assertValidMove(4,0,5,0);
            assertValidMove(5,7,4,7);
            assertValidMove(5,0,4,0);
            assertFalse(board.MovePiece(4,7,6,7));
            assertFalse(board.MovePiece(4,7,2,7));
            assertValidMove(4,7,3,7);
            assertFalse(board.MovePiece(4,0,6,0));
            assertFalse(board.MovePiece(4,0,2,0));
            assertValidMove(4,0,3,0);
        }

        @Test
        @DisplayName("Invalid castle after moving rook")
        void InvalidCastle5() {
            board.FENSetup("r3k2r/1pppppp1/p6p/8/8/P6P/1PPPPPP1/R3K2R w KQkq - 0 1");
            assertValidMove(7,7,7,6);
            assertValidMove(7,0,7,1);
            assertValidMove(7,6,7,7);
            assertValidMove(7,1,7,0);

            assertFalse(board.MovePiece(4,7,6,7));
            assertValidMove(0,7,0,6);
            assertFalse(board.MovePiece(4,0,6,0));
            assertValidMove(0,0,0,1);
            assertValidMove(0,6,0,7);
            assertValidMove(0,1,0,0);
            assertFalse(board.MovePiece(4,7,2,7));
            assertValidMove(4,7,5,7);
            assertFalse(board.MovePiece(4,0,2,0));
            assertValidMove(4,0,5,0);
        }
    }

    void assertValidMove(int initX, int initY, int endX, int endY) {
        Piece piece = board.getPositionStatus(initX, initY);
        assertTrue(board.MovePiece(initX,initY,endX,endY));
        assertEqualPieces(board.getPositionStatus(initX, initY), null);
        assertEqualPieces(board.getPositionStatus(endX, endY), piece);
    }

    void assertEqualPieces(Piece piece1, Piece piece2) {
        if(piece1 == null || piece2 == null) {
            assertNull(piece1);
            assertNull(piece2);
        } else {
            assertTrue((piece1.getName() == piece2.getName()) && (piece1.getColor() == piece2.getColor()));
        }
    }

    void assertCheck() {
        assertEquals(board.FindCheckStatus(), CHECK);
    }

    void assertCheckmate() {
        assertEquals(board.FindCheckStatus(), CHECKMATE);
    }

    void assertNoCheck() {
        assertEquals(board.FindCheckStatus(), NOT_IN_CHECK);
    }

}
