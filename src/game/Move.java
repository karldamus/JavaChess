package game;

import pieces.Piece;

/**
 * Represents a move.
 */
public class Move implements Constants {
    private String moveString;
    private Piece pieceMoved;

    public Move(Board board, int initialRank, int initialFile, int finalRank, int finalFile, Piece pieceMoved, boolean captureMove) {
        this.pieceMoved = pieceMoved;

        char initialRank_char = Character.forDigit(rankArray[initialRank], 10);
        char initialFile_char = fileArray[initialFile];
        char finalRank_char = Character.forDigit(rankArray[finalRank], 10);
        char finalFile_char = fileArray[finalFile];

        String pieceOrigination = "" + initialFile_char + initialRank_char;
        boolean pawn = Character.toLowerCase(pieceMoved.getFenSymbol()) == 'p';
        char fenSymbol = pieceMoved.getFenSymbol();
        int showFenSymbol = 0;

        // determine any disambiguating moves (more than one piece to same square)
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {

                // if piece at board position [rank][file] is not null
                if (board.getBoard()[rank][file].getPiece() != null) {
                    // set up tmpPiece and check for matching fen symbols, then check if isLegalMove
                    Piece tmpPiece = board.getBoard()[rank][file].getPiece();
                    if (tmpPiece.getFenSymbol() == fenSymbol && tmpPiece.isLegalMove(board, initialRank, initialFile, finalRank, finalFile))
                        showFenSymbol += 1; // if above 1, then more than one piece can move. -- see below
                }

            }
        }

        // TEMPORARY HARD-CODE (until isLegalMove() is working in all Piece sub-classes)
        showFenSymbol = 0;

        if (showFenSymbol > 1) {
            if (pawn) {
                moveString = pieceOrigination + (captureMove ? "x" : "") + finalFile_char + finalRank_char;
            } else {
                moveString = Character.toUpperCase(fenSymbol) + pieceOrigination + (captureMove ? "x" : "") + finalFile_char + finalRank_char;
            }
        } else {
            if (pawn)
                moveString = (captureMove ? "x" : "") + finalFile_char + finalRank_char;
            else
                moveString = Character.toUpperCase(fenSymbol) + (captureMove ? "x" : "") + finalFile_char + finalRank_char;
        }
    }

    public String toString() {
        return moveString;
    }

    public String getMoveString() {
        return moveString;
    }

    public Piece getPieceMoved() {
        return pieceMoved;
    }
}
