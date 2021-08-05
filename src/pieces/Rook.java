package pieces;

import game.Board;

public class Rook extends Piece {
    public Rook(boolean white) {
        super(white, 'r');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {

        // check if on horizontal or vertical
        int absRank = Math.abs(finalRank - initialRank);
        int absFile = Math.abs(finalFile - initialFile);

        if (absRank > 0 && absFile > 0)
            return false;

        // check if piece in way
        if (this.pieceInWay(board, initialRank, initialFile, finalRank, finalFile))
            return false;

        return true;
    }
}
