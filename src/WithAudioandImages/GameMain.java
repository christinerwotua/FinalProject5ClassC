package WithAudioandImages;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    // Define named constants for the drawing graphics
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);  // Red #EF6950
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225); // Blue #409AE1
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Tambahkan deklarasi untuk isBotMode dan nama pemain
    private boolean isBotMode = false;
    private String playerOneName = "Player 1";
    private String playerTwoName = "Bot";  // Nama default bot

    // Define game objects
    private Board board;         // the game board
    private State currentState;  // the current state of the game
    private Seed currentPlayer;  // the current player
    private JLabel statusBar;    // for displaying status message

    // Variabel untuk timer
    private Timer turnTimer;
    private JLabel timerLabel; // Label untuk menampilkan timer
    private int timeLeft;
    private final int TURN_TIME = 10; // 10 detik per giliran

    /**
     * Constructor to setup the UI and game components
     */
    // 🔇 Variabel untuk tombol mute
    private JToggleButton muteButton;
    private boolean isMuted = false;

    public GameMain() {
        if (!isMuted && !BackgroundMusic.isPlaying()) {
            BackgroundMusic.playLoop("/audio/funk-244706.wav");
        }




        // Menambahkan input mode permainan (Player vs Player atau Player vs Bot)
        Object[] options = {"Player vs Player", "Player vs Bot"};
        int modeChoice = JOptionPane.showOptionDialog(null, "Pilih Mode Permainan:", "Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        isBotMode = (modeChoice == 1); // Jika memilih "Player vs Bot"

        // Jika memilih Player vs Player, masukkan nama pemain
        if (!isBotMode) {
            playerOneName = JOptionPane.showInputDialog(null, "Masukkan Nama Pemain 1:");
            playerTwoName = JOptionPane.showInputDialog(null, "Masukkan Nama Pemain 2:");
        } else {
            // Jika Player vs Bot, hanya nama pemain 1 yang diminta
            playerOneName = JOptionPane.showInputDialog(null, "Masukkan Nama Pemain:");
        }

        // Inisialisasi timerLabel
        timerLabel = new JLabel("Time: " + TURN_TIME);
        timerLabel.setFont(FONT_STATUS);
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        timerLabel.setOpaque(true);
        timerLabel.setBackground(COLOR_BG_STATUS);

        // Inisialisasi turnTimer
        turnTimer = new Timer(1000, new ActionListener() { // Setiap 1 detik
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time: " + timeLeft); // Changed language

                if (timeLeft <= 0) {
                    turnTimer.stop();
                    // Lewati giliran
                    SoundEffect.EXPLODE.play();
                    switchPlayer();

                    if (isBotMode && currentPlayer == Seed.NOUGHT) { // Jika bot mode dan giliran bot
                        performBotMove(); // Bot bergerak
                    }
                    repaint(); // Perbarui tampilan
                    startTurnTimer(); // Mulai timer untuk giliran berikutnya
                }
            }
        });

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        turnTimer.stop(); // Hentikan timer saat pemain membuat gerakan
                        // Update cells[][] and return the new game state after the move
                        currentState = board.stepGame(currentPlayer, row, col);

                        // Play appropriate sound clip
                        if (currentState == State.PLAYING) {
                            SoundEffect.EAT_FOOD.play();
                        } else if (currentState == State.DRAW) {
                            SoundEffect.EXPLODE.play();
                        } else {
                            SoundEffect.DIE.play();
                        }

                        if (currentState == State.PLAYING) {
                            switchPlayer(); // Pindah giliran
                            if (isBotMode && currentPlayer == Seed.NOUGHT) { // Jika mode bot dan giliran bot
                                performBotMove(); // Bot bergerak
                            } else {
                                startTurnTimer(); // Mulai timer untuk pemain berikutnya
                            }
                        } else {
                            // Game berakhir (menang atau seri), hentikan timer
                            turnTimer.stop();
                        }
                    }
                } else {        // game over
                    newGame();  // restart the game
                }
                // Refresh the drawing canvas
                repaint();  // Callback paintComponent().
            }
        });

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        // Tombol Mute/Unmute
        muteButton = new JToggleButton();
        muteButton.setPreferredSize(new Dimension(40, 20));        muteButton.setFocusPainted(false);
        muteButton.setBackground(Color.LIGHT_GRAY);
        muteButton.setFont(new Font("Arial", Font.BOLD, 12));

        // Set ikon awal (unmute)
        ImageIcon unmuteIcon = new ImageIcon(getClass().getResource("/icons/unmute.png"));
        Image unmuteImg = unmuteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        muteButton.setIcon(new ImageIcon(unmuteImg));

        // Aksi toggle
        muteButton.addActionListener(e -> {
            if (muteButton.isSelected()) {
                BackgroundMusic.stop();

                ImageIcon muteIcon = new ImageIcon(getClass().getResource("/icons/mute.png"));
                Image muteImg = muteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                muteButton.setIcon(new ImageIcon(muteImg));

                isMuted = true;
            } else {
                BackgroundMusic.resume();

                ImageIcon unmuteIcon2 = new ImageIcon(getClass().getResource("/icons/unmute.png"));
                Image unmuteImg2 = unmuteIcon2.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                muteButton.setIcon(new ImageIcon(unmuteImg2));

                isMuted = false;
            }
        });

        // ✅ Tambahkan button Back to Home
        JButton backButton = new JButton("Back to Home");
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.setContentPane(new GameMain());
            topFrame.revalidate();
        });



        super.setLayout(new BorderLayout());
