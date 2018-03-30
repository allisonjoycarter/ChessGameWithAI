package ChessW18;

import javax.swing.*;

/**********************************************************************
 * The main class used to run the chess program and holds the JFrame
 * and its MenuBar
 *
 * @author George
 *********************************************************************/
public class ChessGUI {


 public static void main(String[] args) {
  JFrame frame = new JFrame("Chess Game");
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

  JMenu fileMenu = new JMenu("File");
  JMenuItem quitItem = new JMenuItem("Quit");
  quitItem.addActionListener(e -> System.exit(0));
  JMenuItem gameItem = new JMenuItem("New Game");
  JMenuItem saveGame = new JMenuItem("Save Game");
  JMenuItem loadGame = new JMenuItem("Load Game");
  JCheckBoxMenuItem colorBlind = new JCheckBoxMenuItem("Color Blind Mode");

  JMenuItem undoMove = new JMenuItem("Undo Move");
  JCheckBoxMenuItem enableAI = new JCheckBoxMenuItem("Enable AI");

  fileMenu.add(gameItem);
  fileMenu.add(quitItem);
  fileMenu.add(saveGame);
  fileMenu.add(loadGame);
  fileMenu.add(colorBlind);

  JMenuBar menus = new JMenuBar();
  frame.setJMenuBar(menus);
  menus.add(fileMenu);
  menus.add(undoMove);
  menus.add(enableAI);

  ChessPanel panel = new ChessPanel(gameItem, saveGame, loadGame, colorBlind, undoMove, enableAI);

  frame.getContentPane().add(panel);

  frame.pack();
  frame.setVisible(true);
 }
}
