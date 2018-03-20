package ChessW18;

import javax.swing.*;

public class ChessGUI {

 private static JMenu fileMenu;
 private static JMenuItem quitItem;
 private static JMenuItem gameItem;
 private static JMenuBar menus;

 public static void main(String[] args) {
  JFrame frame = new JFrame("Chess Game");
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  fileMenu = new JMenu("File");
  quitItem = new JMenuItem("quit");
  gameItem = new JMenuItem("new game");

  fileMenu.add(gameItem);
  fileMenu.add(quitItem);
  menus = new JMenuBar();
  frame.setJMenuBar(menus);
  menus.add(fileMenu);

  ChessPanel panel = new ChessPanel(quitItem, gameItem);
  frame.getContentPane().add(panel);

  frame.pack();
  frame.setVisible(true);
 }
}
