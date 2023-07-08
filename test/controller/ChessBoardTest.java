package controller;

import org.junit.jupiter.api.*;

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

        @Test
        @DisplayName("Test")
        void sampleTest() {
            System.out.println("example");
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
