package pieces;

import game.Board;

public class Bishop extends Piece {

    public Bishop(boolean white) {
        super(white, 'b');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        return true;
    }
}
