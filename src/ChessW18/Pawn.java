package ChessW18;

/**********************************************************************
 * ChessPiece to represent a Pawn. Holds the player of the piece, the
 * type of piece it is, and whether a move is valid or invalid
 *
 * @author Allison
 * @version 3/14/2018
 *********************************************************************/
public class Pawn extends ChessPiece {

    boolean isFirstTurn;
    boolean ableToBePassanted;


    public Pawn(Player player) {
        super(player); //create chess piece with given player
        isFirstTurn = true;
        ableToBePassanted = false;

    }

    /******************************************************************
     * Returns the type of the piece as a string.
     * @return
     *****************************************************************/
    @Override
    public String type() {
        return "Pawn";
    }

    /******************************************************************
     * Checks to see if a given move is valid on the board. This looks
     * at if the pawn can move forward twice on the first move,
     * if the pawn can move diagonally to take a piece, and if the pawn
     * can take a piece through an an passant move.
     *
     * @param move The move that should be made.
     * @param board The board that the move will be made on.
     * @return True if the move can be made.
     *
     * @author Alison
     *****************************************************************/
    public boolean isValidMove(Move move, IChessPiece[][] board) {
        if (super.isValidMove(move, board)) {
            int toCenter = 1; //to be added to row


            if (player() == Player.WHITE)
            // up a row for black, down a row for white
                toCenter = -1; //makes the pawn move towards the center of the board,
            //if there is a piece of opposite player to capture,
            // diagonal movement 1 square
                // or -1 depending on the player, to the center of the
                // board
            if (move.newRow == move.oldRow + toCenter &&
                    board[move.newRow][move.newColumn] != null &&
                    (move.newColumn == move.oldColumn + 1
                            || move.newColumn == move.oldColumn - 1)
                    && board[move.newRow][move.newColumn]
                    .player() == opponent()) {
                return true;
            }
            //if the move was an en passant
            //meaning the piece passed in front of another piece
            //diagonally
                //if move was one towards center
            if (move.newRow == move.oldRow + toCenter &&
                    //if there is a piece to en passant
                    board[move.oldRow][move.newColumn] != null &&
                    //if that piece is a pawn
                    board[move.oldRow][move.newColumn].type().
                            equals("Pawn") &&
                    ((Pawn) board[move.oldRow][move.newColumn]).
                            isAbleToBePassanted() &&
                    //if it moves diagonally only one column
                    (move.newColumn == move.oldColumn + 1 ||
                    move.newColumn == move.oldColumn - 1) &&
                    //if en passanted piece belongs to opponent
                    board[move.oldRow][move.newColumn].
                           player() == board
                            [move.oldRow][move.oldColumn].opponent()){
                return true;
            }
            //if it's the first turn, the pawn can move two spaces up
            if (isFirstTurn && move.oldColumn == move.newColumn &&
                    move.newRow == move.oldRow + (toCenter * 2) &&
                    board[move.oldRow + toCenter][move.oldColumn] ==
                    null && board[move.newRow][move.newColumn]
                    == null) {
                return true;
            }
            //if move is 1 square forward
            if (move.oldColumn == move.newColumn &&
                    move.newRow == move.oldRow + toCenter &&
                    board[move.newRow][move.newColumn] == null) {
                return true;
            }

            if ((player() == Player.WHITE &&
                    move.oldRow < move.newRow) ||
                    (player() == Player.BLACK &&
                            move.oldRow > move.newRow))
                return false;
        }
        return false;
    }

    public void setFirstTurn(boolean isFirstTurn) {
        this.isFirstTurn = isFirstTurn;
    }

    public void setAbleToBePassanted(boolean ableToBePassanted) {
        this.ableToBePassanted = ableToBePassanted;
    }

    public boolean isAbleToBePassanted() {
        return ableToBePassanted;
    }

}
