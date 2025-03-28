import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SOSGameLogic {
    private static final Logger LOGGER = Logger.getLogger(SOSGameLogic.class.getName());
    
    private int size;
    private boolean isSimple;
    private char[][] board;
    private boolean blueTurn = true;
    private int blueScore = 0;
    private int redScore = 0;
    private boolean gameEnded = false;
    private List<int[]> lastSOSCoordinates = new ArrayList<>();

    public SOSGameLogic(int size, boolean isSimple) {
        validateBoardSize(size);
        this.size = size;
        this.isSimple = isSimple;
        this.board = new char[size][size];
        initializeBoard();
    }

    private void validateBoardSize(int size) {
        if (size < SOSGameConfig.MIN_BOARD_SIZE || size > SOSGameConfig.MAX_BOARD_SIZE) {
            throw new IllegalArgumentException(
                "Board size must be between " + SOSGameConfig.MIN_BOARD_SIZE + 
                " and " + SOSGameConfig.MAX_BOARD_SIZE
            );
        }
    }

    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = '\0';
            }
        }
    }

    public boolean makeMove(int row, int col, char letter) {
        LOGGER.info(() -> String.format("Attempt to make move: row=%d, col=%d, letter=%c", row, col, letter));
        
        if (gameEnded || !isValidMove(row, col)) {
            return false;
        }
    
        board[row][col] = letter;
        
        boolean formedSOS = checkForSOS(row, col, letter);
        
        if (formedSOS) {
            updateScore();
            
            if (isSimple) {
                gameEnded = true;
                return true;
            }
        } else {
            switchTurn();
        }
        
        checkGameEnd();
        
        return formedSOS;
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size && board[row][col] == '\0';
    }

    private void updateScore() {
        if (blueTurn) {
            blueScore++;
        } else {
            redScore++;
        }
    }

    private void switchTurn() {
        blueTurn = !blueTurn;
    }

    private void checkGameEnd() {
        if (isBoardFull()) {
            gameEnded = true;
        }
    }

    private boolean checkForSOS(int row, int col, char letter) {
        lastSOSCoordinates.clear();
        boolean formedSOS = false;
        
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        
        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];
            
            if (letter == 'S') {
                formedSOS |= checkSPattern(row, col, dr, dc);
            } else if (letter == 'O') {
                formedSOS |= checkOPattern(row, col, dr, dc);
            }
        }
        
        return formedSOS;
    }

    private boolean checkSPattern(int row, int col, int dr, int dc) {
        boolean formedSOS = false;
        
        int r1 = row + dr;
        int c1 = col + dc;
        int r2 = row + 2 * dr;
        int c2 = col + 2 * dc;
        
        if (isValidPosition(r1, c1) && isValidPosition(r2, c2) && 
            board[r1][c1] == 'O' && board[r2][c2] == 'S') {
            formedSOS = true;
            addSOSCoordinates(row, col, r1, c1, r2, c2);
        }
        
        r1 = row - dr;
        c1 = col - dc;
        r2 = row - 2 * dr;
        c2 = col - 2 * dc;
        
        if (isValidPosition(r1, c1) && isValidPosition(r2, c2) && 
            board[r1][c1] == 'O' && board[r2][c2] == 'S') {
            formedSOS = true;
            addSOSCoordinates(r2, c2, r1, c1, row, col);
        }
        
        return formedSOS;
    }

    private boolean checkOPattern(int row, int col, int dr, int dc) {
        int rPrev = row - dr;
        int cPrev = col - dc;
        int rNext = row + dr;
        int cNext = col + dc;
        
        if (isValidPosition(rPrev, cPrev) && isValidPosition(rNext, cNext) && 
            board[rPrev][cPrev] == 'S' && board[rNext][cNext] == 'S') {
            addSOSCoordinates(rPrev, cPrev, row, col, rNext, cNext);
            return true;
        }
        
        return false;
    }

    private void addSOSCoordinates(int r1, int c1, int r2, int c2, int r3, int c3) {
        lastSOSCoordinates.add(new int[]{r1, c1});
        lastSOSCoordinates.add(new int[]{r2, c2});
        lastSOSCoordinates.add(new int[]{r3, c3});
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    public SOSGameState getGameState() {
        return new SOSGameState(
            blueScore, 
            redScore, 
            gameEnded, 
            blueTurn, 
            new ArrayList<>(lastSOSCoordinates)
        );
    }

    public int getSize() {
        return size;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public boolean isBlueTurn() {
        return blueTurn;
    }

    public int getBlueScore() {
        return blueScore;
    }

    public int getRedScore() {
        return redScore;
    }

    public List<int[]> getLastSOSCoordinates() {
        return new ArrayList<>(lastSOSCoordinates);
    }

    public char[][] getBoard() {
        char[][] copyBoard = new char[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, copyBoard[i], 0, size);
        }
        return copyBoard;
    }

    public boolean isSimpleGame() {
        return isSimple;
    }

    public void resetGame() {
        initializeBoard();
        blueTurn = true;
        blueScore = 0;
        redScore = 0;
        gameEnded = false;
        lastSOSCoordinates.clear();
    }

    public void setGameMode(boolean isSimple) {
        if (!gameEnded) {
            this.isSimple = isSimple;
        } else {
            throw new IllegalStateException("Cannot change after game end");
        }
    }
}