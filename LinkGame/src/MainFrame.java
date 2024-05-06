import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    public MainFrame() {
        initJFrame();
    }

    private void initJFrame() {
        this.setSize(800, 600);
        this.setTitle("欢乐连连看");
        this.setAlwaysOnTop(false);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 创建背景图片
        ImageIcon backgroundImage = new ImageIcon("LinkGame\\image\\bg_mainFrame.png");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(null); // 使用FlowLayout布局管理器
        backgroundLabel.setSize(800, 600); // 设置大小
        this.add(backgroundLabel);

        JButton button1 = new JButton("基本模式");
        button1.setBounds(20, 210, 100, 60); // 设置按钮位置和大小
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //MainFrame.this.setVisible(false);
                new GameFrame().setVisible(true);
            }
        });
        backgroundLabel.add(button1);

        JButton button2 = new JButton("休闲模式");
        button2.setBounds(20, 325, 100, 60);
        backgroundLabel.add(button2);

        JButton button3 = new JButton("关卡模式");
        button3.setBounds(20, 440, 100, 60);
        backgroundLabel.add(button3);

        JButton button4 = new JButton("排行榜");
        button4.setBounds(500, 500, 80, 30);
        backgroundLabel.add(button4);

        JButton button5 = new JButton("设置");
        button5.setBounds(600, 500, 60, 30);
        backgroundLabel.add(button5);

        JButton button6 = new JButton("帮助");
        button6.setBounds(700, 500, 60, 30);
        button6.addActionListener(new EvenListener.HelpButtonListener());
        backgroundLabel.add(button6);

    }

}
