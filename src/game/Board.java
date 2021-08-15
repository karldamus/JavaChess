package game;

import pieces.*;
import stockfish.Client;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.function.UnaryOperator.identity;

public class Board implements Constants  {
    // All spaces on board
    private Space[][] board = new Space[RANKS][FILES];

    // Game and FEN variables
    private static int halfmoveNumber = 0;
    private static int fullMoveNumber = 1;
    private boolean whiteToMove = true;
    private boolean lastMoveEnPassant = false;
    private Movelist movelist = new Movelist();
    private Fen fenString;

    // Game related variables
    public boolean moved = false;
    public boolean twoPlayer = false;
    public boolean gameInProgress;

    // AI
    Client client = new Client();

    // Default white pieces
    public static final Piece[] whitePieces = new Piece[] {
            new Rook(true), new Knight(true), new Bishop(true), new Queen(true), new King(true), new Bishop(true), new Knight(true), new Rook(true),
    };

    // Default black pieces
    public static final Piece[] blackPieces = new Piece[] {
            new Rook(false), new Knight(false), new Bishop(false), new Queen(false), new King(false), new Bishop(false), new Knight(false), new Rook(false),
    };

    /**
     * Create a 'chess board' to host squares and pieces.
     *
     * @see Piece
     * @see Space
     */
    public Board() {
        gameInProgress = false;

        // Start stockfish engine if not pass n' play game (two players)
        if (!twoPlayer)
            client.start("stockfish");

        // add squares to spaces[][]
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {
                boolean isWhite = ((file % 2 == 0 && rank % 2 == 0) || (file % 2 != 0 && rank % 2 != 0));
                this.board[rank][file] = new Space(isWhite, rank, fileArray[file]);
            }
        }

        // add pieces to spaces[][] in default start position
        for (int i = 0; i < blackPieces.length; i++) {
            this.board[0][i].setPiece(blackPieces[i]);
            this.board[1][i].setPiece(new Pawn(false));
        }
        for (int i = 0; i < whitePieces.length; i++) {
            this.board[7][i].setPiece(whitePieces[i]);
            this.board[6][i].setPiece(new Pawn(true));
        }

