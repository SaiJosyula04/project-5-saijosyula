import java.util.Observable;

@SuppressWarnings("deprecation")
public class ReversiModel extends Observable {
    public ReversiBoard.Cell[][] grid;
    public ReversiBoard.Cell turn;
    public boolean done;

    public ReversiModel() {
        grid = new ReversiBoard.Cell[8][8];
        startGame();
    }

    public void startGame() {
        // make everything empty
        int row = 0;
        while (row < 8) {
            int col = 0;
            while (col < 8) {
                grid[row][col] = ReversiBoard.Cell.EMPTY;
                col++;
            }
            row++;
        }
        
        // put starting pieces in middle
        grid[3][3] = ReversiBoard.Cell.WHITE;
        grid[4][4] = ReversiBoard.Cell.WHITE;
        grid[3][4] = ReversiBoard.Cell.BLACK;
        grid[4][3] = ReversiBoard.Cell.BLACK;
        
        turn = ReversiBoard.Cell.WHITE;
        done = false;
        
        setChanged();
        notifyObservers(makeBoard());
    }

    public void loadOldGame(ReversiBoard old) {
        int i = 0;
        while (i < 8) {
            int j = 0;
            while (j < 8) {
                grid[i][j] = old.get(i, j);
                j++;
            }
            i++;
        }
        turn = ReversiBoard.Cell.WHITE;
        done = false;
        setChanged();
        notifyObservers(makeBoard());
    }

    public boolean checkMove(int row, int col, ReversiBoard.Cell who) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return false;
        }
        
        if (grid[row][col] != ReversiBoard.Cell.EMPTY) {
            return false;
        }
        
        // try all 8 ways
        boolean ok = false;
        if (lookDirection(row, col, -1, -1, who)) ok = true;
        if (lookDirection(row, col, -1, 0, who)) ok = true;
        if (lookDirection(row, col, -1, 1, who)) ok = true;
        if (lookDirection(row, col, 0, -1, who)) ok = true;
        if (lookDirection(row, col, 0, 1, who)) ok = true;
        if (lookDirection(row, col, 1, -1, who)) ok = true;
        if (lookDirection(row, col, 1, 0, who)) ok = true;
        if (lookDirection(row, col, 1, 1, who)) ok = true;
        
        return ok;
    }

    public boolean lookDirection(int row, int col, int rowAdd, int colAdd, ReversiBoard.Cell who) {
        ReversiBoard.Cell enemy = ReversiBoard.Cell.WHITE;
        if (who == ReversiBoard.Cell.WHITE) {
            enemy = ReversiBoard.Cell.BLACK;
        }
        
        int r = row + rowAdd;
        int c = col + colAdd;
        int enemyCount = 0;
        
        while (true) {
            if (r < 0 || r > 7 || c < 0 || c > 7) {
                break;
            }
            
            if (grid[r][c] == ReversiBoard.Cell.EMPTY) {
                break;
            }
            
            if (grid[r][c] == enemy) {
                enemyCount++;
                r = r + rowAdd;
                c = c + colAdd;
            } else if (grid[r][c] == who) {
                if (enemyCount > 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                break;
            }
        }
        return false;
    }

    public boolean doMove(int row, int col, ReversiBoard.Cell who) {
        if (checkMove(row, col, who) == false) {
            return false;
        }
        
        grid[row][col] = who;
        
        // flip in all directions
        flipStuff(row, col, -1, -1, who);
        flipStuff(row, col, -1, 0, who);
        flipStuff(row, col, -1, 1, who);
        flipStuff(row, col, 0, -1, who);
        flipStuff(row, col, 0, 1, who);
        flipStuff(row, col, 1, -1, who);
        flipStuff(row, col, 1, 0, who);
        flipStuff(row, col, 1, 1, who);
        
        setChanged();
        notifyObservers(makeBoard());
        return true;
    }

    public void flipStuff(int row, int col, int rowAdd, int colAdd, ReversiBoard.Cell who) {
        boolean canFlip = lookDirection(row, col, rowAdd, colAdd, who);
        if (canFlip == false) {
            return;
        }
        
        ReversiBoard.Cell enemy = ReversiBoard.Cell.WHITE;
        if (who == ReversiBoard.Cell.WHITE) {
            enemy = ReversiBoard.Cell.BLACK;
        }
        
        int r = row + rowAdd;
        int c = col + colAdd;
        
        while (grid[r][c] == enemy) {
            grid[r][c] = who;
            r = r + rowAdd;
            c = c + colAdd;
        }
    }

    public boolean anyMoves(ReversiBoard.Cell who) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (checkMove(row, col, who)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ReversiBoard makeBoard() {
        return new ReversiBoard(grid);
    }

    public int countPieces(ReversiBoard.Cell who) {
        int num = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (grid[row][col] == who) {
                    num = num + 1;
                }
            }
        }
        return num;
    }

    public ReversiBoard.Cell whoWon() {
        int whiteNum = countPieces(ReversiBoard.Cell.WHITE);
        int blackNum = countPieces(ReversiBoard.Cell.BLACK);
        
        if (whiteNum > blackNum) {
            return ReversiBoard.Cell.WHITE;
        }
        if (blackNum > whiteNum) {
            return ReversiBoard.Cell.BLACK;
        }
        return ReversiBoard.Cell.EMPTY;
    }
}