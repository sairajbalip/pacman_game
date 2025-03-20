import javax.swing.ImageIcon;
import javax.swing.JFrame;


public class App {
public static void main(String args[]) {
	int rows = 21;
	int columns = 19;
	int tilesize = 32;//32 pixels
	int breadth_of_board = columns*tilesize;
	int height_of_board = rows*tilesize;
	
	
	JFrame frame = new JFrame("pacman game");
	frame.setSize(breadth_of_board, height_of_board);
	frame.setResizable(false);
	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	ImageIcon image = new ImageIcon("pngegg.png");
	frame.setIconImage(image.getImage());
	frame.setVisible(true);
	
	pacmangame pacman = new pacmangame();
	frame.add(pacman);
	pacman.requestFocus();
	frame.pack();
}
}
