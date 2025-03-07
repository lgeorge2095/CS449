import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.Field;


class TestableGameFrame extends GameFrame {
    public TestableGameFrame() {
        super();
    }
    
    public JRadioButton getBlueS() {
        try {
            Field field = GameFrame.class.getDeclaredField("blueS");
            field.setAccessible(true);
            return (JRadioButton) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Could not access blueS field", e);
        }
    }
    
    public JRadioButton getBlueO() {
        try {
            Field field = GameFrame.class.getDeclaredField("blueO");
            field.setAccessible(true);
            return (JRadioButton) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Could not access blueO field", e);
        }
    }
    
    public JRadioButton getRedS() {
        try {
            Field field = GameFrame.class.getDeclaredField("redS");
            field.setAccessible(true);
            return (JRadioButton) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Could not access redS field", e);
        }
    }
    
    public JRadioButton getRedO() {
        try {
            Field field = GameFrame.class.getDeclaredField("redO");
            field.setAccessible(true);
            return (JRadioButton) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Could not access redO field", e);
        }
    }
}
 
public class SOSGameTest {
    private GameFrame frame;
    
    @Before
    public void setUp() {
        frame = new TestableGameFrame();
    }
    
    @Test
    public void testChooseBoardSizeValid() {
        frame.boardSizeField.setText("6");
        frame.startNewGame();
        
        assertEquals(6, getFieldValue(frame, "boardSize"));
        
        GameFrame.GameBoard gameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        JButton[][] buttons = (JButton[][]) getFieldValue(gameBoard, "buttons");
        assertEquals(6, buttons.length);
        assertEquals(6, buttons[0].length);
    }
    
    @Test
    public void testChooseBoardSizeInvalid() {
        GameFrame.GameBoard originalGameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        
        frame.boardSizeField.setText("2");
        
        frame.startNewGame();
        GameFrame.GameBoard currentGameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        assertNotEquals(3, getFieldValue(frame, "boardSize"));
        frame.boardSizeField.setText("abc");
        frame.startNewGame();
        currentGameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        assertNotEquals("abc", getFieldValue(frame, "boardSize").toString());
    }
    
    @Test
    public void testChooseSimpleGameMode() {
        JRadioButton generalGameRadio = (JRadioButton) getFieldValue(frame, "generalGameRadioButton");
        generalGameRadio.setSelected(true);
        frame.startNewGame();
        
        assertFalse((Boolean) getFieldValue(frame, "isSimpleGame"));
        
        JRadioButton simpleGameRadio = (JRadioButton) getFieldValue(frame, "simpleGameRadioButton");
        simpleGameRadio.setSelected(true);
        frame.startNewGame();
        
        assertTrue((Boolean) getFieldValue(frame, "isSimpleGame"));
    }
    
    @Test
    public void testChooseGeneralGameMode() {
        assertTrue((Boolean) getFieldValue(frame, "isSimpleGame"));
        
        JRadioButton generalGameRadio = (JRadioButton) getFieldValue(frame, "generalGameRadioButton");
        generalGameRadio.setSelected(true);
        frame.startNewGame();
        
        assertFalse((Boolean) getFieldValue(frame, "isSimpleGame"));
        
        JRadioButton simpleGameRadio = (JRadioButton) getFieldValue(frame, "simpleGameRadioButton");
        assertFalse(simpleGameRadio.isSelected());
        assertTrue(generalGameRadio.isSelected());
    }
    
