import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SOSGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GameFrame();
        });
    }
}

class GameFrame extends JFrame {
    private int boardSize = 3;
    private boolean isSimpleGame = true;
    private GameBoard gameBoard;
    private JLabel statusLabel;
    private JRadioButton simpleGameRadioButton;
    private JRadioButton generalGameRadioButton;
    JTextField boardSizeField;
    private JButton newGameButton;
    private JRadioButton blueS;
    private JRadioButton blueO;
    private JRadioButton redS;
    private JRadioButton redO;
    
    public GameFrame() {
        setTitle("SOS Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        
        createLayout();
        setVisible(true);
    }
    
    private void createLayout() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setBackground(Color.WHITE);
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        topPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("SOS Game");
        titleLabel.setFont(new Font("Ariel", Font.BOLD, 20));
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
        
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> startNewGame());
        topPanel.add(newGameButton);
        
        contentPane.add(topPanel, BorderLayout.NORTH);
        
        gameBoard = new GameBoard(boardSize, isSimpleGame);
        contentPane.add(gameBoard, BorderLayout.CENTER);
        
        JPanel bluePanel = createPlayerPanel("Blue player", true);
        contentPane.add(bluePanel, BorderLayout.WEST);
        
        JPanel redPanel = createPlayerPanel("Red player", false);
        contentPane.add(redPanel, BorderLayout.EAST);
        
        statusLabel = new JLabel("Current turn: blue", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        contentPane.add(statusLabel, BorderLayout.SOUTH);
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
        
        return panel;
    }
    
    void startNewGame() {
        try {
            boardSize = Integer.parseInt(boardSizeField.getText().trim());
            if (boardSize < 3 || boardSize > 12) {
                JOptionPane.showMessageDialog(this, "Board size must be between 3 and 12", 
                                             "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for board size", 
                                         "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        isSimpleGame = simpleGameRadioButton.isSelected();
        
        Container contentPane = getContentPane();
        contentPane.remove(gameBoard);
        gameBoard = new GameBoard(boardSize, isSimpleGame);
        contentPane.add(gameBoard, BorderLayout.CENTER);
        statusLabel.setText("Current turn: blue");
        revalidate();
        repaint();
    }
    
    public char getSelectedLetter(boolean blueTurn) {
        if (blueTurn) {
            return blueS.isSelected() ? 'S' : 'O';
        } else {
            return redS.isSelected() ? 'S' : 'O';
        }
    }
    
    class ColorCircle extends JPanel {
        private Color color;
        
        public ColorCircle(Color color) {
            this.color = color;
            setPreferredSize(new Dimension(10, 10));
            setBackground(Color.WHITE);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            g.fillOval(0, 0, 10, 10);
        }
    }
    
    class GameBoard extends JPanel {
        private int size;
        private boolean isSimple;
        private JButton[][] buttons;
        private char[][] board;
        private boolean blueTurn = true;
        private int blueScore = 0;
        int redScore = 0;
        private boolean gameEnded = false;
        
        private List<int[]> lastSOSCoordinates = new ArrayList<>();
        
        public GameBoard(int size, boolean isSimple) {
            this.size = size;
            this.isSimple = isSimple;
            
            setLayout(new GridLayout(size, size));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            buttons = new JButton[size][size];
            board = new char[size][size];
            
            initializeBoard();
        }
        
        private void initializeBoard() {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    buttons[i][j] = new JButton("");
                    buttons[i][j].setFont(new Font("SansSerif", Font.BOLD, 18));
                    buttons[i][j].setFocusPainted(false);
                    buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    buttons[i][j].setBackground(Color.WHITE);
                    
                    final int row = i;
                    final int col = j;
                    
                    buttons[i][j].addActionListener(e -> {
                        if (!gameEnded && board[row][col] == '\0') {
                            makeMove(row, col);
                        }
                    });
                    
                    add(buttons[i][j]);
                    board[i][j] = '\0';
                }
            }
        }
        public void makeMove(int row, int col) {
            char letter = getSelectedLetter(blueTurn);
            
            board[row][col] = letter;
            buttons[row][col].setText(String.valueOf(letter));
            buttons[row][col].setForeground(blueTurn ? Color.BLUE : Color.RED);
            
            boolean formedSOS = false;
            
            if (formedSOS) {
                if (blueTurn) {
                    blueScore++;
                } else {
                    redScore++;
                }
                
                if (isSimple) {
                    gameEnded = true;
                    statusLabel.setText((blueTurn ? "Blue" : "Red") + " wins!");
                    return;
                }
                
            } else {
                blueTurn = !blueTurn;
            }
            
            if (isBoardFull()) {
                gameEnded = true;
                if (blueScore == redScore) {
                    statusLabel.setText("Game Over - Draw!");
                } else {
                    statusLabel.setText("Game Over - " + (blueScore > redScore ? "Blue" : "Red") + " wins!");
                }
            } else {
                if (!gameEnded) {
                    statusLabel.setText("Current turn: " + (blueTurn ? "blue" : "red"));
                }
            }
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
    }
}