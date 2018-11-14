/*
* Othello Project 
* ECE-469: Artifical Intelligence
* Game.java
*
*
* @author: Raymond Lee
*/

/*
* This class serves to specify the state of the game board.
*/

public class Game {
	final static int EMPTY = 0;
	final static int ILLEGAL = -1;
	final static int BLACK = 1;
	final static int WHITE = 2;
	final static int BOARD_WIDTH = 10;
	final static int BOARD_HEIGHT = 10;
	final int board[][] = new int[BOARD_HEIGHT][BOARD_WIDTH];

	/*
	 * Default board constructor
	 */

	public Game() {

	}

	/*
	 * Game constructor creates an instance of a board by copying the board given
	 * from 'game'.
	 * 
	 * @param game game board of 'game';
	 */

	public Game(Game game) {

		for (int y = 0; y < BOARD_HEIGHT; y++) {
			for (int x = 0; x < BOARD_WIDTH; x++) {
				this.board[y][x] = game.board[y][x];
			}
		}

	}


	public boolean legalMove(int row, int column, int color, boolean flip) {
		// return value
		boolean legal = false;

		// Check if cell is filled
		// If filled, move is not legal
		if (board[row][column] == 0) {
			int curX;
			int curY;
			boolean found;
			int current;

			// Search in each of 9 directions
			for (int x = -1; x < 2; x++) {
				for (int y = -1; y < 2; y++) {
					curY = row + y;
					curX = column + x;
					found = false;
					current = board[curY][curX];

					// Check if current position is empty or filled with current player's piece
					// If either, keep searching until it finds opponent's piece
					if (current == -1 || current == 0 || current == color) {
						continue;
					}

					// Check along direction if opponent piece found
					while (!found) {
						curY += y;
						curX += x;
						current = board[curY][curX];

						if (current == color) {
							found = true;
							legal = true;

							// Reverse the direction and flip pieces until at the original position
							if (flip) {
								// Set reverse direction
								curY -= y;
								curX -= x;
								current = board[curY][curX];

								while (current != 0 && current != color) {
									board[curY][curX] = color;
									curY -= y;
									curX -= x;
									current = board[curY][curX];
								}

							}
						}
						// Reached end of board or empty space
						// Go back to loop to find new direction
						else if (current == -1 || current == 0) {
							found = true;
						}
					}
				}
			}
		}
		return legal;
		
	}
	/*
	 * Assign points to potential move
	 */

	public Move pointMove(int r, int c, int color, boolean flip, int[][] point) 
	{
		// Initialize a default Move object
		Move newMove = new Move();
		
		if (board[r][c] == 0) {
			int posX;
			int posY;
			boolean found;
			int current;
			int sum;
			
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					posX = c + x;
					posY = r + y;
					found = false;
					current = board[posY][posX];
					sum = 0;
					
					if (current == -1 || current == 0 || current == color) {
						continue;
					}
					else {
						// First piece is an enemy so add to the point count
						sum += point[posY][posX];
					}
					
					while (!found) {
						posX += x;
						posY += y;
						current = board[posY][posX];
						
						if (current == color) {
							found = true;
							newMove.legal = true;
							newMove.x = c;
							newMove.y = r;
							newMove.points += point[c][r];
				
							if (flip) {
								posX -= x;
								posY -= y;
								current = board[posY][posX];
								
								while(current != 0) {
									board[posY][posX] = color;
									posX -= x;
									posY -= y;
									current = board[posY][posX];
								}
							}
						}
						else if (current == -1 || current == 0) {
							// The pieces in this direction won't be flipped so reset sum to 0
							sum = 0;
							found = true;
						}
						else  {
							// Piece is an enemy so add to the point count
							sum += point[posY][posX];
						}
					}
					
					// Done checking this direction so add the sum to the Move object point co
					newMove.points += sum;
				}
			}		
		}
        return newMove;
    }
	/*
	 * Print board method for debugging purposes
	 */

	public void printBoard() {

		for (int i = 1; i < BOARD_HEIGHT - 1; i++) {
			for (int j = 1; j < BOARD_WIDTH - 1; j++) {
				System.out.print("[" + board[i][j] + "]");
			}
			System.out.println();
		}
	}

}