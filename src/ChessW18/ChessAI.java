package ChessW18;

import java.beans.beancontext.BeanContext;
import java.util.ArrayList;

public class ChessAI {
    ChessModel model;
    IChessPiece[][] board;
    Player player = Player.BLACK;

    //for scoring moves?
    private final int PAWN = 1;
    private final int KNIGHT = 3;
    private final int BISHOP = 3;
    private final int ROOK = 5;
    private final int QUEEN = 9;
    private final int KING = 100;

    /******************************************************************
     * Runs through every method for finding a move for the AI. The
     * move that should always have an option is found at the end
     * of block.
     *
     * @param model An object of type ChessModel that allows for the use
     *              of the methods within the ChessModel class with an
     *              upto date version of the board.
     *****************************************************************/
    public ChessAI(ChessModel model) {
        this.model = model;
        board = model.getBoard();

        Move bestMove = saveKing();
        if (bestMove != null)
            model.move(bestMove);
        else {
            bestMove = takeKing();
            if (bestMove != null)
                model.move(bestMove);
            else {
                bestMove = pieceInDanger();
                if (bestMove != null)
                    model.move(bestMove);
                else {
                    bestMove = takePiece();
                    if (bestMove != null)
                        model.move(bestMove);
                    else {
                        bestMove = makeMove();
                        if (bestMove != null) ;
                        model.move(bestMove);
                    }
                }
            }
        }
    }

