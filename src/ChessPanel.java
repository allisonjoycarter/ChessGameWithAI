import ChessW18.ChessModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessPanel extends JPanel {
	
	   private JButton[][] board;   
	   private ChessModel model;
	   private final int BOARDSIZE = 8;
	   
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

	   // declare other instance variables as needed

	   private ButtonListener buttonListener = new ButtonListener();

	   public ChessPanel() {
		   board = new JButton[BOARDSIZE][BOARDSIZE];
		   boardInit();
	   }
	   
	   /**
	    * Sets up the board, a two dimensional matrix of JButtons,
	    * to contain an icon for a chess piece in each button. The 
	    * icons are specific to each button.
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
		   
//		   ButtonListener listener = new ButtonListener();
		   
		   //Creates buttons for the white pieces
		   board[0][0] = new JButton(wRook);
		   board[0][1] = new JButton(wKnight);
		   board[0][2] = new JButton(wBishop);
		   board[0][3] = new JButton(wKing);
		   board[0][4] = new JButton(wQueen);
		   board[0][5] = new JButton(wBishop);
		   board[0][6] = new JButton(wKnight);
		   board[0][7] = new JButton(wRook);

		   //Creates buttons for the black pieces
		   board[7][0] = new JButton(bRook);
		   board[7][1] = new JButton(bKnight);
		   board[7][2] = new JButton(bBishop);
		   board[7][3] = new JButton(bKing);
		   board[7][4] = new JButton(bQueen);
		   board[7][5] = new JButton(bBishop);
		   board[7][6] = new JButton(bKnight);
		   board[7][7] = new JButton(bRook);
		   
		   //Creates all of the pawns.
		   for(int row = 0; row < BOARDSIZE; row++) 
			   for(int col = 0; col < BOARDSIZE; col++) {
				   if(row == 1) {
					   board[row][col] = new JButton(wPawn);
				   }
				   
				   else if(row == 7) {
					   board[row][col] = new JButton(bPawn);
				   }
				   add(board[row][col]);
//				   board[row][col].addActionListener(listener);
			   }
		   
	   }

	   
	   /** 
	    * Method that updates the board.
	    */
	   private void displayBoard() {
		   // complete this
	   }
	   
	   /***************************************************************
	    * Puts the board in the starting setup. 
	    * I
	    **************************************************************/
	   private void resetBoard() {
		   
	   }


	   /** 
	    * Inner class that represents action listener for buttons
	    */
	   private class ButtonListener implements ActionListener {

		   @Override
		   public void actionPerformed(ActionEvent e) {

		   }
	   }

}