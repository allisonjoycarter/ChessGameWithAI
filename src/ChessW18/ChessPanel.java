package ChessW18;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessPanel extends JPanel {

	private JButton[][] board;
	private ChessModel model;
	private final int BOARDSIZE = 8;

	private JLabel message;

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


	// declare other instance variables as needed

	private ButtonListener buttonListener = new ButtonListener();

	public ChessPanel() {
		model = new ChessModel();
		move = new Move();
		board = new JButton[BOARDSIZE][BOARDSIZE];
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

		//This images still need to be added to the repository
		wPawn = new ImageIcon("wPawn.png");
		wRook = new ImageIcon("wRook.png");
		wBishop = new ImageIcon("wBishop.png");
		wKnight = new ImageIcon("wKnight");
		wKing = new ImageIcon("wKing");
		wQueen = new ImageIcon("wQueen");

		//This images still need to be added to the repository
		bPawn = new ImageIcon("bPawn");
		bRook = new ImageIcon("bRook");
		bBishop = new ImageIcon("bBishop");
		bKnight = new ImageIcon("bKnight");
		bKing = new ImageIcon("bKing");
		bQueen = new ImageIcon("bQueen");

		ButtonListener listener = new ButtonListener();

//		setLayout(new GridBagLayout()); /** I'm setting this up so that
//		 the buttons will fall into place when created */
        setLayout(new GridLayout(BOARDSIZE, BOARDSIZE)); //this can be changed back, just temporary for seeing the grid
		GridBagConstraints loc;

		//Creates all of the pawns.
		for(int row = 0; row < BOARDSIZE; row++)
			for(int col = 0; col < BOARDSIZE; col++) {
		        board[row][col] = new JButton();
				board[row][col].addActionListener(listener);
                if ((row % 2 == 1 && col % 2 == 1) ||
                        (row % 2 == 0 && col % 2 == 0))
                    board[row][col].setBackground(Color.black);
                else
                    board[row][col].setBackground(Color.white);
				add(board[row][col]);
			}

		message = new JLabel("");
		model.reset();
	}


	/**
	 * Method that updates the board.
	 *
	 * needs to be finished
	 */
	private void displayBoard() {
		for (int row = 0; row < BOARDSIZE; row++) {
			for (int col = 0; col < BOARDSIZE; col++) {
				IChessPiece temp = model.pieceAt(row,col); //variable to improve readability
				if (temp == null) { //if there is no piece, keep going on the loop
                    board[row][col].setIcon(null);
                    continue;
                }
				//set pawn icon
				if (temp.type().equals("Pawn")) {
					if (temp.player().equals(Player.WHITE))
						board[row][col].setIcon(wPawn);
					else
						board[row][col].setIcon(bPawn);
				} else if (temp.type().equals("Knight")) { //set knight icon
					if (temp.player().equals(Player.WHITE))
						board[row][col].setIcon(wKnight);
					else
						board[row][col].setIcon(bKnight);
				} else if (temp.type().equals("Rook")) { //set rook icon
					if (temp.player().equals(Player.WHITE))
						board[row][col].setIcon(wRook);
					else
						board[row][col].setIcon(bRook);
				} else if (temp.type().equals("Bishop")) { //set bishop icon
					if (temp.player().equals(Player.WHITE))
						board[row][col].setIcon(wBishop);
					else
						board[row][col].setIcon(bBishop);
				} else if (temp.type().equals("King")) { //set king icon
					if (temp.player().equals(Player.WHITE))
						board[row][col].setIcon(wKing);
					else
						board[row][col].setIcon(bKing);
				} else if (temp.type().equals("Queen")) { //set queen icon
					if (temp.player().equals(Player.WHITE))
						board[row][col].setIcon(wQueen);
					else
						board[row][col].setIcon(bQueen);
				}
			}
		}

		board[move.newRow][move.newColumn] =
				board[move.oldRow][move.oldColumn];
		board[move.oldRow][move.oldColumn] = new JButton();

//		message = new JLabel(model.getMessage(),
//				(model.getMessage()).length());

		repaint();
	}

	/***************************************************************
	 * Puts the board in the starting setup.
	 *
	 **************************************************************/
	private void resetBoard() {
		boardInit();
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
			 * then store the location of the first button.
			 * -If the player chooses the first button, this will be
			 * dealt with through the move in ChessModel. The player
			 * must choose another button.
			 * -If the player chooses a different button, then this will
			 * be stored and model.move is called. The turn will be
			 * switched in chessModel.
			 *
			 * -Somewhere, probably not here, we should make sure a piece of the
			 *  correct team is being chosen.
			 * -Should make the button look differently, but still
			 *  pressable?
			 * */
			for (int row = 0; row < 8; row++)
				for (int col = 0; col < 8; col++)
					if (event.equals(board[row][col])) {
						if(!pieceChosen) { //stores the first pressed button.
							move.oldColumn = col;
							move.oldRow = row;
							pieceChosen = true;
						}
						else { //stores the second pressed button.
							move.newColumn = col;
							move.newRow = row;
							pieceChosen = false;

                            //make sure pieces can't move certain ways after the first turn
                            if (model.pieceAt(row, col) != null) {
                                if (model.pieceAt(row, col).type().equals("Pawn"))
                                    ((Pawn) model.pieceAt(row, col)).setFirstTurn(false);
                                else if (model.pieceAt(row,col).type().equals("King"))
                                    ((King) model.pieceAt(row, col)).setCanCastle(false);
                                else if (model.pieceAt(row, col).type().equals("Rook"))
                                    ((Rook) model.pieceAt(row, col)).setCanCastle(false);
                            }
                            model.addToMoveStack(move);
						}
					}

			model.move(move);//The move method that is called here will check for validity.
			displayBoard();
		}



	}
}