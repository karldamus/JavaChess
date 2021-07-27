package game;

import pieces.Piece;

public class Movelist implements Constants {
    private String[] moveList;
    private int moveNumber = 1;

    public Movelist() {
        moveList = new String[100];
    }

    public void updateMoveList(Board board, int initialRank, int initialFile, int finalRank, int finalFile, Piece pieceMoved, boolean captureMove) {
        char initialRank_char = Character.forDigit(rankArray[initialRank], 10);
        char initialFile_char = fileArray[initialFile];
        char finalRank_char = Character.forDigit(rankArray[finalRank], 10);
        char finalFile_char = fileArray[finalFile];
        int lastIndexAvailable;
//        int moveNumber;
        String pieceOrigination;

        String move;

        // if moveList is at limit, increase size
        if (moveList[moveList.length - 1] != null) {
            String[] tmpMoveList = new String[moveList.length + 100];
            for (int i = 0; i < moveList.length; i++) {
                tmpMoveList[i] = moveList[i];
            }
            moveList = tmpMoveList;
        }

        // find last index position
        lastIndexAvailable = -1;
        for (int i = 0; i < moveList.length; i++) {
            if (moveList[i] == null) {
                lastIndexAvailable = i;
                break;
            }
        }

        // determine any disambiguating moves
        int canMoveCounter = 0;
        char fenSymbol = pieceMoved.getFenSymbol();
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {
                if (board.getBoard()[rank][file].getPiece() != null) {
                    Piece tmpPiece = board.getBoard()[rank][file].getPiece();
                    if (tmpPiece.getFenSymbol() == fenSymbol && tmpPiece.isLegalMove(board, initialRank, initialFile, finalRank, finalFile))
                        canMoveCounter += 1;
                }
            }
        }

        boolean showFenSymbol = canMoveCounter > 1;
        boolean pawn = Character.toLowerCase(pieceMoved.getFenSymbol()) == 'p';
        String moveNumberStr = moveNumber + ". ";
        pieceOrigination = "" + initialFile_char + initialRank_char;

        // ! TEMPORARY FALSE STATEMENT - deprecate!
        showFenSymbol = false;

        if (showFenSymbol) {
            if (pawn) {
                move = pieceOrigination + (captureMove ? "x" : "") + finalFile_char + finalRank_char;
            } else {
                move = Character.toUpperCase(fenSymbol) + pieceOrigination + (captureMove ? "x" : "") + finalFile_char + finalRank_char;
            }
        } else {
            if (pawn)
                move = (captureMove ? "x" : "") + finalFile_char + finalRank_char;
            else
                move = Character.toUpperCase(fenSymbol) + (captureMove ? "x" : "") + finalFile_char + finalRank_char;
        }

//        System.out.println(move);
        moveList[lastIndexAvailable] = move;

        // update moveNumber
        if (!pieceMoved.isWhite())
            moveNumber += 1;
    }

    /**
     * Print the movelist in a readable fashion.
     * -- CONSOLE ONLY
     */
    public void printMoveList() {
        int moveNumber = 1;
        for (int i = 0; i < moveList.length; i += 2) {
            if (moveList[i] != null) {
                System.out.format("%1s%1s%6s", (moveNumber + ". "), moveList[i], moveList[i+1]);
                System.out.println();
                moveNumber += 1;
            }
        }
    }

    public String[] getMoveList() {
        return moveList;
    }


}
