package pieces;

import game.Board;

public class Queen extends Piece {
    public Queen(boolean white) {
        super(white, 'q');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {

        // check if on diagonal, horizontal, or vertical
        int absRank = Math.abs(finalRank - initialRank);
        int absFile = Math.abs(finalFile - initialFile);

        if (
                (absRank > 0 && absFile != 0 && absRank != absFile) ||
                (absFile > 0 && absRank != 0 && absFile != absRank)
        )
            return false;

        // check if piece in way
        if (this.pieceInWay(board, initialRank, initialFile, finalRank, finalFile))
            return false;

        return true;
    }
}
