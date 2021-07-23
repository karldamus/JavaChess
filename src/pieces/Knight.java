package pieces;

import game.Board;

public class Knight extends Piece {

    public Knight(boolean white) {
        super(white, 'n');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        return true;
    }
}
