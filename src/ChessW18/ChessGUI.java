package ChessW18;

import javax.swing.*;

public class ChessGUI {

 private static JMenu fileMenu;
 private static JMenuItem quitItem;
 private static JMenuItem gameItem;
 private static JMenuItem undoMove;
 private static JMenuBar menus;

 public static void main(String[] args) {
  JFrame frame = new JFrame("Chess Game");
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

  fileMenu = new JMenu("File");
  quitItem = new JMenuItem("quit");
  gameItem = new JMenuItem("new game");
  undoMove = new JMenuItem("Undo Move");
  JCheckBoxMenuItem colorBlind = new JCheckBoxMenuItem("Color Blind Mode");

  fileMenu.add(gameItem);
  fileMenu.add(quitItem);
  fileMenu.add(colorBlind);
  menus = new JMenuBar();
  frame.setJMenuBar(menus);
  menus.add(fileMenu);
  menus.add(undoMove);

  ChessPanel panel = new ChessPanel(quitItem, gameItem, colorBlind, undoMove);
  frame.getContentPane().add(panel);

  frame.pack();
  frame.setVisible(true);
 }
}
