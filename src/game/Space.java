package game;

import pieces.Piece;

public class Space {
    private int rank;
    private char file;
    private boolean white;
    private Piece piece;

    public Space(boolean isWhite, int rank, char file) {
        setWhite(isWhite);
        setRank(rank);
        setFile(file);
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

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}
