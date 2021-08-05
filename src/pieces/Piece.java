package pieces;

import game.Board;

import javax.swing.*;
import java.awt.*;

public abstract class Piece {
    private boolean white;
    private char fenSymbol;
    private ImageIcon pieceIcon;
    private boolean hasMoved;

    public Piece(boolean white, char fenSymbol) {
        this.white = white;
        this.fenSymbol = isWhite() ? Character.toUpperCase(fenSymbol) : fenSymbol;
        this.hasMoved = false;

        // image icon setup
        String prefix = this.isWhite() ? "w" : "b";
        this.pieceIcon = new ImageIcon("sprites/" + prefix + fenSymbol + "60.png");
    }

    public boolean isSameColour(Piece piece) {
        if ((this.isWhite() && piece.isWhite()) || (!this.isWhite() && !piece.isWhite()))
            return true;
        return false;
    }

    public abstract boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile);

    /**
     * directional vector-matrix code sourced from 'Andreas Dolk' on Stackoverflow -- see README
     * @param board
     * @param initialRank
     * @param initialFile
     * @param finalRank
     * @param finalFile
     * @return true if piece in way, false otherwise
     */
    public boolean pieceInWay(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        Point direction = new Point((int) Math.signum(finalRank - initialRank), (int) Math.signum(finalFile - initialFile));
        Point current = new Point((int) (initialRank + direction.getX()), (int) (initialFile + direction.getY()));
        Point destination = new Point(finalRank, finalFile);

        while (!current.equals(destination)) {
            if (board.getBoard()[current.x][current.y].getPiece() != null)
                return true;
            current.x = current.x + direction.x;
            current.y = current.y + direction.y;
        }

        return false;
    }

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

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}
