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
    private GameCanvas gameCanvas; // Panel khusus untuk menggambar papan

    // Variabel untuk timer
    private Timer turnTimer;
    private JLabel timerLabel; // Label untuk menampilkan timer
    private int timeLeft;
    private final int TURN_TIME = 10; // 10 detik per giliran
    //Round and Score variables
    private int totalRounds = 1;
    private int currentRound = 1;
    private int playerOneScore = 0;
    private int playerTwoScore = 0;
    private JLabel scoreLabel;
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

        WelcomeMessage.showWelcomeMessage();

        //Get total rounds from user
        String roundsInput = JOptionPane.showInputDialog(null, "Enter total number of rounds:", "Number of Rounds", JOptionPane.QUESTION_MESSAGE); //
        try { //
            totalRounds = Integer.parseInt(roundsInput); //
            if (totalRounds <= 0) { //
                totalRounds = 1; // Default to 1 if invalid input //
                JOptionPane.showMessageDialog(null, "Invalid number of rounds. Defaulting to 1 round.", "Error", JOptionPane.ERROR_MESSAGE); //
            }
        } catch (NumberFormatException e) { //
            totalRounds = 1; // Default to 1 if input is not a number //
            JOptionPane.showMessageDialog(null, "Invalid input for rounds. Defaulting to 1 round.", "Error", JOptionPane.ERROR_MESSAGE); //
        }

        // Menambahkan input mode permainan (Player vs Player atau Player vs Bot)
        Object[] options = {"Player vs Player", "Player vs Bot"};
        int modeChoice = JOptionPane.showOptionDialog(null, "Pilih Mode Permainan:", "Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        isBotMode = (modeChoice == 1); // Jika memilih "Player vs Bot"

        // Jika memilih Player vs Player, masukkan nama pemain
        if (!isBotMode) { //
            playerOneName = JOptionPane.showInputDialog(null, "Masukkan Nama Pemain 1:"); //
            if (playerOneName == null || playerOneName.trim().isEmpty()) playerOneName = "Player 1"; //
            playerTwoName = JOptionPane.showInputDialog(null, "Masukkan Nama Pemain 2:"); //
            if (playerTwoName == null || playerTwoName.trim().isEmpty()) playerTwoName = "Player 2"; //
        } else { //
            // Jika Player vs Bot, hanya nama pemain 1 yang diminta
            playerOneName = JOptionPane.showInputDialog(null, "Masukkan Nama Pemain:"); //
            if (playerOneName == null || playerOneName.trim().isEmpty()) playerOneName = "Player"; //
            playerTwoName = "Bot"; //
        }

        // Inisialisasi timerLabel
        timerLabel = new JLabel("Time: " + TURN_TIME);
        timerLabel.setFont(FONT_STATUS);
        timerLabel.setHorizontalAlignment(JLabel.LEFT);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        timerLabel.setOpaque(true);
        timerLabel.setBackground(COLOR_BG_STATUS);

        //Initialize scoreLabel
        scoreLabel = new JLabel("Score: " + playerOneName + " " + playerOneScore + " - " + playerTwoScore + " " + playerTwoName); //
        scoreLabel.setFont(FONT_STATUS); //
        scoreLabel.setHorizontalAlignment(JLabel.CENTER); //
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adjusted padding //
        scoreLabel.setOpaque(true); //
        scoreLabel.setBackground(COLOR_BG_STATUS); //

        // Inisialisasi turnTimer
        turnTimer = new Timer(1000, new ActionListener() { // Setiap 1 detik
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time: " + timeLeft); // Changed language

                // Di dalam turnTimer ActionListener:
                if (timeLeft <= 0) {
                    turnTimer.stop();
                    System.out.println("Time's up! Turn skipped.");
                    SoundEffect.EXPLODE.play();

                    if (currentState == State.PLAYING) {
                        switchPlayer();
                        gameCanvas.repaint(); // Perbarui tampilan setelah pemain diganti

                        if (isBotMode && currentState == State.PLAYING && currentPlayer == Seed.NOUGHT) {
                            scheduleBotMove(); // <-- Panggil scheduleBotMove
                        } else {
                            startTurnTimer(); // Mulai timer untuk pemain berikutnya jika bukan bot
                        }
                    } else { // Jika game berakhir karena waktu habis (misal, seri)
                        gameCanvas.repaint();
                        GameMain.this.repaint();
                        handleRoundEnd();
                    }
                }
            }
        });



        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.CENTER);
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
            BackgroundMusic.stop();
            turnTimer.stop();
            topFrame.setContentPane(new WelcomePanel(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Ketika "CONTINUE" dari WelcomePanel diklik, kembali ke GameMain
                    topFrame.setContentPane(new GameMain());
                    topFrame.revalidate();
                }
            }));
            topFrame.revalidate();
        });



        super.setLayout(new BorderLayout());

        gameCanvas = new GameCanvas();
        super.add(gameCanvas, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel statusScorePanel = new JPanel(new GridLayout(2, 1)); // Two rows for status and score //
        statusScorePanel.add(statusBar);
        statusScorePanel.add(scoreLabel);
        bottomPanel.add(statusScorePanel, BorderLayout.CENTER);


        JPanel topPanel = new JPanel(new BorderLayout()); // Deklarasi topPanel di sini
        topPanel.setBackground(COLOR_BG_STATUS);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding untuk timer
        topPanel.add(timerLabel, BorderLayout.WEST);

        JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); //
        topButtonsPanel.setBackground(COLOR_BG_STATUS); //
        topButtonsPanel.add(muteButton); //
        topButtonsPanel.add(backButton); //
        topPanel.add(topButtonsPanel, BorderLayout.EAST); // Tambahkan panel tombol ke KANAN atas

        super.add(topPanel, BorderLayout.PAGE_START);

        super.add(topPanel, BorderLayout.PAGE_START);

        super.add(bottomPanel, BorderLayout.PAGE_END);


        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 100));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Set up Game
        initGame();
        newGame(true);
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
    public void newGame(boolean isNewGame) {
        if (isNewGame) { //
            currentRound = 1; //
            playerOneScore = 0; //
            playerTwoScore = 0; //
        } else { //
            currentRound++; //
        }

        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED; // all cells empty
            }
        }
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState = State.PLAYING;  // ready to play
        startTurnTimer();
        repaint();
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
            } else {
                // Game berakhir (menang atau seri), hentikan timer
                turnTimer.stop();
                handleRoundEnd();
            }
        } else {
            // Jika bot tidak bisa bergerak
            currentState = State.DRAW; // Atau handle sesuai kebutuhan
            turnTimer.stop();
            handleRoundEnd();
        }
        gameCanvas.repaint();
        GameMain.this.repaint();
    }

    // NEW: Handle end of a round
    private void handleRoundEnd() { //
        if (currentState == State.CROSS_WON) { //
            playerOneScore++; //
        } else if (currentState == State.NOUGHT_WON) { //
            playerTwoScore++; //
        }

        if (currentRound < totalRounds) { //
            int option = JOptionPane.showConfirmDialog(null, //
                    "Round " + currentRound + " of " + totalRounds + " completed!\n" + //
                            playerOneName + " Score: " + playerOneScore + "\n" + //
                            playerTwoName + " Score: " + playerTwoScore + "\n\n" + //
                            "Do you want to play next round?", //
                    "Round End", JOptionPane.YES_NO_OPTION); //
            if (option == JOptionPane.YES_OPTION) { //
                newGame(false); // Start a new round //
            } else { //
                showFinalResults(); //
            }
        } else { //
            showFinalResults(); //
        }
    }

    // NEW: Show final results
    private void showFinalResults() { //
        String message; //
        if (playerOneScore > playerTwoScore) { //
            message = playerOneName + " wins the game!"; //
        } else if (playerTwoScore > playerOneScore) { //
            message = playerTwoName + " wins the game!"; //
        } else { //
            message = "The game is a Draw!"; //
        }

        int option = JOptionPane.showConfirmDialog(null, //
                "Game Over!\n" + //
                        playerOneName + " Final Score: " + playerOneScore + "\n" + //
                        playerTwoName + " Final Score: " + playerTwoScore + "\n\n" + //
                        message + "\n\n" + //
                        "Do you want to play again from the start?", //
                "Game Over", JOptionPane.YES_NO_OPTION); //

        if (option == JOptionPane.YES_OPTION) { //
            // Restart the whole game by creating a new GameMain instance
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this); //
            topFrame.setContentPane(new GameMain()); //
            topFrame.revalidate(); //
        } else { //
            // Exit the application
            System.exit(0); //
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
        if (currentState == State.PLAYING) { //
            statusBar.setForeground(Color.BLACK); //
            statusBar.setText("Round " + currentRound + " of " + totalRounds + " | " + ((currentPlayer == Seed.CROSS) ? playerOneName : playerTwoName) + "'s Turn"); //
            scoreLabel.setText("Score: " + playerOneName + " " + playerOneScore + " - " + playerTwoScore + " " + playerTwoName); //
        } else if (currentState == State.DRAW) { //
            statusBar.setForeground(Color.RED); //
            statusBar.setText("It's a Draw!"); //
            scoreLabel.setText("Score: " + playerOneName + " " + playerOneScore + " - " + playerTwoScore + " " + playerTwoName); //
        } else if (currentState == State.CROSS_WON) { //
            statusBar.setForeground(Color.RED); //
            statusBar.setText("'" + playerOneName + "' Won this round!"); //
            scoreLabel.setText("Score: " + playerOneName + " " + playerOneScore + " - " + playerTwoScore + " " + playerTwoName); //
        } else if (currentState == State.NOUGHT_WON) { //
            statusBar.setForeground(Color.RED); //
            statusBar.setText("'" + playerTwoName + "' Won this round!"); //
            scoreLabel.setText("Score: " + playerOneName + " " + playerOneScore + " - " + playerTwoScore + " " + playerTwoName); //
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
    // Ini adalah kode yang harus Anda tambahkan di akhir file GameMain.java, sebelum kurung kurawal terakhir.
    class GameCanvas extends JPanel {
        private static final long serialVersionUID = 1L;

        public GameCanvas() {
            // Tetapkan ukuran pilihan untuk area papan gambar
            setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT));

            // Pindahkan MouseListener ke canvas ini
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    int row = mouseY / Cell.SIZE;
                    int col = mouseX / Cell.SIZE;

                    if (currentState == State.PLAYING) {
                        if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                                && board.cells[row][col].content == Seed.NO_SEED) {
                            turnTimer.stop(); // Hentikan timer saat pemain bergerak
                            currentState = board.stepGame(currentPlayer, row, col);

                            // Mainkan klip suara yang sesuai
                            if (currentState == State.PLAYING) {
                                SoundEffect.EAT_FOOD.play();
                            } else if (currentState == State.DRAW) {
                                SoundEffect.EXPLODE.play();
                            } else {
                                SoundEffect.DIE.play();
                            }

                            if (currentState == State.PLAYING) {
                                switchPlayer(); // Pindah giliran
                                repaint(); // Perbarui tampilan canvas setelah pemain bergerak
                                GameMain.this.repaint(); // Perbarui GameMain untuk status bar
                                if (isBotMode && currentState == State.PLAYING && currentPlayer == Seed.NOUGHT) {
                                    scheduleBotMove(); // Bot bergerak
                                } else {
                                    startTurnTimer();
                                }
                            } else {
                                // Game berakhir (menang atau seri), hentikan timer
                                turnTimer.stop();
                                repaint();
                                GameMain.this.repaint();
                                handleRoundEnd();
                            }
                        }
                    } else {        // Game berakhir

                    }
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g); // memapstikan latar belakang dibersihkan
            setBackground(COLOR_BG); // Atur warna latar belakangnya

            board.paint(g);
        }
    }

    private void scheduleBotMove() {
        // Hentikan timer giliran pemain jika masih berjalan
        if (turnTimer.isRunning()) {
            turnTimer.stop();
        }

        // Tampilkan pesan "Bot berpikir..." atau sejenisnya
        statusBar.setText("Round " + currentRound + " of " + totalRounds + " | Bot is thinking...");
        GameMain.this.repaint(); // Pastikan status bar diperbarui

        // Gunakan Swing Timer untuk menunda gerakan bot
        Timer botDelayTimer = new Timer(700, new ActionListener() { // Jeda 700 milidetik (0.7 detik)
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer) e.getSource()).stop(); // Hentikan timer ini setelah sekali jalan
                performBotMove(); // Panggil metode bot untuk bergerak
                startTurnTimer(); // Mulai timer untuk giliran berikutnya (jika game masih PLAYING)
            }
        });
        botDelayTimer.setRepeats(false); // Pastikan timer hanya berjalan sekali
        botDelayTimer.start();
    }


}