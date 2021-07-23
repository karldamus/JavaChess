package game;

import pieces.*;

public class Board {
    private static final int RANKS = 8;
    private static final int FILES = 8;
    private static Space[][] spaces = new Space[RANKS][FILES];

    private boolean whiteToMove = true;

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
        for (int rank = 0; rank < FILES; rank++) {
            for (int file = 0; file < RANKS; file++) {

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

        System.out.println("Initial piece colour: " + (initialPiece.isWhite() ? "W" : "B"));
        System.out.println("Colour to move: " + (whiteToMove ? "W" : "B"));

        // check if turn to move
        if ((initialPiece.isWhite() != whiteToMove)) {
            System.out.println("Not turn to move");
            return;
        }


        // piece selected and piece at destination (finalFile && finalRank)
        if (initialPiece != null && finalPiece != null) {
            // same colour check
            if (initialPiece.isSameColour(finalPiece)) {
                System.out.println("You can't capture your own piece!");
                return;
            }
        }

        // all ok, move piece
        this.spaces[finalRank][finalFile].clearPiece();
        this.spaces[finalRank][finalFile].setPiece(initialPiece);
        this.spaces[initialRank][initialFile].clearPiece();

        finishMove();

//        if (spaces[initialFile][initialRank].getPiece().isLegalMove(initialFile, initialRank, finalFile, finalRank))

    }

    public void finishMove() {
        this.whiteToMove = !this.whiteToMove;
    }


    // ========================

    public static void main(String[] args) {
        Board board = new Board();
//        board.printBoardGrid();
//        board.printPiecesOnBoard();

        board.printPiecesOnBoard();
//        board.movePiece(0,0,7,4);
        board.movePiece('a', 2, 'b', 1);
        board.movePiece('b', 7, 'b', 5);
        board.movePiece('c', 2, 'c', 3);
        board.printPiecesOnBoard();


//        board.printPiecesOnBoard();
//        board.movePiece(1,1,3,3);
//        board.printPiecesOnBoard();



//        board.printPiecesOnBoard();
//        board.printPieceColours();
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
}
