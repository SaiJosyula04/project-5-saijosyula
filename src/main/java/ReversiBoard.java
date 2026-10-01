
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class ReversiBoard implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final int SIZE = 8;

    public enum Cell {
        EMPTY, WHITE, BLACK
    }

    private final Cell[][] cells;

    public ReversiBoard(Cell[][] src) {
        cells = new Cell[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            cells[r] = Arrays.copyOf(src[r], SIZE);
        }
    }

    public Cell get(int r, int c) {
        return cells[r][c];
    }

    public int size() {
        return SIZE;
    }

    public int count(Cell who) {
        int n = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (cells[r][c] == who) {
                    n++;
                }
            }
        }
        return n;
    }
}
