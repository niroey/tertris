import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GameOver extends JDialog {
	private GameFrame gameFrame;
	private JTextField playerNameField;
    private JLabel scoreLabel;
    private JButton saveButton;
    
    private int score;
    
    public GameOver(GameFrame gameFrame, int score) {
        super(gameFrame, "게임 오버", true);
        this.gameFrame = gameFrame;  // GameFrame 인스턴스를 초기화
        this.score = score;
        
        setLayout(new BorderLayout());

        scoreLabel = new JLabel("당신의 점수: " + score);
        playerNameField = new JTextField(" ");
        saveButton = new JButton("저장");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText();
                playerName = (playerName != null) ? playerName.trim() : ""; // null이면 빈 문자열로 대체, 앞뒤 공백 제거

                if (!playerName.isEmpty() && gameFrame != null) { // 빈 문자열이 아닌 경우에만 저장
                    gameFrame.saveScore(playerName, score);
                }

                // 저장 후에 다이얼로그를 닫음
                dispose();
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(scoreLabel);
        panel.add(playerNameField);
        panel.add(saveButton);

        add(panel, BorderLayout.CENTER);

        setSize(300, 150);
        setLocationRelativeTo(gameFrame);
    }
    
 // score 변수에 대한 getter 메소드 추가
    public int getScore() {
        return score;
    }

    // playerNameField 변수에 대한 getter 메소드 추가
    public JTextField getPlayerNameField() {
        return playerNameField;
    }
}