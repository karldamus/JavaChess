import game.Board;
import game.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Game implements Constants {

    private JPanel boardPanel;

    public Game() {
        Board board = new Board();

        setUpGui();



    }

    public void setUpGui() {
        boardPanel = new JPanel(BOARD_GRID_LAYOUT);
        boardPanel.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        boardPanel.setMaximumSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        boardPanel.setMinimumSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    }


}
