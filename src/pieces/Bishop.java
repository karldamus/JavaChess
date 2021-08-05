package pieces;

import game.Board;

import java.awt.*;

public class Bishop extends Piece {

    public Bishop(boolean white) {
        super(white, 'b');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {

        // check if not on the diagonal
        int absRank = Math.abs(finalRank - initialRank);
        int absFile = Math.abs(finalFile - initialFile);

        if (absRank != absFile)
            return false;

        // check if piece in way
        if (this.pieceInWay(board, initialRank, initialFile, finalRank, finalFile))
            return false;

        return true;
    }
}
