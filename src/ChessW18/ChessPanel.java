package ChessW18;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static java.lang.System.exit;

public class ChessPanel extends JPanel {

	private JMenuItem gameItem;
	private JMenuItem quitItem;
    private JMenuItem undoMove;

	private JButton[][] board;
	private ChessModel model;
	private final int BOARDSIZE = 8;

	private JLabel outMessage;
    private String message;

	private ImageIcon wPawn;
	private ImageIcon wRook;
	private ImageIcon wBishop;
	private ImageIcon wKing;
	private ImageIcon wQueen;
	private ImageIcon wKnight;

	private ImageIcon bPawn;
	private ImageIcon bRook;
	private ImageIcon bBishop;
	private ImageIcon bKing;
	private ImageIcon bQueen;
	private ImageIcon bKnight;

	private Move move;
	/** pieceChoice is used to tell if the player is choosing a piece
	 * or a position. */
	private boolean pieceChosen;

	/** the current player */
	private Player currentPlayer;


	// declare other instance variables as needed

	private ButtonListener buttonListener = new ButtonListener();

	public ChessPanel(JMenuItem pquitItem, JMenuItem pgameItem, JMenuItem undoMove) {
		model = new ChessModel();
		move = new Move();
		currentPlayer = model.currentPlayer();
		pieceChosen = false;
		board = new JButton[BOARDSIZE][BOARDSIZE];

		gameItem = pgameItem;
		quitItem = pquitItem;
		this.undoMove = undoMove;
		quitItem.addActionListener(e -> exit(1));
		gameItem.addActionListener( e -> resetBoard());
		this.undoMove.addActionListener(e -> {
            model.undoLastMove();
            currentPlayer = model.currentPlayer();
            displayBoard();
        });

		boardInit();
		displayBoard();
	}

	/**
	 * Sets up the board, a two dimensional matrix of JButtons,
	 * to contain an icon for a chess piece in each button. The
	 * icons are specific to each button.
	 *
	 * @author George
	 * @version 3/18
	 */
	private void boardInit() {

		wPawn = new ImageIcon("wPawn.png");
		wRook = new ImageIcon("wRook.png");
		wBishop = new ImageIcon("wBishop.png");
		wKnight = new ImageIcon("wKnight.png");
		wKing = new ImageIcon("wKing.png");
		wQueen = new ImageIcon("wQueen.png");

		bPawn = new ImageIcon("bPawn.png");
		bRook = new ImageIcon("bRook.png");
		bBishop = new ImageIcon("bBishop.png");
		bKnight = new ImageIcon("bKnight.png");
		bKing = new ImageIcon("bKing.png");
		bQueen = new ImageIcon("bQueen.png");

		ButtonListener listener = new ButtonListener();

		//TODO: create a panel that shows captures
//		setLayout(new GridBagLayout()); /** I'm setting this up so that
//		 the buttons will fall into place when created */
        setLayout(new GridLayout(BOARDSIZE, BOARDSIZE)); //this can be changed back, just temporary for seeing the grid
		GridBagConstraints loc;

		//Creating the grid of buttons
		for(int row = 0; row < BOARDSIZE; row++)
			for(int col = 0; col < BOARDSIZE; col++) {
		        board[row][col] = new JButton();
				board[row][col].addActionListener(listener);
				//making a grid. maybe it should have its own panel?
                if ((row % 2 == 1 && col % 2 == 1) ||
                        (row % 2 == 0 && col % 2 == 0))
                    board[row][col].setBackground(Color.black);
                else
                    board[row][col].setBackground(Color.white);
				add(board[row][col]);
			}

		outMessage = new JLabel("");
		model.reset();
	}


	/******************************************************************
	 * Method that updates the board by setting the correct icons.
	 *
	 * @author Allison
	 *****************************************************************/
	private void displayBoard() {
		for (int row = 0; row < BOARDSIZE; row++) {
			for (int col = 0; col < BOARDSIZE; col++) {
				IChessPiece temp = model.pieceAt(row,col); //variable to improve readability
				if (temp == null) { //if there is no piece, keep going on the loop
                    board[row][col].setIcon(null);
                    continue;
                }

                switch (temp.type()) {
                    case "Pawn":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wPawn);
                        else
                            board[row][col].setIcon(bPawn);
                        break;
                    case "Knight":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wKnight);
                        else
                            board[row][col].setIcon(bKnight);
                        break;
                    case "Rook":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wRook);
                        else
                            board[row][col].setIcon(bRook);
                        break;
                    case "Bishop":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wBishop);
                        else
                            board[row][col].setIcon(bBishop);
                        break;
                    case "King":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wKing);
                        else
                            board[row][col].setIcon(bKing);
                        break;
                    case "Queen":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wQueen);
                        else
                            board[row][col].setIcon(bQueen);
                }
			}
		}
