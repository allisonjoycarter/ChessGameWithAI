package ChessW18;

/**********************************************************************
 * ChessW18.ChessPiece object that represents a ChessW18.Bishop. Holds which player is
 * the owner of the piece, the type, and a method to determine whether
 * a move is valid or invalid.
 *
 * @author Allison
 * @version 3/14/2018
 *********************************************************************/
public class Bishop extends ChessPiece {

    /******************************************************************
     * Constructor for ChessW18.Bishop, which holds the player that
     * owns/controls the piece
     *
     * @param player which player owns the piece (white or black)
     *****************************************************************/
    public Bishop(Player player) {
        super(player);
    }

    /******************************************************************
     * Used to return a name to describe the type of piece
     *
     * @return String that describes the name of the piece
     *****************************************************************/
    @Override
    public String type() {
        return "Bishop";
    }

    /******************************************************************
     * Checks if a move for a bishop is valid. First checks the super
     * method for isValidMove, then checks if there is an intercepting
     * piece, and finally checks if the move is diagonal
     *
     * @author Allison
     * @param move the move to check
     * @param board the chess board
     * @return true if the move follows the rules, false if there is
     * a piece in between or the move is not a square diagonal
     *****************************************************************/
    @Override
    public boolean isValidMove(Move move, IChessPiece[][] board) {
        if (super.isValidMove(move,board)) {
            //check to make sure there is no piece in the way
            //for moving right diagonal
            if (move.oldRow > move.newRow) {
                for (int row = move.oldRow; row > move.newRow; row--)
                    for (int column = move.oldColumn; column > move.newColumn; column--)
                        if (board[row][column] != null)
                            return false;
            //for moving left diagonal
            } else if (move.oldRow < move.newRow) {
                for (int row = move.oldRow; row < move.newRow; row++)
                    for (int column = move.oldColumn; column < move.newColumn; column++)
                        if (board[row][column] != null)
                            return false;
            }
            //if the amount of rows moved is equal to the amount of columns moved
            if (Math.abs(move.oldRow - move.newRow) == Math.abs(move.oldColumn - move.newColumn))
                return true;
        }
        return false;
    }
}
