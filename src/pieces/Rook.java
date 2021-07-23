package pieces;

import game.Board;

public class Rook extends Piece {
    public Rook(boolean white) {
        super(white, 'r');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        return true;
    }
}
