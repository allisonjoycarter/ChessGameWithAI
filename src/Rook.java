public class Rook extends ChessPiece {

    public boolean canCastle; //set false if move is made

    public Rook(Player player) {
        super(player);
        canCastle = true;
    }

    @Override
    public String type() {
        return "Rook";
    }

    /******************************************************************
     * Looks along diagonal that the bishop is moving in to make sure
     * that the piece can move without impediance. 
     * 
     * @param move Is the move that the player wishes to make.
     * @param board Is the current board.
     *****************************************************************/
    public boolean isValidMove(Move move, IChessPiece[][] board) {

		if (!super.isValidMove(move, board))
			return false;

		//The if's are to make sure we check the right direction, and
		//dont go through loops we don't need to.
		if (move.oldRow < move.newRow)
			if (move.oldColumn < move.newColumn) {
				for (int i = move.newRow; i <= move.oldRow; i++)
					for (int j = move.newColumn; j <= move.oldColumn; j++) {
						if (board[i][j] != null)
							return false;
					}
			}

			else if (move.oldColumn > move.newColumn) {

				for (int i = move.newRow; i <= move.oldRow; i++)
					for (int j = move.oldColumn; j <= move.newColumn; j++) {
						if (board[i][j] != null)
							return false;
					}
			}

		if (move.oldRow > move.newRow)
			if (move.oldColumn < move.newColumn) {

				for (int i = move.oldRow; i <= move.newRow; i++)
					for (int j = move.newColumn; j <= move.oldColumn; j++) {
						if (!(board[i][j] == null))
							return false;
					}
			}

			else if (move.oldColumn > move.newColumn) {
				for (int i = move.oldRow; i <= move.newRow; i++)
					for (int j = move.oldColumn; j <= move.newColumn; j++) {
						if (!(board[i][j] == null))
							return false;
					}
			}

		return true;
	}
}
