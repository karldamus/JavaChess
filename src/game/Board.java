package game;

import pieces.*;

public class Board {
    private static final int RANKS = 8;
    private static final int FILES = 8;
    private static Space[][] spaces = new Space[FILES][RANKS];

    public static final Piece[] whitePieces = new Piece[] {
            new Rook(true), new Knight(true), new Bishop(true), new Queen(true), new King(true), new Bishop(true), new Knight(true), new Rook(true),
    };

    public static final Piece[] blackPieces = new Piece[] {
            new Rook(false), new Knight(false), new Bishop(false), new Queen(false), new King(false), new Bishop(false), new Knight(false), new Rook(false),
    };

    private static final char[] fileArray = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    public Board() {
        // add squares to spaces[][]
        for (int file = 0; file < FILES; file++) {
            for (int rank = 0; rank < RANKS; rank++) {

                boolean isWhite = ((rank % 2 == 0 && file % 2 == 0) || (rank % 2 != 0 && file % 2 != 0));
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

    // ========================

    public static void main(String[] args) {
        Board board = new Board();
        board.printPiecesOnBoard();
    }

    private void printBoardGrid() {
        Board iBoard = this;

        for (int fileCounter = 0; fileCounter < spaces.length; fileCounter++) {
            for (int rankCounter = 0; rankCounter < spaces.length; rankCounter++) {
                System.out.print(iBoard.spaces[fileCounter][rankCounter].getFileChar());
                System.out.print(iBoard.spaces[fileCounter][rankCounter].getRankChar());
                System.out.print(" ");
            }
            System.out.println();
        }

    }

    private void printPiecesOnBoard() {
        Board iBoard = this;

        for (int fileCounter = 0; fileCounter < spaces.length; fileCounter++) {
            for (int rankCounter = 0; rankCounter < spaces.length; rankCounter++) {
                System.out.print(iBoard.spaces[fileCounter][rankCounter].getPiece().getFenSymbol());
                System.out.print(" ");
            }
            System.out.println();
        }

    }
}
