### September 1, 2021; 10:18pm 
It's been a while since my last commit. Took a break from this personal project to focus on life and work. Anyways, I'm back haha.

- [Game](https://github.com/karldamus/JavaChess/blob/main/src/Game.java) now has working registration and successfully adds users to a users.txt file.
- [ColourScheme](https://github.com/karldamus/JavaChess/blob/main/src/users/ColourScheme.java) is a work in progress, but it will be used to hold the different types of colour schemes applicable to the chess board.
- [SchemePresets](https://github.com/karldamus/JavaChess/blob/main/src/users/SchemePresets.java) is a simple enum to hold the different types of colour scheme presets.
- [Settings](https://github.com/karldamus/JavaChess/blob/main/src/users/Settings.java): simple updates to this class, work in progress.
- [User](https://github.com/karldamus/JavaChess/blob/main/src/users/User.java): added getter.

### August 15, 2021; 10:20pm
[Game](https://github.com/karldamus/JavaChess/blob/main/src/Game.java) now has a working Stockfish engine implementation. 

### August 15, 2021; 7:55pm
`These updates have been in progress this past week.`

Menus Branch:
- [Game](https://github.com/karldamus/JavaChess/blob/main/src/Game.java):
  - Added a menubar.
  - Added a login window.
  - Added new game (w/ saveGame check) functionality.
- Created [Settings](https://github.com/karldamus/JavaChess/blob/main/src/users/Settings.java) class.
- Created [User](https://github.com/karldamus/JavaChess/blob/main/src/users/User.java) class.
- Created users.txt (hidden)

### August 11, 2021; 1:32am
Relocate all png files from sprites to sprites/chess.

Added [Client](https://github.com/karldamus/JavaChess/blob/main/src/stockfish/Client.java) class to handle Stockfish engine bestmoves.

Added placePiece sound.

GUI mostly functional ([Game](https://github.com/karldamus/JavaChess/blob/main/src/Game.java) class):
- Issue with AI taking pieces. Timeout issue or something.
- Occasional AI movement causes other pieces to move?

AI implemented (mostly):
- Stockfish engine.
- Best move generation.

Added isLegalMove functionality to [Knight](https://github.com/karldamus/JavaChess/blob/main/src/pieces/Knight.java)

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
