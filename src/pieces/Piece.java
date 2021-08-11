package pieces;

import game.Board;
import game.Constants;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public abstract class Piece implements Constants {
    private boolean white;
    private char fenSymbol;
    private boolean hasMoved;

    private ImageIcon pieceImageIcon;
    private JLabel pieceJLabel;

    private String movePieceSound = "src/sounds/placePiece.wav";

    /**
     * Base Piece constructor.
     * @param white whether the piece is white or black
     * @param fenSymbol the FEN symbol associated with the piece.
     *                  E.g., p = black pawn. Q = white queen.
     */
    public Piece(boolean white, char fenSymbol) {
        this.white = white;
        this.fenSymbol = isWhite() ? Character.toUpperCase(fenSymbol) : fenSymbol;
        this.hasMoved = false;

        setupImage();
    }

    /**
     * Set-up the image/icon and JLabel associated with the piece.
     */
    private void setupImage() {
        // image icon setup
        String prefix = this.isWhite() ? "w" : "b";
        pieceImageIcon = new ImageIcon("src/sprites/chess/" + prefix + fenSymbol + "60.png");

        // JLabel setup
        pieceJLabel = new JLabel();
        pieceJLabel.setSize(LABEL_SIZE);
        pieceJLabel.setMaximumSize(LABEL_SIZE);
        pieceJLabel.setMinimumSize(LABEL_SIZE);

        pieceJLabel.setIcon(pieceImageIcon);
        pieceJLabel.repaint();
    }


    /**
     * Test whether a piece is the same colour as this.
     * @param piece the piece to compare against this.
     * @return true if same colour, else false.
     */
    public boolean isSameColour(Piece piece) {
        if ((this.isWhite() && piece.isWhite()) || (!this.isWhite() && !piece.isWhite()))
            return true;
        return false;
    }

    public abstract boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile);

    /**
     * Determine whether a piece is in way using directional vectors.
     * <li>Directional vector-matrix code sourced from 'Andreas Dolk' on Stackoverflow -- see README</li>
     * @param board the board in play.
     * @param initialRank the initial rank of the piece.
     * @param initialFile the initial file of the piece.
     * @param finalRank the destination rank of the piece.
     * @param finalFile the destination file of the piece.
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

    /**
     * Play the pieces sound.
     * @param takeMove Determine whether to play the 'takeMove' sound or the regular sound.
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    public void playSound(boolean takeMove) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(movePieceSound).getAbsoluteFile());
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
    }

    // ==========================

    public boolean isWhite() {
        return white;
    }

    public char getFenSymbol() {
        return fenSymbol;
    }

    public ImageIcon getPieceImageIcon() {
        return pieceImageIcon;
    }

    public void setPieceImageIcon(ImageIcon pieceImageIcon) {
        this.pieceImageIcon = pieceImageIcon;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public JLabel getPieceJLabel() {
        return pieceJLabel;
    }

    public void setPieceJLabel(JLabel pieceJLabel) {
        this.pieceJLabel = pieceJLabel;
    }
}
