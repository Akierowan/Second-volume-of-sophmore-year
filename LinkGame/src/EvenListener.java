import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EvenListener {
    public static class SettingButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
    public static class HelpButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                BufferedImage helpImage = ImageIO.read(new File("LinkGame\\image\\basic_help.png"));

                JLabel helpLabel = new JLabel(new ImageIcon(helpImage));
                helpLabel.setHorizontalAlignment(JLabel.CENTER);
                helpLabel.setVerticalAlignment(JLabel.CENTER);
                JScrollPane jScrollPane = new JScrollPane(helpLabel);
                jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                JPanel helpPanel = new JPanel();
                helpPanel.setLayout(new BorderLayout());
                helpPanel.add(jScrollPane, BorderLayout.CENTER);
                JDialog dialog = new JDialog();
                dialog.setTitle("欢乐连连看-游戏说明");
                dialog.setSize(500, 400);
                dialog.add(helpPanel);
                dialog.setLocationRelativeTo(null); // 没有外部类实例，传入 null
                dialog.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
