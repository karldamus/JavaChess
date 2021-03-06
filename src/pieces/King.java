package pieces;

import game.Board;
import game.Space;

public class King extends Piece {

    public King(boolean white) {
        super(white, 'k');
    }

    @Override
    public boolean isLegalMove(Board board, int initialRank, int initialFile, int finalRank, int finalFile) {
        Space[][] spaces = board.getBoard();
        Board tmpBoard = board;
        Piece tmpPiece = board.getBoard()[initialRank][initialFile].getPiece();
        tmpPiece.setPieceJLabel(board.getBoard()[initialRank][initialFile].getPiece().getPieceJLabel());

        int absRank = Math.abs(finalRank - initialRank);
        int absFile = Math.abs(finalFile - initialFile);

        // check if movement distance is greater than one square
        if ((absRank > 1) || (absFile > 1)) {
            System.out.println("The King can only move a distance of 1.");
            return false;
        }

        // setup tmp board
        tmpBoard.getBoard()[initialRank][initialFile].setPiece(null);
        tmpBoard.getBoard()[finalRank][finalFile].setPiece(tmpPiece);

        // if tmpBoard with new king position results in the king being in check, it is not a valid king move
        if (tmpBoard.inCheck(tmpPiece.isWhite()) > 0)
            return false;



        // todo: check if opposing king is within 1 square of destination square
        // https://stackoverflow.com/a/5802694/13280626
        // https://codereview.stackexchange.com/a/68638
//        int startFile = (finalFile - 1 < 0) ? finalFile : finalFile-1;
//        int startRank = (finalRank - 1 < 0) ? finalRank : finalRank-1;
//        int endFile = (finalFile + 1 > 7) ? finalFile : finalFile+1;
//        int endRank = (finalRank + 1 > 7) ? finalRank : finalRank+1;
//
//        for (int rowNum=startPosX; rowNum<=endPosX; rowNum++) {
//            for (int colNum=startPosY; colNum<=endPosY; colNum++) {
//                // All the neighbors will be grid[rowNum][colNum]
//            }
//        }


//        // check if destination square is under attack
//        for (int rank = 0; rank < spaces.length; rank++) {
//            for (int file = 0; file < spaces.length; file++) {
//                if (spaces[rank][file].getPiece() != null && (spaces[rank][file].getPiece().isWhite() != this.isWhite())) {
//                    if (spaces[rank][file].getPiece().isLegalMove(board, rank, file, finalRank, finalFile)) {
//                        System.out.println("The King cannot move into check.");
//                        return false;
//                    }
//                }
//            }
//        }

        return true;
    }
}