//		outMessage.setText(message);
	}

	/***************************************************************
	 * Puts the board in the starting setup.
	 *
	 **************************************************************/
	private void resetBoard() {
		board = new JButton[BOARDSIZE][BOARDSIZE];
		boardInit();
		displayBoard();
		repaint();
	}


	/**
	 * Inner class that represents action listener for buttons
	 */
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			/*
			 * We need to store the location of the first button, and
			 * then store the location of the second button.
			 * -If the player chooses the first button, this will be
			 * dealt with through the move in ChessModel. The player
			 * must choose another button.
			 * -If the player chooses a different button, then this will
			 * be stored and model.move is called. The turn will be
			 * switched in chessModel.
			 *
			 * */
			for (int row = 0; row < board.length; row++)
				for (int col = 0; col < board.length; col++)
					if (event.getSource() == board[row][col]) {
			            boolean check = model.inCheck(currentPlayer);
						if(!pieceChosen &&
                                model.pieceAt(row, col) != null && //need to make sure user is selecting an actual piece
                                model.pieceAt(row, col).player().equals(currentPlayer))//prevents playing for opponent
						    {
						    if (!check) {
                                move.oldRow = row;
                                move.oldColumn = col;

                                ArrayList<Move> moves = model.filterLegalMoves(model.legalMoves(row, col));
                                for (Move move : moves)
                                    board[move.newRow][move.newColumn].setBorder(new LineBorder(Color.blue, 5));

                                //a border so you can see which piece is selected
                                board[row][col].setBorder(new LineBorder(Color.orange, 5));
                            } else { //player in check should only be able to move out of check
                                move.oldRow = row;
                                move.oldColumn = col;

                                ArrayList<Move> moves = model.movesToEscapeCheck(currentPlayer);
                                if (moves.isEmpty()) { //if there are no moves to escape check, then checkmate
                                    System.out.println("GAME OVER");
                                    break;
                                }
                                for (Move move : moves) //show border for moves that get player out of check
                                    board[move.newRow][move.newColumn].setBorder(new LineBorder(Color.green, 5));

                                board[row][col].setBorder(new LineBorder(Color.red, 5));

                            }
                                pieceChosen = true;
						} else if (pieceChosen){ //stores the second pressed button.
							move.newColumn = col;
							move.newRow = row;
                            for (int r = 0; r < board.length; r++)
                                for (int c = 0; c < board.length; c++)
                                    if (board[r][c].getBorder() != null)
                                        board[r][c].setBorder(null);

							//not sure if he wants an invalid move to throw an error
                            try {
                                if (!check) {
                                    model.move(move);//The move method that is called here will check for validity.
                                    model.switchPlayer();
                                    currentPlayer = model.currentPlayer();
//                                    System.out.println(currentPlayer);
//                                    System.out.println(model.moveStack.size());
//                                    System.out.println(model.moveStack.peek().oldRow + " Col " + model.moveStack.peek().oldColumn);
                                    move = new Move(); //to prevent null pointer errors from trying to move a piece that isn't there anymore
                                } else {
                                    boolean moveWillEscape = false;
                                    move.newRow = row;
                                    move.newColumn = col;
                                    for (Move testMove :
                                            model.movesToEscapeCheck(currentPlayer))
                                        //check if the move the player is trying to make is an escaping one
                                        if (testMove.oldRow == move.oldRow &&
                                                testMove.oldColumn == move.oldColumn &&
                                                testMove.newRow == move.newRow &&
                                                testMove.newColumn == move.newColumn)
                                            moveWillEscape = true;

                                    if (moveWillEscape) {
                                        model.move(move);
                                        model.switchPlayer();
                                        currentPlayer = model.currentPlayer();
                                        move = new Move();
                                    }
                                }
                            } catch (IllegalArgumentException e) {
                                message = "Illegal Move"; //a JOptionPane is another option
                            }
							pieceChosen = false;
                            displayBoard();
						}
//						else if (pieceChosen && check) { //make sure move will escape check
//						    boolean moveWillEscape = false;
//						    move.newRow = row;
//						    move.newColumn = col;
//                            for (Move testMove :
//                                    model.movesToEscapeCheck(currentPlayer)) {
//                                //check if the move the player is trying to make is an escaping one
//                                if (testMove.newRow == move.newRow &&
//                                        testMove.newColumn == move.newColumn)
//                                    moveWillEscape = true;
//                            }
//                            if (moveWillEscape) {
//                                try {
//                                    model.move(move);
//                                    model.switchPlayer();
//                                    currentPlayer = model.currentPlayer();
//                                    move = new Move();
//                                } catch (Exception er) {
//                                    System.out.println("Illegal move");
//                                }
//
//                                for (int r = 0; r < board.length; r++) {
//                                    for (int c = 0; c < board.length; c++) {
//                                        if (board[r][c].getBorder() != null)
//                                            board[r][c].setBorder(null);
//                                    }
//                                }
//                                pieceChosen = false;
//                                displayBoard();
//                            } else { //if the move will not escape check
//                                System.out.println("Please choose an escaping move.");
//                            }
//
//                        }
					}
		}
	}
}