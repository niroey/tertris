import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.*;

public class GamePanel extends JFrame {
    private GameFrame gameFrame;
    private JButton soundButton;
    private JMenu levelMenu;
    private JMenuItem[] levelMenuItems;
    private Clip clip;
    private File file;
    private AudioInputStream stream;
    private AudioFormat format;
    private DataLine.Info info;

    private boolean isSoundOn;
    private int currentLevel = 1; // 초기 레벨 1로 설정

    public GamePanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        createMenu();
        createSoundButton();
        loadSound();
    }

    // 메뉴 및 버튼 생성
    private void createMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu screenMenu = new JMenu("설정");
        JMenuItem[] menuItem = new JMenuItem[3];
        String[] itemTitle = {"일시정지", "다시시작", "나가기"};

        MenuActionListener listener = new MenuActionListener();
        for (int i = 0; i < menuItem.length; i++) {
            menuItem[i] = new JMenuItem(itemTitle[i]);
            menuItem[i].addActionListener(listener);
            screenMenu.add(menuItem[i]);
        }

        levelMenu = new JMenu("레벨");
        levelMenuItems = new JMenuItem[3];
        for (int i = 0; i < levelMenuItems.length; i++) {
            levelMenuItems[i] = new JMenuItem("레벨" + (i + 1));
            levelMenuItems[i].addActionListener(listener);
            levelMenu.add(levelMenuItems[i]);
        }
        
        // 음량 및 레벨 설정 버튼 추가
        soundButton = new JButton("음악 ON");
        soundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSound();
            }
        });

        mb.add(screenMenu);
        mb.add(levelMenu);
        mb.add(Box.createHorizontalGlue());
        mb.add(soundButton);
        setJMenuBar(mb);
    }

    // 음악 ON/OFF 버튼 생성
    private void createSoundButton() {
        soundButton = new JButton("음악 ON");
        soundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSound();
            }
        });
    }

    // 음악 파일 로드
    private void loadSound() {
        file = new File("테트리스.wav");
        try {
            stream = AudioSystem.getAudioInputStream(file);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 음악 ON/OFF
    private void toggleSound() {
        if (isSoundOn) {
            clip.stop();
            clip.close();
            isSoundOn = false;
        } else {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            isSoundOn = true;
        }
        soundButton.setText(isSoundOn ? "음악 OFF" : "음악 ON");
    }

    // 메뉴 액션 리스너
    private class MenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            switch (cmd) {
                case "일시정지":
                    gameFrame.togglePause();
                    break;
                case "다시시작":
                    gameFrame.resumeGame();
                    break;
                case "나가기":
                    System.exit(0);
                    break;
            }
        }
    }
}