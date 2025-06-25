package WithAudioandImages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class WelcomePanel extends JPanel {

    public WelcomePanel(ActionListener onContinue) {
        setLayout(new BorderLayout());
        setBackground(new Color(230, 230, 230)); // soft gray background

        JLabel title = new JLabel("WELCOME TO TIC TAC TOE", JLabel.CENTER);
        title.setFont(new Font("Impact", Font.BOLD, 28));
        title.setForeground(new Color(135, 206, 250)); // sky blue text
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));
        add(title, BorderLayout.CENTER);

        JButton continueButton = new JButton("▶ CONTINUE ▶");
        continueButton.setFont(new Font("Arial", Font.BOLD, 16));
        continueButton.setBackground(Color.WHITE); // soft button background
        continueButton.setForeground(new Color(135, 206, 250)); // sky blue text
        continueButton.setFocusPainted(false);
        continueButton.setBorder(BorderFactory.createLineBorder(new Color(135, 206, 250), 3)); // sky blue border
        continueButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        continueButton.addActionListener(onContinue);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 230, 230));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));
        buttonPanel.add(continueButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
