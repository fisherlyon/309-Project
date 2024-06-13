package project_csc309_spring_2024;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;


/**
 * A JPanel that displays the map
 * with buttons so the user can choose
 * their preferred scene.
 * 
 * @author Fisher Lyon
 */
public class ModePanel extends JPanel implements ActionListener {
    private JPanel buttonSide = new JPanel();
    private JPanel descriptionSide = new JPanel();
    private JLabel descriptionLabel = new JLabel();
    private Button pressedButton = null;
    private boolean buttonPressed = false;
    private PropertyReader propertyReader = new PropertyReader("gamemodes.properties");
    private String[] labels = {"Story Mode", "Time Attack", "CPU PvP", "Join PvP Game"};

    private int PANEL_DIM = 600;
    private int BUTTON_WIDTH = 150;
    private int BUTTON_HEIGHT = 40;


    private AudioPlayer audioPlayer;


    public ModePanel() {
        this.audioPlayer = GameData.getInstance().getAudioPlayer();
        setLayout(null);
        initButtonSide();
        initDescriptionSide();
        add(buttonSide);
        add(descriptionSide);
    }

    private void initButtonSide() {
        buttonSide.setBounds(0, 0, PANEL_DIM, PANEL_DIM);
        buttonSide.setLayout(null);
        buttonSide.setBackground(Color.black);

        JLabel label = new JLabel("SELECT GAME MODE");
        label.setForeground(Color.white);
        label.setFont(GameData.getInstance().getCustomFont().deriveFont(25f)); // Use custom font here
        label.setBounds(150, 80, 350, 40);
        buttonSide.add(label);

        for (int i = 0; i < labels.length; i++) {
            Button button = new Button(labels[i], (PANEL_DIM/2), (i*60) + 150, BUTTON_WIDTH, BUTTON_HEIGHT);
            button.setFont(GameData.getInstance().getCustomFont().deriveFont(12f));
            button.addSelf(buttonSide);
            button.setButtonColor(Color.red, Color.white);
            button.setPressedColor(Color.MAGENTA);
            button.addActionListener(this);
        }
    }

    private void initDescriptionSide() {
        descriptionSide.setBounds(PANEL_DIM, 0, PANEL_DIM, PANEL_DIM);
        descriptionSide.setLayout(new BorderLayout());
        descriptionSide.setBackground(Color.darkGray);
        descriptionLabel.setForeground(Color.white);
        descriptionLabel.setFont(GameData.getInstance().getCustomFont().deriveFont(24f)); // Use custom font here

        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setVerticalAlignment(SwingConstants.CENTER);
        descriptionSide.add(descriptionLabel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Button) {
            audioPlayer.play("select");
            buttonPressed = true;
            Button button = (Button) e.getSource();

            if (pressedButton != null) {
                pressedButton.setToDefaultColor();
            }

            String description = propertyReader.getDescription(button.getLabel());
            descriptionLabel.setText("<html><div style='text-align: center;'>" + description + "</div></html>");

            button.setToPressedColor();
            pressedButton = button;
            GameData.getInstance().setGameMode(button.getLabel());
        }
    }

    public JPanel getButtonSidePanel() { return buttonSide; }
}
