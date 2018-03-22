package ChessW18;

import java.util.ArrayList;
import java.util.Stack;

public class ChessModel implements IChessModel {

    private IChessPiece[][] board = new IChessPiece[8][8];
    private Player currentPlayer;

    /** holds the captures of players */
    private ArrayList<IChessPiece> whiteCaptures, blackCaptures;

    /** holds moves so that undo method can easily reference last in */
    Stack<Move> moveStack;

    /** holds moves that captured a piece */
    Stack<Move> captureMoveStack;

    private String message;

    public ChessModel() {
        placeStartingPieces();
        whiteCaptures = new ArrayList<>();
        blackCaptures = new ArrayList<>();
        moveStack = new Stack<>();
        captureMoveStack = new Stack<>();
    }

    private void placeStartingPieces() {
        //white player starts
        currentPlayer = Player.WHITE;

        //placing the pawns on the board
        for (int column = 0; column < board.length; column++) {
            board[1][column] = new Pawn(Player.BLACK);
            board[6][column] = new Pawn(Player.WHITE);
        }
        //placing the rest of the pieces
        board[0][0] = new Rook(Player.BLACK);
        board[0][1] = new Knight(Player.BLACK);
        board[0][2] = new Bishop(Player.BLACK);
        board[0][3] = new Queen(Player.BLACK);
        board[0][4] = new King(Player.BLACK);
        board[0][5] = new Bishop(Player.BLACK);
        board[0][6] = new Knight(Player.BLACK);
        board[0][7] = new Rook(Player.BLACK);

        board[7][0] = new Rook(Player.WHITE);
        board[7][1] = new Knight(Player.WHITE);
        board[7][2] = new Bishop(Player.WHITE);
        board[7][3] = new Queen(Player.WHITE);
        board[7][4] = new King(Player.WHITE);
        board[7][5] = new Bishop(Player.WHITE);
        board[7][6] = new Knight(Player.WHITE);
        board[7][7] = new Rook(Player.WHITE);
    }

    @Override
    public boolean isComplete() {
        //if a player is in check and cannot get out of check, the game is complete
        return ((inCheck(Player.WHITE) && movesToEscapeCheck(Player.WHITE).isEmpty()) ||
                (inCheck(Player.BLACK) && movesToEscapeCheck(Player.BLACK).isEmpty()));
    }

    @Override
    public boolean isValidMove(Move move) { //overloaded

        if (!board[move.oldRow][move.oldColumn].isValidMove(move, board))
            return false;

        IChessPiece temp = null; //to store a piece to be captured
        if (board[move.newRow][move.newColumn] != null &&
                board[move.newRow][move.newColumn].player() != board[move.oldRow][move.oldColumn].player())
            temp = board[move.newRow][move.newColumn];

        //placing the move that needs to be validated on the board
        board[move.newRow][move.newColumn] = board[move.oldRow][move.oldColumn];
        if (move.wasEnPassant())
            board[move.oldRow][move.newColumn] = null;
        else if (move.wasCastle())
            ; //TODO: implement castle
        board[move.oldRow][move.oldColumn] = null;

        //if the move placed ends up putting player in check, then return false
        if (inCheck(currentPlayer)){

            //putting piece back to old location
            board[move.oldRow][move.oldColumn] = board[move.newRow][move.newColumn];
            if (temp != null)
                board[move.newRow][move.newColumn] = temp; //replacing captured piece
            else
                board[move.newRow][move.newColumn] = null;
            return false;
        } else {

            //putting pieces back
            board[move.oldRow][move.oldColumn] = board[move.newRow][move.newColumn];
            if (temp != null)
                board[move.newRow][move.newColumn] = temp;
            else
                board[move.newRow][move.newColumn] = null;
            return true; //move is okay if it did not put player in check
        }

    }

