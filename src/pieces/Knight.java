package pieces;

import game.Board;

public class Knight extends Piece {

    public Knight(boolean white) {
        super(white, 'n');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        int absRank = Math.abs(finalRank - initialRank);
        int absFile = Math.abs(finalFile - initialFile);

        if (absRank == 2 && absFile == 1)
            return true;
        if (absRank == 1 && absFile == 2)
            return true;

        return false;
    }
}
