/*
* Othello Project 
* ECE-469: Artifical Intelligence
* Player.java
*
*
* @author: Raymond Lee
*/

/*
 * This class serves to specify the available moves and attributes
 * of the player using white pieces
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Player {

	final static int[][] heuristic = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 200, -100, 100, 50, 50, 100, -100, 200, 0},
			{0, -100, -200, -50, -50, -50, -50, -200, -100, 0},
			{0, 100, -50, 100, 0, 0, 100, -50, 100, 0},
			{0, 50, -50, 0, 0, 0, 0, -50, 50, 0},
			{0, 50, -50, 0, 0, 0, 0, -50, 50, 0},
			{0, 100, -50, 100, 0, 0, 100, -50, 100, 0},
			{0, -100, -200, -50, -50, -50, -50, -200, -100, 0},
			{0, 200, -100, 100, 50, 50, 100, -100, 200, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
	};
	final static int BLACK = 1;
	final static int WHITE = 2;
	final double INFINITE = 100000000;
	final double POS_WEIGHT = 361;
	final double MOBILITY_WEIGHT = 123;
	final double END_WEIGHT = 750;
	final int MAX_DEPTH = 8;
	int turn;
	int opp;

	Move abpBestMove;

	public Player(int color) {
		turn = color;
		if(turn == BLACK) {
			opp = WHITE;
		}
		else if(turn == WHITE) {
			opp = BLACK;
		}
	}

	public Game strategy(Game game, int color, boolean done, int time) {
		return alphaBetaStrategy(game, done, color, time);
	}	


	public Game randStrategy(Game game, int color) {

		int row = (int)(Math.random()*(game.BOARD_HEIGHT-2)) + 1;
		int column = (int)(Math.random()*(game.BOARD_WIDTH-2)) + 1;
			
		while (!game.legalMove(row,column,color,true)) {
				row = (int)(Math.random()*(game.BOARD_HEIGHT-2)) + 1;
				column = (int)(Math.random()*(game.BOARD_WIDTH-2)) + 1;
		}
			
		game.board[row][column] = color;
			
		return game;
	}

	public Game alphaBetaStrategy(Game game, boolean done, int color, int time) {
		if(!done) {
			double startTime = new Date().getTime();

			Node currentState = new Node(new Move(), game, color);
			currentState = buildTree(currentState, MAX_DEPTH);

			double endTime = new Date().getTime();

			System.out.println("Time to build game tree: " + (endTime - startTime)/1000 + " seconds (s)");
			// printTree(currentState);
			int abp;
			if(color == WHITE) {
				abp = white_alphaBetaPruning(currentState, -500000, 500000, time, color);
			}
			else {
				abp = black_alphaBetaPruning(currentState, -500000, 500000, time, color);
			}
			
			if(abpBestMove.legal) {
				game.pointMove(abpBestMove.y, abpBestMove.x, color, true, heuristic);
				game.board[abpBestMove.y][abpBestMove.x] = color;
			}
		}

		return game;
	}


	public int white_alphaBetaPruning(Node root, int alpha, int beta, int depth, int color) {
		int retVal;
		Move tmp = new Move();
		if(depth == 0 || root.children.size() == 0 /* || root.result != -1 */) {

			// System.out.println("ABP caught in IF statement.");
			// System.out.println("root.position: " + root.position);

			return root.position;
		
		}

		if(color == turn) {
			retVal = (int)-INFINITE;
			maxLoop:
			for(Node n : root.children) {
				retVal = Math.max(n.position, white_alphaBetaPruning(n, alpha, beta, depth -1 , opp));
				// alpha = Math.max(alpha, retVal);
				if(alpha < retVal) {
					alpha = retVal;
					tmp = n.lastMove;
				}
				if(alpha >= beta) {
					break maxLoop;
				}
			}
			abpBestMove = tmp;
			return alpha;
		}

		else {
			retVal = (int)INFINITE;
			minLoop:
			for(Node n : root.children) {
				retVal = Math.min(n.position, white_alphaBetaPruning(n, alpha, beta, depth-1 ,turn));
				if(beta > retVal) {
					beta = retVal;
					tmp = n.lastMove;
				}

				if(alpha >= beta) {
					break minLoop;
				}
			}
			abpBestMove = tmp;
			return beta;
		}
	}

	public int black_alphaBetaPruning(Node root, int alpha, int beta, int depth, int color) {
		int retVal;
		Move tmp = new Move();
		if(depth == 0 || root.children.size() == 0 /* || root.result != -1 */) {
			// System.out.println("ABP caught in IF statement.");
			// System.out.println("root.position: " + root.position);
			return -root.position;
		}

		if(color == turn) {
			retVal = (int)-INFINITE;
			maxLoop:
			for(Node n : root.children) {
				retVal = Math.max(n.position, black_alphaBetaPruning(n, alpha, beta, depth -1 , opp));
				// alpha = Math.max(alpha, retVal);
				if(alpha < retVal) {
					alpha = retVal;
					tmp = n.lastMove;
				}
				if(alpha >= beta) {
					break maxLoop;
				}
			}
			abpBestMove = tmp;
			return alpha;
		}

		else {
			retVal = (int)INFINITE;
			minLoop:
			for(Node n : root.children) {
				retVal = Math.min(n.position, black_alphaBetaPruning(n, alpha, beta, depth-1 ,turn));
				if(beta > retVal) {
					beta = retVal;
					tmp = n.lastMove;
				}

				if(alpha >= beta) {
					break minLoop;
				}
			}
			abpBestMove = tmp;
			return beta;
		}
	}
	
	public Node buildTree(Node parent, int depth) {

		// Check to see if there is still more to expand and if the game isn't done
		if (depth > 0 && parent.result == -1) {

			// Decrease the depth count
			depth--;
			
			// Determine the subsequent turn ahead of time
			int nextTurn;
			if (parent.turn == Game.BLACK) {
					nextTurn = Game.WHITE;
			}
			else {
				nextTurn = Game.BLACK;
			}


			// Searches for legal moves
			for (int i = 1; i <= 8; i++)
			{
				for (int j = 1; j <= 8; j++)
				{	
					// Store the current move being checked
					Move currentMove = parent.state.pointMove(i, j, parent.turn, false, heuristic);
					
					// If it is legal
					if (currentMove.legal) {	
						// Create a Game object that attempts the current move
						Game futureGame = new Game(parent.state);
						futureGame = makeMove(futureGame, false, parent.turn, currentMove);
						
						// Create a Node that holds the current move, the future game
						Node newNode = new Node(currentMove, futureGame, nextTurn);
						
						// The position value of the new node is the number of points gained by the move leading up to that node
						newNode.position = currentMove.points;
						// Check the number of potential moves of the future game
						newNode.nextMoves = mobilityCheck(futureGame, nextTurn);
						// Checked whether or not the future game has ended
						newNode.result =  endCheck(futureGame);

						parent.addChild(newNode);
						newNode.parent = parent;
					}
				}
			}
		
			// Check again to see if there is still more to expand
			if (depth > 0)
			{
				// Build a sub tree for each child
				Collections.shuffle(parent.children);
				for (Node n : parent.children)
				{
					n = buildTree(n, depth - 1) ;
				}
			}
			
			// Calculate mobility while recursing backwards
			parent.nextMoves = parent.children.size();
		}
		return parent;
	}

	public Game makeMove(Game game, boolean done, int color, Move move) {
		if (!done) {
			if (move.legal) {
				game.pointMove(move.y, move.x, color, true, heuristic);
				game.board[move.y][move.x] = color;
			}
		}
      return game;
  }



	public int mobilityCheck(Game game, int color) {
		int result = 0;

		for (int i=1; i<game.BOARD_HEIGHT-1; i++) {
			for (int j=1; j<game.BOARD_WIDTH-1; j++) {
				if ((game.legalMove(i, j, color, false))) {
					result++;
				}
			}
		}

		return result;
	}

	public int endCheck(Game game) {
		int result = -1;
		int whiteSum = 0;
		int blackSum = 0;

		for (int i=1; i < game.BOARD_HEIGHT-1; i++) {
			for (int j=1; j<game.BOARD_WIDTH-1; j++) {
				if ((game.legalMove(i, j, Game.BLACK, false)) || (game.legalMove(i, j, Game.WHITE, false))) {
					result = -1;
					return result;
				}
				
				if (game.board[i][j] == Game.BLACK) {
					blackSum++;
				}
				else if (game.board[i][j] == Game.WHITE) {
					whiteSum++;
				}
			}
		}

		if (blackSum > whiteSum) {
			result = Game.BLACK;
		}
		else if (whiteSum > blackSum) {
			result = Game.WHITE;
		}
		else {
			result = 0;
		}
		
		return result;
	}

	public void printTree(Node parent) {
		ArrayList<Node> q = new ArrayList<Node>();
		Node temp = parent;
		for (Node n : temp.children)
		{
			q.add(n);
		}

		System.out.println("Parent Node: " + temp.lastMove.x + " " + temp.lastMove.y);

		while (!q.isEmpty())
		{
		
			temp = (Node)q.remove(0);
			System.out.println("Node: " + temp.lastMove.x + " " + temp.lastMove.y +
								" (Parent: " + temp.parent.lastMove.x + " " + temp.parent.lastMove.y + ")");
			System.out.println("Position: " + temp.position);
			System.out.println("Mobility: " + temp.nextMoves);
			System.out.println("End: " + temp.result);
			
			for (Node n : temp.children)
			{
				q.add(n);
			}
		}
	}


	public void printScores() {
		for (int i = 0; i <= 9; i++) {
			for (int j = 0; j <= 9; j++) {
				System.out.print("[" + heuristic[i][j] + "]");
			}
			System.out.println();
		}
	}

}