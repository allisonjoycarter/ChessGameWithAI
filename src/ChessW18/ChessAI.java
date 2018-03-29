package ChessW18;

import java.util.ArrayList;

public class ChessAI {
    ChessModel model;
    IChessPiece[][] board;
    Player player;

    public ChessAI(Player player, ChessModel model) {
        this.player = player;
        this.model = model;
        board = model.getBoard();
    }

    public Move scanDatabase() {
        System.out.println(model.getGameData());
        Move nextMove = new Move();
        GameFileHandler handler = new GameFileHandler(model);
        ArrayList<String> moveCodes = handler.databaseGames();
        System.out.println(moveCodes.size());
        for (String moveCode : moveCodes) {
            ArrayList<String> databaseMoves = handler.separateMoves(
                    moveCode);
            ArrayList<String> gameMoves = handler.separateMoves(
                    model.getGameData());
            //while gameMoves has similar moves to databaseMoves
            //AI should execute response from the database
            boolean flag = true;
            if (gameMoves.size() < 8) {
                for (int j = 0; j < gameMoves.size(); j++) {
                    if (!databaseMoves.get(j).contains(gameMoves.get(j)) &&
                            !databaseMoves.get(j).equals(gameMoves.get(j))) {
                        flag = false;
                        break;
                    }

                }
            } else {
                for (int j = gameMoves.size() - 8; j < gameMoves.size(); j+=2) {
                    if (!databaseMoves.get(j).contains(gameMoves.get(j)) &&
                            !databaseMoves.get(j).equals(gameMoves.get(j))) {
                        flag = false;
                        break;
                    }

                }
                if (!flag) {
                    flag = true;
                    for (int j = gameMoves.size() - 6; j < gameMoves.size(); j+=2) {
                        if (!databaseMoves.get(j).contains(gameMoves.get(j)) &&
                                !databaseMoves.get(j).equals(gameMoves.get(j))) {
                            flag = false;
                            break;
                        }
                    }
                }
                if (!flag) {
                    flag = true;
                    for (int j = gameMoves.size() - 4; j < gameMoves.size(); j+=2) {
                        if (!databaseMoves.get(j).contains(gameMoves.get(j)) &&
                                !databaseMoves.get(j).equals(gameMoves.get(j))) {
                            flag = false;
                            break;
                        }
                    }
                }
            }
            if (flag)
                nextMove = handler.decodeMove(databaseMoves.get(gameMoves.size()));

        }
        System.out.println(nextMove.newColumn);
        return nextMove;
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
                        ArrayList<Move> possibilities = model.legalMoves(row, col);

                        //Goes through every possible move.
                        for (int index = possibilities.size(); index >= 0; index--) {
                            Move newMove = possibilities.get(index);

                            //In case an opponents piece is going to be taken, we need to know what it was.
                            IChessPiece oldPiece = board[newMove.newRow][newMove.newColumn];
                            board[newMove.newRow][newMove.newColumn] = board[row][col];

                            //If the AI is still in check, the move in excecuted with the Move method.
                            if (model.inCheck(Player.BLACK)) {
                                board[row][col] = oldPiece;
                                model.move(newMove);
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
        /**
         * We should make a list of moves that could take a piece
         * so that the value of a piece can determine which piece should be saved.
         * As it is right now, this will save the first piece it finds. Well, it
         * will know that it can save a move, it can't actually make a move right
         * now.
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
                            model.move(escape); //Call the move method to officially make the move.
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
                                        model.move(move); //Call the move method to officially make the move.
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
