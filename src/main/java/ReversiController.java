import java.util.ArrayList;
import java.util.Random;

public class ReversiController {
    ReversiModel theModel;

    public ReversiController(ReversiModel mod) {
        theModel = mod;
    }

    public boolean humanTurn(int row, int col) {
        if (theModel.done == true) {
            return false;
        }

        if (theModel.turn != ReversiBoard.Cell.WHITE) {
            return false;
        }

        boolean worked = theModel.doMove(row, col, ReversiBoard.Cell.WHITE);
        
        if (worked) {
            theModel.turn = ReversiBoard.Cell.BLACK;
            
            // see if black can go
            boolean blackCanGo = theModel.anyMoves(ReversiBoard.Cell.BLACK);
            if (blackCanGo == false) {
                boolean whiteCanGo = theModel.anyMoves(ReversiBoard.Cell.WHITE);
                if (whiteCanGo == false) {
                    theModel.done = true;
                } else {
                    theModel.turn = ReversiBoard.Cell.WHITE;
                }
            }
            return true;
        }
        return false;
    }

    public void computerTurn() {
        if (theModel.done) {
            return;
        }

        if (theModel.turn != ReversiBoard.Cell.BLACK) {
            return;
        }

        // get all The moves
        ArrayList<Integer> rows = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (theModel.checkMove(r, c, ReversiBoard.Cell.BLACK)) {
                    rows.add(r);
                    cols.add(c);
                }
            }
        }

        int numMoves = rows.size();
        
        if (numMoves == 0) {
            boolean whiteCanGo = theModel.anyMoves(ReversiBoard.Cell.WHITE);
            if (whiteCanGo == false) {
                theModel.done = true;
            } else {
                theModel.turn = ReversiBoard.Cell.WHITE;
            }
            return;
        }

        // find which move gives most pieces
        int bestScore = -1;
        ArrayList<Integer> bestRows = new ArrayList<>();
        ArrayList<Integer> bestCols = new ArrayList<>();

        int idx = 0;
        while (idx < numMoves) {
            int testRow = rows.get(idx);
            int testCol = cols.get(idx);
            int points = tryMove(testRow, testCol);
            
            if (points > bestScore) {
                bestScore = points;
                bestRows.clear();
                bestCols.clear();
                bestRows.add(testRow);
                bestCols.add(testCol);
            } else if (points == bestScore) {
                bestRows.add(testRow);
                bestCols.add(testCol);
            }
            idx++;
        }

        // pick random from best
        Random r = new Random();
        int pick = r.nextInt(bestRows.size());
        int moveRow = bestRows.get(pick);
        int moveCol = bestCols.get(pick);
        
        theModel.doMove(moveRow, moveCol, ReversiBoard.Cell.BLACK);
        theModel.turn = ReversiBoard.Cell.WHITE;

        boolean whiteCanGo = theModel.anyMoves(ReversiBoard.Cell.WHITE);
        if (whiteCanGo == false) {
            boolean blackCanGo = theModel.anyMoves(ReversiBoard.Cell.BLACK);
            if (blackCanGo == false) {
                theModel.done = true;
            } else {
                theModel.turn = ReversiBoard.Cell.BLACK;
            }
        }
    }

    public int tryMove(int row, int col) {
        // copy the board
        ReversiBoard.Cell[][] copy = new ReversiBoard.Cell[8][8];
        int i = 0;
        while (i < 8) {
            int j = 0;
            while (j < 8) {
                copy[i][j] = theModel.grid[i][j];
                j++;
            }
            i++;
        }
        
        copy[row][col] = ReversiBoard.Cell.BLACK;
        
        // flip pieces 
        fakeFlipper(copy, row, col, -1, -1);
        fakeFlipper(copy, row, col, -1, 0);
        fakeFlipper(copy, row, col, -1, 1);
        fakeFlipper(copy, row, col, 0, -1);
        fakeFlipper(copy, row, col, 0, 1);
        fakeFlipper(copy, row, col, 1, -1);
        fakeFlipper(copy, row, col, 1, 0);
        fakeFlipper(copy, row, col, 1, 1);
        
        // count blacks
        int total = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (copy[r][c] == ReversiBoard.Cell.BLACK) {
                    total++;
                }
            }
        }
        return total;
    }

    public void fakeFlipper(ReversiBoard.Cell[][] b, int row, int col, int rAdd, int cAdd) {
        int r = row + rAdd;
        int c = col + cAdd;
        ArrayList<Integer> flipR = new ArrayList<>();
        ArrayList<Integer> flipC = new ArrayList<>();
        
        while (r >= 0 && r < 8 && c >= 0 && c < 8) {
            if (b[r][c] == ReversiBoard.Cell.EMPTY) {
                return;
            }
            if (b[r][c] == ReversiBoard.Cell.WHITE) {
                flipR.add(r);
                flipC.add(c);
                r = r + rAdd;
                c = c + cAdd;
            } else if (b[r][c] == ReversiBoard.Cell.BLACK) {
                // flip all the whites 
                for (int x = 0; x < flipR.size(); x++) {
                    b[flipR.get(x)][flipC.get(x)] = ReversiBoard.Cell.BLACK;
                }
                return;
            }
        }
    }

    public boolean gameOver() {
        return theModel.done;
    }

    public ReversiBoard.Cell winner() {
        return theModel.whoWon();
    }

    public int score(ReversiBoard.Cell p) {
        return theModel.countPieces(p);
    }
}