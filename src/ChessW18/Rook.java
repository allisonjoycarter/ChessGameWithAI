package ChessW18;

/**********************************************************************
 * Contains the necessary information for a ChessW18.Rook piece to exist. Such
 * as the player that owns the piece, a the name of the piece, and 
 * has the ability to check whether the piece can execute a specified
 * move. 
 * 
 * @author George
 * @version 3/14
 *********************************************************************/
public class Rook extends ChessPiece {

    public boolean canCastle; //set false if move is made

	/******************************************************************
	 * Creates the piece using the constructor of ChessW18.ChessPiece.
	 *****************************************************************/
    public Rook(Player player) {
        super(player);
        canCastle = true;
    }

    /******************************************************************
     * Returns the name of the piece as a string.
     *****************************************************************/
    @Override
    public String type() {
        return "Rook";
    }

    /******************************************************************
     * Looks along diagonal that the rook is moving in to make sure
     * that the piece can move without impedance.
     * 
     * @param move Is the move that the player wishes to make.
     * @param board Is the current board.
     *****************************************************************/
    public boolean isValidMove(Move move, IChessPiece[][] board) {

		if (!super.isValidMove(move, board))
			return false;

		//The if's are to make sure we check the right direction, and
		//dont go through loops we don't need to.
        //add one to the starting location so it does not count itself and return false
		if (move.oldRow < move.newRow) { //moving down
		    for (int i = move.oldRow; i < move.newRow; i++)
		        if (board[i][move.oldColumn] != null && //there should be no piece in between
                        board[move.oldRow][move.oldColumn] != board[i][move.oldColumn]) //do not count yourself
		            return false;
		} else if (move.oldRow > move.newRow) { //moving up
		    for (int i = move.oldRow; i > move.newRow; i--)
		        if (board[i][move.oldColumn] != null &&
                        board[move.oldRow][move.oldColumn] != board[i][move.oldColumn])
		            return false;
		} else if (move.oldColumn < move.newColumn) { //moving right
            for (int i = move.oldColumn; i < move.newColumn; i++) {
                if (board[move.oldRow][i] != null &&
                        board[move.oldRow][move.oldColumn] != board[move.oldRow][i])
                    return false;
            }
        } else if (move.oldColumn > move.newColumn) { //moving left
            for (int i = move.oldColumn; i > move.newColumn; i--) {
                if (board[move.oldRow][i] != null &&
                        board[move.oldRow][move.oldColumn] != board[move.oldRow][i])
                    return false;
            }
        }
		
		//Makes sure the piece stayed within a column or row.
        return move.oldColumn == move.newColumn ^
                move.oldRow == move.newRow;

    }

	public void setCanCastle(boolean canCastle) {
		this.canCastle = canCastle;
	}
}
