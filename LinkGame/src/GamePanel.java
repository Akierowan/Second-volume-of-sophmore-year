import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamePanel extends JPanel {
    int imageAmount;
    int columns;
    int rows;
    int margin;
    int[][] arr;
    JFrame jFrame;  //容纳该JPanel的JFrame

    BufferedImage element;
    BufferedImage mask;
    BufferedImage background;

    Timer timer;
    JProgressBar progressBar;
    JLabel timerLabel;
    int remainingTime;
    boolean running;
    boolean active = false;

    boolean selectOne;
    int selectX, selectY;
    List<int[][]> track;
    public GamePanel(JFrame jFrame) {
        remainingTime = Config.COUNTDOWN;
        imageAmount = Config.IMAGE_AMOUNT;
        columns = Config.COLUMNS;
        rows = Config.ROWS;
        margin = Config.MARGIN;

        running = false;
        selectOne = false;
        this.jFrame = jFrame;
        try {
            element = ImageIO.read(new File("LinkGame\\image\\fruit_element.png"));
            mask = ImageIO.read(new File("LinkGame\\image\\fruit_mask.png"));
            background = ImageIO.read(new File("LinkGame\\image\\bg_game.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        arr = new int[rows][columns];
        track = new ArrayList<>();
        Utils utils = new Utils(arr, track);
        utils.initArray();

        setLayout(null);

        JButton startPauseButton = new JButton("开始游戏");
        startPauseButton.setBounds(690, 50, 85, 30);
        JButton hintButton = new JButton("提示");
        hintButton.setBounds(690, 130, 85, 30);
        JButton shuffleButton = new JButton("重排");
        shuffleButton.setBounds(690, 210, 85, 30);
        JButton settingsButton = new JButton("设置");
        settingsButton.setBounds(720, 420, 60, 30);
        JButton helpButton = new JButton("帮助");
        helpButton.setBounds(720, 500, 60, 30);

        add(startPauseButton);
        add(hintButton);
        add(shuffleButton);
        add(settingsButton);
        add(helpButton);

        progressBar = new JProgressBar(0, remainingTime);
        progressBar.setStringPainted(true);//进度条的百分比显示
        timerLabel = new JLabel(String.valueOf(remainingTime));
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(remainingTime >= 0) {
                    progressBar.setValue(remainingTime);
                    timerLabel.setText(String.valueOf(remainingTime));
                    remainingTime--;
                } else {
                    progressBar.setValue(0); // 设置进度条值为最大值
                    timerLabel.setText("0"); // 设置剩余时间为 0
                    timer.stop();
                    JOptionPane.showMessageDialog(null,"游戏结束!","时间到",JOptionPane.INFORMATION_MESSAGE);
                    jFrame.dispose();
                    new MainFrame().setVisible(true);
                }
            }
        });
        progressBar.setBounds(50, 500, 500, 40);
        timerLabel.setBounds(600, 500, 50,40);
        this.add(progressBar);
        this.add(timerLabel);

        //开始、暂停、继续 按钮的事件监听
        startPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(active == false) {
                    active = true;
                }
                if(!running) {
                    timer.start();
                    startPauseButton.setText("暂停游戏");
                    GamePanel.this.repaint();
                } else {
                    timer.stop();
                    startPauseButton.setText("继续游戏");
                    Graphics2D g = (Graphics2D) getGraphics();
                    try{
                        BufferedImage pauseImage = ImageIO.read(new File("LinkGame\\image\\fruit_pause2.png"));
                        g.drawImage(pauseImage, margin, margin, GamePanel.this);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                running = !running;
            }
        });

        //提示按钮的事件监听
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(utils.hint()) {
                    drawTrack(Color.getHSBColor(0.55f, 0.5f, 0.9f));
                    GamePanel.this.repaint();
                } else {
                    JOptionPane.showMessageDialog(null,"无可消除的板块!","提示",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        //重排按钮的事件监听
        shuffleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                utils.rearrange();
                GamePanel.this.repaint();
            }
        });

        //帮助按钮的事件监听
        helpButton.addActionListener(new EvenListener.HelpButtonListener());

        //鼠标点击监听
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();   //获取点击的坐标
                int y = e.getY();
                if(running == false || x <= margin || y <= margin || x >= margin + columns * 40 || y >= margin + rows * 40) {
                    return;
                }
                x = (x - margin) / 40;  //坐标转化为图片的为止
                y = (y - margin) / 40;
                if(arr[y][x] == -1){
                    return;
                }
                Graphics g = getGraphics();
                g.setColor(Color.RED);
                g.drawRect(margin + x * 40, margin + y * 40, 39, 39);
                if(selectOne == false) {
                    selectOne = true;
                    selectX = y;
                    selectY = x;
                } else {
                    selectOne = false;
                    if(selectX == y && selectY == x) {
                        return;
                    }
                    if(utils.isLink(selectX, selectY, y, x)) {   //若是连通则先画出轨迹再将图片消除
                        drawTrack(Color.GREEN);
                        arr[selectX][selectY] = -1;
                        arr[y][x] = -1;
                    }
                    GamePanel.this.repaint();
                    if (utils.victory()){     //每一次消除图片都判断是否获胜
                        JOptionPane.showMessageDialog(null,"游戏胜利!","祝贺",JOptionPane.INFORMATION_MESSAGE);
                        jFrame.dispose();
                    }
                }
            }
        });
    }


    //绘制连连看的所有小图片
    @Override
    public void paintComponent(Graphics g) {
        try {
            Image background = ImageIO.read(new File("LinkGame/image/bg_game.png"));
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(active == false) {
            try{
                BufferedImage originImage = ImageIO.read(new File("LinkGame/image/original.png"));
                ((Graphics2D)g).drawImage(originImage, margin, margin, GamePanel.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int bgX = margin + j * 40;
                int bgY = margin + i * 40;
                int idx = arr[i][j];
                if(idx == -1) {
                    continue;
                }
                for (int x = 0; x < 40; x++) {
                    for (int y = 0; y < 40; y++) {
                        int X = bgX + x;
                        int Y = bgY + y;
                        g.setColor(getPixelColor(x, idx * 40 + y, X, Y));
                        g.fillRect(X, Y, 1, 1); // 绘制单个像素点
                    }
                }
            }
        }
    }
    //获取某一个点的像素值
    private Color getPixelColor(int x, int y, int bgX, int bgY){
        int e_pixel = element.getRGB(x, y);
        int m_pixel = mask.getRGB(x, y);
        int bg_pixel = background.getRGB(bgX, bgY);
        int pixel = m_pixel == -1? e_pixel : bg_pixel;
        return new Color(pixel);
    }

    //绘制消除路径
    private void drawTrack(Color color){
        Graphics2D g = (Graphics2D) getGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(4));
        for (int[][] points : track) {
            System.out.println(Arrays.toString(points[0]) + " " + Arrays.toString(points[1]));
            g.drawLine(points[0][1] * 40 + margin + 20, points[0][0] * 40 + margin + 20, points[1][1] * 40 + margin + 20, points[1][0] * 40 + margin + 20);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
