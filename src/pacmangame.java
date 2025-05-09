import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class pacmangame extends JPanel implements ActionListener, KeyListener {
	
	/* creating a class so that all the blocks of game can be made an object of same properties*/ 
	class Block{
		int x;
		int y;
		int startX; // as pacman and ghosts are going to move
		int startY;
		int weidth;
		int height;
		Image image;
		
		char direction = 'U';//default 
		int velocityX = 0;// for static blocks it needs to be zero
		int velocityY = 0;
		
		
		Block(Image image, int x, int y, int height, int weidth){
			this.x = x;
			this.y = y;
			this.height = height;
			this.weidth = weidth;
			this.startX = x;// initial x is going to be the startX
			this.startY = y;
			this.image = image;
		}
		
		void updatedirection(char direction) {
			char prevdirection = this.direction;//storing previous direction
			this.direction = direction;
			updatevelocity();
			this.x += this.velocityX;//take a step forwrd to check for collision
			this.y += this.velocityY;
			for(Block wall : walls) {
				if(collision(this,wall)) {
					this.x -= this.velocityX;
					this.y -= this.velocityY;
					this.direction = prevdirection;
					updatevelocity();//calling it again if there was a collision
				}
			}
		}
		
		void updatevelocity() {
			if(this.direction == 'U') {
				this.velocityX = 0;
				this.velocityY = -tilesize/4;//for upward direction y is decreasing 
			}
			else if(this.direction == 'D') {
				this.velocityX = 0;
				this.velocityY = tilesize/4;
			}
			else if(this.direction == 'R') {
				this.velocityX = tilesize/4;
				this.velocityY = 0;
			}
			else if(this.direction == 'L') {
				this.velocityX = -tilesize/4;
				this.velocityY = 0;
			}
		}
		public void teleport1(int x) {
			this.x = -this.weidth;
		}
		public void teleport2(int x) {
			this.x = columns*tilesize;
		}
		
		void reset() {
			this.x = this.startX;
			this.y = this.startY;
		}//to reset initial positions
		
	}
	
	HashSet<Block> walls; // hashset for better performance although we can use arraylist
	HashSet<Block> foods;
	HashSet<Block> ghosts;
	HashSet<Block> powerfood;
	Block pacman;
	
	Timer gameloop;
	char[] directions = {'U','L','R','D'};
	int score =0;
	int highscore = 0;
	int lives = 3;
	boolean gameover = false;
	
	
	 //X = wall, O = nothing, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
	 private String[] tileMap = {
		        "XXXXXXXXXXXXXXXXXXX",
		        "X        X     f  X",
		        "X XX XXX X XXX XX X",
		        "X                 X",
		        "X XX X XXXXX X XX X",
		        "Xf   X       X    X",
		        "XXXX XXXX XXXX XXXX",
		        "OOOX X       X XOOO",
		        "XXXX X XXrXX X XXXX",
		        "O       bpo       O",
		        "XXXX X XXXXX X XXXX",
		        "OOOX X       X XOOO",
		        "XXXX X XXXXX X XXXX",
		        "X        X        X",
		        "X XX XXX X XXX XX X",
		        "X  X     P     Xf X",
		        "XX X X XXXXX X X XX",
		        "X    X   X   X    X",
		        "X XXXXXX X XXXXXX X",
		        "X  f              X",
		        "XXXXXXXXXXXXXXXXXXX" 
		    };
	
	private int rows = 21;
	private int columns = 19;
	private int tilesize = 32;//32 pixels
	private int breadth_of_board = columns*tilesize;
	private int height_of_board = rows*tilesize;
	
	private Image wallimage;
	
	private Image pacmanupimage;
	private Image pacmandownimage;
	private Image pacmanleftimage;
	private Image pacmanrightimage;
	
	private Image blueghostimage;
	private Image redghostimage;
	private Image orangeghostimage;
	private Image pinkghostimage;
	

	
	Random random = new Random();
	pacmangame(){
		setPreferredSize(new Dimension(breadth_of_board,height_of_board));
		setBackground(Color.BLACK);
		addKeyListener(this);
		setFocusable(true );
		
		
		wallimage = new ImageIcon(getClass().getResource("./wall.png")).getImage();		
		pacmanupimage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
		pacmandownimage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
		pacmanleftimage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
		pacmanrightimage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
		blueghostimage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
		redghostimage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
		pinkghostimage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
		orangeghostimage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
		
		loadmap();
		loadHighScore();
		for(Block Ghost : ghosts) {
			char newdirection = directions[random.nextInt(4)];
			Ghost.updatedirection(newdirection);
		}
		gameloop = new Timer(50,this);
		
	}
	
	// to make the map
	public void loadmap() {
		walls = new HashSet<Block>();
		foods = new HashSet<Block>();
		ghosts = new HashSet<Block>();
		powerfood = new HashSet<Block>();
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < columns; c++) {
				String row = tileMap[r];
				char tileMapchar = row.charAt(c);
				
				int x = c*tilesize;// location of block
				int y = r*tilesize;
				
				if(tileMapchar == 'X') {
					Block wall = new Block(wallimage, x, y, tilesize, tilesize);
					walls.add(wall);					
				}
				else if(tileMapchar == 'r') {
					Block Ghost = new Block(redghostimage, x, y, tilesize, tilesize);
					ghosts.add(Ghost);
				}
				else if(tileMapchar == 'b') {
					Block Ghost = new Block(blueghostimage, x, y, tilesize, tilesize);
					ghosts.add(Ghost);
				}
				else if(tileMapchar == 'o') {
					Block Ghost = new Block(orangeghostimage, x, y, tilesize, tilesize);
					ghosts.add(Ghost);
				}
				else if(tileMapchar == 'p') {
					Block Ghost = new Block(pinkghostimage, x, y, tilesize, tilesize);
					ghosts.add(Ghost);
				}
				else if(tileMapchar == 'P') {
				    pacman = new Block(pacmanrightimage, x, y, tilesize, tilesize);
				}
				else if(tileMapchar == ' ') {
					Block food = new Block(null, x + 14, y + 14, 4, 4);//we want to create a food pellet of size 4x4 pixels 
					foods.add(food);
				}
				else if(tileMapchar == 'f') {
					Block Powerfood = new Block(null, x + 9, y + 9, 14, 14);
					powerfood.add(Powerfood);
				}// no point in writing the code for 'O' BLOCK
			}
		}
	}
	public void paintComponent(Graphics g) {
	     super.paintComponent(g);
	     draw(g);
	     
	}
	
	
	public void draw(Graphics g) {
		int temp = highscore;
		g.drawImage(pacman.image, pacman.x, pacman.y, pacman.weidth, pacman.height, null);
		
		for(Block Ghost : ghosts) {
			g.drawImage(Ghost.image, Ghost.x, Ghost.y, Ghost.weidth, Ghost.height, null);
		}
		for(Block wall : walls) {
			g.drawImage(wall.image, wall.x, wall.y, wall.weidth, wall.height, null);
		}
		
		
		g.setColor(Color.white);
		for(Block food : foods) {
			g.fillRect( food.x, food.y, food.weidth, food.height);
		}
		for(Block Powerfood : powerfood) {
			g.fillRect( Powerfood.x, Powerfood.y, Powerfood.weidth, Powerfood.height);
		}
		g.setFont(new Font("Ariel", Font.PLAIN, 18));
		if(gameover) {
		    checkAndUpdateHighScore(score);
			if(highscore > temp){
				g.drawString("GAME OVER!  Final score:" + String.valueOf(score) + " NEW HIGHSCORE:" + String.valueOf(highscore) , tilesize/2,tilesize/2);
			}
			else{
				g.drawString("GAME OVER!  Final score:" + String.valueOf(score) + " HIGHSCORE:" + String.valueOf(highscore), tilesize/2,tilesize/2);
			}
		}
		else {
			g.drawString("x" + String.valueOf(lives) + " Score :" + String.valueOf(score) + " Highscore" + String.valueOf(highscore) , tilesize/2, tilesize/2);
		}
	}
	// to move the pacman
	public void move() {
		pacman.x += pacman.velocityX;
		pacman.y += pacman.velocityY;
		
		for(Block wall : walls) {
			// if collision is true then negate the velocity which will be equivalent to not moving
			if(collision(pacman,wall)) {
				pacman.x -= pacman.velocityX;
				pacman.y -= pacman.velocityY;
				break;
			}
		}
		
		
		Block foodeaten = null;
		for(Block food : foods) {
			if(collision(pacman,food)) {
				foodeaten = food;
				score +=5;
			}
		}
		foods.remove(foodeaten);
		Block powerfoodeaten = null;
		for(Block Powerfood : powerfood) {
			if(collision(pacman,Powerfood)) {
				powerfoodeaten = Powerfood;
				score +=20;
			}
		}
		powerfood.remove(powerfoodeaten);
		
		if(pacman.x >= columns*tilesize) {
			pacman.teleport1(pacman.x);
		}
		else if(pacman.x <= -pacman.weidth) {
			pacman.teleport2(pacman.x);
		}
		
		if(foods.isEmpty()) {
			gameloop.stop();
		    loadmap();
			resetpositions();
			
		}
	}
	private boolean frozen = false;
    	private long freezeEndTime;

		public void freezeFor(int milliseconds) {
			frozen = true;
			freezeEndTime = System.currentTimeMillis() + milliseconds;
		}
	public void update() {
        if (frozen) {
            if (System.currentTimeMillis() > freezeEndTime) {
                frozen = false;
            } else {
                return; // Skip movement while frozen
            }
        }
		for(Block Ghost : ghosts) {
			if(collision(pacman,Ghost)) {
				lives -=1;
				gameloop.stop();
				if(lives == 0) {
					gameover = true;
					return;
				
				}
				resetpositions();
			}
			if(Ghost.y == 3*tilesize) {
				if(Ghost.x == 4*tilesize || Ghost.x == 6*tilesize || Ghost.x == 8*tilesize|| Ghost.x == 10*tilesize|| Ghost.x == 12*tilesize|| Ghost.x == 14*tilesize) {
					char newdirections = directions[random.nextInt(4)];
					Ghost.updatedirection(newdirections);
				}
			}
			else if(Ghost.y == 5*tilesize) {
				if( Ghost.x == 4*tilesize || Ghost.x == 9*tilesize || Ghost.x == 14*tilesize) {
					char newdirections = directions[random.nextInt(4)];
					Ghost.updatedirection(newdirections);
				}
			}
			else if(Ghost.y == 7*tilesize && Ghost.x == 9*tilesize) {
				char newdirections = directions[random.nextInt(4)];
				Ghost.updatedirection(newdirections);
			}
			else if(Ghost.y == 9*tilesize) {
				if( Ghost.x == 4*tilesize ||  Ghost.x == 6*tilesize|| Ghost.x == 9*tilesize|| Ghost.x == 12*tilesize || Ghost.x == 14*tilesize) {
					char newdirections = directions[random.nextInt(4)];
					Ghost.updatedirection(newdirections);
				}
			}
			else if(Ghost.y == 11*tilesize) {
				if( Ghost.x == 12*tilesize ||  Ghost.x == 6*tilesize) {
					char newdirections = directions[random.nextInt(4)];
					Ghost.updatedirection(newdirections);
				}
			}
			else if(Ghost.y == 13*tilesize) {
				if( Ghost.x == 4*tilesize ||  Ghost.x == 6*tilesize|| Ghost.x == 12*tilesize|| Ghost.x == 14*tilesize) {
					char newdirections = directions[random.nextInt(4)];
					Ghost.updatedirection(newdirections);
				}
			}
			else if(Ghost.y == 15*tilesize) {
				if( Ghost.x == 4*tilesize ||  Ghost.x == 6*tilesize|| Ghost.x == 12*tilesize|| Ghost.x == 14*tilesize) {
					char newdirections = directions[random.nextInt(4)];
					Ghost.updatedirection(newdirections);
				}
			}
			else if(Ghost.y == 17*tilesize) {
				if( Ghost.x == 2*tilesize ||  Ghost.x == 16*tilesize) {
					char newdirections = directions[random.nextInt(4)];
					Ghost.updatedirection(newdirections);
				}
			}
			else if(Ghost.y == 19*tilesize) {
				if( Ghost.x == 8*tilesize ||  Ghost.x == 10*tilesize) {
					char newdirections = directions[random.nextInt(4)];
					Ghost.updatedirection(newdirections);
				}
			}
			
			Ghost.x += Ghost.velocityX;
			Ghost.y += Ghost.velocityY;
			for(Block wall :walls) {
			if(collision(Ghost,wall) || Ghost.x <=0 || Ghost.x >= (columns-1)*tilesize) {
				Ghost.x -= Ghost.velocityX;
				Ghost.y -= Ghost.velocityY;
				char newdirection = directions[random.nextInt(4)];
				Ghost.updatedirection(newdirection);
			}
			}
			
		}

        
    }







	private void loadHighScore() {
    try (BufferedReader br = new BufferedReader(new FileReader("highscore.txt"))) {
        String line = br.readLine();
        if (line != null) {
            highscore = Integer.parseInt(line);
        }
    } catch (IOException e) {
        System.out.println("High score file not found. Starting with high score of 0.");
    }
}
	private void checkAndUpdateHighScore(int currentScore) {
		if (currentScore > highscore) {
			highscore = currentScore;
			saveHighScore();
		}
	}
	private void saveHighScore() {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter("highscore.txt"))) {
        bw.write(String.valueOf(highscore));
    } catch (IOException e) {
        System.out.println("Error saving high score.");
    }
}

	
	public void resetpositions(){
		pacman.reset();
		pacman.velocityX = 0;
		pacman.velocityY = 0;
		for(Block Ghost : ghosts) {
			Ghost.reset();
			char newdirection = directions[random.nextInt(4)];
			Ghost.updatedirection(newdirection);
		}
		
	}
	
	public boolean collision(Block a, Block b) {
		return a.x < b.x + b.weidth &&
			   a.x + a.weidth > b.x &&
			   a.y < b.y +b.height &&
			   a.y + a.height > b.y ;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		move();// we call it for every gameloop timer
		// we first move then redraw the object
		repaint();
		update();
		if(gameover) {
			gameloop.stop();
		}
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	
		gameloop.start();
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			pacman.updatedirection('U');
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			pacman.updatedirection('D');
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			pacman.updatedirection('L');
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			pacman.updatedirection('R');
		}
		
		if(pacman.direction == 'U') {
			pacman.image = pacmanupimage;
		}
		else if(pacman.direction == 'D') {
			pacman.image = pacmandownimage;
		}
		else if(pacman.direction == 'R') {
			pacman.image = pacmanrightimage;
		}
		else if(pacman.direction == 'L') {
			pacman.image = pacmanleftimage;
		}
		
		
		if(gameover) {
			loadmap();
			resetpositions();
			lives = 3;
			score = 0;
			gameover = false;
			gameloop.start();
		}
	}

	
	
}
