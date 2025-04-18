import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.border.TitledBorder;

interface GameController {
    boolean makeMove(int row, int col, char letter);
    void startNewGame(int size, boolean isSimple);
    boolean isGameEnded();
    boolean isBlueTurn();
    List<int[]> getSOSCoordinates();
    int getBlueScore();
    int getRedScore();
    void setBluePlayerType(PlayerType type);
    void setRedPlayerType(PlayerType type);
    boolean isCurrentPlayerComputer();
    Move getComputerMove();
}

public class SOSGameGUI {
    private class SOSGameController implements GameController {
        private SOSGameLogic gameLogic;
        
        public SOSGameController(int initialSize, boolean isSimple) {
            this.gameLogic = SOSGameLogic.createGame(initialSize, isSimple);
        }
        
        @Override
        public boolean makeMove(int row, int col, char letter) {
            return gameLogic.makeMove(row, col, letter);
        }
        
        @Override
        public void startNewGame(int size, boolean isSimple) {
            this.gameLogic = SOSGameLogic.createGame(size, isSimple);
        }
        
        @Override
        public boolean isGameEnded() {
            return gameLogic.isGameEnded();
        }
        
        @Override
        public boolean isBlueTurn() {
            return gameLogic.isBlueTurn();
        }
        
        @Override
        public List<int[]> getSOSCoordinates() {
            return gameLogic.getLastSOSCoordinates();
        }
        
        @Override
        public int getBlueScore() {
            return gameLogic.getBlueScore();
        }
        
        @Override
        public int getRedScore() {
            return gameLogic.getRedScore();
        }
        
        @Override
        public void setBluePlayerType(PlayerType type) {
            gameLogic.setBluePlayerType(type);
        }
        
        @Override
        public void setRedPlayerType(PlayerType type) {
            gameLogic.setRedPlayerType(type);
        }
        
        @Override
        public boolean isCurrentPlayerComputer() {
            return gameLogic.isCurrentPlayerComputer();
        }
        
        @Override
        public Move getComputerMove() {
            return gameLogic.getComputerMove();
        }
    }
    
    private JFrame frame;
    private JButton[][] buttons;
    private JLabel statusLabel;
    private JRadioButton blueS;
    private JRadioButton blueO;
    private JRadioButton redS;
    private JRadioButton redO;
    private JRadioButton simpleGameRadioButton;
    private JRadioButton generalGameRadioButton;
    private JTextField boardSizeField;
    private JComboBox<String> bluePlayerComboBox;
    private JComboBox<String> redPlayerComboBox;
    private GameController controller;
    private Timer computerMoveTimer;

    public SOSGameGUI() {
        controller = new SOSGameController(3, true);
        
        computerMoveTimer = new Timer(500, e -> {
            if (!controller.isGameEnded() && controller.isCurrentPlayerComputer()) {
                makeComputerMove();
            } else {
                computerMoveTimer.stop();
            }
        });
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("SOS Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        createLayout();
        frame.setVisible(true);
    }
    
    private void createLayout() {
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setBackground(Color.WHITE);
        
        JPanel topPanel = createTopPanel();
        contentPane.add(topPanel, BorderLayout.NORTH);
        
        int boardSize = 3;
        JPanel gameBoardPanel = createGameBoardPanel(boardSize);
        contentPane.add(gameBoardPanel, BorderLayout.CENTER);
        
        JPanel bluePanel = createPlayerPanel("Blue player", true);
        contentPane.add(bluePanel, BorderLayout.WEST);
        
        JPanel redPanel = createPlayerPanel("Red player", false);
        contentPane.add(redPanel, BorderLayout.EAST);
        
        statusLabel = new JLabel("Current turn: blue", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        contentPane.add(statusLabel, BorderLayout.SOUTH);
        
        checkAndStartComputerTurn();
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        topPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("SOS Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel);
        
        simpleGameRadioButton = new JRadioButton("Simple game", true);
        generalGameRadioButton = new JRadioButton("General game");
        
        ButtonGroup gameModeGroup = new ButtonGroup();
        gameModeGroup.add(simpleGameRadioButton);
        gameModeGroup.add(generalGameRadioButton);
        
        topPanel.add(simpleGameRadioButton);
        topPanel.add(generalGameRadioButton);
        
        topPanel.add(new JLabel("Board size"));
        boardSizeField = new JTextField("3", 2);
        topPanel.add(boardSizeField);
        
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> startNewGame());
        topPanel.add(newGameButton);
        
        return topPanel;
    }
    
    private JPanel createGameBoardPanel(int size) {
        JPanel gameBoard = new JPanel(new GridLayout(size, size));
        gameBoard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gameBoard.setName("gameBoard");
        
        buttons = new JButton[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final int row = i;
                final int col = j;
                
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("SansSerif", Font.BOLD, 18));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                buttons[i][j].setBackground(Color.WHITE);
                
                buttons[i][j].addActionListener(e -> {
                    if (!controller.isGameEnded() && !controller.isCurrentPlayerComputer()) {
                        makeMove(row, col);
                    }
                });
                
                gameBoard.add(buttons[i][j]);
            }
        }
        
        return gameBoard;
    }
    
