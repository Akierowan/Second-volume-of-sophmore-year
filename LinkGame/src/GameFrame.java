import javax.swing.*;
public class GameFrame extends JFrame {
    public GameFrame(){
        this.setSize(800, 600);
        this.setTitle("欢乐连连看");
        this.setAlwaysOnTop(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GamePanel gamePanel = new GamePanel(this);
        gamePanel.setBounds(0, 0, 800, 600);
        this.add(gamePanel);
    }
}