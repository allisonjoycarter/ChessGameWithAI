package ChessW18;

/**********************************************************************
 * Contains the necessary information for a queen piece to exist. Such
 * as the player that owns the piece, a the name of the piece, and 
 * has the ability to check whether the piece can excecute a specified
 * move. 
 * 
 * @author George
 * @version 3/14
 *********************************************************************/
public class Queen extends ChessPiece {

    private Player tempPlayer;

	/******************************************************************
	 * Creates the piece using the constructor of ChessW18.ChessPiece.
	 *****************************************************************/
    public Queen(Player player) {
        super(player);
        tempPlayer = player;
    }

    /******************************************************************
     * Returns the name of the piece as a string.
     *****************************************************************/
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