        // setup fenString
        fenString = new Fen(this, whiteToMove, halfmoveNumber, fullMoveNumber);
    }

    /**
     * Move piece method allows one String argument. For example:
     * "e2e4" or "d6d4" ... etc.
     * @param pattern the two squares involved in movement.
     *
     * @see #movePiece(char, int, char, int)
     */
    public void movePiece(String pattern) {
        String[] arrPattern = pattern.split("");

        try {
            char fileOrigin = pattern.charAt(0);
            char fileDestination = pattern.charAt(2);
            int rankOrigin = Integer.parseInt(arrPattern[1]);
            int rankDestination = Integer.parseInt(arrPattern[3]);

            this.movePiece(fileOrigin, rankOrigin, fileDestination, rankDestination);
        } catch (Exception e) {
            System.out.println(e.toString());
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
     *
     * @see #movePiece(int, int, int, int)
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
     * The actual movePiece function which takes converted index positions and alters the 2d array spaces[][].
     * @param initialRank Integer from 0-7 (outer array)
     * @param initialFile Integer from 0-7 (inner array)
     * @param finalRank Integer from 0-7 (outer array)
     * @param finalFile Integer from 0-7 (inner array)
     */
    public void movePiece(int initialRank, int initialFile, int finalRank, int finalFile) {
        Piece initialPiece = null;
        Piece finalPiece = null;
        Board tmpBoard = this;

        boolean takeMove = false;

        // initialize Piece variables (if possible)
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
            } else {
                takeMove = true;
            }
        }


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

        // individual piece updates
        try {
            board[initialRank][initialFile].getPiece().setHasMoved(true);
        } catch (Exception e) { e.printStackTrace(); }

        // update moveList
        System.out.println("Updating movelist");
        movelist.updateMoveList(new Move(this, initialRank, initialFile, finalRank, finalFile, initialPiece, finalPiece != null));

        // all ok, move piece
        board[finalRank][finalFile].clearPiece();
        board[finalRank][finalFile].setPiece(initialPiece);
        board[initialRank][initialFile].clearPiece();

        // change turn
        this.whiteToMove = !this.whiteToMove;

        // play move sound
        try {
            board[finalRank][finalFile].getPiece().playSound(takeMove);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

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

        // update FEN
        fenString = new Fen(this, whiteToMove, halfmoveNumber, fullMoveNumber);

//        this.printPiecesOnBoard();

        // move successful, set moved = true (useful for GUI ... see Game.java)
        moved = true;

        gameInProgress = true;
    }

    /**
     * Consider whether a king is in check.
     * @param white colour in question of being in check.
     * @return the number of pieces attacking the king.
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

    /**
     * Determine best move according to Stockfish engine.
     * @param fen the current FEN position.
     * @return String format of the best move. E.g., "e2e4"
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     *
     * @see <a href="https://www.andreinc.net/2021/04/22/writing-a-universal-chess-interface-client-in-java">Andrei Ciobanu - Writing a Universal Chess Interface (UCI) Client in Java</a>
     */
    public String bestMove(String fen) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        // initialize engine
        client.command("uci", identity(), (s) -> s.startsWith("uciok"), 4000l);

        // set current position
        client.command("position fen " + fen, identity(), s -> s.startsWith("readyok"), 4000l);

        // find best move
        String bestMove = client.command(
                "go movetime 1000",
                lines -> lines.stream().filter(s->s.startsWith("bestmove")).findFirst().get(),
                line -> line.startsWith("bestmove"),
                5000l)
                .split(" ")[1];

        return bestMove;
    }

    /**
     * Given a size-2 String array (e.g. e2), determine the corresponding array coordinates.
     * @param bc the chess-notation coordinates
     * @return the corresponding array coordinates for this.getBoard() (spaces[][]). (e,2 == 6,4)
     *
     * @see "root/design/chessBoard-indices.png"
     */
    public int[] getArrayPositionFromBoardCoordinates(String[] bc) {
        int[] boardCoordinates = new int[]{-1,-1};

        boardCoordinates[0] = Integer.parseInt(bc[1]);

        for (int i = 0; i < fileArrayStr.length; i++) {
            if (bc[0].equals(fileArrayStr[i]))
                boardCoordinates[1] = i;
        }

        return boardCoordinates;
    }

    // ========================

//    private void printPieceColours() {
//        Board iBoard = this;
//
//        for (int rankCounter = 0; rankCounter < board.length; rankCounter++) {
//            for (int fileCounter = 0; fileCounter < board.length; fileCounter++) {
//                try {
//                    System.out.print(iBoard.board[rankCounter][fileCounter].getPiece().isWhite() ? "W" : "B");
//                } catch (Exception e) {
//                    System.out.print(" ");
//                }
//                System.out.print(" ");
//            }
//            System.out.println();
//        }
//    }
//
//    private void printBoardGrid() {
//        Board iBoard = this;
//
//        System.out.println("===============");
//
//        for (int rankCounter = 0; rankCounter < board.length; rankCounter++) {
//            for (int fileCounter = 0; fileCounter < board.length; fileCounter++) {
//                System.out.print(iBoard.board[rankCounter][fileCounter].getFileChar());
//                System.out.print(iBoard.board[rankCounter][fileCounter].getRankChar());
//                System.out.print(" ");
//            }
//            System.out.println();
//        }
//
//        System.out.println("===============");
//
//    }
//
//    public void printPiecesOnBoard() {
//        Board iBoard = this;
//
//        System.out.println("===============");
//
//        for (int rankCounter = 0; rankCounter < board.length; rankCounter++) {
//            for (int fileCounter = 0; fileCounter < board.length; fileCounter++) {
//                try {
//                    System.out.print(iBoard.board[rankCounter][fileCounter].getPiece().getFenSymbol());
//                } catch (Exception e) {
//                    System.out.print(" ");
//                }
//                System.out.print(" ");
//            }
//            System.out.println();
//        }
//
//        System.out.println("===============");
//
//    }

    public Space[][] getBoard() {
        return this.board;
    }

    public Movelist getMovelist() { return this.movelist; }

    public Fen getFen() { return this.fenString; }
}