    @Test
    public void testStartNewGameWithDifferentSize() {
        GameFrame.GameBoard initialGameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        
        frame.boardSizeField.setText("10");
        frame.startNewGame();
        
        GameFrame.GameBoard newGameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        assertEquals(10, getFieldValue(frame, "boardSize"));
        assertNotSame(initialGameBoard, newGameBoard);
        JButton[][] buttons = (JButton[][]) getFieldValue(newGameBoard, "buttons");
        assertEquals(10, buttons.length);
        assertEquals(10, buttons[0].length);
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertEquals("", buttons[i][j].getText());
            }
        }
        
        JLabel statusLabel = (JLabel) getFieldValue(frame, "statusLabel");
        assertEquals("Current turn: blue", statusLabel.getText());
    }
    
    @Test
    public void testStartNewGameWithDifferentMode() {
        assertTrue((Boolean) getFieldValue(frame, "isSimpleGame"));
        
        GameFrame.GameBoard gameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        TestableGameFrame testFrame = (TestableGameFrame) frame;
        
        testFrame.getBlueS().setSelected(true);
        gameBoard.makeMove(0, 0);
        
        testFrame.getRedO().setSelected(true);
        gameBoard.makeMove(0, 1);
        
        JRadioButton generalGameRadio = (JRadioButton) getFieldValue(frame, "generalGameRadioButton");
        generalGameRadio.setSelected(true);
        frame.startNewGame();
        
        assertFalse((Boolean) getFieldValue(frame, "isSimpleGame"));
        
        GameFrame.GameBoard newGameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        JButton[][] buttons = (JButton[][]) getFieldValue(newGameBoard, "buttons");
        
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                assertEquals("", buttons[i][j].getText());
            }
        }
        
        assertEquals(0, getFieldValue(newGameBoard, "blueScore"));
        assertEquals(0, getFieldValue(newGameBoard, "redScore"));
    }
    
    @Test
    public void testSimpleGameEndOnSOS() {
        JRadioButton simpleGameRadio = (JRadioButton) getFieldValue(frame, "simpleGameRadioButton");
        simpleGameRadio.setSelected(true);
        frame.startNewGame();
        
        GameFrame.GameBoard gameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        JButton[][] buttons = (JButton[][]) getFieldValue(gameBoard, "buttons");
        TestableGameFrame testFrame = (TestableGameFrame) frame;
        
        testFrame.getBlueS().setSelected(true);
        gameBoard.makeMove(1, 0); 
        assertEquals("S", buttons[1][0].getText());
        assertEquals(Color.BLUE, buttons[1][0].getForeground());
    
        testFrame.getRedO().setSelected(true);
        gameBoard.makeMove(1, 1); 
        assertEquals("O", buttons[1][1].getText());
        assertEquals(Color.RED, buttons[1][1].getForeground());
    
        testFrame.getBlueS().setSelected(true);
        gameBoard.makeMove(1, 2);
        assertEquals("S", buttons[1][2].getText());
        assertEquals(Color.BLUE, buttons[1][2].getForeground());
    
        assertTrue((Boolean) getFieldValue(gameBoard, "gameEnded"));
    
        JLabel statusLabel = (JLabel) getFieldValue(frame, "statusLabel");
        assertTrue(statusLabel.getText().contains("Blue wins") || statusLabel.getText().contains("Game Over"));
    }
    
    
    @Test
    public void testSimpleGameTurnSwitching() {
        JRadioButton simpleGameRadio = (JRadioButton) getFieldValue(frame, "simpleGameRadioButton");
        simpleGameRadio.setSelected(true);
        frame.startNewGame();
        
        GameFrame.GameBoard gameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        JButton[][] buttons = (JButton[][]) getFieldValue(gameBoard, "buttons");
        TestableGameFrame testFrame = (TestableGameFrame) frame;
        
        assertTrue((Boolean) getFieldValue(gameBoard, "blueTurn"));

        testFrame.getBlueS().setSelected(true);
        gameBoard.makeMove(0, 0);
        assertEquals("S", buttons[0][0].getText());
        assertEquals(Color.BLUE, buttons[0][0].getForeground());
        
        assertFalse((Boolean) getFieldValue(gameBoard, "blueTurn"));
        JLabel statusLabel = (JLabel) getFieldValue(frame, "statusLabel");
        assertEquals("Current turn: red", statusLabel.getText());
        
        testFrame.getRedO().setSelected(true);
        gameBoard.makeMove(0, 1);
        assertEquals("O", buttons[0][1].getText());
        assertEquals(Color.RED, buttons[0][1].getForeground());
        
        assertTrue((Boolean) getFieldValue(gameBoard, "blueTurn"));
        statusLabel = (JLabel) getFieldValue(frame, "statusLabel");
        assertEquals("Current turn: blue", statusLabel.getText());
    }
    
    @Test
    public void testGeneralGameContinuesAfterSOS() {
        JRadioButton generalGameRadio = (JRadioButton) getFieldValue(frame, "generalGameRadioButton");
        generalGameRadio.setSelected(true);
        frame.startNewGame();
        
        GameFrame.GameBoard gameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        JButton[][] buttons = (JButton[][]) getFieldValue(gameBoard, "buttons");
        TestableGameFrame testFrame = (TestableGameFrame) frame;
        
        testFrame.getBlueS().setSelected(true);
        gameBoard.makeMove(0, 0);
        assertEquals("S", buttons[0][0].getText());
        testFrame.getRedO().setSelected(true);
        gameBoard.makeMove(0, 1);
        assertEquals("O", buttons[0][1].getText());
        testFrame.getBlueS().setSelected(true);
        gameBoard.makeMove(0, 2);
        assertEquals("S", buttons[0][2].getText());
        
        assertFalse((Boolean) getFieldValue(gameBoard, "gameEnded"));
        assertEquals(1, getFieldValue(gameBoard, "blueScore"));
        assertTrue((Boolean) getFieldValue(gameBoard, "blueTurn"));
        
        testFrame.getBlueO().setSelected(true);
        gameBoard.makeMove(1, 0);
        assertEquals("O", buttons[1][0].getText());
        assertFalse((Boolean) getFieldValue(gameBoard, "blueTurn"));
    }
    
    @Test
    public void testGeneralGameScoreTracking() {
        JRadioButton generalGameRadio = (JRadioButton) getFieldValue(frame, "generalGameRadioButton");
        generalGameRadio.setSelected(true);
        frame.startNewGame();
        
        GameFrame.GameBoard gameBoard = (GameFrame.GameBoard) getFieldValue(frame, "gameBoard");
        JButton[][] buttons = (JButton[][]) getFieldValue(gameBoard, "buttons");
        TestableGameFrame testFrame = (TestableGameFrame) frame;
        
        assertEquals(0, getFieldValue(gameBoard, "blueScore"));
        assertEquals(0, getFieldValue(gameBoard, "redScore"));
        testFrame.getBlueS().setSelected(true);
        gameBoard.makeMove(0, 0);
        testFrame.getBlueO().setSelected(true);
        gameBoard.makeMove(0, 1);
        testFrame.getBlueS().setSelected(true);
        gameBoard.makeMove(0, 2);
        assertEquals(0, getFieldValue(gameBoard, "blueScore"));
        assertEquals(0, getFieldValue(gameBoard, "redScore"));
        testFrame.getBlueS().setSelected(true);
        gameBoard.makeMove(1, 0);
        testFrame.getRedS().setSelected(true);
        gameBoard.makeMove(1, 1);
        testFrame.getRedO().setSelected(true);
        gameBoard.makeMove(1, 2);
        testFrame.getRedS().setSelected(true);
        gameBoard.makeMove(1, 2);
        
        assertEquals(0, getFieldValue(gameBoard, "blueScore"));
        assertEquals(0, getFieldValue(gameBoard, "redScore"));
    }
    
    private Object getFieldValue(Object object, String fieldName) {
        try {
            Field field = findField(object.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException("Could not access field: " + fieldName, e);
        }
    }
    
    private Field findField(Class<?> clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                field = findField(clazz.getSuperclass(), fieldName);
            }
        }
        return field;
    }
}