    /******************************************************************
     * evaluates the score for a player based on the pieces they own
     *
     * @param player The player for which a score will be calculated.
     * @return The score of the player based upon the board.
     *
     * @author Allison
     *****************************************************************/
    private int evaluateScore(Player player) {
        int score = 0;
        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++) {
                if (board[row][col] != null)
                    if (board[row][col].player() == player)
                        //add score for the player's existing pieces
                        score += getPieceValue(board[row][col].type());

                //remove score for their pieces that have been captured
                ArrayList<IChessPiece> captures = (player ==
                        Player.BLACK ?
                        model.getWhiteCaptures() :
                        model.getBlackCaptures());
                for (IChessPiece piece : captures) {
                    score -= getPieceValue(piece.type());
                }
            }
        return score;
    }

    /**
     * If the king is in check, this method will check every black
     * piece on the board to see if it can be moved to save the king.
     *
     * @return True if a move was made to save the king.
     * @author George
     * @version 3/28/18
     */
    private Move saveKing() {
        ArrayList<Move> savingMoves = new ArrayList<Move>();
        Move bestMove;

        //If the AI is in check, get it out of check.
        if (model.inCheck(Player.BLACK)) {

            for (int row = 0; row < board.length; row++)
                for (int col = 0; col < board.length; col++) {

                    if (board[row][col] != null)
                        //Looks if this place on the board has a Black
                        //piece on it.
                        if (board[row][col].player() == Player.BLACK) {
                            ArrayList<Move> possibilities =
                                    model.legalMoves(row, col);

                            //Goes through every possible move.
                            for (int index = possibilities.size();
                                 index >= 0; index--) {
                                Move newMove = possibilities.get(index);

                                //In case an opponents piece is going to
                                //be taken, we need to know what it was.
                                IChessPiece oldPiece = board
                                        [newMove.oldRow]
                                        [newMove.oldColumn];
                                IChessPiece newPiece = board
                                        [newMove.newRow]
                                        [newMove.newColumn];
                                board[newMove.newRow][newMove.newColumn]
                                        = newPiece;
                                board[newMove.oldRow][newMove.oldColumn]
                                        = null;

                                //If the AI is no longer in check, the
                                // saving move is saved.
                                if (!model.inCheck(Player.BLACK)) {
                                    savingMoves.add(newMove);
                                }

                                //Undo the move.
                                board[newMove.oldRow][newMove.oldColumn]
                                        = oldPiece;
                                board[newMove.newRow][newMove.newColumn]
                                        = newPiece;

                            }
                        }
                }
        }
        return getBestMove(savingMoves);

    }

    private Move getBestMove(ArrayList<Move> savingMoves) {
        Move bestMove;//More defensive
        if (evaluateScore(Player.BLACK) < evaluateScore(Player.WHITE)) {
            bestMove = getBestDefensiveMove(savingMoves);
            if (bestMove != null)
                return bestMove;
            else {
                bestMove = getBestOffensiveMove(savingMoves);
                if (bestMove != null)
                    return bestMove;
                else {
                    bestMove = getSacraficialOffensiveMove(savingMoves);
                    if (bestMove != null)
                        return bestMove;
                    else
                        return null;
                }
            }
        }
        //More offensive
        else {
            bestMove = getBestOffensiveMove(savingMoves);
            if (bestMove != null)
                return bestMove;
            else {
                bestMove = getSacraficialOffensiveMove(savingMoves);
                if (bestMove != null)
                    return bestMove;
                else {
                    bestMove = getBestDefensiveMove(savingMoves);
                    if (bestMove != null)
                        return bestMove;
                    else
                        return null;
                }
            }

        }
    }

    /**
     * If the white king is not in check, this method looks to see if
     * if it could be in check within the next move.
     *
     * @return The move that could put the white king in check, null
     * if there is no such move.
     * @author George
     * @version 3/30/2018
     */
    private Move takeKing() {
        ArrayList<Move> savingMoves = new ArrayList<Move>();
        Move bestMove;

        //If the AI is in check, get it out of check.
        if (!model.inCheck(Player.WHITE)) {

            for (int row = 0; row < board.length; row++)
                for (int col = 0; col < board.length; col++) {

                    if (board[row][col] != null)
                        //Looks if this place on the board has a Black
                        // piece on it.
                        if (board[row][col].player() == Player.WHITE) {
                            ArrayList<Move> possibilities =
                                    model.legalMoves(row, col);

                            //Goes through every possible move.
                            for (Move newMove : possibilities) {

                                //In case an opponents piece is going to
                                //be taken, we need to know what it was.
                                IChessPiece oldPiece = board
                                        [newMove.oldRow]
                                        [newMove.oldColumn];
                                IChessPiece newPiece = board
                                        [newMove.newRow]
                                        [newMove.newColumn];
                                board[newMove.newRow][newMove.newColumn]
                                        = newPiece;
                                board[newMove.oldRow][newMove.oldColumn]
                                        = null;

                                //If the AI is no longer in check, the
                                // saving move is saved.
                                if (model.inCheck(Player.WHITE)) {
                                    savingMoves.add(newMove);
                                }

                                //Undo the move.
                                board[newMove.oldRow][newMove.oldColumn]
                                        = oldPiece;
                                board[newMove.newRow][newMove.newColumn]
                                        = newPiece;

                            }
                        }
                }
        }
        //If there were no moves that can put their king in check,
        //don't try to figure out which move is best.
        if (savingMoves.isEmpty())
            return null;

        //Since the game could possibly be won, be safely offensive.
        bestMove = getBestOffensiveMove(savingMoves);
        if (bestMove != null)
            return bestMove;
        else {
            bestMove = getSacraficialOffensiveMove(savingMoves);
            if (bestMove != null)
                return bestMove;
            //This else shouldn't ever be hit, but it's here for safety.
            else {
                bestMove = getBestDefensiveMove(savingMoves);
                if (bestMove != null)
                    return bestMove;
                else
                    return null;

            }
        }


    }

    /******************************************************************
     * This checks for if a piece is in danger and if a move can be
     * executed to save it. If there is, the move is returned.
     *
     * @return True if a move was made to save a piece.
     *
     * @author George
     * @version 3/29/18
     *****************************************************************/
    private Move pieceInDanger() {
        /**
         * Was about to make it so that the method returned an array of
         * locations for the badguys.
         *
         * This method could compare the change in value of the board
         * after the badguys have succeded, and then prevent the worst
         * outcome.
         */
        int badGuys[] = new int[16];

        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++)

                if (board[row][col] != null)
                    if (board[row][col].player() == Player.BLACK) {

                        for (int piecerow = 0; piecerow < board.length;
                             piecerow++)
                            for (int piececol = 0; piececol <
                                    board.length; piececol++) {

                                IChessPiece temp = board[piecerow]
                                        [piececol];

                                //if piece exists and belongs
                                //to opponent
                                if (temp != null && !temp.player().
                                        equals(Player.WHITE)) {

                                    //find all valid moves for opponent
                                    ArrayList<Move> moves = model.
                                            legalMoves(piecerow,
                                                    piececol);

                                    for (Move move : moves) {
                                        //check if valid moves includes
                                        // capturing the piece
                                        if (move.newRow == row &&
                                                move.newColumn == col)
                                            return move;

                                    }
                                }
                            }
                    }
        return null;
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
    private Move selfSave(int pRow, int pCol) {
        //The piece that needs to be saved.
        IChessPiece old = board[pRow][pCol];
        //to save any piece in the destination before the official move.
        IChessPiece temp;

        ArrayList<Move> escapeMoves = model.legalMoves(pRow, pCol);
        for (Move escape : escapeMoves) {

            //This sorta makes the move happen. An an passant won't
            // result from this, and neither will a castle.
            //However, if the move method ends up being called later on,
            // an an passant or castle could  occur.

            //Save what was there.
            temp = board[escape.newRow][escape.newColumn];

            //Put the piece that needs to be saved there.
            board[escape.newRow][escape.newColumn] = old;
            board[pRow][pCol] = null; //make the old spot empty.

            //Search the board
            for (int row = 0; row < board.length; row++)
                for (int col = 0; col < board.length; col++)
                    if (board[row][col] != null)

                        //Find white pieces
                        if (board[row][col].player() == Player.WHITE) {
                            //See what their moves are
                            ArrayList<Move> moves =
                                    model.legalMoves(row, col);
                            boolean valid = true;

                            //See if any of the moves are threatening
                            for (Move move : moves) {

                                if (move.newRow == escape.newRow &&
                                        move.newColumn ==
                                                escape.newColumn) {
                                    //The move didn't work.
                                    valid = false;
                                }
                            }

                            if (valid && !model.inCheck(Player.BLACK)) {

                                //Put it back to how it was.
                                board[escape.newRow][escape.newColumn]
                                        = temp;

                                //Put the piece back to where it was.
                                board[pRow][pCol] = old;

                                //Call the move method to officially
                                //make the move.
                                return (escape);
                            } else {

                                //Put it back to how it was.
                                board[escape.newRow][escape.newColumn]
                                        = temp;

                                //Put the piece back to where it was.
                                board[pRow][pCol] = old;
                            }
                        }
        }

        //If we get here, that means the piece wasn't
        // able to save itself.
        return (saveOther(pRow, pCol));
    }

    /*******************************************************************
     * If there is a piece that needs to be saved, this method will use
     * another piece to save it. It does this by checking every black
     * piece for a move that could save the piece, and then checks every
     * white piece for if a the piece is still in danger.
     *
     * @param pRow The row of the piece that needs to be saved.
     * @param pCol The column of the piece that needs to be saved.
     * @return True of a move was made to save the piece.
     * @author George
     * @version 3/28/2018
     ******************************************************************/
    private Move saveOther(int pRow, int pCol) {
        ArrayList<Move> goodMoves = new ArrayList<Move>();
        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++) {
                if (board[row][col] != null)
                    //We only want to use black pieces to save the piece
                    if (board[row][col].player() == Player.BLACK) {

                        //Save the state of the pieces that are being
                        // manipulated.
                        IChessPiece savior = board[row][col];
                        IChessPiece dest;

                        //This holds all of the moves that the savior
                        // piece cold make
                        ArrayList<Move> saveMoves =
                                model.legalMoves(row, col);

                        for (Move move : saveMoves) {

                            //This sorta makes the move happen. An an
                            // passant won't result from this, and
                            // neither will a castle.
                            //However, if the move method ends up being
                            // called later on, an an passant or castle
                            // could  occur.
                            dest = board[move.newRow][move.newColumn];
                            board[move.newRow][move.newColumn] = savior;
                            board[move.oldRow][move.oldColumn] = null;

                            for (int tRow = 0; tRow < board.length;
                                 tRow++)
                                for (int tCol = 0; tCol < board.length;
                                     tCol++)
                                    if (board[tRow][tCol] != null)
                                        if (board[tRow][tCol].player()
                                                == Player.WHITE) {

                                            //See what their moves are
                                            ArrayList<Move> killerMoves
                                                    = model.legalMoves
                                                    (tRow, tCol);
                                            boolean valid = true;

                                            //See if any of the moves
                                            // re threatening
                                            for (Move killer :
                                                    killerMoves) {

                                            if (killer.newRow ==
                                                    move.newRow &&
                                                    killer.newColumn
                                                    == move.newColumn) {
                                                    valid = false;
                                                }
                                            }
                                            if (valid && !model.
                                                    inCheck
                                                    (Player.BLACK)) {

                                            //Put it back to how it was.
                                                board[move.newRow]
                                                        [move.newColumn]
                                                        = dest;
                                   //Put the piece back to where it was.
                                                board[pRow][pCol] =
                                                        savior;
                                                goodMoves.add(move);
                                            } else {
                                            //Put it back to how it was.
                                                board[move.newRow]
                                                        [move.newColumn]
                                                        = dest;

                                   //Put the piece back to where it was.
                                                board[pRow][pCol] =
                                                        savior;
                                            }
                                        }
                        }
                    }
            }
        if (!goodMoves.isEmpty())
            return (getBestOffensiveMove(goodMoves));
        else
            return null;
    }

    /******************************************************************
     * Looks to see if the given piece is in danger of being taken.
     *
     * @param prow Is the row of the piece that is being checked.
     * @param pcol Is the column of the piece that is being
     *                  checked.
     * @return True if the piece is in danger.
     *
     * @author George
     * @version 3/30/2018
     *****************************************************************/
    private boolean pieceInDanger(int prow, int pcol) {

        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++) {

                if (board[row][col] != null)
                    if (board[row][col].player() == Player.WHITE) {
                        //Makes a list of possible moves of a
                        //white piece
                        ArrayList<Move> dangerMove =
                                model.legalMoves(row, col);
                        for (Move move : dangerMove)
                          //If the white piece go to the given location.
                            if (move.newRow == prow &&
                                    move.newColumn == pcol)
                                return true;
                    }
            }
        return false;
    }

    /*******************************************************************
     * Looks for if there is a white piece that can be taken without
     * sacrifice.
     *
     * @return True if a move was made to take a piece.
     *
     * @author George
     * @version 3/29/2018
     ******************************************************************/
    private Move takePiece() {
        /* The rows will be for the pieces, the columns are for the
         * possible moves of those pieces. The first two indexes for the
         * array are for newRow and newColumn. Everything afterward
         * is the value of the board after the move.
         * This is so that the capture can be given a worth.*/

        ArrayList<Move> possibleMoves = new ArrayList<Move>();
        Move bestMove = null;
        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++)
                if (board[row][col] != null)
                    if (board[row][col].player() == Player.BLACK) {
                        ArrayList<Move> pieceMoves =
                                model.legalMoves(row, col);
                        possibleMoves.addAll(pieceMoves);

                    }
        bestMove = getBestOffensiveMove(possibleMoves);

        if (bestMove != null)
            return (bestMove);

        else {
            bestMove = getSacraficialOffensiveMove(possibleMoves);
            if (bestMove != null)
                return bestMove;
            else
                return null;
        }
    }

    /*******************************************************************
     * This method attempts to predict the move of the player. It does
     * this by finding every move that the AI could legally make. It
     * then does all of these moves and sees if the player could screw
     * something up for the AI. If the player can, the AI will move on
     * to the next move.
     *
     * @return A move that the AI can legally make.
     * @author George
     * @version 3/30/2018
     ******************************************************************/
    private Move makeMove() {

        //Stores the move that the AI will make.
        Move betterThanSomeMove = null;

        //Hold the moves that the AI can make.
        ArrayList<Move> primaryMoves = new ArrayList<Move>();

        int beforeBlack, afterBlack, biggestDifferenceBlack = 100;
        int beforeWhite, afterWhite, biggestDifferenceWhite = -1;

        int beforeBlack2, afterBlack2, biggestDifferenceBlack2 = 100;
        int beforeWhite2, afterWhite2, biggestDifferenceWhite2 = -1;

        //Find every move that is possible of the black pieces.
        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++)

                if (board[row][col] != null)
                    if (board[row][col].player() == Player.BLACK)
                        primaryMoves.addAll(model.legalMoves(row, col));

        //Test every piece that is possible of a black piece.
        for (Move move : primaryMoves) {
            ArrayList<Move> nextMoves = new ArrayList<Move>();
            IChessPiece saveNew;
            IChessPiece saveOld;

            //Find the scores
            beforeBlack = evaluateScore(Player.BLACK);
            beforeWhite = evaluateScore(Player.WHITE);

            //Do the move
            saveNew = board[move.newRow][move.newColumn];
            saveOld = board[move.oldRow][move.oldColumn];
            board[move.newRow][move.newColumn] = saveOld;
            board[move.oldRow][move.oldColumn] = null;

            //Find the new score
            afterBlack = evaluateScore(Player.BLACK);
            afterWhite = evaluateScore(Player.WHITE);

            //See if the move was good.
            if (beforeWhite - afterWhite >= biggestDifferenceWhite &&
                    beforeBlack - afterBlack <= biggestDifferenceBlack
                    && !pieceInDanger(move.newRow, move.newColumn)) {

                //Set the new favorable differences.
                biggestDifferenceBlack = beforeBlack - afterBlack;
                biggestDifferenceWhite = beforeWhite - afterWhite;

                //Find every possible move that a white piece could
                //do with this new board.
                for (int row = 0; row < board.length; row++)
                    for (int col = 0; col < board.length; col++)
                        if (board[row][col] != null)
                            if (board[row][col].player() ==
                                    Player.WHITE) {
                                nextMoves.addAll(model.
                                        legalMoves(row, col));

                                for (Move newMove : nextMoves) {

                                    //Save the things that were there.
                                    IChessPiece saveNew2 = board
                                            [newMove.newRow]
                                            [newMove.newColumn];
                                    IChessPiece saveOld2 = board
                                            [newMove.oldRow]
                                            [newMove.oldColumn];

                                    //Find the score
                                    beforeBlack2 = evaluateScore
                                            (Player.BLACK);
                                    beforeWhite2 = evaluateScore
                                            (Player.WHITE);

                                    //Do the move.
                                    board[newMove.newRow]
                                            [newMove.newColumn]
                                            = saveOld2;
                                    board[newMove.oldRow]
                                            [newMove.oldColumn]
                                            = null;

                                    //Find the new score.
                                    afterBlack2 = evaluateScore
                                            (Player.BLACK);
                                    afterWhite2 = evaluateScore(
                                            Player.WHITE);

                                    //Undo the move
                                    board[newMove.newRow]
                                            [newMove.newColumn]
                                            = saveNew2;
                                    board[newMove.oldRow]
                                            [newMove.oldColumn]
                                            = saveOld2;

                                    //Was the move good?
                                    if (beforeWhite2 - afterWhite2
                                            >= biggestDifferenceWhite2
                                            && beforeBlack2 -
                                            afterBlack2
                                            <= biggestDifferenceBlack2){

                                     //The black move was good, save it.
                                        betterThanSomeMove = move;

                                        //Save the new favored differences.
                                        biggestDifferenceWhite2 =
                                                beforeWhite2 -
                                                        afterWhite2;
                                        biggestDifferenceBlack2 =
                                                beforeBlack2 -
                                                        afterBlack2;
                                    }

                                }
                                //Undo the move
                                board[move.newRow][move.newColumn]
                                        = saveNew;
                                board[move.oldRow][move.oldColumn]
                                        = saveOld;
                            }
            } else {//Undo the move
                board[move.newRow][move.newColumn] = saveNew;
                board[move.oldRow][move.oldColumn] = saveOld;
            }
        }
        //The move isn't great, but it's better than nothing, and it
        //took a lot to make sure it wasn't bad.
        return betterThanSomeMove;
    }

    /*******************************************************************
     * Goes through the moves that are given, and looks to see which
     * will offer the largest change in score without puting another
     * piece in danger.
     *
     * @param possibleMoves Moves that can be made
     * @return The move that offers the biggest change in score.
     * @author George
     * @version 3/30/2018
     ******************************************************************/
    private Move getBestOffensiveMove(ArrayList<Move> possibleMoves) {
        int before;
        int after;
        int biggestDifference = 0;
        Move bestMove = null;

        for (Move newMove : possibleMoves) {
            before = evaluateScore(Player.WHITE);
            IChessPiece temp = board[newMove.newRow][newMove.newColumn];
            IChessPiece old = board[newMove.oldRow][newMove.oldColumn];
            board[newMove.newRow][newMove.newColumn] =
                    board[newMove.oldRow][newMove.oldColumn];
            board[newMove.oldRow][newMove.oldColumn] = null;
            after = evaluateScore(Player.WHITE);
            board[newMove.newRow][newMove.newColumn] = temp;
            board[newMove.oldRow][newMove.oldColumn] = old;

            if (before - after > biggestDifference &&
                    !pieceInDanger(newMove.newRow, newMove.newColumn)) {
                biggestDifference = before - after;
                bestMove = newMove;
            }
        }
        return bestMove;
    }

    /******************************************************************
     * Goes through the moves that are given, and looks to see if there
     * is a move that will result in the largest change in the board
     * score of the white team and minimizing the change in score for
     * the black team.
     *
     * @param possibleMoves Moves that can be made
     * @return The move that offers the biggest change in score.
     *
     * @author George
     * @version 3/30/2018
     *****************************************************************/
    private Move getSacraficialOffensiveMove(ArrayList<Move>
                                                     possibleMoves) {
        int beforeWhite, beforeBlack;
        int afterWhite, afterBlack;
        int biggestDifferenceWhite = 0;
        int biggestDifferenceBlack = 0;
        Move bestMove = null;

        for (Move newMove : possibleMoves) {
            beforeWhite = evaluateScore(Player.WHITE);
            beforeBlack = evaluateScore(Player.BLACK);

            IChessPiece temp = board[newMove.newRow][newMove.newColumn];
            IChessPiece old = board[newMove.oldRow][newMove.oldColumn];
            board[newMove.newRow][newMove.newColumn] =
                    board[newMove.oldRow][newMove.oldColumn];
            board[newMove.oldRow][newMove.oldColumn] = null;

            afterBlack = evaluateScore(Player.BLACK);
            afterWhite = evaluateScore(Player.WHITE);

            board[newMove.newRow][newMove.newColumn] = temp;
            board[newMove.oldRow][newMove.oldColumn] = old;

            //Looks to see if the board score has decreased for the
            //white team and hasn't decreased for the black.
            //Makes sure that the piece being taken is bigger than the
            //last piece that was taken. If it is, it makes sure that if
            //the moving piece is in danger that the danger is worth it,
            //if the piece isn't in danger, then it can move without
            //worry.
            if (beforeWhite - afterWhite > biggestDifferenceWhite)
                if (pieceInDanger(newMove.newRow, newMove.newColumn) &&
                        (getPieceValue(board[newMove.oldRow]
                                [newMove.oldColumn].type())
                                >= getPieceValue(board[newMove.newRow]
                                [newMove.newColumn].type()))) {
                    bestMove = newMove;
                    biggestDifferenceWhite = beforeWhite - afterWhite;
                    biggestDifferenceBlack = beforeBlack - afterBlack;

                } else {
                    bestMove = newMove;
                    biggestDifferenceWhite = beforeWhite - afterWhite;
                    biggestDifferenceBlack = beforeBlack - afterBlack;
                }
        }
        return bestMove;
    }

    /******************************************************************
     * Takes a list of moves and looks to see which can
     *
     * @param possibleMoves The moves that need to be sifted through.
     * @return A move that heavily favors safety.
     *
     * @author George
     * @version 3/30/2018
     *****************************************************************/
    private Move getBestDefensiveMove(ArrayList<Move> possibleMoves) {
        int before;
        int after;
        int biggestDifference = 100; //Something big
        Move bestMove = null;

        for (Move newMove : possibleMoves) {
            IChessPiece temp = board[newMove.newRow][newMove.newColumn];
            IChessPiece old = board[newMove.oldRow][newMove.oldColumn];

            before = evaluateScore(Player.BLACK);
            board[newMove.newRow][newMove.newColumn] =
                    board[newMove.oldRow][newMove.oldColumn];
            board[newMove.oldRow][newMove.oldColumn] = null;
            after = evaluateScore(Player.BLACK);

            board[newMove.newRow][newMove.newColumn] = temp;
            board[newMove.oldRow][newMove.oldColumn] = old;

            if (before - after < biggestDifference &&
                    !pieceInDanger(newMove.newRow, newMove.newColumn)) {
                biggestDifference = before - after;
                bestMove = newMove;
            }
        }
        return bestMove;
    }

    /*******************************************************************
     * Returns the value of a given piece.
     *
     * @param type The piece that needs to be assigned a value.
     * @return The value of that piece.
     ******************************************************************/
    private int getPieceValue(String type) {
        switch (type) {
            case "Pawn":
                return PAWN;
            case "Knight":
                return KNIGHT;
            case "Bishop":
                return BISHOP;
            case "Rook":
                return ROOK;
            case "Queen":
                return QUEEN;
            case "King":
                return KING;
        }
        return 0;
    }

}
