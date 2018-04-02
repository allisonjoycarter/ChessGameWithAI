package ChessW18;

/**********************************************************************
 * ChessPiece object to represent a ChessW18.King. Holds the
 * Player that owns the piece, whether the piece can Castle, the type
 * of piece, and whether a move proposed is valid given the general
 * rules of Chess
 *
 * @author Allison
 * @version 3/25/2018
 *********************************************************************/
public class King extends ChessPiece {

    /** holds whether the king can castle */
    public boolean canCastle;

    /*****************************************************************
     * Constructor for a king chess piece
     *
     * @param player the player that owns the king
     ****************************************************************/
    public King(Player player) {
        super(player);
        canCastle = true;
    }

    /*****************************************************************
     * Returns the type of piece the king is
     *
     * @return "King"
     ****************************************************************/
    @Override
    public String type() {
        return "King";
    }

    public boolean isValidMove(Move move, IChessPiece[][] board) {
        if (super.isValidMove(move, board)) {

            if(canCastle) {
                boolean valid = true;

                if (move.oldRow == move.newRow &&
                        (move.oldRow == 0 || move.oldRow == 7) &&
                        (move.newColumn == 2 || move.newColumn == 6)) {

                        switch (move.newColumn) {
                            case 2:
                                if (board[move.oldRow][1] != null ||
                                        board[move.oldRow][2] != null ||
                                        board[move.oldRow][3] != null)
                                    valid = false;
                                break;
                            case 6:
                                if (board[move.oldRow][5] != null ||
                                        board[move.oldRow][6] != null)
                                    valid = false;
                                break;

                        }
                        return valid;
                }
            }

            //if the move is more than 1 square out, return false
            //checks for out of bounds, but this may be unnecessary
            return (move.oldColumn >= board.length - 1 ||
                    move.newColumn <= move.oldColumn + 1) &&
                    (move.oldColumn < 1 || move.newColumn
                            >= move.oldColumn - 1) &&
                    (move.oldRow >= board.length - 1 ||
                            move.newRow <= move.oldRow + 1) &&
                    (move.oldRow < 1 || move.newRow >= move.oldRow - 1);
        }
        return false;
    }

    /******************************************************************
     * Setter for if the king is able to castle (has not moved)
     *
     * @param canCastle true if the king is able to castle
     *****************************************************************/
    public void setCanCastle(boolean canCastle) {
        this.canCastle = canCastle;
    }

}
