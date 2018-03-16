package ChessW18;

/**********************************************************************
 * Contains the necessary information for a knight piece to exist. Such
 * as the player that owns the piece, a the name of the piece, and has
 * the ability to check whether the piece can excecute a specified
 * move.
 * 
 * @author
 * @version 3/14
 *********************************************************************/
public class Knight extends ChessPiece {

	/******************************************************************
	 * Creates the piece using the constructor of ChessW18.ChessPiece.
	 * 
	 * @param player
	 *            The player that owns the piece.
	 *****************************************************************/
	protected Knight(Player player) {
		super(player);
	}

	/******************************************************************
	 * Returns the name of the piece as a string.
	 *****************************************************************/
	@Override
	public String type() {
		return "Knight";
	}

	/******************************************************************
	 * A knight has four possible moves. A increase of 2 in row and an
	 * increase/decrease of 1 in the column, or an increase of 2 in
	 * column and an increase/decrease of 1 in the row. This checks for
	 * if the move can fail. If the move does fail, then a false is
	 * returned and no other test are run. This works because a check
	 * occurs before each test to see which test needs to occur.
	 * 
	 * @param move
	 *            Is the move the player wishes to make.
	 * @param board
	 *            Is the current board.
	 * @return false if the move fails, true if it doesn't.
	 *****************************************************************/
	public boolean isValidMove(Move move, IChessPiece[][] board) {
		/*
		 * I realized that this class, and out other piece classes all
		 * assume that the given move only contains moves that are
		 * within the range of the board. Meaning that we need to make 
		 * sure that's the case when these methods are called. 
		 */
		
		if (!super.isValidMove(move, board))
			return false;

		//Sees if the piece moved twice in a row and once in a column.
		if(Math.abs(move.newRow - move.oldRow) == 2) {
			if ((Math.abs(move.newColumn - move.oldColumn)) == 1)
				return true;
		}
			
		//Sees if the piece move twice in a column and once in a row.
		else if(Math.abs(move.newColumn - move.oldColumn) == 2) {
				if(Math.abs(move.newRow - move.oldRow) == 1) {
					return true;
			}
		}
		
		return false;

	}
}
