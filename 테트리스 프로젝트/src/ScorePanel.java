import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ScorePanel extends JPanel {
    private JLabel scoreLabel;
    private JLabel levelLabel;
    
    public ScorePanel() {
        scoreLabel = new JLabel("Score: 0");
        levelLabel = new JLabel("Level: 1"); // 기본 레벨은 1로 설정
        
        // 패널 설정
        setLayout(new GridLayout(2, 1));
        setBackground(Color.PINK);

        // Score 라벨 설정
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(Color.PINK);
        scoreLabel.setFont(new Font("neodgm", Font.PLAIN, 20));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Level 라벨 설정
        levelLabel.setOpaque(true);
        levelLabel.setBackground(Color.PINK);
        levelLabel.setFont(new Font("neodgm", Font.PLAIN, 20));
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 패널에 라벨 추가
        add(scoreLabel);
        add(levelLabel);
        
        setPreferredSize(new Dimension(110, 100));
    }

    public void setScore(int score, int level) {
        scoreLabel.setText("Score: " + score);
        levelLabel.setText("Level: " + level);
    }

    public int getScore() {
        String scoreText = scoreLabel.getText().replace("Score: ", "");
        return Integer.parseInt(scoreText);
    }

    public int getLevel() {
        String levelText = levelLabel.getText().replace("Level: ", "");
        return Integer.parseInt(levelText);
    }
}