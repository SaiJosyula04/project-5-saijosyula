import static org.junit.jupiter.api.Assertions.*;

class Test {

    @org.junit.jupiter.api.Test
    void testStartBoardCounts() {
        ReversiModel model = new ReversiModel();
        assertEquals(2, model.countPieces(ReversiBoard.Cell.WHITE));
        assertEquals(2, model.countPieces(ReversiBoard.Cell.BLACK));
    }

    @org.junit.jupiter.api.Test
    void testLegalMoveWorks() {
        ReversiModel model = new ReversiModel();
        boolean result = model.doMove(2, 3, ReversiBoard.Cell.WHITE);
        assertTrue(result);  // should be valid on starting board
        assertEquals(ReversiBoard.Cell.WHITE, model.grid[2][3]);
    }
}
