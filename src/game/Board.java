package game;

import pieces.*;

import java.util.Scanner;

public class Board {
    private static final int RANKS = 8;
    private static final int FILES = 8;
    private static Space[][] spaces = new Space[RANKS][FILES];

    private static int halfmoveNumber = 0;
    private static int fullMoveNumber = 1;

    private boolean whiteToMove = true;

    private String[] moveList = new String[100];

    public static final Piece[] whitePieces = new Piece[] {
            new Rook(true), new Knight(true), new Bishop(true), new Queen(true), new King(true), new Bishop(true), new Knight(true), new Rook(true),
    };

    public static final Piece[] blackPieces = new Piece[] {
            new Rook(false), new Knight(false), new Bishop(false), new Queen(false), new King(false), new Bishop(false), new Knight(false), new Rook(false),
    };

    private static final char[] fileArray = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    private static final int[] rankArray = new int[] {8, 7, 6, 5, 4, 3, 2, 1};

    public Board() {
        // add squares to spaces[][]
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {

                boolean isWhite = ((file % 2 == 0 && rank % 2 == 0) || (file % 2 != 0 && rank % 2 != 0));
                this.spaces[rank][file] = new Space(isWhite, rank, fileArray[file]);

            }
        }

        // add pieces to spaces[][]
        for (int i = 0; i < blackPieces.length; i++) {
            this.spaces[0][i].setPiece(blackPieces[i]);
            this.spaces[1][i].setPiece(new Pawn(false));
        }
        for (int i = 0; i < whitePieces.length; i++) {
            this.spaces[7][i].setPiece(whitePieces[i]);
            this.spaces[6][i].setPiece(new Pawn(true));
        }
    }

    /**
     * Move piece method to allow for movement with chess notation.
     * @param initialRank Integer from 1-8
     * @param initialFile Character from a-h
     * @param finalRank Integer from 1-8
     * @param finalFile Character from a-h
     *
     * This method allows for something like:
     *                  board.movePiece('a', 8, 'c', 3);
     *                  which means a8 -> c3
     *      instead of:
     *                  board.movePiece(0,0,5,2);
     *                  which means the same thing but corresponds
     *                  to the array index values instead
     */
    public void movePiece(char initialFile, int initialRank, char finalFile, int finalRank) {
        // variables for index version of files and ranks
        int initialFileIndex = -1;
        int finalFileIndex = -1;
        int initialRankIndex = -1;
        int finalRankIndex = -1;

        // char file -> file index int
        for (int i = 0; i < fileArray.length; i++) {
            if (initialFile == fileArray[i])
                initialFileIndex = i;
            if (finalFile == fileArray[i])
                finalFileIndex = i;
        }

        // int rank -> rank index int
        for (int i = 0; i < rankArray.length; i++) {
            if (initialRank == rankArray[i])
                initialRankIndex = i;
            if (finalRank == rankArray[i])
                finalRankIndex = i;
        }

        // check if any values are still -1
        int[] fileAndRankIndexes = new int[] {initialFileIndex, finalFileIndex, initialRankIndex, finalRankIndex};
        for (int index : fileAndRankIndexes) {
            if (index == -1) {
                System.out.println("Invalid chess square array index.");
                return;
            }
        }

        movePiece(initialRankIndex, initialFileIndex, finalRankIndex, finalFileIndex);
    }

    public void movePiece(int initialRank, int initialFile, int finalRank, int finalFile) {
        Piece initialPiece = null;
        Piece finalPiece = null;

        try {
            initialPiece = spaces[initialRank][initialFile].getPiece();
            finalPiece = spaces[finalRank][finalFile].getPiece();
        } catch (Exception e) { }

        // no piece selected
        if (initialPiece == null)
            return;

        // check if turn to move
        if ((initialPiece.isWhite() != whiteToMove)) {
            System.out.println("Not your turn to move.");
            return;
        }

        // check if piece moved from spot
        if ((initialRank == finalRank) && (initialFile == finalFile)) {
            System.out.println("You have to move the piece from it's square.");
            return;
        }

        // check if piece at destination (finalFile && finalRank)
        if (finalPiece != null) {
            // same colour check
            if (initialPiece.isSameColour(finalPiece)) {
                System.out.println("You can't capture your own piece!");
                return;
            }
        }

        if (!(initialPiece.isLegalMove(this, initialRank, initialFile, finalRank, finalFile))) {
            System.out.println("Illegal Move");
            return;
        }

        updateMoveList(initialRank, initialFile, finalRank, finalFile, initialPiece, finalPiece != null);

        // all ok, move piece
        spaces[finalRank][finalFile].clearPiece();
        spaces[finalRank][finalFile].setPiece(initialPiece);
        spaces[initialRank][initialFile].clearPiece();

        // individual piece updates
        spaces[finalRank][finalFile].getPiece().setHasMoved(true);

        /**
         * halfmoveNumber and fullmoveNumber increment
         * {@see <a href="http://kirill-kryukov.com/chess/doc/fen.html">kirill-kryukov.com/chess/doc/fen.html</a>}
         *          16.1.3.5
         *          16.1.3.6
          */
        Piece pieceToCheck = spaces[finalRank][finalFile].getPiece();
        char fenSymbol = pieceToCheck.getFenSymbol();

        // reset halfMoveNumber if pawn moved else increment halfMoveNumber
        if (Character.toLowerCase(fenSymbol) == 'p') {
            halfmoveNumber = 0;
        } else {
            halfmoveNumber += 1;
        }
        // update fullMoveNumber if black just played
        if (!whiteToMove)
            fullMoveNumber += 1;

        this.printPiecesOnBoard();

        // change turn
        this.whiteToMove = !this.whiteToMove;
    }

    public void updateMoveList(int initialRank, int initialFile, int finalRank, int finalFile, Piece pieceMoved, boolean captureMove) {
        char initialRank_char = Character.forDigit(rankArray[initialRank], 10);
        char initialFile_char = fileArray[initialFile];
        char finalRank_char = Character.forDigit(rankArray[finalRank], 10);
        char finalFile_char = fileArray[finalFile];
        int lastIndexAvailable;
        int moveNumber;
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

        // determine move number
        moveNumber = -1;
        if (lastIndexAvailable + 1 == 1)
            moveNumber = 1;
        else if ((lastIndexAvailable + 1) % 2 != 0)
            moveNumber = lastIndexAvailable + 2;
        else
            moveNumber = lastIndexAvailable + 1;

        // determine any disambiguating moves
        int canMoveCounter = 0;
        char fenSymbol = pieceMoved.getFenSymbol();
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {
                if (spaces[rank][file].getPiece() != null) {
                    Piece tmpPiece = spaces[rank][file].getPiece();
                    if (tmpPiece.getFenSymbol() == fenSymbol && tmpPiece.isLegalMove(this, initialRank, initialFile, finalRank, finalFile))
                        canMoveCounter += 1;
                }
            }
        }
        if (canMoveCounter > 1) {
            // specify which piece is moving
            pieceOrigination = "" + initialFile_char + initialRank_char;

            // set String move with ambiguity
            move = moveNumber + ". " + Character.toUpperCase(fenSymbol) + pieceOrigination + (captureMove ? "x" : "") + finalFile_char + finalRank_char;

        } else {
            // set String move without ambiguity
            move = moveNumber + ". " + Character.toUpperCase(fenSymbol) + (captureMove ? "x" : "") + finalFile_char + finalRank_char;
        }
        System.out.println(move);
        moveList[lastIndexAvailable] = move;
    }

    /**
     * Generate a String FEN notation from spaces[][]
     *
     * @return standard FEN string used for chess engines.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation">wikipedia.org/wiki/Forsyth-Edwards-Notation</a>
     * @see <a href="http://kirill-kryukov.com/chess/doc/fen.html">kirill-kryukov.com/chess/doc/fen.html</a>
     */
    public String generateFenString() {
        StringBuilder fenString = new StringBuilder();
        int spaceCounter = 0;

        /**
         * Go through every space in spaces[][]
         *      append fend symbol for each piece
         *      append spaceCounter for every empty space
         *      append / for each rank
         * {@see <a href="http://kirill-kryukov.com/chess/doc/fen.html">kirill-kryukov.com/chess/doc/fen.html</a>} 16.1.3.1
         */
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {
                // piece detected
                if (spaces[rank][file].getPiece() != null) {
                    // append incremental counter
                    if (spaceCounter > 0)
                        fenString.append(spaceCounter);
                    // append piece fenSymbol
                    fenString.append(spaces[rank][file].getPiece().getFenSymbol());
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
            pieceAtA1 = spaces[7][0].getPiece();
            pieceAtE1 = spaces[7][4].getPiece();
            pieceAtH1 = spaces[7][7].getPiece();
            pieceAtA8 = spaces[0][0].getPiece();
            pieceAtE8 = spaces[0][4].getPiece();
            pieceAtH8 = spaces[0][7].getPiece();
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
//        if ()

        /**
         * Append halfMoveNumber and fullMoveNumber
         * {@see <a href="http://kirill-kryukov.com/chess/doc/fen.html">kirill-kryukov.com/chess/doc/fen.html</a>}
         *      16.1.3.5
         *      16.1.3.6
         */
        fenString.append(" " + halfmoveNumber);
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

    // ========================

    public static void main(String[] args) {
        Board board = new Board();

        board.printPiecesOnBoard();

        board.movePiece('e', 2, 'e', 3);
        board.movePiece('b', 7, 'b', 5);
        board.movePiece('c', 2, 'c', 3);
        board.movePiece('a', 8, 'a', 6);
        board.movePiece('a', 2, 'a', 3);
        board.movePiece('h', 8, 'h', 6);

        System.out.println(board.generateFenString());

        for (String move : board.moveList) {
            if (move != null)
                System.out.println(move);
        }

//        board.printPiecesOnBoard();
    }

    private void printPieceColours() {
        Board iBoard = this;

        for (int fileCounter = 0; fileCounter < spaces.length; fileCounter++) {
            for (int rankCounter = 0; rankCounter < spaces.length; rankCounter++) {
                try {
                    System.out.print(iBoard.spaces[fileCounter][rankCounter].getPiece().isWhite() ? "W" : "B");
                } catch (Exception e) {
                    System.out.print(" ");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    private void printBoardGrid() {
        Board iBoard = this;

        System.out.println("===============");

        for (int fileCounter = 0; fileCounter < spaces.length; fileCounter++) {
            for (int rankCounter = 0; rankCounter < spaces.length; rankCounter++) {
                System.out.print(iBoard.spaces[fileCounter][rankCounter].getFileChar());
                System.out.print(iBoard.spaces[fileCounter][rankCounter].getRankChar());
                System.out.print(" ");
            }
            System.out.println();
        }

        System.out.println("===============");

    }

    private void printPiecesOnBoard() {
        Board iBoard = this;

        System.out.println("===============");

        for (int fileCounter = 0; fileCounter < spaces.length; fileCounter++) {
            for (int rankCounter = 0; rankCounter < spaces.length; rankCounter++) {
                try {
                    System.out.print(iBoard.spaces[fileCounter][rankCounter].getPiece().getFenSymbol());
                } catch (Exception e) {
                    System.out.print(" ");
                }
                System.out.print(" ");
            }
            System.out.println();
        }

        System.out.println("===============");

    }

    public Space[][] getSpaces() {
        return this.spaces;
    }
}