// Gabungkan status bar dan tombol mute di panel bawah
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusBar, BorderLayout.CENTER);

        JPanel mutePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mutePanel.setBackground(COLOR_BG_STATUS);
        mutePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        mutePanel.add(muteButton);
        mutePanel.add(backButton);
        bottomPanel.add(mutePanel, BorderLayout.EAST);

        JPanel timerWrapperPanel = new JPanel(new BorderLayout());
        timerWrapperPanel.setBackground(COLOR_BG_STATUS);
        timerWrapperPanel.add(timerLabel, BorderLayout.CENTER);
        timerWrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Padding kiri


        super.add(bottomPanel, BorderLayout.PAGE_END);


        super.add(timerWrapperPanel, BorderLayout.PAGE_START);

        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30 + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Set up Game
        initGame();
        newGame();
    }

    /**
     * Initialize the game (run once)
     */
    public void initGame() {
        board = new Board();  // allocate the game-board
    }

    /**
     * Reset the game-board contents and the current-state, ready for new game
     */
    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED; // all cells empty
            }
        }
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState = State.PLAYING;  // ready to play
        startTurnTimer();
    }

    /**
     * Metode untuk memulai atau mereset timer giliran.
     */
    private void startTurnTimer() { //
        timeLeft = TURN_TIME; //
        timerLabel.setText("Time: " + timeLeft); //
        turnTimer.start(); //
    }

    /**
     * Metode untuk mengganti pemain
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
    }

    /**
     * Metode untuk melakukan gerakan bot
     */
    private void performBotMove() {
        int[] botMove = Bot.getMove(board); // Mendapatkan langkah bot secara acak
        if (botMove != null) {
            int botRow = botMove[0];
            int botCol = botMove[1];
            // Update game state setelah langkah bot
            currentState = board.stepGame(currentPlayer, botRow, botCol); //

            if (currentState == State.PLAYING) {
                SoundEffect.EAT_FOOD.play();
            } else if (currentState == State.DRAW) {
                SoundEffect.EXPLODE.play();
            } else {
                SoundEffect.DIE.play();
            }

            if (currentState == State.PLAYING) {
                switchPlayer(); // Ganti giliran pemain setelah langkah bot
                startTurnTimer(); // Mulai timer untuk pemain berikutnya
            } else {
                // Game berakhir (menang atau seri), hentikan timer
                turnTimer.stop();
            }
        } else {
            // Jika bot tidak bisa bergerak (mungkin papan penuh tapi belum DRAW, kasus jarang)
            currentState = State.DRAW; // Atau handle sesuai kebutuhan
            turnTimer.stop();
        }
    }

    /**
     * Custom painting codes on this JPanel
     */
    @Override
    public void paintComponent(Graphics g) {  // Callback via repaint()
        super.paintComponent(g);
        setBackground(COLOR_BG); // set its background color

        board.paint(g);  // ask the game board to paint itself
        // Print status-bar message
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? playerOneName + "'s Turn" : playerTwoName + "'s Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'" + playerOneName + "' Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'" + playerTwoName + "' Won! Click to play again.");
        }
    }

    /**
     * The entry "main" method
     */
    public static void main(String[] args) {
        // Run GUI construction codes in Event-Dispatching thread for thread safety
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TITLE);
                frame.setContentPane(new GameMain());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null); // center the application window
                frame.setVisible(true);            // show it
            }
        });
    }
}