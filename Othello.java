
/*
* Othello Project 
* ECE-469: Artifical Intelligence
* Othello.java
*
* @author: Raymond Lee
*/



import java.util.*;
import java.util.Scanner.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.Thread;
import java.io.*;
import java.nio.file.*;

public class Othello extends JPanel {

	// Initialize square conditions
	final static int EMPTY = 0;
	final static int ILLEGAL = -1;
	final static int BLACK = 1;
	final static int WHITE = 2;

	// Initialize Players
	Player black = new Player(BLACK);
	Player white = new Player(WHITE);
	private boolean white_has_moves;
	private boolean black_has_moves;

	// Initialize game state
	public Game game;
	public int moves = 1;
	private javax.swing.Timer timer;
    private static int delay;
	private static long startTime, stopTime, runTime = 0;
	public int turn;
	public Move lastPlayerMove;
	public int depthLimit;
	/*
	 * Default constructor will give computer a time limit of 1000ms by default
	 */

	public Othello() {

	}

	/*
	 * Constructor Sets up initial configurations
	 * 
	 * @param delay 	Time the computer gets to "think" about its move
	 */

	public Othello(int mode, int first, String inputFile, int depth_Limit) {
		depthLimit = depth_Limit;

		System.out.println("Depth Limit: " + depthLimit);
		if(inputFile != null) {
			String data = readFileAsString(inputFile);
			Game newGame = createBoardGame(data);
			this.game = newGame;
			// for(int i = 1; i <= 8; i++ ) {
			// 	for(int j = 1; j <= 8; j++) {
			// 		System.out.printf("i: %d, j: %d\tboard[%d][%d] = %d\n", i,j,i,j, this.game.board[i][j]);
			// 	}
			// }
			// initializeGame(newGame);
			// System.out.printf("data: \n%s\n", data);
		} 
		else {
			game = new Game();
			initializeGame(game);
		}
		
		// for(int i = 1; i <= 8; i++ ) {
		// 	for(int j = 1; j <= 8; j++) {
		// 		System.out.printf("i: %d, j: %d\tboard[%d][%d] = %d\n", i,j,i,j, game.board[i][j]);
		// 	}
		// }

		this.turn = first;
		setBackground(Color.GRAY);
		turn = first;
		if(mode == 0) {
			timer = new javax.swing.Timer(1000, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						playGame();
						repaint();
					}
				});
				
