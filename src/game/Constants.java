package game;

import java.awt.*;

public interface Constants {
    static final char[] fileArray = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    static final String[] fileArrayStr = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
    static final int[] rankArray = new int[] {8, 7, 6, 5, 4, 3, 2, 1};
    static final int RANKS = 8;
    static final int FILES = 8;

    static final int BOARD_WIDTH = 480;
    static final int BOARD_HEIGHT = 480;
    static final int LABEL_WIDTH = 60;
    static final int LABEL_HEIGHT = 60;
    static final Color BLACK_COLOR = Color.decode("#007E08");
    static final Color WHITE_COLOR = Color.decode("#EBEBEB");
    static final Dimension BOARD_SIZE = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    static final Dimension LABEL_SIZE = new Dimension(LABEL_WIDTH, LABEL_HEIGHT);
    static final GridLayout BOARD_GRID_LAYOUT = new GridLayout(RANKS, FILES);

    static final String GAME_TITLE = "Java Chess by Karl";

    String BASE_FEN_STRING = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w  - 0 1";
}
