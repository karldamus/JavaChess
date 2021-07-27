package game;

import pieces.Piece;

public class Fen implements Constants {
    String fenString;

    public Fen(Board board, boolean whiteToMove, int halfMoveNumber, int fullMoveNumber) {
        this.fenString = generateFenString(board, whiteToMove, halfMoveNumber, fullMoveNumber);
    }

    /**
     * Generate a String FEN notation from spaces[][]
     *
     * @return standard FEN string used for chess engines.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation">wikipedia.org/wiki/Forsyth-Edwards-Notation</a>
     * @see <a href="http://kirill-kryukov.com/chess/doc/fen.html">kirill-kryukov.com/chess/doc/fen.html</a>
     */
    public String generateFenString(Board board, boolean whiteToMove, int halfMoveNumber, int fullMoveNumber) {
        StringBuilder fenString = new StringBuilder();
        int spaceCounter = 0;

        /**
         * Go through every space in spaces[][]
         *      append FEN symbol for each piece
         *      append spaceCounter for every empty space
         *      append / for each rank
         * {@see <a href="http://kirill-kryukov.com/chess/doc/fen.html">kirill-kryukov.com/chess/doc/fen.html</a>} 16.1.3.1
         */
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {
                // piece detected
                if (board.getBoard()[rank][file].getPiece() != null) {
                    // append incremental counter
                    if (spaceCounter > 0)
                        fenString.append(spaceCounter);
                    // append piece fenSymbol
                    fenString.append(board.getBoard()[rank][file].getPiece().getFenSymbol());
                    // reset incremental counter
                    spaceCounter = 0;
                } else {
                    spaceCounter++;
                }
            }

            // no more squares to check. add spaceCounter if > 0
            if (spaceCounter > 0) {
                fenString.append(spaceCounter);
                spaceCounter = 0;
            }

            // new rank -> add "/"
            if (rank != RANKS - 1)
                fenString.append("/");
        }

        /**
         * Append 'w' or 'b' based on whose turn it is to move
         * {@see <a href="http://kirill-kryukov.com/chess/doc/fen.html">kirill-kryukov.com/chess/doc/fen.html</a>} 16.1.3.2
         */
        fenString.append(" " + (whiteToMove ? "w " : "b "));

        /**
         * Append castling availability.
         *      set tmp pieces to null and try assigning the piece at their designated 2d array positions
         */
        Piece pieceAtA1 = null, pieceAtE1 = null, pieceAtH1 = null, pieceAtA8 = null, pieceAtE8 = null, pieceAtH8 = null;

        try {
            pieceAtA1 = board.getBoard()[7][0].getPiece();
            pieceAtE1 = board.getBoard()[7][4].getPiece();
            pieceAtH1 = board.getBoard()[7][7].getPiece();
            pieceAtA8 = board.getBoard()[0][0].getPiece();
            pieceAtE8 = board.getBoard()[0][4].getPiece();
            pieceAtH8 = board.getBoard()[0][7].getPiece();
        } catch (Exception e) { }

        // check squares: e1 king elif a1 & h1 rook ... see else statement
        fenString = castlingCheck(fenString, pieceAtA1, pieceAtE1, pieceAtH1, false);
        // check squares: e8 king elif a8 & h8 rook ... see else statement
        fenString = castlingCheck(fenString, pieceAtA8, pieceAtE8, pieceAtH8, true);

        /**
         * Append en passant target square.
         *      if no en passant square is available, append "-"
         * {@see <a href="http://kirill-kryukov.com/chess/doc/fen.html">kirill-kryukov.com/chess/doc/fen.html</a>} 16.1.3.4
         */


        /**
         * Append halfMoveNumber and fullMoveNumber
         * {@see <a href="http://kirill-kryukov.com/chess/doc/fen.html">kirill-kryukov.com/chess/doc/fen.html</a>}
         *      16.1.3.5
         *      16.1.3.6
         */
        fenString.append(" " + halfMoveNumber);
        fenString.append(" " + fullMoveNumber);

        // return finalized FEN string
        return fenString.toString();
    }

    /**
     * Helper method for {@link #generateFenString}. Checks to see if castling is available and appends
     *      the appropriate values to {@param fenString} which is promptly returned for continued alteration.
     * @param fenString the StringBuilder from {@link #generateFenString}.
     * @param aFileRook the Piece in the a-file rook position
     * @param king the Piece in the king position
     * @param hFileRook the Piece in the h-file rook position
     * @param lowercase whether or not the second paramater for {@link #hasPieceMoved(Piece, char)}
     *                  should be lowercase or not. this is dependent on the colour of the piece
     *                  we are looking for.
     * @return modified StringBuilder from {@link #generateFenString}
     */
    private StringBuilder castlingCheck(StringBuilder fenString, Piece aFileRook, Piece king, Piece hFileRook, boolean lowercase) {
        if (hasPieceMoved(king, lowercase ? 'k' : 'K')) {
            fenString.append("- ");
        } else if (hasPieceMoved(aFileRook, lowercase ? 'r' : 'R') && hasPieceMoved(hFileRook, lowercase ? 'r' : 'R')) {
            fenString.append("- ");
        } else {
            // check rooks separately
            if (!hasPieceMoved(hFileRook, lowercase ? 'r' : 'R'))
                fenString.append(lowercase ? "k" : "K");
            if (!hasPieceMoved(aFileRook, lowercase ? 'r' : 'R'))
                fenString.append(lowercase ? "q" : "Q");
        }

        return fenString;
    }

    /**
     * Helper method for {@link #generateFenString}. Checks if {@param piece} has moved.
     * @param piece the piece in question.
     * @param symbol the symbol to compare against {@link Piece#getFenSymbol()}.
     * @return true if piece has moved, false if has not moved.
     */
    private boolean hasPieceMoved(Piece piece, char symbol) {
        if (piece == null || (piece.getFenSymbol() == symbol && piece.hasMoved()) || piece.getFenSymbol() != symbol)
            return true;
        return false;
    }

    public void printFenString() {
        System.out.println(fenString);
    }
}
