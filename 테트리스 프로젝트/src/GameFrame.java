import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.*;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private GameGround gameGround;
    private ScorePanel scorePanel;
    
    private Color currentColor;
    private int currentRow;
    private int currentCol;
    private boolean isDownKeyPressed;
    private boolean[][] isCellFilled;
    private boolean isGameRunning = true;
    private int score; //점수를 증가시키기 위한 변수
    private String playerName;
    private boolean isPaused = false; //gamePanel에 쓰일 일시정지 및 다시 시작
    private int levelSpeed; //블록 떨어지는 시간변수
    
    public GameFrame() {
        setTitle("테트리스");
        setLayout(new BorderLayout());
        setSize(650, 600);

        gameGround = new GameGround(); // GameGround 인스턴스 생성
        isCellFilled = gameGround.getIsCellFilled(); // isCellFilled 배열 가져오기
        add(gameGround, BorderLayout.CENTER);

        scorePanel = new ScorePanel(); // ScorePanel 인스턴스 생성
        add(scorePanel, BorderLayout.EAST);
        
        gamePanel = new GamePanel(this);  // GamePanel 인스턴스 생성
        JMenuBar gamePanelMenuBar = gamePanel.getJMenuBar();  // GamePanel의 메뉴 바 가져오기
        setJMenuBar(gamePanelMenuBar);  // GameFrame의 메뉴 바로 설정

        Thread th = new Thread(new GameRunnable());
        th.start();
        levelSpeed = 300;
        
        addKeyListener(new MyKeyListener());
        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private Color getRandomColor(int level) {
    	Color[] level1Colors = {Color.PINK, Color.CYAN, Color.GREEN, Color.YELLOW};
        Color[] level2Colors = {Color.PINK, Color.CYAN, Color.GREEN, Color.YELLOW, Color.DARK_GRAY, Color.RED};
        Color[] level3Colors = {Color.PINK, Color.CYAN, Color.GREEN, Color.YELLOW, Color.LIGHT_GRAY, 
        		Color.RED, Color.DARK_GRAY, Color.BLUE, Color.MAGENTA};
    
        Color[] colors;
        
        if (level == 1) {
            colors = level1Colors;
        } else if (level == 2) {
            colors = level2Colors;
        } else if (level == 3) {
            colors = level3Colors;
        } else {
            // 기본값으로 레벨 1의 색상 사용
            colors = level1Colors;
        }

        return colors[(int) (Math.random() * colors.length)];
    }

    private class GameRunnable implements Runnable {
        @Override
        public void run() {
            spawn();
            while (isGameRunning) {
            	if (!isPaused) {
                    moveDown();
                    check();
                    updateScore();
                    repaint();
                    try {
                        Thread.sleep(levelSpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 일시정지 중에는 스레드를 블록
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            showGameOverDialog();
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }
    
    public void resumeGame() {
        isPaused = false;
        synchronized (this) {
            this.notify();  // 일시정지에서 깨우기
        }
    }
    synchronized private void spawn() {
    	currentColor = getRandomColor(calculateLevel());
        currentRow = 0;
        currentCol = 4; // 중앙에서 시작하도록
        drawBlock();
    }

    synchronized private void clearBlock() { //내려오는 블록의 기존 위치를 지우는 역할
        isCellFilled[currentRow][currentCol] = false;
        gameGround.getGridLabels()[currentRow][currentCol].setBackground(Color.LIGHT_GRAY);
    }

    synchronized private void fixBlock() { //내려온 블록의 위치를 저장하는 역할
    	gameGround.getGridLabels()[currentRow][currentCol].setBackground(currentColor);
        isCellFilled[currentRow][currentCol] = true;
        
        //check(); //블록터지기 전에 확인하는 메소드
    }

    synchronized private void drawBlock() { //내려온 위치를 그리는 역할
    	gameGround.getGridLabels()[currentRow][currentCol].setBackground(currentColor);
    }

    synchronized private void moveDown() {
        if (currentRow < 8 && isCellFilled[currentRow + 1][currentCol] == false) {
            clearBlock();
            currentRow++;
            drawBlock();
        } else {
            fixBlock();
            spawn();

            if (currentRow == 0) {
                gameOver();
                return;
            }
        }
    }

    private class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
        	if (!isGameRunning) {
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT && currentCol > 0) { //왼쪽 키를 누를때
                if (!isCellFilled[currentRow][currentCol - 1]) { //한칸 왼쪽에 블록이 존재하면
                	gameGround.getGridLabels()[currentRow][currentCol].setBackground(Color.LIGHT_GRAY); //현재위치는 배경으로 취급하고
                    currentCol--; //왼쪽으로 간다
                    drawBlock();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && currentCol < 8) {
                if (!isCellFilled[currentRow][currentCol + 1]) {
                	gameGround.getGridLabels()[currentRow][currentCol].setBackground(Color.LIGHT_GRAY);
                    currentCol++;
                    drawBlock();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                isDownKeyPressed = true;
                moveDown();
            }
        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                isDownKeyPressed = false;
            }
        }
    }
    
    synchronized private void gameOver() {
    	for (int col = 0; col < 8; col++) {
            if (isCellFilled[0][col]) {
                isGameRunning = false;
                showGameOverDialog();

                break;
            }
        }
    }
    
    private void updateScore() {
        int level = calculateLevel(); 
        scorePanel.setScore(score, level); // ScorePanel에 현재 score 및 level 설정
        
        if (level == 2) {
            levelSpeed = 200; // 0.2초
        } else if (level == 3) {
            levelSpeed = 100; // 0.1초
        }
    }

    private int calculateLevel() {
    	if (score >= 800) {
            return 3;
        } else if (score >= 300) {
            return 2;
        } else {
            return 1;
        }
    }
    
    synchronized private void check() {
        ArrayList<Point> remove = new ArrayList<>(); // 터져야 할 블록들을 담을 리스트
        boolean[][] visited = new boolean[9][9]; // 이미 방문한 블록인지 확인하는 함수

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (isCellFilled[i][j] && !visited[i][j]) { // 블록이 채워져 있고 아직 방문하지 않은 경우
                    Color targetColor = gameGround.getGridLabels()[i][j].getBackground();
                    ArrayList<Point> connectedBlocks = new ArrayList<>();
                    find(i, j, targetColor, connectedBlocks, visited); // 연결된 블록 찾기

                    // 찾은 연결된 블록이 3개 이상인 경우 터트릴 대상에 추가
                    if (connectedBlocks.size() >= 3) {
                    	remove.addAll(connectedBlocks);
                        score += connectedBlocks.size() * 10; // 한 그룹당 10점 추가
                    }
                }
            }
        }

        for (Point point : remove) {// 터트릴 대상의 블록들을 순회하며 제거
            clearBlock(point.x, point.y);
        }
        // 블록들이 터진 후에 위에 있는 블록들을 아래로 이동
        moveBlocksDown();
    }

    // 현재 위치에서 연결된 블록들을 찾는 함수
    private void find(int row, int col, Color targetColor, 
    		ArrayList<Point> connectedBlocks, boolean[][] visited) {
        if (row < 0 || row >= 9 || col < 0 || col >= 9  //row, col이 0이랑 8사이에 있나 보기
        		|| visited[row][col] || !isCellFilled[row][col] // 범위를 벗어났거나 이미 방문한 경우 중지, 좌표에 블록이 존재하면 중지
        				|| !gameGround.getGridLabels()[row][col].getBackground().equals(targetColor)) {
            //해당 좌표의 배경색이 목표한 색깔과 똑같은지도 확인
        	return;
        }
        
        // 현재 위치를 방문했음을 표시
        visited[row][col] = true;
        // 연결된 블록 리스트에 현재 위치 추가
        connectedBlocks.add(new Point(row, col));

        // 상하좌우로 연결된 블록 찾기
        //현재 좌표가 유효하고, 연결된 블록에 속한다면, 이 함수는 재귀적으로 상하좌우의 인접한 좌표들에 대해 find 함수를 호출
        find(row - 1, col, targetColor, connectedBlocks, visited); // 위
        find(row + 1, col, targetColor, connectedBlocks, visited); // 아래
        find(row, col - 1, targetColor, connectedBlocks, visited); // 왼쪽
        find(row, col + 1, targetColor, connectedBlocks, visited); // 오른쪽
    }

    // 블록을 지우는 메서드
    synchronized private void clearBlock(int row, int col) {
        isCellFilled[row][col] = false; // 해당 위치의 블록을 빈 공간으로 설정
        gameGround.getGridLabels()[row][col].setBackground(Color.LIGHT_GRAY);
    }

    synchronized private void moveBlocksDown() {// 블록들이 터진 후에 위에 있는 블록들을 아래로 이동시키는 메서드
        for (int col = 0; col < 9; col++) { // 전체 그리드 열을 순회
            for (int row = 8; row > 0; row--) {  // 아래에서 위로 순회
                if (!isCellFilled[row][col] && isCellFilled[row - 1][col]) {
                    // 현재 위치로 블록 이동
                	gameGround.getGridLabels()[row][col].setBackground(gameGround.getGridLabels()[row - 1][col].getBackground());
                    // 이전 위치를 비움
                	gameGround.getGridLabels()[row - 1][col].setBackground(Color.LIGHT_GRAY);
                	// 상태 배열 업데이트
                    isCellFilled[row][col] = true;
                    isCellFilled[row - 1][col] = false;
                }
            }
        }
    }
    private void showGameOverDialog() {
        // GameOver 다이얼로그를 생성하고 표시
        GameOver gameOverDialog = new GameOver(this, score);
        gameOverDialog.setVisible(true);
        dispose();
    }
    
    public void saveScore(String playerName, int score) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("scores.txt", true))) {
            // 파일에 플레이어의 이름과 점수를 저장
            writer.println(playerName + "의 점수는 " + score);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}