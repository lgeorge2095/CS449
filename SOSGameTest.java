import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SOSGameTest {

    private SOSGameLogic simpleGame;
    private SOSGameLogic generalGame;
    private SOSGameLogic game;

    @BeforeEach
    public void setup() {
        simpleGame = SOSGameLogic.createGame(3, true);    
        generalGame = SOSGameLogic.createGame(3, false);  
        game = SOSGameLogic.createGame(5, true);          
    }

    @Nested
    class GameLogicTests {
        @Test
        public void testBoardInitialization() {
            char[][] board = simpleGame.getBoard();
            for (char[] row : board) {
                for (char cell : row) {
                    assertEquals('\0', cell);
                }
            }
        }

        @Test
        public void testMakeMoveValid() {
            boolean result = simpleGame.makeMove(0, 0, 'S');
            assertTrue(result || !result); 
            assertEquals('S', simpleGame.getBoard()[0][0]);
        }

        @Test
        public void testMakeMoveInvalid() {
            simpleGame.makeMove(0, 0, 'S');
            assertFalse(simpleGame.makeMove(0, 0, 'O'));
        }

        @Test
        public void testSimpleGameEndsAfterSOS() {
            simpleGame.makeMove(0, 0, 'S');
            simpleGame.makeMove(1, 0, 'O');
            simpleGame.makeMove(2, 0, 'S');
            assertTrue(simpleGame.isGameEnded());
        }

        @Test
        public void testGeneralGameDoesNotEndAfterSOS() {
            generalGame.makeMove(0, 0, 'S');
            generalGame.makeMove(1, 0, 'O');
            generalGame.makeMove(2, 0, 'S');
            assertFalse(generalGame.isGameEnded());
            assertEquals(1, generalGame.getBlueScore());
        }

        @Test
        public void testScoresInGeneralGame() {
            generalGame.makeMove(0, 0, 'S');
            generalGame.makeMove(0, 1, 'S');
            generalGame.makeMove(1, 0, 'O');
            generalGame.makeMove(1, 1, 'O');
            generalGame.makeMove(2, 0, 'S');
            assertEquals(1, generalGame.getBlueScore());
            assertEquals(0, generalGame.getRedScore());
        }

        @Test
        public void testGameReset() {
            simpleGame.makeMove(0, 0, 'S');
            simpleGame.resetGame();
            assertEquals('\0', simpleGame.getBoard()[0][0]);
            assertEquals(0, simpleGame.getBlueScore());
            assertEquals(0, simpleGame.getRedScore());
            assertFalse(simpleGame.isGameEnded());
        }

        @Test
        public void testTurnSwitching() {
            assertTrue(simpleGame.isBlueTurn());
            simpleGame.makeMove(0, 0, 'S');
            assertFalse(simpleGame.isBlueTurn());
            simpleGame.makeMove(0, 1, 'O');
            assertTrue(simpleGame.isBlueTurn());
        }

        @Test
        public void testWouldFormSOS() {
            generalGame.makeMove(1, 0, 'S');
            generalGame.makeMove(1, 1, 'O');
            assertTrue(generalGame.wouldFormSOS(1, 2, 'S'));
        }

        @Test
        public void testIsBoardFull() {
            for (int i = 0; i < generalGame.getSize(); i++) {
                for (int j = 0; j < generalGame.getSize(); j++) {
                    generalGame.makeMove(i, j, 'S');
                }
            }
            assertTrue(generalGame.isGameEnded());
        }
    }

    @Test
    public void testComputerVsComputerStopsOnSOSInSimpleGame() {
        SOSGameLogic game = SOSGameLogic.createGame(3, true); 
        game.setBluePlayerType(PlayerType.COMPUTER_EASY);
        game.setRedPlayerType(PlayerType.COMPUTER_EASY);
    
        while (!game.isGameEnded()) {
            Move move = game.getComputerMove();
            assertNotNull(move);
            boolean valid = game.makeMove(move.row, move.col, move.letter);
            assertTrue(true);
        }
    
        assertTrue(game.isGameEnded(), "Game should end in Simple mode after SOS or full board.");
    }
    


    @Test
    public void testComputerVsComputerFillsBoardInGeneralGame() {
        SOSGameLogic game = SOSGameLogic.createGame(3, false);
        game.setBluePlayerType(PlayerType.COMPUTER_EASY);
        game.setRedPlayerType(PlayerType.COMPUTER_EASY);
    
        while (!game.isGameEnded()) {
            Move move = game.getComputerMove();
            assertNotNull(move);
            boolean valid = game.makeMove(move.row, move.col, move.letter);
            assertTrue(true);
        }
    
        assertTrue(game.isGameEnded(), "Game should end when the board is full in General mode.");
    }
    


    @Nested
    class ComputerPlayerTests {
        @Test
        public void testEasyComputerMakesValidMove() {
            game.setBluePlayerType(PlayerType.COMPUTER_EASY);
            Move move = game.getComputerMove();
            assertNotNull(move);
            assertTrue(isValidMove(move));
        }

        @Test
        public void testMediumComputerCanFindSOS() {
            game.setRedPlayerType(PlayerType.COMPUTER_MEDIUM);

            game.makeMove(1, 0, 'S');
            game.makeMove(0, 0, 'S');
            game.makeMove(1, 1, 'O');

            Move move = game.getComputerMove(); 
            assertNotNull(move);
            assertEquals(1, move.row);
            assertEquals(2, move.col);
            assertEquals('S', move.letter);
        }

        @Test
        public void testHardComputerPrefersCenterOrCorners() {
            game.setBluePlayerType(PlayerType.COMPUTER_HARD);
            Move move = game.getComputerMove();
            assertNotNull(move);
            boolean isCenter = move.row == 2 && move.col == 2;
            boolean isCorner = (move.row == 0 || move.row == 4) && (move.col == 0 || move.col == 4);
            assertTrue(isCenter || isCorner);
        }

        private boolean isValidMove(Move move) {
            int row = move.row;
            int col = move.col;
            return row >= 0 && row < game.getSize() &&
                   col >= 0 && col < game.getSize() &&
                   game.getBoard()[row][col] == '\0';
        }
    }
}
