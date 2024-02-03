import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class StartPanel extends JPanel {
    private RankingManager rankingManager;
    private ImageIcon image;
    private Rectangle startButton;
    private Rectangle rankingButton;
    private Rectangle exitButton;

    public StartPanel() {
        image = new ImageIcon("open.png");

        startButton = new Rectangle(360, 220, 300, 80);
        rankingButton = new Rectangle(400, 320, 300, 80);
        exitButton = new Rectangle(440, 420, 300, 80);

        rankingManager = new RankingManager();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (startButton.contains(e.getPoint())) {
                    // "게임시작" 버튼 클릭 시 GameFrame을 생성하고 보이도록 함
                    SwingUtilities.invokeLater(() -> {
                        JFrame gameFrame = new GameFrame();
                        gameFrame.setVisible(true);
                        // 기존의 StartPanel이 속한 JFrame을 숨김
                        SwingUtilities.getWindowAncestor(StartPanel.this).setVisible(false);
                    });
                } else if (rankingButton.contains(e.getPoint())) {
                    // "랭킹" 버튼 클릭 시 실행할 코드
                    showRankingDialog();
                } else if (exitButton.contains(e.getPoint())) {
                    // "나가기" 버튼 클릭 시 실행할 코드
                    System.exit(0);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
        drawRanking(g);
    }

    private void drawRanking(Graphics g) {
        List<RankingManager.RankingEntry> ranking = rankingManager.getRanking();
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));

        int startY = 480;
        int lineHeight = 30;

        for (int i = 0; i < ranking.size(); i++) {
            RankingManager.RankingEntry entry = ranking.get(i);
            String line = (i + 1) + ". " + entry.getPlayerName() + " - " + entry.getScore();
            g.drawString(line, 50, startY + i * lineHeight);
        }
    }

    private void showRankingDialog() {
        // 랭킹 정보를 보여줄 새로운 창 생성
        JDialog rankingDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "랭킹", true);
        rankingDialog.setLayout(new BorderLayout());

        JTextArea rankingTextArea = new JTextArea();
        rankingTextArea.setEditable(false);

        // score.txt 파일에서 랭킹 정보를 읽어와 JTextArea에 추가
        List<String> rankingLines = readRankingFromFile("scores.txt");
        for (String line : rankingLines) {
            rankingTextArea.append(line + "\n");
        }

        rankingDialog.add(new JScrollPane(rankingTextArea), BorderLayout.CENTER);

        rankingDialog.setSize(300, 300);
        rankingDialog.setLocationRelativeTo(this);
        rankingDialog.setVisible(true);
    }
    
    private List<String> readRankingFromFile(String filePath) {
        List<String> rankingLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rankingLines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rankingLines;
    }

    public static void main(String[] args) {
        JFrame startFrame = new JFrame();
        StartPanel startPanel = new StartPanel();
        startFrame.add(startPanel);
        startFrame.setSize(800, 600);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);
    }
}