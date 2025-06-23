package WithAudioandImages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class WelcomePanel extends JPanel {

    public WelcomePanel(ActionListener onContinue) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JLabel title = new JLabel("WELCOME TO TIC TAC TOE", JLabel.CENTER);
        title.setFont(new Font("Impact", Font.BOLD, 28));
        title.setForeground(Color.RED);
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));
        add(title, BorderLayout.CENTER);

        JButton continueButton = new JButton("▶ CONTINUE ▶");
        continueButton.setFont(new Font("Arial", Font.BOLD, 16));
        continueButton.setBackground(Color.RED);
        continueButton.setForeground(Color.BLACK);
        continueButton.setFocusPainted(false);
        continueButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        continueButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        continueButton.addActionListener(onContinue);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));
        buttonPanel.add(continueButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
