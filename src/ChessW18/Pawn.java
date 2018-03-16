package ChessW18;

/**********************************************************************
 * ChessW18.ChessPiece to represent a ChessW18.Pawn. Holds the player of the piece, the
 * type of piece it is, and whether a move is valid or invalid
 *
 * @author Allison
 * @version 3/14/2018
 *********************************************************************/
public class Pawn extends ChessPiece {


    public Pawn(Player player) {
        super(player); //create chess piece with given player
    }

    @Override
    public String type() {
        return "Pawn";
    }


    public boolean isValidMove(Move move, IChessPiece[][] board) {
        int toCenter = 1; //to be added to row
        if (player() == Player.WHITE)
            toCenter = -1; //makes the pawn move towards the center of the board, up a row for black, down a row for white
        if (super.isValidMove(move, board)) {
            //if there is a piece of opposite player to capture, diagonal movement 1 square
			if (move.newRow == move.oldRow + toCenter && // or -1 depending on the player, to the center of the board
					(move.newColumn == move.oldColumn + 1
							|| move.newColumn == move.oldColumn - 1)
					&& board[move.newRow][move.newColumn]
							.player() == opponent())
				return true;
			//if move is 1 square forward
            if (move.oldColumn == move.newColumn &&
                    move.newRow == move.oldRow + toCenter) //or -1
                return true;
        }
        return false;
    }


}