    @Override
    public void move(Move move) {
        if (isValidMove(move)) {

            //check for a capture
            IChessPiece captured = null;
            //check if move is to an occupied location and if the occupying piece belongs to the opponent
            //if so, set that piece to be captured
            if (board[move.newRow][move.newColumn] != null &&
                    board[move.newRow][move.newColumn].player() == board[move.oldRow][move.oldColumn].opponent()) {
                captured = board[move.newRow][move.newColumn];
            } else if (board[move.oldRow][move.newColumn] != null && //deals with en passant
                    board[move.oldRow][move.oldColumn].type().equals("Pawn") && //piece moving is a pawn
                    board[move.oldRow][move.newColumn].type().equals("Pawn") && //piece passanted is pawn
                    board[move.oldRow][move.newColumn].player().
                            equals(board[move.oldRow][move.oldColumn].opponent())) {
                move.setWasEnPassant(true);
                captured = board[move.oldRow][move.newColumn]; //sets captured piece to row above/below moving pawn
            }
            if (captured != null) {//if there is a piece to be captured
                //add to list of respective player's captures
                if (currentPlayer.equals(Player.BLACK))
                    blackCaptures.add(captured);
                else
                    whiteCaptures.add(captured);

                captureMoveStack.add(move); //adding to see which moves were captures
            }

            //transferring piece from old square to new square
            board[move.newRow][move.newColumn] = board[move.oldRow][move.oldColumn];
            if (move.wasEnPassant()) //removing pawn if en passanted
                board[move.oldRow][move.newColumn] = null;
            board[move.oldRow][move.oldColumn] = null;

            //if the King or Rook is moved, Castling is no longer an option
            IChessPiece temp = board[move.newRow][move.newColumn];
                if (temp.type().equals("King"))
                    ((King) temp).canCastle = false;
                if (temp.type().equals("Rook"))
                    ((Rook) temp).canCastle = false;
                if (temp.type().equals("Pawn"))
                    ((Pawn) temp).setFirstTurn(false);
            moveStack.add(move);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /******************************************************************
     * Reverts board back to the position before the most recent move
     * by popping the first element off the moveStack.
     *
     * @author Allison
     *****************************************************************/
    public void undoLastMove() {
        if (moveStack.empty()) //if there were no moves made, exit the method
            return;

        Move lastMove = moveStack.pop(); //remove and return the previous move

        //setting piece back to old location
        board[lastMove.oldRow][lastMove.oldColumn] = board[lastMove.newRow][lastMove.newColumn];
        if (!captureMoveStack.empty() && lastMove == captureMoveStack.peek()) { //checking if the last move was a capture
            captureMoveStack.pop(); //remove the capture

            ArrayList<IChessPiece> captures;
            if (board[lastMove.oldRow][lastMove.oldColumn].player().equals(Player.WHITE)) {
                captures = whiteCaptures;
            } else {
                captures = blackCaptures;
            }

            //set the old location to the captured piece
            if (captures.size() > 0) {
                if (lastMove.wasEnPassant()) { //en passant requires different locations for previous pieces
                    board[lastMove.oldRow][lastMove.newColumn] = captures.get(captures.size() - 1);
                    board[lastMove.newRow][lastMove.newColumn] = null;
                } else //otherwise just put the captured piece back where it was
                    board[lastMove.newRow][lastMove.newColumn] = captures.get(captures.size() - 1);
                captures.remove(captures.size() - 1); //piece is no longer captured after undo
            }
        } else if (lastMove.wasCastle()) {
            //TODO: reverting a castle
        } else //otherwise just set the old location to null
            board[lastMove.newRow][lastMove.newColumn] = null;

        //if the King or Rook move was undone, they should now be able to castle
        IChessPiece temp = board[lastMove.oldRow][lastMove.oldColumn];
        if (temp.type().equals("King") &&
                lastMove.wasCastle())
            ((King) temp).canCastle = true;
        if (temp.type().equals("Rook") &&
                lastMove.wasCastle())
            ((Rook) temp).canCastle = true;
        if (temp.type().equals("Pawn") &&
                //if move was from starting position for black
                ((lastMove.oldRow == 1 && board[lastMove.oldRow][lastMove.oldColumn].player().equals(Player.BLACK)) ||
                        //if move was from starting position for white
                (lastMove.oldRow == 6 && board[lastMove.oldRow][lastMove.oldColumn].player().equals(Player.WHITE))))
                ((Pawn) temp).setFirstTurn(true);
        switchPlayer();
    }

    /******************************************************************
     * Finds player p's king's location and determines whether possible
     * move for the opponent's pieces contains the king's location
     *
     * @author Allison
     * @param  p {@link Move} the Player to verify if in check
     * @return true if an opponent's piece can capture player p's king
     *****************************************************************/
    @Override
    public boolean inCheck(Player p) {
        int kingRow = 0;
        int kingColumn = 0;
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                if (board[row][col] != null &&
                        board[row][col].player().equals(p) &&
                        board[row][col].type().equals("King")) {
                    kingRow = row;
                    kingColumn = col;
                    break; //avoid useless lines of code after finding the king
                }
            }
        }
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                IChessPiece temp = board[row][col];
                if (temp != null && !temp.player().equals(p)) { //if piece exists and belongs to opponent
                    ArrayList<Move> moves = legalMoves(row, col); //find all valid moves for opponent
                    for (Move move : moves) {
                        //check if valid moves includes capturing the king
                        if (move.newRow == kingRow &&
                                move.newColumn == kingColumn)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /******************************************************************
     * Finds player p's king's location and moves pieces of the same
     * player it to see if that move will get it out of check. If it
     * does, adds it to an array and returns that array.
     *
     * @author Allison
     * @param p player that needs to escape check
     * @return the moves that get the player out of check, checkmate if
     *          this returns empty
     *****************************************************************/
    public ArrayList<Move> movesToEscapeCheck (Player p) {
        ArrayList<Move> moves = new ArrayList<>(); //array to hold escaping moves
        int kingRow = 0; //storing the king's location
        int kingColumn = 0;
        //finding the king
        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++)
                if (board[row][col] != null &&
                        board[row][col].player().equals(p) &&
                        board[row][col].type().equals("King")) {
                    kingRow = row;
                    kingColumn = col;
                    break; //stop after finding king
                }

        //check all current player pieces to see if there's a move to get out of check
        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++)
                if (board[row][col] != null && //if there is a piece and it is the current player's
                        board[row][col].player() == board[kingRow][kingColumn].player()) {
                    ArrayList<Move> testMoves = legalMoves(row, col);
                    for (Move test : testMoves)
                        //if player is not in check after move, it is an escaping move
                        if (isValidMove(test)) {
                            move(test);
                            if (!inCheck(p))
                                moves.add(test);
                            switchPlayer();
                            undoLastMove();
                        }

                }
        return moves;
    }

    /******************************************************************
     * Creates moves from current location to all locations on the
     * board and tests to see if those moves are valid. If they are,
     * it adds them to an array and returns that array.
     *
     * @author Allison
     * @param currentRow row of the piece to be tested
     * @param currentCol column of the piece to be tested
     * @return valid moves for the piece at currentRow and currentCol
     *****************************************************************/
    public ArrayList<Move> legalMoves(int currentRow, int currentCol) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                Move temp = new Move(currentRow, currentCol, row, col);
                if (board[currentRow][currentCol].isValidMove(temp, board))
                    possibleMoves.add(temp);
            }
        }
        return possibleMoves;
    }

    /*****************************************************************
     * Method that will take out moves that put a player in check.
     * Had to separate this method due to stack overflow errors.
     *
     * @author Allison
     * @param moves array of possible moves a piece can make, should
     *              be from legalMoves()
     * @return array of possible moves that will not put the player
     *         in check
     *****************************************************************/
    public ArrayList<Move> filterLegalMoves(ArrayList<Move> moves) {
        ArrayList<Move> betterMoves = new ArrayList<>();
        //take out moves that fail isValidMove(move)
        for (Move move: moves) {
            if (isValidMove(move))
                betterMoves.add(move);
        }
        return betterMoves;
    }

    @Override
    public Player currentPlayer() {
        return currentPlayer;
    }

    public void switchPlayer() {
        if (currentPlayer().equals(Player.BLACK))
            currentPlayer = Player.WHITE;
        else
            currentPlayer = Player.BLACK;
    }

    public boolean gameOver() {
        return false;
    }

    public IChessPiece pieceAt(int row, int column) {
        return board[row][column];
    }

    public void reset() {
        //clear all data from the game
        blackCaptures.clear();
        whiteCaptures.clear();
        moveStack.clear();
        captureMoveStack.clear();

        //remake the board
        board = new IChessPiece[8][8];
        placeStartingPieces();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
