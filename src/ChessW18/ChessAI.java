package ChessW18;

import java.util.ArrayList;
import java.util.Random;

public class ChessAI {
    private ChessModel model;
    private IChessPiece[][] board;
    private Player player;

    public ChessAI(Player player, ChessModel model) {
        this.player = player;
        this.model = model;
        board = model.getBoard();
    }

    //TODO: I made this method to return a move but some of the AI methods return boolean
    //should this return something else?
    public Move aiMove() {
        Move bestMove = scanDatabase();
        if (bestMove.oldRow == 0 && bestMove.oldColumn == 0 &&
                bestMove.newRow == 0 && bestMove.newColumn == 0)
            bestMove = randomMove();
        //get out of check
        if (model.inCheck(Player.BLACK))
            bestMove = model.movesToEscapeCheck(Player.BLACK).get(
                    new Random().nextInt(
                            model.movesToEscapeCheck(Player.BLACK).size() - 1));

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                //saving a piece in danger
                if (pieceInDanger() && board[row][col] != null &&
                        board[row][col].player() == Player.BLACK) {
                    if(savePiece(row, col))
                        return null;
                }

                //block off escape to checkmate opponent
                if (model.inCheck(Player.WHITE)&&
                        board[row][col] != null &&
                            board[row][col].player() == Player.BLACK){
                        ArrayList<Move> escapingMoves = model.movesToEscapeCheck(Player.WHITE);
                        for (Move escape :
                                escapingMoves) {
                            if (model.isValidMove(new Move(row, col, escape.newRow, escape.newColumn)))
                                bestMove = new Move(row, col, escape.newRow, escape.newColumn);
                        }
                }
            }
        }

        return bestMove;
    }

    /******************************************************************
     * Searches through the database of moves and attempts to find and
     * return a move to add to a similar move sequence
     *
     * @return a new Move if no similar move sequence is found,
     *         otherwise returns the next move in the matching
     *         sequence
     *****************************************************************/
    public Move scanDatabase() {
        Move nextMove = new Move();
        GameFileHandler handler = new GameFileHandler(model);
        //separating the games into individual move sequences
        ArrayList<String> sequences = handler.databaseGames();

        //iterate through each sequence to find similar moves
        for (String moves : sequences) {
            //moves from the database
            ArrayList<String> databaseMoves = handler.separateMoves(
                    moves);
            //moves from the current game
            ArrayList<String> gameMoves = handler.separateMoves(
                    model.getGameData());

            //while gameMoves has similar moves to databaseMoves
            //AI should execute response from the database
            boolean flag = true; //for similar moves, set to false when moves aren't similar
            boolean secondFlag; //when 3 of the same moves can't be found, use 2
            if (gameMoves.size() < 3) {
                //if opening is the same
                if (!databaseMoves.get(0).contains(gameMoves.get(0)))
                    flag = false;
                if (flag)
                    nextMove = handler.decodeMove(databaseMoves.get(1));
            } else if (gameMoves.size() < 5) {
                //if first 2 move are the same
                if (!databaseMoves.get(0).contains(gameMoves.get(0)) &&
                        !databaseMoves.get(2).contains(gameMoves.get(0)))
                    flag = false;
                if (flag)
                    nextMove = handler.decodeMove(databaseMoves.get(3));
            } else
                //when game is at least 5 moves in
                for (int i = 4; i < databaseMoves.size(); i++) {
                    //if last two moves are the same
                    secondFlag = databaseMoves.get(i - 2).contains(gameMoves.get(gameMoves.size() - 3)) &&
                            databaseMoves.get(i).contains(gameMoves.get(gameMoves.size() - 1));

                    //if the past three moves are the same
                    flag = databaseMoves.get(i - 4).contains(gameMoves.get(gameMoves.size() - 5)) &&
                            secondFlag;
                    //check if the next move in a sequence of 3 similar ones is valid
                    if (flag && i + 1 < databaseMoves.size() &&
                            model.isValidMove(handler.decodeMove(databaseMoves.get(i + 1)))) {
                        nextMove = handler.decodeMove(databaseMoves.get(i + 1));

                        //check if the next move in a sequence of 2 similar ones is valid
                    } else if (secondFlag &&
                            model.isValidMove(handler.decodeMove(databaseMoves.get(i + 1)))) {
                        nextMove = handler.decodeMove(databaseMoves.get(i + 1));
                    }
                }
        }
        return nextMove; //will return (0, 0, 0, 0) if nothing was found
    }

    /**
     * Performs a random move out of all possible moves
     *
     * @return move from all valid moves
     */
    public Move randomMove() {
        ArrayList<Move> allMoves = new ArrayList<>();
        Random random = new Random();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                if (board[row][col] != null &&
                        board[row][col].player() == player)
                    allMoves.addAll(model.filterLegalMoves(model.legalMoves(row,col)));
            }
        }
        return allMoves.get(random.nextInt(allMoves.size() - 1));
    }

    /**
     * If the king is in check, this method will check every black
     * piece on the board to see if it can be moved to save the king.
     *
     * @return True if a move was made to save the king.
     *
     * @author George
     * @version 3/26/18
     */
    private boolean saveKing() {
        //If the AI is in check, get it out of check.
        if (model.inCheck(Player.BLACK)) {
            for (int row = 0; row < board.length; row++)
                for (int col = 0; col < board.length; col++) {

                    //Looks if this place on the board has a Black piece on it.
                    if (board[row][col].player() == Player.BLACK) {
                        ArrayList<Move> possibilities = model.filterLegalMoves(model.legalMoves(row, col));

                        //Goes through every possible move.
                        for (int index = possibilities.size() - 1; index >= 0; index--) {
                            Move newMove = possibilities.get(index);

                            //In case an opponents piece is going to be taken, we need to know what it was.
                            IChessPiece oldPiece = board[newMove.newRow][newMove.newColumn];
                            board[newMove.newRow][newMove.newColumn] = board[row][col];

                            //If the AI is still in check, the move in executed with the Move method.
                            if (model.inCheck(Player.BLACK)) {
                                board[row][col] = oldPiece;
                                model.getHandler().moveAndAddToSequence(newMove);
                                return true;
                            } else
                                board[newMove.newRow][newMove.newColumn] = null;

                        }
                    }
                }
        }
        return false;
    }

    /******************************************************************
     * This can be used to determine if a black piece is in danger.
     *
     * @return True if a move was made to save a piece.
     *
     * @author George
     * @version 3/26/18
     *****************************************************************/
    private boolean pieceInDanger() {
        /*
          We should make a list of moves that could take a piece
          so that the value of a piece can determine which piece should be saved.
          As it is right now, this will save the first piece it finds. Well, it
          will know that it can save a move, it can't actually make a move right
          now.
         */
        for(int row = 0; row < board.length; row++)
            for(int col = 0; col < board.length; col++)
                if(board[row][col].player() == player.BLACK) {

                    for (int piecerow = 0; piecerow < board.length; piecerow++) {
                        for (int piececol = 0; piececol < board.length; piececol++) {
                            IChessPiece temp = board[piecerow][piececol];
                            if (temp != null && !temp.player().equals(player.WHITE)) { //if piece exists and belongs to opponent
                                ArrayList<Move> moves = model.legalMoves(piecerow, piececol); //find all valid moves for opponent
                                for (Move move : moves) {
                                    //check if valid moves includes capturing the piece
                                    if (move.newRow == row &&
                                            move.newColumn == col)

                                        return true;
                                }
                            }
                        }
                    }
                }
    return false;
    }

    /*******************************************************************
     * Makes an array list of moves that the piece that needs to be
     * saved can make. Then runs through every white piece on the board
     * to see if the piece is still in danger after the piece was moved.
     *
     * @param pRow The row of the piece that should be saved.
     * @param pCol The column of the piece that should be saved.
     * @return True if a move was made to save the piece.
     *
     * @author George
     * @version 3/28/2018
     ******************************************************************/
    private boolean savePiece(int pRow, int pCol) {
        IChessPiece old = board[pRow][pCol]; //The piece that needs to be saved.
        IChessPiece temp; //to save any piece in the destination before the official move.

        ArrayList<Move> escapeMoves = model.legalMoves(pRow, pCol);
        for(Move escape: escapeMoves) {

            //This sorta makes the move happen. An an passant won't result from this, and neither will a castle.
            //However, if the move method ends up being called later on, an an passant or castle could  occur.
            temp = board[escape.newRow][escape.newColumn]; //Save what was there.
            board[escape.newRow][escape.newColumn] = old; //Put the piece that needs to be saved there.
            board[pRow][pCol] = null; //make the old spot empty.

            for (int row = 0; row < board.length; row++) //Search the board
                for (int col = 0; col < board.length; col++)

                    if (board[row][col].player() == Player.WHITE) { //Find white pieces
                        ArrayList<Move> moves = model.legalMoves(row, col); //See what their moves are
                        boolean valid = true;

                        for (Move move : moves) { //See if any of the moves are threatening

                            if (move.newRow == escape.newRow && move.newColumn == escape.newColumn) {
                                valid = false; //The move didn't work.
                            }
                        }

                        if(valid && !model.inCheck(Player.BLACK)) { //A smarter AI would also check for if the pieces that we are the most about are in danger with this new move.
                            board[escape.newRow][escape.newColumn] = temp; //Put it back to how it was.
                            board[pRow][pCol] = old; //Put the piece back to where it was.
                            model.getHandler().moveAndAddToSequence(escape);//Call the move method to officially make the move.
                            return true;
                        }
                        else {
                            board[escape.newRow][escape.newColumn] = temp; //Put it back to how it was.
                            board[pRow][pCol] = old; //Put the piece back to where it was.
                        }
                    }
        }

        //If we get here, that means the piece wasn't able to save itself.
        for(int row = 0; row < board.length; row++)
            for(int col = 0; col < board.length; col++) {

                //We only want to use black pieces to save the piece.
                if(board[row][col].player() == Player.BLACK) {

                    //Save the state of the pieces that are being manipulated.
                    IChessPiece savior = board[row][col];
                    IChessPiece dest;

                    //This holds all of the moves that the savior piece cold make
                    ArrayList<Move> saveMoves = model.legalMoves(row,col);

                    for(Move move: saveMoves) {

                        //This sorta makes the move happen. An an passant won't result from this, and neither will a castle.
                        //However, if the move method ends up being called later on, an an passant or castle could  occur.
                        dest = board[move.newRow][move.newColumn];
                        board[move.newRow][move.newColumn] = savior;
                        board[move.oldRow][move.oldColumn] = null;

                        for (int tRow = 0; tRow < board.length; tRow++) //Search the board for pieces
                            for (int tCol = 0; tCol < board.length; tCol++)

                                if (board[tRow][tCol].player() == Player.WHITE) { //find white pieces

                                    ArrayList<Move> killerMoves = model.legalMoves(tRow, tCol); //See what their moves are
                                    boolean valid = true;

                                    for (Move killer : killerMoves) { //See if any of the moves are threatening

                                        if (killer.newRow == move.newRow && killer.newColumn == move.newColumn) {
                                            valid = false; //The move of the piece that needs to be saved didn't work.
                                        }
                                    }
                                    if(valid && !model.inCheck(Player.BLACK)) { //A smarter AI would also check for if the pieces that we are the most about are in danger with this new move.
                                        board[move.newRow][move.newColumn] = dest; //Put it back to how it was.
                                        board[pRow][pCol] = savior; //Put the piece back to where it was.
                                        model.getHandler().moveAndAddToSequence(move); //Call the move method to officially make the move.
                                        return true;
                                    }
                                    else {
                                        board[move.newRow][move.newColumn] = dest; //Put it back to how it was.
                                        board[pRow][pCol] = savior; //Put the piece back to where it was.
                                    }
                                }

                    }
                }
            }
        return false;
    }
}
