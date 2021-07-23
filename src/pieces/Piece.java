package pieces;

import javax.swing.*;

public abstract class Piece {
    private boolean white;
    private char fenSymbol;
    private ImageIcon pieceIcon;

    public Piece(boolean white, char fenSymbol) {
        this.white = white;
        this.fenSymbol = fenSymbol;

        // image icon setup
        String prefix = this.isWhite() ? "w" : "b";
        this.pieceIcon = new ImageIcon("sprites/" + prefix + fenSymbol + "60.png");
    }

    public boolean isSameColour(Piece piece) {
        if ((this.isWhite() && piece.isWhite()) || (!this.isWhite() && !piece.isWhite()))
            return true;
        return false;
    }

//    public abstract boolean isLegalMove(int initialFile, int initialRank, int finalFile, int finalRank);

    // ==========================

    public boolean isWhite() {
        return white;
    }

    public char getFenSymbol() {
        return fenSymbol;
    }

    public ImageIcon getPieceIcon() {
        return pieceIcon;
    }

    public void setPieceIcon(ImageIcon pieceIcon) {
        this.pieceIcon = pieceIcon;
    }
}
