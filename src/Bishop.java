/**********************************************************************
 * Contains the necessary information for a bishop piece to exist. Such as the
 * player that owns the piece, a the name of the piece, and has the ability to
 * check whether the piece can excecute a specified move.
 * 
 * @author
 * @version
 *********************************************************************/
public class Bishop extends ChessPiece {

	private boolean canCastle; // set false if move is made

	/******************************************************************
	 * Creates the piece using the constructor of ChessPiece.
	 *****************************************************************/
	public Bishop(Player player) {
		super(player);
	}

	/******************************************************************
	 * Returns the name of the piece as a string.
	 *****************************************************************/
	@Override
	public String type() {
		return "Bishop";
	}

	/******************************************************************
	 * Looks along straights that the bishop is moving in to make sure that the
	 * piece can move without impediance.
	 * 
	 * @param move
	 *            Is the move that the player wishes to make.
	 * @param board
	 *            Is the current board.
	 *****************************************************************/
	public boolean isValidMove(Move move, IChessPiece[][] board) {

		if (!super.isValidMove(move, board))
			return false;

		// The curly brakets are here because something was making
		// my embedded statements get attached to the wrong things.

		// The if statements exist to lower the number of loops we go
		// through.
		if (move.newRow == move.oldRow) { // Row is constant.

			if (move.oldColumn < move.newColumn) {
				for (int i = move.oldColumn; i < move.newColumn; i++)

					if (board[move.oldRow][i] == null)
						return false;
			} else
				for (int i = move.newColumn; i < move.oldColumn; i++)

					if (board[move.oldRow][i] == null)
						return false;
		}

		else { // Column is constant.

			if (move.newRow < move.oldRow) {
				for (int i = move.oldRow; i < move.newRow; i++)

					if (board[i][move.oldColumn] == null)
						return false;
			} else {
				for (int i = move.newRow; i < move.oldRow; i++)

					if (board[i][move.oldColumn] == null)
						return false;
			}
		}
		
		return true;
	}
}
