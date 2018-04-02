package ChessW18;

/**********************************************************************
 * Contains the necessary information for a ChessW18.Rook piece to exist. Such
 * as the player that owns the piece, a the name of the piece, and 
 * has the ability to check whether the piece can execute a specified
 * move. 
 * 
 * @author George
 * @version 3/21
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

		if (move.oldRow < move.newRow) { //moving down
		    for (int i = move.oldRow+1; i < move.newRow; ++i)
		        if (board[i][move.oldColumn] != null)
		            return false;
		} else if (move.oldRow > move.newRow) { //moving up
		    for (int i = move.oldRow-1; i > move.newRow; --i)
		        if (board[i][move.oldColumn] != null)
		            return false;
		} else if (move.oldColumn < move.newColumn) { //moving right
            for (int i = move.oldColumn+1; i < move.newColumn; ++i) {
                if (board[move.oldRow][i] != null)
                    return false;
            }
        } else if (move.oldColumn > move.newColumn) { //moving left
            for (int i = move.oldColumn-1; i > move.newColumn; --i) {
                if (board[move.oldRow][i] != null)
                    return false;
            }
        }

		//Makes sure the piece stayed within a column or row.
        return move.oldColumn == move.newColumn ^
                move.oldRow == move.newRow;

		//move for castling is in King class

    }

    /******************************************************************
     * Setter for whether the rook has not moved and is therefore able
     * to castle
     *
     * @param canCastle true if the rook is able to castle
     *****************************************************************/
	public void setCanCastle(boolean canCastle) {
		this.canCastle = canCastle;
	}
}
