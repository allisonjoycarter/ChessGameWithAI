package ChessW18;

import java.util.ArrayList;
import java.util.Stack;

public class ChessModel implements IChessModel {

    private IChessPiece[][] board = new IChessPiece[8][8];
    private Player currentPlayer; //to be changed
    private ArrayList<IChessPiece> whiteCaptures;
    private ArrayList<IChessPiece> blackCaptures;
    Stack<Move> moveStack;
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
        return false;
    }

    @Override
    public boolean isValidMove(Move move) { //overloaded
        //valid if:
            //board[][].isValidMove returns true
        if (board[move.oldRow][move.oldColumn].isValidMove(move, board))
            return true;
            //player will not be in check
            //move will get player out of check if they are in check
        return false;
    }

    /******************************************************************
     * Checks if a move is a capture and returns the captured piece
     *
     * @return the piece that was captured
     * @param move
     * @param board
     *****************************************************************/
    private IChessPiece checkForAndCapture(Move move, IChessPiece[][] board) {
        IChessPiece tempPiece = board[move.newRow][move.newColumn];
        //if the player of the place to move to is opposite of the player of the moving piece
        if (tempPiece != null &&
                tempPiece.player().equals(board[move.oldRow][move.oldColumn].opponent())) {
            return tempPiece;
        } else if (board[move.oldRow][move.newColumn] != null && //deals with en passant
                board[move.oldRow][move.newColumn].type().equals("Pawn") &&
                board[move.oldRow][move.newColumn].player().
                        equals(board[move.oldRow][move.oldColumn].opponent())) {
            move.setWasEnPassant(true);
            return board[move.oldRow][move.newColumn];
        }
        return null;
    }

    @Override
    public void move(Move move) {
        if (isValidMove(move)) {
            IChessPiece captured = checkForAndCapture(move, board);
            if (captured != null) {//if there is a piece to be captured
                //add to list of respective player's captures
                if (currentPlayer.equals(Player.BLACK))
                    blackCaptures.add(captured);
                else
                    whiteCaptures.add(captured);

                captureMoveStack.add(move);
            }

            //transferring piece from old square to new square
            board[move.newRow][move.newColumn] = board[move.oldRow][move.oldColumn];
            if (move.wasEnPassant())
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

        } else {
            throw new IllegalArgumentException();
        }
    }

    /******************************************************************
     * Reverts board back to the position before the most recent move.
     *
     * @author Allison
     *****************************************************************/
    public void undoLastMove() {
        if (moveStack.empty())
            return;
        Move lastMove = moveStack.pop();
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
                if (lastMove.wasEnPassant()) {
                    board[lastMove.oldRow][lastMove.newColumn] = captures.get(captures.size() - 1);
                    board[lastMove.newRow][lastMove.newColumn] = null;
                } else
                    board[lastMove.newRow][lastMove.newColumn] = captures.get(captures.size() - 1);
                captures.remove(captures.size() - 1);
            }
        } else if (lastMove.wasCastle()) {
            //reverting a castle
        } else //otherwise just set the old location to null
            board[lastMove.newRow][lastMove.newColumn] = null;

        //if the King or Rook move was undone, they should now be able to castle
        IChessPiece temp = board[lastMove.oldRow][lastMove.oldColumn];
        if (temp.type().equals("King") &&
                ((lastMove.oldColumn == 4 && lastMove.oldRow == 0 && //if the move started in starting position
                        board[lastMove.oldRow][lastMove.oldColumn].player().equals(Player.BLACK)) ||
                (lastMove.oldColumn == 4 && lastMove.oldRow == 7 && //if the move was in starting positon for white
                        board[lastMove.oldRow][lastMove.oldColumn].player().equals(Player.WHITE))))
            ((King) temp).canCastle = true;
        if (temp.type().equals("Rook") &&
                //if move was starting position for black
                (((lastMove.oldColumn == 0 || lastMove.oldColumn == 7) && lastMove.oldRow == 0 &&
                board[lastMove.oldRow][lastMove.oldColumn].player().equals(Player.BLACK)) ||
                        //if move was starting position for white
                ((lastMove.oldColumn == 0 || lastMove.oldColumn == 7) && lastMove.oldRow == 7 &&
                board[lastMove.oldRow][lastMove.oldColumn].player().equals(Player.WHITE))))
            ((Rook) temp).canCastle = true;
        if (temp.type().equals("Pawn") &&
                //if move was starting position for black
                ((lastMove.oldRow == 1 && board[lastMove.oldRow][lastMove.oldColumn].player().equals(Player.BLACK)) ||
                        //if move was from starting position for white
                (lastMove.oldRow == 6 && board[lastMove.oldRow][lastMove.oldColumn].player().equals(Player.WHITE))))
                ((Pawn) temp).setFirstTurn(true);
        switchPlayer();
    }

    public void addToMoveStack(Move move) {
        moveStack.add(move);
    }

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
                }
            }
        }
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                IChessPiece temp = board[row][col];
                if (temp != null && !temp.player().equals(p)) { //if piece exists and belongs to opponent
                    ArrayList<Move> moves = findValidMoves(row, col); //find all valid moves for opponent
                    for (int i = 0; i < moves.size(); i++) {
                        //check if valid moves includes capturing the king
                        if (moves.get(i).newRow == kingRow &&
                                moves.get(i).newRow == kingColumn)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<Move> movesToEscapeCheck (Player p) {
        ArrayList<Move> moves = new ArrayList<>();
        int kingRow = 0;
        int kingColumn = 0;
        if (inCheck(p)) {
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board.length; col++) {
                    if (board[row][col] != null &&
                            board[row][col].player().equals(p) &&
                            board[row][col].type().equals("King")) {
                        kingRow = row;
                        kingColumn = col;
                    }
                }
            }
            ArrayList<Move> movesToTest = findValidMoves(kingRow, kingColumn);
            for (int i = 0; i < movesToTest.size(); i++) {
                //do and undo move?
            }
        }
        return moves;
    }

    public ArrayList<Move> findValidMoves(int currentRow, int currentCol) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                Move temp = new Move(currentRow, currentCol, row, col);
                if (isValidMove(temp))
                    possibleMoves.add(temp);
            }
        }
        return possibleMoves;
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
        blackCaptures.clear();
        whiteCaptures.clear();
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
