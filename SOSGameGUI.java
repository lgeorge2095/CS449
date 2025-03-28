import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

public class SOSGameGUI {
    private static final Logger LOGGER = Logger.getLogger(SOSGameGUI.class.getName());
    
    private SOSGameLogic gameLogic;
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

    public SOSGameGUI() {
        SwingUtilities.invokeLater(this::initializeGUI);
    }

    private void initializeGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.warning("Could not set system look and feel: " + e.getMessage());
        }
        
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame(SOSGameConfig.GAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);
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
        
        int boardSize = SOSGameConfig.DEFAULT_BOARD_SIZE;
        boolean isSimpleGame = true;
        gameLogic = new SOSGameLogic(boardSize, isSimpleGame);
        JPanel gameBoardPanel = createGameBoardPanel(boardSize);
        contentPane.add(gameBoardPanel, BorderLayout.CENTER);
        
        JPanel bluePanel = createPlayerPanel("Blue player", true);
        contentPane.add(bluePanel, BorderLayout.WEST);
        
        JPanel redPanel = createPlayerPanel("Red player", false);
        contentPane.add(redPanel, BorderLayout.EAST);
        
        statusLabel = new JLabel("Current turn: blue", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        contentPane.add(statusLabel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        topPanel.setBackground(Color.WHITE);
        
        setupTitleLabel(topPanel);
        setupGameModePanel(topPanel);
        setupBoardSizeInput(topPanel);
        setupNewGameButton(topPanel);
        
        return topPanel;
    }

    private void setupTitleLabel(JPanel topPanel) {
        JLabel titleLabel = new JLabel(SOSGameConfig.GAME_TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel);
    }

    private void setupGameModePanel(JPanel topPanel) {
        ButtonGroup gameModeGroup = new ButtonGroup();
        simpleGameRadioButton = new JRadioButton("Simple game", true);
        generalGameRadioButton = new JRadioButton("General game");
        
        gameModeGroup.add(simpleGameRadioButton);
        gameModeGroup.add(generalGameRadioButton);
        
        topPanel.add(simpleGameRadioButton);
        topPanel.add(generalGameRadioButton);
    }

    private void setupBoardSizeInput(JPanel topPanel) {
        topPanel.add(new JLabel("Board size"));
        boardSizeField = new JTextField(String.valueOf(SOSGameConfig.DEFAULT_BOARD_SIZE), 2);
        topPanel.add(boardSizeField);
    }

    private void setupNewGameButton(JPanel topPanel) {
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> startNewGame());
        topPanel.add(newGameButton);
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
                
                buttons[i][j] = createGameButton(row, col);
                gameBoard.add(buttons[i][j]);
            }
        }
        
        return gameBoard;
    }

    private JButton createGameButton(int row, int col) {
        JButton button = new JButton("");
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setBackground(Color.WHITE);
        
        button.addActionListener(e -> {
            if (!gameLogic.isGameEnded()) {
                makeMove(row, col);
            }
        });
        
        return button;
    }
    
    private JPanel createPlayerPanel(String title, boolean isBlue) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        JRadioButton sButton = new JRadioButton("S", true);
        JRadioButton oButton = new JRadioButton("O");
        
        Color playerColor = isBlue ? SOSGameConfig.BLUE_PLAYER_COLOR : SOSGameConfig.RED_PLAYER_COLOR;
        sButton.setForeground(playerColor);
        oButton.setForeground(playerColor);
        
        if (isBlue) {
            blueS = sButton;
            blueO = oButton;
        } else {
            redS = sButton;
            redO = oButton;
        }
        
        ButtonGroup letterGroup = new ButtonGroup();
        letterGroup.add(sButton);
        letterGroup.add(oButton);
        
        panel.add(createLetterPanel(sButton));
        panel.add(createLetterPanel(oButton));
        
        return panel;
    }

    private JPanel createLetterPanel(JRadioButton button) {
        JPanel letterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        letterPanel.setBackground(Color.WHITE);
        letterPanel.add(button);
        return letterPanel;
    }
    
    private void makeMove(int row, int col) {
        char letter = getSelectedLetter(gameLogic.isBlueTurn());
        
        buttons[row][col].setText(String.valueOf(letter));
        buttons[row][col].setForeground(gameLogic.isBlueTurn() ? 
            SOSGameConfig.BLUE_PLAYER_COLOR : SOSGameConfig.RED_PLAYER_COLOR);
        
        boolean formedSOS = gameLogic.makeMove(row, col, letter);
        
        resetSOSHighlights();
        highlightSOSCoordinates();
        
        updateStatus();
    }
    
    private void resetSOSHighlights() {
        for (int i = 0; i < gameLogic.getSize(); i++) {
            for (int j = 0; j < gameLogic.getSize(); j++) {
                buttons[i][j].setBackground(Color.WHITE);
            }
        }
    }

    private void highlightSOSCoordinates() {
        List<int[]> sosCoordinates = gameLogic.getLastSOSCoordinates();
        Color highlightColor = gameLogic.isBlueTurn() ? 
            SOSGameConfig.BLUE_SOS_HIGHLIGHT : SOSGameConfig.RED_SOS_HIGHLIGHT;
        
        for (int[] coord : sosCoordinates) {
            buttons[coord[0]][coord[1]].setBackground(highlightColor);
        }
    }
    
    private void updateStatus() {
        SOSGameState gameState = gameLogic.getGameState();
        
        if (gameState.isGameEnded()) {
            statusLabel.setText("Game Over - " + gameState.getGameResult());
        } else {
            statusLabel.setText("Current turn: " + (gameState.isBlueTurn() ? "blue" : "red"));
        }
    }
    
    private void startNewGame() {
        try {
            int boardSize = validateAndParseBoardSize();
            boolean isSimpleGame = simpleGameRadioButton.isSelected();
            
            recreateGameBoard(boardSize, isSimpleGame);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), 
                                         "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int validateAndParseBoardSize() {
        String sizeInput = boardSizeField.getText().trim();
        try {
            int boardSize = Integer.parseInt(sizeInput);
            if (boardSize < SOSGameConfig.MIN_BOARD_SIZE || 
                boardSize > SOSGameConfig.MAX_BOARD_SIZE) {
                throw new IllegalArgumentException(
                    "Board size must be between " + SOSGameConfig.MIN_BOARD_SIZE + 
                    " and " + SOSGameConfig.MAX_BOARD_SIZE
                );
            }
            return boardSize;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid number for board size");
        }
    }

    private void recreateGameBoard(int boardSize, boolean isSimpleGame) {
        Container contentPane = frame.getContentPane();
        Component oldGameBoard = findGameBoard(contentPane);
        
        if (oldGameBoard != null) {
            contentPane.remove(oldGameBoard);
        }
        
        gameLogic = new SOSGameLogic(boardSize, isSimpleGame);
        JPanel gameBoardPanel = createGameBoardPanel(boardSize);
        contentPane.add(gameBoardPanel, BorderLayout.CENTER);
        
        statusLabel.setText("Current turn: blue");
        
        frame.revalidate();
        frame.repaint();
    }

    private Component findGameBoard(Container contentPane) {
        for (Component comp : contentPane.getComponents()) {
            if (comp instanceof JPanel && "gameBoard".equals(comp.getName())) {
                return comp;
            }
        }
        return null;
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