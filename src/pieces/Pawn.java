package pieces;

import game.Board;

public class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white, 'p');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        int absRank = Math.abs(finalRank - initialRank);
        int absFile = Math.abs(finalFile - initialFile);

        // greater than 2 squares on the rank or 1 square on the file
        if (absRank > 2 || absFile > 1)
            return false;
        // 2 space first move check
        if (absRank == 2 && this.hasMoved())
            return false;
        // diagonal move on two square forwards
        if (absRank == 2 && absFile > 0)
            return false;
        // check if diagonal 1 square move is capturing a piece
        if (absRank == 1 && absFile == 1) {
            if (board.getBoard()[finalRank][finalFile].getPiece() == null)
                return false;
        }
        // backwards movement check
        if (this.isWhite() && (finalRank > initialRank))
            return false;
        if (!this.isWhite() && (finalRank < initialRank))
            return false;

        // all cases covered, therefore it is a legal move
        return true;
    }
}
