### August 5, 2021; 12:58am 
Added isLegalMove functionality to the following classes:
- [Bishop](https://github.com/karldamus/JavaChess/blob/main/src/pieces/Bishop.java)
- [King](https://github.com/karldamus/JavaChess/blob/main/src/pieces/King.java)
- [Pawn](https://github.com/karldamus/JavaChess/blob/main/src/pieces/Pawn.java)
- [Queen](https://github.com/karldamus/JavaChess/blob/main/src/pieces/Queen.java)
- [Rook](https://github.com/karldamus/JavaChess/blob/main/src/pieces/Rook.java)

Deprecated [Movecheck](https://github.com/karldamus/JavaChess/blob/main/src/pieces/Movecheck.java).

Added pieceInWay method using directional vectors to [Piece](https://github.com/karldamus/JavaChess/blob/main/src/pieces/Piece.java) courtesy of 'Andreas Dolk' on Stackoverflow (see Readme for credit link).

Added constants used for Swing GUI to [Constants](https://github.com/karldamus/JavaChess/blob/main/src/game/Constants.java).

Implemented inCheck method to [Board](https://github.com/karldamus/JavaChess/blob/main/src/game/Board.java) (currently not working correctly).

Added initial Swing GUI setup to [Game](https://github.com/karldamus/JavaChess/blob/main/src/Game.java).

- - -

### August 4, 2021; 11:32pm
Add inCheck method to [Board](https://github.com/karldamus/JavaChess/blob/main/src/game/Board.java) to determine if a king of either colour is in check.

- - -

### July 27, 2021; 12:11am
Cleaned up [Board](https://github.com/karldamus/JavaChess/blob/main/src/game/Board.java)
- updateMoveList method moved to [Movelist](https://github.com/karldamus/JavaChess/blob/main/src/game/Movelist.java).
  - Movelist object is now implemented in [Board](https://github.com/karldamus/JavaChess/blob/main/src/game/Board.java).
- generateFenString method and helper methods (castlingCheck & hasPieceMoved) moved to [Fen](https://github.com/karldamus/JavaChess/blob/main/src/game/Fen.java).
  - Fen object is now implemented in [Board](https://github.com/karldamus/JavaChess/blob/main/src/game/Board.java).

Added [Constants](https://github.com/karldamus/JavaChess/blob/main/src/game/Constants.java) interface to remove repetitiveness.

Added [Move](https://github.com/karldamus/JavaChess/blob/main/src/game/Move.java) class to create Move objects (to be implemented soon).

[Movelist](https://github.com/karldamus/JavaChess/blob/main/src/game/Movelist.java) now displays proper move number, and doesn't display fen symbol for pawn movements.

- - -

### July 25, 2021; 12:16am
Working FEN notation generation (minus en passant).

Mostly working move list. Not included yet:
- No fen symbol for pawn movements.
- Pawn promotion.
- Draw offer.
- Castling.
- Check.
- Checkmate.
- End of game.

- - -

### July 22, 2021; 10:48pm
Working movement with basic legal move checks:
- Check if piece selected.
- Check if turn to move. Disallow if not on turn. Switch turn if move complete.
- Check if piece is trying to capture another. Disallow if same colour.

- - -

### July 22, 2021; 9:26am
 
The chess board properly sets up with black on the 7,8 ranks and white on the 1,2 ranks. These are associated with their corresponding array indices which can be determined from inside Space.java. Given a file ('a', 'b', etc.) and a rank (1, 2, etc.); you can easily get the corresponding array indices with the 'get' methods inside Space.java. 

Overall, this method is much better and more concise than my previous attempt at a chess game. I have since made the previous chess repository private as I shall be continuing with this new edition.

I learned lots in the many mistakes I made with the previous edition, I hope to plan better and overcome difficulties before they turn into impossibilities.
