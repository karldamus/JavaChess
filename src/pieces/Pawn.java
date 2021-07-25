package pieces;

import game.Board;

public class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white, 'p');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        int pawnRankDistance = Math.abs(finalRank - initialRank);
        int pawnFileDistance = Math.abs(finalFile - initialFile);

        // greater than 2 squares on the rank or 1 square on the file
        if (pawnRankDistance > 2 || pawnFileDistance > 1)
            return false;
        // 2 space first move check
        if (pawnRankDistance == 2 && this.hasMoved())
            return false;
        // check if diagonal 1 square move is capturing a piece
        if (pawnRankDistance == 1 && pawnFileDistance == 1) {
            if (board.getSpaces()[finalRank][finalFile].getPiece() == null)
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
