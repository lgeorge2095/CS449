import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.IOException;

interface Game {
    boolean makeMove(int row, int col, char letter);
    boolean isGameEnded();
    void resetGame();
    int getSize();
}

enum PlayerType {
    HUMAN,
    COMPUTER_EASY,
    COMPUTER_MEDIUM,
    COMPUTER_HARD
}

class Move {
    int row;
    int col;
    char letter;
    
    public Move(int row, int col, char letter) {
        this.row = row;
        this.col = col;
        this.letter = letter;
    }
}

interface Player {
    Move getNextMove(SOSGameLogic game);
    PlayerType getType();
    boolean isComputer();
}

class HumanPlayer implements Player {
    @Override
    public Move getNextMove(SOSGameLogic game) {
        return null;
    }
    
    @Override
    public PlayerType getType() {
        return PlayerType.HUMAN;
    }
    
    @Override
    public boolean isComputer() {
        return false;
    }
}

abstract class ComputerPlayer implements Player {
    protected Random random = new Random();
    
    @Override
    public boolean isComputer() {
        return true;
    }
    
    protected Move findRandomMove(SOSGameLogic game) {
        int size = game.getSize();
        char[][] board = game.getBoard();
        List<Integer> emptyCells = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == '\0') {
                    emptyCells.add(i * size + j);
                }
            }
        }
        
        if (emptyCells.isEmpty()) {
            return null;
        }
        
        int randomIndex = random.nextInt(emptyCells.size());
        int cellIndex = emptyCells.get(randomIndex);
        int row = cellIndex / size;
        int col = cellIndex % size;
        char letter = random.nextBoolean() ? 'S' : 'O';
        
        return new Move(row, col, letter);
    }
}

class EasyComputerPlayer extends ComputerPlayer {
    @Override
    public Move getNextMove(SOSGameLogic game) {
        Move potentialMove = findPotentialSOS(game);
        if (potentialMove != null) {
            return potentialMove;
        }

        return findRandomMove(game);
    }

    private Move findPotentialSOS(SOSGameLogic game) {
        int size = game.getSize();
        char[][] board = game.getBoard();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == '\0') {
                    if (game.wouldFormSOS(i, j, 'S')) {
                        return new Move(i, j, 'S');
                    }
                    if (game.wouldFormSOS(i, j, 'O')) {
                        return new Move(i, j, 'O');
                    }
                }
            }
        }
        return null;
    }

    @Override
    public PlayerType getType() {
        return PlayerType.COMPUTER_EASY;
    }
}

class MediumComputerPlayer extends ComputerPlayer {
    @Override
    public Move getNextMove(SOSGameLogic game) {
        Move potentialMove = findPotentialSOS(game);
        if (potentialMove != null) {
            return potentialMove;
        }

        Move blockMove = findBlockingMove(game);
        if (blockMove != null) {
            return blockMove;
        }

        return findRandomMove(game);
    }

    protected Move findBlockingMove(SOSGameLogic game) {
        SOSGameLogic tempGame = game.createCopy();
        tempGame.toggleTurn(); 
        return findPotentialSOS(tempGame);
    }

    protected Move findPotentialSOS(SOSGameLogic game) {
        int size = game.getSize();
        char[][] board = game.getBoard();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == '\0') {
                    if (game.wouldFormSOS(i, j, 'S')) {
                        return new Move(i, j, 'S');
                    }
                    if (game.wouldFormSOS(i, j, 'O')) {
                        return new Move(i, j, 'O');
                    }
                }
            }
        }
        return null;
    }

    @Override
    public PlayerType getType() {
        return PlayerType.COMPUTER_MEDIUM;
    }
}

class HardComputerPlayer extends MediumComputerPlayer {
    @Override
    public Move getNextMove(SOSGameLogic game) {
        Move sosMove = findPotentialSOS(game);
        if (sosMove != null) {
            return sosMove;
        }

        Move blockMove = findBlockingMove(game);
        if (blockMove != null) {
            return blockMove;
        }

        Move strategicMove = findBestStrategicMove(game);
        if (strategicMove != null) {
            return strategicMove;
        }

        return findRandomMove(game);
    }

    private Move findBestStrategicMove(SOSGameLogic game) {
        int size = game.getSize();
        char[][] board = game.getBoard();
        Move bestMove = null;
        int maxScore = Integer.MIN_VALUE;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == '\0') {
                    for (char letter : new char[]{'S', 'O'}) {
                        int score = evaluateMove(game, i, j, letter);
                        if (score > maxScore) {
                            maxScore = score;
                            bestMove = new Move(i, j, letter);
                        }
                    }
                }
            }
        }
        return bestMove;
    }

    private int evaluateMove(SOSGameLogic game, int row, int col, char letter) {
        SOSGameLogic tempGame = game.createCopy();
        tempGame.makeMove(row, col, letter);

        int score = 0;
        if (tempGame.wouldFormSOS(row, col, letter)) {
            score += 10; 
        }

        score += countPotentialSOSOpportunities(tempGame, row, col);

        return score;
    }

    private int countPotentialSOSOpportunities(SOSGameLogic game, int row, int col) {
        int opportunities = 0;
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];
            int r1 = row + dr;
            int c1 = col + dc;
            int r2 = row - dr;
            int c2 = col - dc;

            if (game.isValidPosition(r1, c1) && game.isValidPosition(r2, c2)) {
                if (game.getBoard()[r1][c1] == '\0' || game.getBoard()[r2][c2] == '\0') {
                    opportunities++;
                }
            }
        }
        return opportunities;
    }

    @Override
    public PlayerType getType() {
        return PlayerType.COMPUTER_HARD;
    }
}

