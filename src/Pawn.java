public class Pawn extends ChessPiece {


    public Pawn(Player player) {
        super(player); //create chess piece with given player
    }

    @Override
    public String type() {
        return "Pawn";
    }


    public boolean isValidMove(Move move, IChessPiece[][] board) {
        int toCenter = 1;
        if (player() == Player.WHITE)
            toCenter = -1;
        if (super.isValidMove(move, board)) {
            //if there is a piece of opposite player to capture
			if (move.newColumn == move.oldColumn + toCenter && // or -1 depending on the player
					(move.newRow == move.oldRow + 1
							|| move.newRow == move.oldRow - 1)
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