				// Create the Start and Stop buttons
				JButton start = new JButton("Start"); 
				start.setBounds(10,20,80,25); 
				add(start);
				start.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent evt){
							timer.start();
						}
					});
	
				JButton stop = new JButton("Stop"); 
				stop.setBounds(10,80,80,25); 
				add(stop);
			stop.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						timer.stop();
					}
				});
		} 
		if(mode == 1) {
			if(first == 1) {
				addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent evt) {
						int x = evt.getX();
						int y = evt.getY();
						int screenWidth = getWidth();
						int screenHeight = getHeight();
						int column = (x*(game.BOARD_WIDTH-2))/screenWidth + 1;
						int row = (y*(game.BOARD_HEIGHT-2))/screenHeight + 1;
	
				black_has_moves = false;
				checker:
				for(int i = 1; i <= 8; i++) {
					for(int j = 1; j <= 8; j++) {
						if(game.legalMove(i, j, BLACK, false)) {
							System.out.println("Legal move for BLACK found!");
							black_has_moves = true;
							break checker;
						}	
					}
				}
				System.out.println("It is BLACK's turn");
				System.out.println("Black has moves? : " + black_has_moves);
						
				if(black_has_moves) {	
				// Black always goes first
				// Black will always flip on the first move
					if(!game.legalMove(row, column, turn, true)) {
						System.out.println("Sorry not a valid move!");
					}	
					else {
						game.board[row][column] = turn;
						repaint();
						System.out.println("\nAFTER BLACK MOVE");
						game.printBoard();
						whiteMove();
						System.out.println("\nAFTER WHITE MOVE");
						game.printBoard();
					}
				}
				else {
					System.out.println("BLACK has no available moves. Please click anywhere on the board to continue.");
					System.out.println("Moving onto WHITE's turn by default.");
					whiteMove();
					System.out.println("\nAFTER WHITE MOVE");
					game.printBoard();
					repaint();
				}
			}
		});
		
			}
			else {
				//Black goes first
				blackMove();
				//moust listens for WHITE
				addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent evt) {
						int x = evt.getX();
						int y = evt.getY();
						int screenWidth = getWidth();
						int screenHeight = getHeight();
						int column = (x*(game.BOARD_WIDTH-2))/screenWidth + 1;
						int row = (y*(game.BOARD_HEIGHT-2))/screenHeight + 1;
	
				white_has_moves = false;
				checker:
				for(int i = 1; i <= 8; i++) {
					for(int j = 1; j <= 8; j++) {
						if(game.legalMove(i, j, WHITE, false)) {
							System.out.println("Legal move for WHITE found!");
							white_has_moves = true;
							break checker;
						}	
					}
				}
				System.out.println("It is WHITE's turn");
				System.out.println("WHITE has moves? : " + white_has_moves);
						
				if(white_has_moves) {	
				// Black always goes first
				// Black will always flip on the first move
					if(!game.legalMove(row, column, turn, true)) {
						System.out.println("Sorry not a valid move!");
					}	
					else {
						game.board[row][column] = turn;
						repaint();
						System.out.println("\nAFTER WHITE MOVE");
						game.printBoard();
						blackMove();
						System.out.println("\nAFTER BLACK MOVE");
						game.printBoard();
					}
				}
				else {
					System.out.println("WHITE has no available moves. Please click anywhere on the board to continue.");
					System.out.println("Moving onto BLACK's turn by default.");
					blackMove();
					System.out.println("\nAFTER BLACK MOVE");
					game.printBoard();
					repaint();
				}
			}
		});
			}
		}
	}	
	public static String readFileAsString(String filename)  {
		String data = "";
		try {
			data = new String(Files.readAllBytes(Paths.get(filename)));
			
		} catch (Exception e) {
			System.out.println("ERROR: Could not read file: " + filename);
		}
		return data;
	}

	public static int getTurn(String textBoard) {
		String removeNewLine = textBoard.replace("\n", "").replace(" ", "");
		String[] board = removeNewLine.split("");
		return Integer.parseInt(board[board.length-1]);
	}

	public static int getDepth(String textBoard) {
		String removeNewLine = textBoard.replace("\n", "").replace(" ", "");
		String[] board = removeNewLine.split("");
		return Integer.parseInt(board[board.length-2]);
	}

	public Game createBoardGame(String textBoard) {
		String removeNewLine = textBoard.replace("\n", "").replace(" ", "");
		String[] board = removeNewLine.split("");
		Game initGame = new Game();
		// for(int i = 0; i < 64; i++) { 
		// 	System.out.println("i: " + i);
		// 	System.out.printf("board[%d]: %s\n", i, board[i]);
		// }
		
		for(int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				// System.out.printf("board[%d]: %s\n",(((i - 1) * 8) + (j - 1)), board[((i - 1) * 8) + (j - 1)]);
				initGame.board[i][j] = Integer.parseInt(board[(((i-1)*8) + (j-1))]);
			}
		}
		return initGame;
	}

	/*
	 * Initialize state of game
	 *
	 * @param game		game state
	 */
	
	public void initializeGame(Game game) {

		// Initialize off board squares
		for(int i = 0; i < game.BOARD_WIDTH; i++) {
			game.board[0][i] = ILLEGAL;
			game.board[game.BOARD_HEIGHT - 1][i] = ILLEGAL;
			game.board[i][0] = ILLEGAL;
			game.board[i][game.BOARD_HEIGHT - 1] = ILLEGAL;
		}

		// Initialize empty game board
		for(int y = 1; y < game.BOARD_HEIGHT - 1; y++) {
			for(int x = 1; x < game.BOARD_WIDTH - 1; x++) {
				game.board[y][x] = EMPTY;
			}
		}

		// Initialize middle pieces
		game.board[4][4] = BLACK;
		game.board[4][5] = WHITE;
		game.board[5][5] = BLACK;
		game.board[5][4] = WHITE;
	}

	// public void premadeGame(Game game) {
	// 	for(int i = 0; i < game.BOARD_WIDTH; i++) {
	// 		game.board[0][i] = ILLEGAL;
	// 		game.board[game.BOARD_HEIGHT - 1][i] = ILLEGAL;
	// 		game.board[i][0] = ILLEGAL;
	// 		game.board[i][game.BOARD_HEIGHT - 1] = ILLEGAL;
	// 	}


	// }

	public void playGame() {
		if (turn == BLACK) {
            blackMove();
            turn = WHITE;
        }
        else {
            whiteMove();
            turn = BLACK;
		}
	}

	/*
	 * Black move
	 */
	public void blackMove() {
		black_has_moves = false;
		checker:
		for(int i = 1; i <= 8; i++) {
			for(int j = 1; j <= 8; j++) {
				if(game.legalMove(i, j, BLACK, false)) {
					System.out.println("Legal move for BLACK found!");
					black_has_moves = true;
					break checker;
				}
			}
		}
		System.out.println("Black has moves? : " + black_has_moves);
		if(!black_has_moves) {
			System.out.println("BLACK has no more legal moves!");
			System.out.println("WHITE turn by default!");
		}
		else {
			game = black.strategy(game, BLACK, !black_has_moves, depthLimit);
			System.out.println("Finished Search");
		}
	}

	/*
	 * White move
	 */
	public void whiteMove() {
		white_has_moves = false;
		checker:
		for(int i = 1; i <= 8; i++) {
			for(int j = 1; j <= 8; j++) {
				if(game.legalMove(i, j, WHITE, false)) {
					System.out.println("Legal moves for WHITE found!");
					white_has_moves = true;
					break checker;
				}
			}
		}
		if(!white_has_moves) {
			System.out.println("WHITE has no more legal moves!");
			System.out.println("BLACK turn by default!");
		}
		else {
			game = white.strategy(game, WHITE, !white_has_moves, depthLimit);
			System.out.println("Finished Search");
		}
		repaint();
	}

	/*
	 *  Draw board and state of current game
	 */
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int whiteCtr = 0;
		int blackCtr = 0;
		int width = getWidth();
		int height = getHeight();
		int xOffset = width/(game.BOARD_WIDTH - 2);
		int yOffset = height/(game.BOARD_HEIGHT - 2);


		// Draw lines
		g.setColor(Color.BLACK);
		for(int i = 1; i < game.BOARD_HEIGHT - 2; i++) {
			g.drawLine(i*xOffset, 0, i*xOffset, height);
			g.drawLine(0, i*yOffset, width, i*yOffset);
		}

		// Draw pieces on board
		for(int i = 1; i < game.BOARD_HEIGHT - 1; i++) {
			for(int j = 1; j < game.BOARD_WIDTH - 1; j++) {
				if(game.board[i][j] == WHITE) {
					g.setColor(Color.WHITE);
					g.fillOval((j*yOffset) - yOffset + 7, (i*xOffset) - xOffset+7, 50, 50);
					whiteCtr++;
				}
				if(game.board[i][j] == BLACK) {
					g.setColor(Color.BLACK);
					g.fillOval((j*yOffset) - yOffset + 7, (i*xOffset) - xOffset+7, 50, 50);
					blackCtr++;
				}

				// Show legal moves for player
				if(turn == BLACK && game.legalMove(i,j,BLACK, false)) {
					g.setColor(Color.BLACK);
					g.fillOval((j*yOffset+29) - yOffset, (i*xOffset+29) - xOffset, 6, 6);
				}
				if(turn == WHITE && game.legalMove(i,j, WHITE, false)) {
					g.setColor(Color.WHITE);
					g.fillOval((j*yOffset+29) - yOffset, (i*xOffset+29) - xOffset, 6, 6);
				}
			}
		}
		
		// Check for available moves
		g.setColor(Color.RED);

		System.out.printf("WHITE: %d\nBLACK: %d\n", whiteCtr, blackCtr);
		if(((whiteCtr + blackCtr) == 64) || (!black_has_moves && !white_has_moves)) {
			if(whiteCtr > blackCtr) {
				g.drawString("White wins with " + whiteCtr + " pieces.", 10, 20);
			}
			else if(blackCtr > whiteCtr) {
				g.drawString("Black wins with " + blackCtr + " pieces", 10, 20);
			}
			else {
				g.drawString("Game ended with a tie!", 10, 20);
			}
		}
		else {
			if(whiteCtr > blackCtr) {
				g.drawString("White currently in the lead with " + whiteCtr + " discs.", 10, 20);
			}
			else if (blackCtr > whiteCtr){
				g.drawString("Black currently in the lead with " + blackCtr + " discs.", 10 ,20);
			}
			else {
				g.drawString("Currently tied.", 10, 20);
			}
		}
	}

		
	/*
	 * Sleep method wrapper
	 */
	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
			//TODO: handle exception
		}
	}	
	/*
	 * Main Method
	 */
	public static void main(String[] args) {
		Othello content;
		int mode;
		int color;
		int depthLimit;
		char premadeBoard;
		String fileName = "";
		System.out.println("Welcome to ReversiAI!");
		Scanner input = new Scanner(System.in);
		// Prompt for Pre-set board
		System.out.printf("Would you like to start with a preset board?\nPlease type either 'Y' / 'N'\n");
			premadeBoard = input.next().charAt(0);
			System.out.printf("premadeBoard: %c\n", premadeBoard);
			if(premadeBoard == 'Y') {
				System.out.println("Please enter text file in which your pre-made board is defined: ");
				fileName = input.next();
				System.out.println("File name: " + fileName);
			}
			else {
				System.out.printf("You have NOT decided to add a pre-made board.\n");
			}
		System.out.printf("Enter [0] for Computer vs Computer\nEnter [1] for Computer vs Human\n");
		mode = input.nextInt();
		System.out.printf("Please enter the maximum search depth for the AI: ");
		depthLimit =  input.nextInt();
		if(mode == 1) {
			System.out.printf("You have selected to play against the computer.\n");
			if(premadeBoard == 'Y') {
				String data = readFileAsString(fileName);
				color = getTurn(data);
				depthLimit = getDepth(data);
			} 
			else {
				System.out.printf("Enter [1] to start as BLACK or [2] to start as WHITE.\n");
				color = input.nextInt();
				if(color == 1) {
					System.out.printf("You have selected to play as BLACK!\n");
				}
				else if(color == 2) {
					System.out.printf("You have selected to play as WHITE!\n");
				}
				else {
					System.out.printf("You have not put a valid color selecting integer.\nYou will play as BLACK by default.\n");
					color = 1;
				}
			}
		}
		else {
			System.out.printf("You have selected to play computer vs computer!\n");
			if(premadeBoard == 'Y') {
				String data = readFileAsString(fileName);
				color = getTurn(data);
				depthLimit = getDepth(data);
			} 
			else {
				System.out.printf("Enter [1] to start as BLACK or [2] to start as WHITE.\n");
				color = input.nextInt();
				if(color == 1) {
					System.out.printf("You have selected to play as BLACK!\n");
				}
				else if(color == 2) {
					System.out.printf("You have selected to play as WHITE!\n");
				}
				else {
					System.out.printf("You have not put a valid color selecting integer.\nYou will play as BLACK by default.\n");
					color = 1;
				}
			}
		}
		
		if(fileName != "")  {
			content = new Othello(mode, color, fileName, depthLimit);
		}
		else {
			content = new Othello(mode, color, null, depthLimit);
		}
		
		JFrame window = new JFrame("Reversi AI");
		window.setContentPane(content);
		window.setSize(530,557);
		window.setLocation(100,100);
		window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		window.setVisible(true);
	}

}