class PlayerFactory {
    public static Player createPlayer(PlayerType type) {
        switch (type) {
            case HUMAN:
                return new HumanPlayer();
            case COMPUTER_EASY:
                return new EasyComputerPlayer();
            case COMPUTER_MEDIUM:
                return new MediumComputerPlayer();
            case COMPUTER_HARD:
                return new HardComputerPlayer();
            default:
                return new HumanPlayer();
        }
    }
}

public abstract class SOSGameLogic implements Game {
    protected int size;
    protected char[][] board;
    protected boolean blueTurn = true;
    protected int blueScore = 0;
    protected int redScore = 0;
    protected boolean gameEnded = false;
    protected List<int[]> lastSOSCoordinates = new ArrayList<>();
    protected boolean isSimple;
    protected Player bluePlayer = PlayerFactory.createPlayer(PlayerType.HUMAN);
    protected Player redPlayer = PlayerFactory.createPlayer(PlayerType.HUMAN);
    private MoveRecorder moveRecorder = new MoveRecorder();
    
    public SOSGameLogic(int size, boolean isSimple) {
        this.size = size;
        this.isSimple = isSimple;
        this.board = new char[size][size];
        initializeBoard();
    }
    
    protected void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = '\0';
            }
        }
    }
    
    @Override
    public void resetGame() {
        blueTurn = true;
        blueScore = 0;
        redScore = 0;
        gameEnded = false;
        lastSOSCoordinates.clear();
        initializeBoard();
    }
    
    @Override
    public boolean makeMove(int row, int col, char letter) {
        if (gameEnded || board[row][col] != '\0') {
            return false;
        }

        board[row][col] = letter;

        boolean formedSOS = checkForSOS(row, col, letter);

        String color = blueTurn ? "Blue" : "Red";
        boolean isAI = blueTurn ? bluePlayer.isComputer() : redPlayer.isComputer();
        moveRecorder.recordMove(row, col, color, letter, isAI);

        if (formedSOS) {
            if (blueTurn) {
                blueScore++;
            } else {
                redScore++;
            }

            if (isSimple) {
                gameEnded = true;
            }
        } else {
            blueTurn = !blueTurn;
        }

        if (isBoardFull()) {
            gameEnded = true;
        }

        return formedSOS;
    }
    
    public Move getComputerMove() {
        Player currentPlayer = blueTurn ? bluePlayer : redPlayer;
        return currentPlayer.getNextMove(this);
    }
    
    public boolean wouldFormSOS(int row, int col, char letter) {
        char originalValue = board[row][col];
        board[row][col] = letter;
        boolean forms = checkForSOS(row, col, letter);
        board[row][col] = originalValue;
        return forms;
    }
    
    protected void toggleTurn() {
        blueTurn = !blueTurn;
    }
    
    public SOSGameLogic createCopy() {
        SOSGameLogic copy = SOSGameLogic.createGame(size, isSimple);
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                copy.board[i][j] = this.board[i][j];
            }
        }
        
        copy.blueTurn = this.blueTurn;
        copy.blueScore = this.blueScore;
        copy.redScore = this.redScore;
        copy.gameEnded = this.gameEnded;
        copy.bluePlayer = this.bluePlayer;
        copy.redPlayer = this.redPlayer;
        
        return copy;
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
            } else if (letter == 'O') {
                int rPrev = row - dr;
                int cPrev = col - dc;
                int rNext = row + dr;
                int cNext = col + dc;
                
                if (isValidPosition(rPrev, cPrev) && isValidPosition(rNext, cNext) && 
                    board[rPrev][cPrev] == 'S' && board[rNext][cNext] == 'S') {
                    formedSOS = true;
                    addSOSCoordinates(rPrev, cPrev, row, col, rNext, cNext);
                }
            }
        }
        
        return formedSOS;
    }

    private void addSOSCoordinates(int r1, int c1, int r2, int c2, int r3, int c3) {
        lastSOSCoordinates.add(new int[]{r1, c1});
        lastSOSCoordinates.add(new int[]{r2, c2});
        lastSOSCoordinates.add(new int[]{r3, c3});
    }

    protected boolean isValidPosition(int row, int col) {
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

    public static SOSGameLogic createGame(int size, boolean isSimple) {
        return new SOSGameImpl(size, isSimple);
    }

    @Override
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
        return lastSOSCoordinates;
    }
    
    public char[][] getBoard() {
        return board;
    }
    
    public boolean isSimpleGame() {
        return isSimple;
    }
    
    public void setBluePlayerType(PlayerType playerType) {
        this.bluePlayer = PlayerFactory.createPlayer(playerType);
    }
    
    public void setRedPlayerType(PlayerType playerType) {
        this.redPlayer = PlayerFactory.createPlayer(playerType);
    }
    
    public PlayerType getBluePlayerType() {
        return bluePlayer.getType();
    }
    
    public PlayerType getRedPlayerType() {
        return redPlayer.getType();
    }
    
    public boolean isCurrentPlayerComputer() {
        return blueTurn ? bluePlayer.isComputer() : redPlayer.isComputer();
    }

    public List<String> replayMoves(String filePath) throws IOException {
        resetGame();
        return moveRecorder.loadFromFile(filePath);
    }

    public void saveMoves(String filePath) throws IOException {
        moveRecorder.saveToFile(filePath);
    }
}

class SOSGameImpl extends SOSGameLogic {
    public SOSGameImpl(int size, boolean isSimple) {
        super(size, isSimple);
    }
}
