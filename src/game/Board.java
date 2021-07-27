package game;

import pieces.*;

public class Board implements Constants {
    private Space[][] board = new Space[RANKS][FILES];

    private static int halfmoveNumber = 0;
    private static int fullMoveNumber = 1;

    private boolean whiteToMove = true;
    private boolean lastMoveEnPassant = false;

    private Movelist movelist = new Movelist();
    private Fen fenString;

    public static final Piece[] whitePieces = new Piece[] {
            new Rook(true), new Knight(true), new Bishop(true), new Queen(true), new King(true), new Bishop(true), new Knight(true), new Rook(true),
    };

    public static final Piece[] blackPieces = new Piece[] {
            new Rook(false), new Knight(false), new Bishop(false), new Queen(false), new King(false), new Bishop(false), new Knight(false), new Rook(false),
    };

    public Board() {
        // add squares to spaces[][]
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {

                boolean isWhite = ((file % 2 == 0 && rank % 2 == 0) || (file % 2 != 0 && rank % 2 != 0));
                this.board[rank][file] = new Space(isWhite, rank, fileArray[file]);

            }
        }

        // add pieces to spaces[][]
        for (int i = 0; i < blackPieces.length; i++) {
            this.board[0][i].setPiece(blackPieces[i]);
            this.board[1][i].setPiece(new Pawn(false));
        }
        for (int i = 0; i < whitePieces.length; i++) {
            this.board[7][i].setPiece(whitePieces[i]);
            this.board[6][i].setPiece(new Pawn(true));
        }
    }

    /**
     * Move piece method to allow for movement with chess notation and not array index positions.
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

    /**
     * The actual movePiece function which takes the above converted index positions and alters the 2d array spaces[][].
     * @param initialRank Integer from 0-7 (outer array)
     * @param initialFile Integer from 0-7 (inner array)
     * @param finalRank Integer from 0-7 (outer array)
     * @param finalFile Integer from 0-7 (inner array)
     */
    public void movePiece(int initialRank, int initialFile, int finalRank, int finalFile) {
        Piece initialPiece = null;
        Piece finalPiece = null;

        try {
            initialPiece = board[initialRank][initialFile].getPiece();
            finalPiece = board[finalRank][finalFile].getPiece();
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

        // update moveList
        movelist.updateMoveList(new Move(this, initialRank, initialFile, finalRank, finalFile, initialPiece, finalPiece != null));

        // all ok, move piece
        board[finalRank][finalFile].clearPiece();
        board[finalRank][finalFile].setPiece(initialPiece);
        board[initialRank][initialFile].clearPiece();

        // update FEN
        fenString = new Fen(this, whiteToMove, halfmoveNumber, fullMoveNumber);

        // individual piece updates
        board[finalRank][finalFile].getPiece().setHasMoved(true);

        /**
         * halfmoveNumber and fullmoveNumber increment
         * {@see <a href="http://kirill-kryukov.com/chess/doc/fen.html">kirill-kryukov.com/chess/doc/fen.html</a>}
         *          16.1.3.5
         *          16.1.3.6
          */
        Piece pieceToCheck = board[finalRank][finalFile].getPiece();
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

//        fenString = new Fen(this, whiteToMove, halfmoveNumber, fullMoveNumber);
//
//        System.out.println(fenString.toString());

        board.movelist.printMoveList();
        board.fenString.printFenString();

//        System.out.format("%1s%10d%10s", "test1", 2, "test2");

//        board.printPiecesOnBoard();
    }

    private void printPieceColours() {
        Board iBoard = this;

        for (int fileCounter = 0; fileCounter < board.length; fileCounter++) {
            for (int rankCounter = 0; rankCounter < board.length; rankCounter++) {
                try {
                    System.out.print(iBoard.board[fileCounter][rankCounter].getPiece().isWhite() ? "W" : "B");
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

        for (int fileCounter = 0; fileCounter < board.length; fileCounter++) {
            for (int rankCounter = 0; rankCounter < board.length; rankCounter++) {
                System.out.print(iBoard.board[fileCounter][rankCounter].getFileChar());
                System.out.print(iBoard.board[fileCounter][rankCounter].getRankChar());
                System.out.print(" ");
            }
            System.out.println();
        }

        System.out.println("===============");

    }

    private void printPiecesOnBoard() {
        Board iBoard = this;

        System.out.println("===============");

        for (int fileCounter = 0; fileCounter < board.length; fileCounter++) {
            for (int rankCounter = 0; rankCounter < board.length; rankCounter++) {
                try {
                    System.out.print(iBoard.board[fileCounter][rankCounter].getPiece().getFenSymbol());
                } catch (Exception e) {
                    System.out.print(" ");
                }
                System.out.print(" ");
            }
            System.out.println();
        }

        System.out.println("===============");

    }

    public Space[][] getBoard() {
        return this.board;
    }
}
