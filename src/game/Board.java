package game;

import pieces.*;

public class Board implements Constants  {
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

        // set fenString
        fenString = new Fen(this, whiteToMove, halfmoveNumber, fullMoveNumber);
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

        // movePiece method with 2d array values
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
        Board tmpBoard = this;

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

        // check if pieces are in between (disregard knight)
//        if (Character.toLowerCase(initialPiece.getFenSymbol()) != 'n') {
//
//        }

        // check if in check, then check if the move avoids check
//        int inCheckCounter = this.inCheck(!initialPiece.isWhite());
//        if (inCheckCounter > 0) {
//            // if multiple pieces are attacking the king and if the king is not being moved, it is not a valid move
//            if (inCheckCounter > 1) {
//                if (Character.toLowerCase(initialPiece.getFenSymbol()) != 'k')
//                    return;
//            } else {
//                // setup tmp board and test if check is blocked
//                tmpBoard.getBoard()[initialRank][initialFile].setPiece(null);
//                tmpBoard.getBoard()[finalRank][finalFile].setPiece(initialPiece);
//
//                if (tmpBoard.inCheck(initialPiece.isWhite()) > 0)
//                    return;
//            }
//        }

        // check if individual Piece can legally move
        if (!(initialPiece.isLegalMove(this, initialRank, initialFile, finalRank, finalFile))) {
            System.out.println("Illegal Move");
            return;
        }

        // update moveList
        System.out.println("Updating movelist");
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

    /**
     *
     * @param white colour in question of being in check.
     * @return 0 if no piece is attacking the king; +1 for every piece attacking the king
     */
    public int inCheck(boolean white) {
        int rankOfKing = -1;
        int fileOfKing = -1;
        int inCheck = 0;

        // find rank and file of King
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {
                try {
                    Piece piece = getBoard()[rank][file].getPiece();
                    // if
                    if ((piece.isWhite() && white) || (!piece.isWhite() && !white)) {
                        rankOfKing = rank;
                        fileOfKing = file;
                        break; // break inner for loop
                    }
                } catch (Exception e) { }
            }

            // break outer for loop if king already found
            if (rankOfKing != -1)
                break;
        }

        // check if king is in check
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {
                try {
                    Piece piece = getBoard()[rank][file].getPiece();
                    // if
                    if ((piece.isWhite() && !white) || (!piece.isWhite() && white)) {
                        if (piece.isLegalMove(this, rank, file, rankOfKing, fileOfKing))
                            inCheck += 1;
                    }
                } catch (Exception e) { }
            }
        }

        return inCheck;
    }

    // ========================

    public static void main(String[] args) {
        Board board = new Board();

        board.printPiecesOnBoard();

        board.movePiece('e', 2, 'e', 4);
        board.movePiece('d', 7, 'd', 5);
        board.movePiece('f', 1, 'd', 3);
        board.movePiece('d', 8, 'd', 6);
        board.movePiece('d', 3, 'e', 2);
        board.movePiece('d', 6, 'f', 5);

//        board.movelist.printMoveList();
        board.fenString.printFenString();

    }

    private void printPieceColours() {
        Board iBoard = this;

        for (int rankCounter = 0; rankCounter < board.length; rankCounter++) {
            for (int fileCounter = 0; fileCounter < board.length; fileCounter++) {
                try {
                    System.out.print(iBoard.board[rankCounter][fileCounter].getPiece().isWhite() ? "W" : "B");
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

        for (int rankCounter = 0; rankCounter < board.length; rankCounter++) {
            for (int fileCounter = 0; fileCounter < board.length; fileCounter++) {
                System.out.print(iBoard.board[rankCounter][fileCounter].getFileChar());
                System.out.print(iBoard.board[rankCounter][fileCounter].getRankChar());
                System.out.print(" ");
            }
            System.out.println();
        }

        System.out.println("===============");

    }

    private void printPiecesOnBoard() {
        Board iBoard = this;

        System.out.println("===============");

        for (int rankCounter = 0; rankCounter < board.length; rankCounter++) {
            for (int fileCounter = 0; fileCounter < board.length; fileCounter++) {
                try {
                    System.out.print(iBoard.board[rankCounter][fileCounter].getPiece().getFenSymbol());
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