    private JPanel createPlayerPanel(String title, boolean isBlue) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            title, 
            TitledBorder.CENTER, 
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            isBlue ? Color.BLUE : Color.RED
        ));
        
        JPanel playerTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerTypePanel.setBackground(Color.WHITE);
        playerTypePanel.add(new JLabel("Player type:"));
        
        String[] playerTypes = {"Human", "Computer (Easy)", "Computer (Medium)", "Computer (Hard)"};
        JComboBox<String> playerTypeCombo = new JComboBox<>(playerTypes);
        playerTypeCombo.setPreferredSize(new Dimension(150, 25));
        playerTypePanel.add(playerTypeCombo);
        
        if (isBlue) {
            bluePlayerComboBox = playerTypeCombo;
            playerTypeCombo.addActionListener(e -> {
                PlayerType type = getPlayerTypeFromSelection(playerTypeCombo.getSelectedIndex());
                controller.setBluePlayerType(type);
                checkAndStartComputerTurn();
            });
        } else {
            redPlayerComboBox = playerTypeCombo;
            playerTypeCombo.addActionListener(e -> {
                PlayerType type = getPlayerTypeFromSelection(playerTypeCombo.getSelectedIndex());
                controller.setRedPlayerType(type);
                checkAndStartComputerTurn();
            });
        }
        
        panel.add(playerTypePanel);
        panel.add(Box.createVerticalStrut(20));
        
        JLabel letterLabel = new JLabel("Select letter:");
        letterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(letterLabel);
        
        JRadioButton sButton = new JRadioButton("S", true);
        JRadioButton oButton = new JRadioButton("O");
        
        if (isBlue) {
            blueS = sButton;
            blueO = oButton;
            sButton.setForeground(Color.BLUE);
            oButton.setForeground(Color.BLUE);
        } else {
            redS = sButton;
            redO = oButton;
            sButton.setForeground(Color.RED);
            oButton.setForeground(Color.RED);
        }
        
        ButtonGroup letterGroup = new ButtonGroup();
        letterGroup.add(sButton);
        letterGroup.add(oButton);
        
        JPanel sPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sPanel.setBackground(Color.WHITE);
        sPanel.add(sButton);
        
        JPanel oPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        oPanel.setBackground(Color.WHITE);
        oPanel.add(oButton);
        
        panel.add(sPanel);
        panel.add(oPanel);
        
        JLabel scoreLabel = new JLabel("Score: 0");
        scoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        scoreLabel.setForeground(isBlue ? Color.BLUE : Color.RED);
        
        if (isBlue) {
            scoreLabel.setName("blueScore");
        } else {
            scoreLabel.setName("redScore");
        }
        
        panel.add(Box.createVerticalStrut(20));
        panel.add(scoreLabel);
        
        return panel;
    }
    
    private PlayerType getPlayerTypeFromSelection(int selectedIndex) {
        switch (selectedIndex) {
            case 0: return PlayerType.HUMAN;
            case 1: return PlayerType.COMPUTER_EASY;
            case 2: return PlayerType.COMPUTER_MEDIUM;
            case 3: return PlayerType.COMPUTER_HARD;
            default: return PlayerType.HUMAN;
        }
    }
    
    private void makeMove(int row, int col) {
        boolean isBlue = controller.isBlueTurn(); 
        char letter = getSelectedLetter(isBlue);
        
        boolean formedSOS = controller.makeMove(row, col, letter);
        updateUI(row, col, letter, formedSOS, isBlue); 
        
        checkAndStartComputerTurn();
    }
    
    private void makeComputerMove() {
        Move move = controller.getComputerMove();
        if (move != null) {
            boolean isBlue = controller.isBlueTurn(); 
            boolean formedSOS = controller.makeMove(move.row, move.col, move.letter);
            updateUI(move.row, move.col, move.letter, formedSOS, isBlue);
            
            checkAndStartComputerTurn();
        }
    }
    
    private void updateUI(int row, int col, char letter, boolean formedSOS, boolean isBlue) {
        buttons[row][col].setText(String.valueOf(letter));
        buttons[row][col].setForeground(isBlue ? Color.BLUE : Color.RED);
        
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setBackground(Color.WHITE);
            }
        }
        
        List<int[]> sosCoordinates = controller.getSOSCoordinates();
        Color highlightColor = isBlue ? new Color(200, 230, 255) : new Color(255, 220, 220);
        for (int[] coord : sosCoordinates) {
            buttons[coord[0]][coord[1]].setBackground(highlightColor);
        }
        
        updateStatus();
        updateScores();
    }
    
    private void updateStatus() {
        if (controller.isGameEnded()) {
            int blueScore = controller.getBlueScore();
            int redScore = controller.getRedScore();
            
            if (blueScore == redScore) {
                statusLabel.setText("Game Over - Draw!");
            } else {
                String winner = blueScore > redScore ? "Blue" : "Red";
                statusLabel.setText("Game Over - " + winner + " wins!");
            }
            
            computerMoveTimer.stop();
        } else {
            statusLabel.setText("Current turn: " + (controller.isBlueTurn() ? "blue" : "red"));
        }
    }
    
    private void updateScores() {
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component c : panel.getComponents()) {
                    if (c instanceof JLabel && "blueScore".equals(c.getName())) {
                        ((JLabel) c).setText("Score: " + controller.getBlueScore());
                    } else if (c instanceof JLabel && "redScore".equals(c.getName())) {
                        ((JLabel) c).setText("Score: " + controller.getRedScore());
                    }
                }
            }
        }
    }
    
    private void checkAndStartComputerTurn() {
        computerMoveTimer.stop();
        
        if (!controller.isGameEnded() && controller.isCurrentPlayerComputer()) {
            SwingUtilities.invokeLater(() -> {
                computerMoveTimer.start();
            });
        }
    }
    
    private void startNewGame() {
        try {
            int boardSize = Integer.parseInt(boardSizeField.getText().trim());
            if (boardSize < 3 || boardSize > 12) {
                JOptionPane.showMessageDialog(frame, "Board size must be between 3 and 12", 
                                             "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean isSimpleGame = simpleGameRadioButton.isSelected();
            controller.startNewGame(boardSize, isSimpleGame);
            controller.setBluePlayerType(
                getPlayerTypeFromSelection(bluePlayerComboBox.getSelectedIndex())
            );
            
            controller.setRedPlayerType(
                getPlayerTypeFromSelection(redPlayerComboBox.getSelectedIndex())
            );
            
            Container contentPane = frame.getContentPane();
            Component oldGameBoard = null;
            for (Component comp : contentPane.getComponents()) {
                if (comp instanceof JPanel && "gameBoard".equals(comp.getName())) {
                    oldGameBoard = comp;
                    break;
                }
            }
            
            if (oldGameBoard != null) {
                contentPane.remove(oldGameBoard);
            }
            
            JPanel gameBoardPanel = createGameBoardPanel(boardSize);
            contentPane.add(gameBoardPanel, BorderLayout.CENTER);
            statusLabel.setText("Current turn: blue");
            updateScores();
            frame.revalidate();
            frame.repaint();
            computerMoveTimer.stop();
            
            checkAndStartComputerTurn();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number for board size", 
                                         "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private char getSelectedLetter(boolean blueTurn) {
        if (blueTurn) {
            return blueS.isSelected() ? 'S' : 'O';
        } else {
            return redS.isSelected() ? 'S' : 'O';
        }
    }
    
    public static void main(String[] args) {
        new SOSGameGUI();
    }
}
