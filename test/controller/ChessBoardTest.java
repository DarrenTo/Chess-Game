package controller;

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
                assertFalse(board.FENSetup("pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("More than 8 rows")
            void TooManyRows() {
                assertFalse(board.FENSetup("pppppppp/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/RNBQKBNR w KQkq - 0 1"));
            }

            @Test
            @DisplayName("Return True, lots of pawns")
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
