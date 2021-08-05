package pieces;

import game.Board;

import java.awt.*;

/**
 * Functions to determine if a move is viable.
 *
 * @apiNote Direction vector implementation derived courtesy of @DJClayworth on Stackoverflow
 * @see <a href="https://stackoverflow.com/a/4305409/13280626">Stack Overflow Answer by DJClayworth</a>
 */
public class Movecheck {
    Board board;
    int initialRank;
    int initialFile;
    int finalRank;
    int finalFile;

    int pieceRankDistance;
    int pieceFileDistance;

    Piece originPiece; // 'initial'
    Piece destinationPiece; // 'final'

    public boolean canMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        this.board = board;
        this.initialRank = initialRank;
        this.initialFile = initialFile;
        this.finalRank = finalRank;
        this.finalFile = finalFile;

        pieceRankDistance = Math.abs(finalRank - initialRank);
        pieceFileDistance = Math.abs(finalFile - initialFile);

        try {
            originPiece = board.getBoard()[initialRank][initialFile].getPiece();
            destinationPiece = board.getBoard()[initialRank][initialFile].getPiece();
        } catch (Exception e) { }

        if (noPieceMoved())
            return false;

        if (pieceBlockingPath())
            return false;

        if (pieceAtDestinationOfSameColour())
            return false;

        // all cases checked, piece can move
        return true;
    }

    public boolean noPieceMoved() {
        if (pieceRankDistance == 0 && pieceFileDistance == 0)
            return true;

        return false;
    }

    public boolean pieceAtDestinationOfSameColour() {
        if (destinationPiece != null) {
            // if: both white || both black
            if ((originPiece.isWhite() && destinationPiece.isWhite()) || (!originPiece.isWhite() && !destinationPiece.isWhite()))
                return true;
        }

        return false;
    }

    public boolean pieceBlockingPath() {
        // generate a direction vector
        Point direction = new Point((int) Math.signum(finalFile - initialFile), (int) Math.signum(finalRank - initialRank));



        // all cases tested, therefore there is no piece blocking the designated path.
        return false;
    }
}
