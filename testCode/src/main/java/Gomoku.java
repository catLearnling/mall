import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gomoku extends JFrame implements ActionListener {
    private static final int ROWS = 15;
    private static final int COLS = 15;
    private static final int CELL_SIZE = 30;
    private static final int CANVAS_WIDTH = CELL_SIZE * COLS;
    private static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
    private static final int GRID_WIDTH = 2;
    private static final int GRID_HALF_WIDTH = GRID_WIDTH / 2;

    private enum GameState {
        PLAYING, DRAW, BLACK_WON, WHITE_WON
    }
    private GameState currentState;

    private char[][] board;
    private DrawCanvas canvas;
    private JLabel statusBar;
    private JButton newGameButton;

    private char currentPlayer;
    private int currentRow, currentCol;

    public Gomoku() {
        canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                currentRow = mouseY / CELL_SIZE;
                currentCol = mouseX / CELL_SIZE;

                if (currentState == GameState.PLAYING) {
                    if (currentRow >= 0 && currentRow < ROWS && currentCol >= 0 && currentCol < COLS && board[currentRow][currentCol] == ' ') {
                        board[currentRow][currentCol] = currentPlayer;
                        updateGame(currentPlayer, currentRow, currentCol);

                        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                    }
                } else {
                    initGame();
                }
                repaint();
            }
        });

        statusBar = new JLabel("  ");
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(statusBar);
        buttonPanel.add(newGameButton);

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setTitle("Gomoku");
        setVisible(true);

        board = new char[ROWS][COLS];
        initGame();
    }

    public void initGame() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = ' ';
            }
        }
        currentState = GameState.PLAYING;
        currentPlayer = 'X';
        statusBar.setText("X's turn");
    }

    public void updateGame(char player, int row, int col) {
        if (hasWon(player, row, col)) {
            currentState = (player == 'X') ? GameState.BLACK_WON : GameState.WHITE_WON;
            statusBar.setText((player == 'X') ? "X wins!" : "O wins!");
        } else if (isDraw()) {
            currentState = GameState.DRAW;
            statusBar.setText("Draw!");
        } else {
            statusBar.setText((player == 'X') ? "O's turn" : "X's turn");
        }
    }

    public boolean hasWon(char player, int row, int col) {
        int count = 0;

        // Check horizontally
        for (int j = 0; j < COLS; j++) {
            if (board[row][j] == player) {
                count++;
            } else {
                count = 0;
            }

            if (count >= 5) {
                return true;
            }
        }

        count = 0;

        // Check vertically
        for (int i = 0; i < ROWS; i++) {
            if (board[i][col] == player) {
                count++;
            } else {
                count = 0;
            }

            if (count >= 5) {
                return true;
            }
        }

        count = 0;

        // Check diagonally (top left to bottom right)
        int offset = Math.min(row, col);
        int i = row - offset;
        int j = col - offset;

        while (i < ROWS && j < COLS) {
            if (board[i][j] == player) {
                count++;
            } else {
                count = 0;
            }

            if (count >= 5) {
                return true;
            }

            i++;
            j++;
        }

        count = 0;

        // Check diagonally (bottom left to top right)
        offset = Math.min(row, COLS - col - 1);
        i = row - offset;
        j = col + offset;

        while (i < ROWS && j >= 0) {
            if (board[i][j] == player) {
                count++;
            } else {
                count = 0;
            }

            if (count >= 5) {
                return true;
            }

            i++;
            j--;
        }

        return false;
    }

    public boolean isDraw() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    class DrawCanvas extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.WHITE);

            g.setColor(Color.BLACK);

            // Draw vertical lines
            for (int i = 0; i <= COLS; i++) {
                int x = i * CELL_SIZE + GRID_HALF_WIDTH;
                g.drawLine(x, 0, x, CANVAS_HEIGHT);
            }

            // Draw horizontal lines
            for (int i = 0; i <= ROWS; i++) {
                int y = i * CELL_SIZE + GRID_HALF_WIDTH;
                g.drawLine(0, y, CANVAS_WIDTH, y);
            }

            // Draw X and O symbols
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    int x1 = j * CELL_SIZE + GRID_HALF_WIDTH;
                    int y1 = i * CELL_SIZE + GRID_HALF_WIDTH;
                    if (board[i][j] == 'X') {
                        g.drawLine(x1 - 5, y1 - 5, x1 + 5, y1 + 5);
                        g.drawLine(x1 + 5, y1 - 5, x1 - 5, y1 + 5);
                    } else if (board[i][j] == 'O') {
                        g.drawOval(x1 - 10, y1 - 10, 20, 20);
                    }
                }
            }

            // Print game-over message
            if (currentState == GameState.BLACK_WON || currentState == GameState.WHITE_WON || currentState == GameState.DRAW) {
                g.setColor(Color.RED);
                g.setFont(new Font("SansSerif", Font.BOLD, 40));
                String msg = (currentState == GameState.BLACK_WON) ? "X wins!" : (currentState == GameState.WHITE_WON) ? "O wins!" : "Draw!";
                int x = (CANVAS_WIDTH - g.getFontMetrics().stringWidth(msg)) / 2;
                int y = CANVAS_HEIGHT / 2;
                g.drawString(msg, x, y);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Gomoku();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton) {
            initGame();
            repaint();
        }
    }
}