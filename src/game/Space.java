package game;

import pieces.Piece;

import javax.swing.*;

public class Space implements Constants {
    private int rank;
    private char file;
    private boolean white;
    private Piece piece;

    private JPanel spaceJPanel;

    /**
     * Create a new Space which is used within {@link Board#getBoard()}
     * @param isWhite whether the square is white or black.
     * @param rank the rank of the square.
     * @param file the file of the square.
     */
    public Space(boolean isWhite, int rank, char file) {
        setWhite(isWhite);
        setRank(rank);
        setFile(file);

        setupJPanel();
    }

    /**
     * Create the JPanel for this Space.
     */
    private void setupJPanel() {
        spaceJPanel = new JPanel();
        spaceJPanel.setSize(LABEL_SIZE);
        spaceJPanel.setMaximumSize(LABEL_SIZE);
        spaceJPanel.setMinimumSize(LABEL_SIZE);

        spaceJPanel.setBackground(isWhite() ? WHITE_COLOR : BLACK_COLOR);
        spaceJPanel.setName(Character.toString(file) + Integer.toString(rank));
    }

    /**
     * Set this.piece and corresponding JLabel.
     * @param piece the piece to add.
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
        if (piece != null)
            spaceJPanel.add(piece.getPieceJLabel());
    }

    /**
     * Remove any piece associated with this Space.
     */
    public void clearPiece() {
        this.piece = null;
//        spaceJPanel.remove(0);
    }

    // get____Index methods are used for array purposes

    public int getRankIndex() {
        int[] rankArray = new int[] {0,1,2,3,4,5,6,7};
        return rankArray[this.rank];
    }

    public int getFileIndex() {
        char[] fileArray = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

        for (int i = 0; i < fileArray.length; i++) {
            if (this.file == fileArray[i])
                return i;
        }

        return -1;
    }

    // get____Char methods are used for visual purposes

    public char getFileChar() {
        return this.file;
    }

    public char getRankChar() {
        char[] rankArray = new char[] {'8','7','6','5','4','3','2','1'};

        for (int i = 0; i < rankArray.length; i++) {
            if (this.rank == i)
                return rankArray[i];
        }

        return 'z';
    }

    // ===============

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setFile(char file) {
        this.file = file;
    }

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public Piece getPiece() {
        return piece;
    }

    public JPanel getSpaceJPanel() {
        return spaceJPanel;
    }

    public void setSpaceJPanel(JPanel spaceJPanel) {
        this.spaceJPanel = spaceJPanel;
    }
}
