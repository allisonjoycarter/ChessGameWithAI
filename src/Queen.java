public class Queen extends ChessPiece {

    /** represents the player to be used in isValidMove */
    private Player tempPlayer;

    public Queen(Player player) {
        super(player);
        tempPlayer = player;
    }

    @Override
    public String type() {
        return "Queen";
    }

    /******************************************************************
     * A queen acts like a bishop and a rook, so by making a temporary
     * piece of a bishop and rook a valid move can be checked.
     * 
     * @param move Is the move the player wishes to make.
     * @param board Is the current board.
     *****************************************************************/
    public boolean isValidMove(Move move, IChessPiece[][] board) {
    	boolean valid = false;
    	
    	//The queen moves like a bishop and a rook
    	Rook tempRook = new Rook(tempPlayer);
    	Bishop tempBish = new Bishop(tempPlayer);
    	
    	if(tempRook.isValidMove(move,board))
    		valid = true;
    	else if(tempBish.isValidMove(move,board))
    		valid = true;
    	
        return valid;
    }
}
