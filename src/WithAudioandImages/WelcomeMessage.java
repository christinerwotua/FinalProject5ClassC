package WithAudioandImages;

import javax.swing.*;

/**
 * Class untuk menampilkan pesan penyambutan sebelum memulai permainan.
 */
public class WelcomeMessage {
    public static void showWelcomeMessage() {
        // Menampilkan pesan sambutan menggunakan JOptionPane
        JOptionPane.showMessageDialog(null,
                "Welcome to the Tic-Tac-Toe game!\n" +
                        "Created by Christine (028), Adel (120), Willy (166)\n" +
                        "Choose the number of rounds and game mode to get started.\n\n" +
                        "Good luck!",
                "Welcome!",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
