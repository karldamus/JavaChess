package game;

import pieces.Piece;

public class Movelist implements Constants {
    private Move[] movelist;
    private int moveNumber;

    public Movelist() {
        movelist = new Move[100];
        moveNumber = 1;
    }

    public void updateMoveList(Move move) {
        int lastIndexAvailable;

        // if moveList is at limit, increase size
        if (movelist[movelist.length - 1] != null) {
            Move[] tmpMoveList = new Move[movelist.length + 100];
            for (int i = 0; i < movelist.length; i++) {
                tmpMoveList[i] = movelist[i];
            }
            movelist = tmpMoveList;
        }

        // find last index position
        lastIndexAvailable = -1;
        for (int i = 0; i < movelist.length; i++) {
            if (movelist[i] == null) {
                lastIndexAvailable = i;
                break;
            }
        }

        movelist[lastIndexAvailable] = move;

        // update moveNumber
        if (!move.getPieceMoved().isWhite())
            moveNumber += 1;
    }

    /**
     * Print the movelist in a readable fashion.
     * -- CONSOLE ONLY
     */
    public void printMoveList() {
        int moveNumber = 1;
        for (int i = 0; i < movelist.length; i += 2) {
            if (movelist[i] != null) {
                System.out.format("%1s%1s%6s", (moveNumber + ". "), movelist[i].toString(), movelist[i+1].toString());
                System.out.println();
                moveNumber += 1;
            }
        }
    }

    public Move[] getMovelist() {
        return movelist;
    }


}
