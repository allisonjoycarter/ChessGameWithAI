package ChessW18;

/**********************************************************************
 * Contains the necessary information for a ChessW18.Rook piece to exist. Such
 * as the player that owns the piece, a the name of the piece, and 
 * has the ability to check whether the piece can excecute a specified
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
		if (move.oldRow < move.newRow) {
			
			if (move.oldColumn < move.newColumn) {	
				for (int i = move.newRow; i <= move.oldRow; i++)
					for (int j = move.newColumn; j <= move.oldColumn;
							j++) {
						
						if (!(board[i][j] == null))
							return false;
					}
			}

			else if (move.oldColumn > move.newColumn) {
				for (int i = move.newRow; i <= move.oldRow; i++)
					for (int j = move.oldColumn; j <= move.newColumn;
							j++) {
						if (board[i][j] != null)
							return false;
					}
			}
		}
		
		else if (move.oldRow > move.newRow) {
			
			if (move.oldColumn < move.newColumn) {
				for (int i = move.oldRow; i <= move.newRow; i++)
					for (int j = move.newColumn; j <= move.oldColumn;
							j++) {
						if (!(board[i][j] == null))
							return false;
					}
			}
			
			else if (move.oldColumn > move.newColumn) {
				for (int i = move.oldRow; i <= move.newRow; i++)
					for (int j = move.oldColumn; j <= move.newColumn;
							j++) {
						if (!(board[i][j] == null))
							return false;
					}
			}
		}
		
		//Makes sure the piece stayed within a column or row.
		if(move.oldColumn == move.newColumn ||
				move.oldRow == move.oldColumn)
			return true;
		
		return false;
	}
}
