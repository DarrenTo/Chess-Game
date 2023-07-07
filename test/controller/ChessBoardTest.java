package controller;

import org.junit.jupiter.api.*;

public class ChessBoardTest {
    private IChessBoard board;

    @BeforeAll
    static void beforeAll() {
        System.out.println("test");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("test2");
    }

    @Nested
    @DisplayName("FENSetup")
    class FENSetupTest {

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
