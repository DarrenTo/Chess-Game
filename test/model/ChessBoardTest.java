package model;

import org.junit.jupiter.api.*;

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
            @DisplayName("Valid No Castle")
            void ValidCharCastle3() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"));
            }
        }

        @Nested
        @DisplayName("En Passant Target")
        class EnPassantTest {
            @Test
            @DisplayName("Valid target a3")
            void ValidEnPassant() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq a3 0 1"));
            }

            @Test
            @DisplayName("Valid target h3")
            void ValidEnPassant2() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq h3 0 1"));
            }

            @Test
            @DisplayName("Valid target a6")
            void ValidEnPassant3() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq a6 0 1"));
            }

            @Test
            @DisplayName("Valid target h6")
            void ValidEnPassant4() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq h6 0 1"));
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
            @DisplayName("Invalid pawn in first row")
            void InvalidMisc12() {
                assertFalse(board.FENSetup("ppbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Invalid pawn in eighth row")
            void InvalidMisc13() {
                assertFalse(board.FENSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/PPBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Valid pawn in middle rows")
            void ValidMisc5() {
                assertTrue(board.FENSetup("rnbqkbnr/pppppppp/pp6/8/PP6/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
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

    }

    @Nested
    @DisplayName("FindCheck")
    class FindCheckTest {

    }

    @Nested
    @DisplayName("MovePiece")
    class MovePieceTest {

    }


}
