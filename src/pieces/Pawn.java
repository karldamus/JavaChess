package pieces;

import game.Board;

public class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white, 'p');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        return true;
    }
}
