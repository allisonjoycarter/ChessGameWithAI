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

    @Override
    public String type() {
        return "Pawn";
    }


    public boolean isValidMove(Move move, IChessPiece[][] board) {
        if (super.isValidMove(move, board)) {
            int toCenter = 1; //to be added to row
            if (player() == Player.WHITE)
                toCenter = -1; //makes the pawn move towards the center of the board, up a row for black, down a row for white

            //if there is a piece of opposite player to capture, diagonal movement 1 square
            if (move.newRow == move.oldRow + toCenter && // or -1 depending on the player, to the center of the board
                    board[move.newRow][move.newColumn] != null &&
                    (move.newColumn == move.oldColumn + 1
                            || move.newColumn == move.oldColumn - 1)
                    && board[move.newRow][move.newColumn]
                    .player() == opponent()) {
//                ableToBePassanted = false;
                return true;
            }
            //if the move was an en passant
            //meaning the piece passed in front of another piece diagonally
            if (move.newRow == move.oldRow + toCenter && //if move was one towards center
                    board[move.oldRow][move.newColumn] != null && //if there is a piece to en passant
                    board[move.oldRow][move.newColumn].type().equals("Pawn") && //if that piece is a pawn
                    ((Pawn) board[move.oldRow][move.newColumn]).isAbleToBePassanted() &&
                    (move.newColumn == move.oldColumn + 1 || //if it moves diagonally only one column
                    move.newColumn == move.oldColumn - 1) &&
                    board[move.oldRow][move.newColumn]. //if en passanted piece belongs to opponent
                           player() == board[move.oldRow][move.oldColumn].opponent()) {
//                ableToBePassanted = false;
                return true;
            }
                //if it's the first turn, the pawn can move two spaces up
            if (isFirstTurn && move.oldColumn == move.newColumn &&
                    move.newRow == move.oldRow + (toCenter * 2) &&
                    board[move.newRow][move.newColumn] == null) {
//                ableToBePassanted = true;
                return true;
            }
            //if move is 1 square forward
            if (move.oldColumn == move.newColumn &&
                    move.newRow == move.oldRow + toCenter &&
                    board[move.newRow][move.newColumn] == null) {
//                ableToBePassanted = false;
                return true;
            }
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
