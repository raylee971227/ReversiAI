/*
* Othello Project 
* ECE-469: Artifical Intelligence
* Node.java
*
*
* @author: Raymond Lee
*/


import java.util.ArrayList;

public class Node {
  Move lastMove;
  Game state;
  int turn;

  int position = 0;
  int nextMoves = 0;

  /*
   * -1 -> game is not over
   * 0 -> game tied
   * 1 -> BLACK wins game
   * 2 -> WHITE wins game
   * 
   */
  int result = -1;

  Node parent;
  // Children
  ArrayList<Node> children = new ArrayList<Node>();

  public Node(Move lastMove, Game state, int turn) {
    this.lastMove = lastMove;
    this.state = state;
    this.turn = turn;
  }

  public void addChild(Node newNode) {
    if(children != null) {
      children.add(newNode);
    }
    else {
      System.out.println("ERROR: Children array does not exist!");
    }
  }
}