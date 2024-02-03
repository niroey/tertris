import java.awt.*;
import javax.swing.*;

public class GameGround extends JPanel {
    private JLabel[][] gridLabels;
    private boolean[][] isCellFilled;
    
    public GameGround() {
        setLayout(new GridLayout(9, 9));
        
        gridLabels = new JLabel[9][9];
        isCellFilled = new boolean[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                gridLabels[i][j] = new JLabel();
                gridLabels[i][j].setOpaque(true);
                
                gridLabels[i][j].setBorder(BorderFactory.createLineBorder(Color.WHITE));
                
                gridLabels[i][j].setBackground(Color.LIGHT_GRAY);
                add(gridLabels[i][j]);
            }
        }
    }
    
    public JLabel[][] getGridLabels() {
        return gridLabels;
    }
    public boolean[][] getIsCellFilled() {
        return isCellFilled;
    }
}