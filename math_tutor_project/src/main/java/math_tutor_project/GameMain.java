package math_tutor_project;

import javax.swing.*;
import java.awt.*;

public class GameMain extends JFrame {



    public GameMain() {
        
      setLayout(new GridLayout(1, 3));

      MathPanel mathPanel = new MathPanel();
      UserPlayer player = new UserPlayer(75, 200, "player 1", 100);
      CpuPlayer cpu = new CpuPlayer(325, 200,  100);
      DuelPanel duelPanel = new DuelPanel(player, cpu);

      UserPlayer player2 = new UserPlayer(75, 200, "player 1", 100);
      CpuPlayer cpu2 = new CpuPlayer(325, 200,  100);
      MapLevel mapLevel = new MapLevel(duelPanel);

      add(duelPanel);
      add(mathPanel);
      add(mapLevel);

      GameController controller = new GameController();
      mathPanel.addMouseListener(controller);
      mathPanel.addMouseMotionListener(controller);

      GameData.getInstance().addPropertyChangeListener(mathPanel);
    }
  
    public static void main(String[] args) {
  
      GameMain main = new GameMain();
      main.setTitle("Mathematical Madness");
      main.setSize(1800, 600);
      main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      main.setVisible(true);
    }
  }
