package pieces;

import game.Board;

public class Queen extends Piece {
    public Queen(boolean white) {
        super(white, 'q');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        return true;
    }
}
