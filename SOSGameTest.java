import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SOSGameTest {
    private SOSGameLogic gameLogic;
    private SOSGameGUI gameGUI;

    @BeforeEach
    public void setUp() {
        gameLogic = new SOSGameLogic(3, true);
        gameGUI = new SOSGameGUI(); 
    }

    @Test
    public void testBoardSizeValidation_ValidSize() {
        int[] validSizes = {3, 6, 9, 12};
        for (int size : validSizes) {
            SOSGameLogic validGame = new SOSGameLogic(size, true);
            assertEquals(size, validGame.getSize(), "Board size accepted");
        }
    }

    @Test
    public void testBoardSizeValidation_InvalidSmallSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SOSGameLogic(2, true);
        }, "Board size less than 3 should throw an exception");
    }

    @Test
    public void testBoardSizeValidation_InvalidLargeSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SOSGameLogic(13, true);
        }, "Board size greater than 12 should throw an exception");
    }

    @Test
    public void testGameModeSelection_SimpleMode() {
        SOSGameLogic simpleGame = new SOSGameLogic(3, true);
        assertTrue(simpleGame.isSimpleGame(), "Game should be simple mode");
    }

    @Test
    public void testGameModeSelection_GeneralMode() {
        SOSGameLogic generalGame = new SOSGameLogic(3, false);
        assertFalse(generalGame.isSimpleGame(), "Game should be general mode");
    }

    @Test
    public void testMakeMove_OccupiedSpot() {
        gameLogic.makeMove(0, 0, 'S');
        assertFalse(gameLogic.makeMove(0, 0, 'O'), "Should not be able to move there");
    }

    @Test
    public void testSOSFormation_SimpleMode() {
        gameLogic.makeMove(0, 0, 'S');
        gameLogic.makeMove(0, 1, 'O');
        assertTrue(gameLogic.makeMove(0, 2, 'S'), "SOS formation should end the game in simple mode");
        assertTrue(gameLogic.isGameEnded(), "Game should end after SOS formation in simple mode");
    }

    @Test
    public void testSOSFormation_GeneralMode() {
        SOSGameLogic generalGame = new SOSGameLogic(3, false);
        generalGame.makeMove(0, 0, 'S');
        generalGame.makeMove(0, 1, 'O');
        assertTrue(generalGame.makeMove(0, 2, 'S'), "SOS formation should increase score in general mode");
        assertFalse(generalGame.isGameEnded(), "Game should continue in general mode after SOS");
    }

    @Test
    public void testGameEnd_FullBoard() {
        SOSGameLogic fullBoardGame = new SOSGameLogic(3, false);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                fullBoardGame.makeMove(i, j, i % 2 == 0 ? 'S' : 'O');
            }
        }
        assertTrue(fullBoardGame.isGameEnded(), "Game should end when board is full");
    }
}


/* Manual Test Guide

1. Board Size Selection

Verify board size input field accepts valid sizes (3-12)
1. Open game
2. Enter board sizes 3, 6, 9, 12
3. Click "New Game"
4. Verify board is created correctly

Verify error handling for invalid board sizes
1. Enter board sizes 2, 13, 0, -1
2. Verify error message is displayed
3. Confirm game does not start

2. Game Mode Selection

Verify Simple Game Mode
1. Game set to Simple
2. Start new game
3. Form an SOS
4. Verify game immediately ends

Verify General Game Mode
1. Select "General game" radio button
2. Start new game
3. Form multiple SOSes
4. Verify game continues until board is full
5. Verify final score determines winner

3. Move Checking

Move Validation
Verify player can only place letter on empty spots
1. Try to place letter on an already occupied spot
2. Verify move is not allowed

Scoring and Game End
Verify score tracking in General Mode
1. Form multiple SOSes
2. Verify correct player score is displayed

4. Game End Checking

Verify game end conditions
1. Fill entire board
2. Verify game ends
3. Verify winner is correctly determined or draw is declared
*/