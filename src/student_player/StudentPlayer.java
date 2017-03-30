package student_player;

import java.util.ArrayList;

import boardgame.Move;
import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;
import bohnenspiel.BohnenspielMove.MoveType;
import student_player.mytools.MyTools;
import student_player.mytools.Tuple;

/** A Hus player submitted by a student. */
public class StudentPlayer extends BohnenspielPlayer {

    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super("260556530"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class bohnenspiel.RandomPlayer
     * for another example agent. */
    public BohnenspielMove chooseMove(BohnenspielBoardState board_state)
    {
        // Get the contents of the pits so we can use it to make decisions.
        int[][] pits = board_state.getPits();

        // Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.
        int[] my_pits = pits[player_id];
        int[] op_pits = pits[opponent_id];

        // Use code stored in ``mytools`` package.
        MyTools.getSomething();

        
        // We can see the effects of a move like this...
        Tuple<Integer, BohnenspielMove> miniMax = minimax(9, board_state,0);
        BohnenspielMove move1 = miniMax.getMove();
	    

        // But since this is a placeholder algorithm, we won't act on that information.
        return move1;
    }
    
    
	 /** Recursive minimax at level of depth for either maximizing or minimizing player.
	  	 Return int[3] of {score, row, col}  */
	private Tuple<Integer,BohnenspielMove> minimax(int depth, BohnenspielBoardState board_state, int numberOfTabs) {
	   // Generate possible next moves in a List of int[2] of {row, col}.
		ArrayList<BohnenspielMove> nextMoves = board_state.getLegalMoves();
	
	   // mySeed is maximizing; while oppSeed is minimizing
	   int bestScore = (board_state.getTurnPlayer() == player_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
	   //System.out.println("PLayer ID -> " + board_state.getTurnPlayer());
	   int currentScore;
	   BohnenspielMove bestMove = new BohnenspielMove();
	   
	   
	   if (nextMoves.isEmpty() || depth == 0) {
	      // Gameover or depth reached, evaluate score
	      // bestScore = evaluate();
		   bestScore = board_state.getScore(player_id) - board_state.getScore(opponent_id);
	   } else {
		   for (int i=0; i<nextMoves.size(); i++) {
	         // Try this move for the current "player"
			 BohnenspielBoardState cloned_board_state = (BohnenspielBoardState) board_state.clone();
		     BohnenspielMove move1 = nextMoves.get(i);
		     cloned_board_state.move(move1);
//		     for (int j=0; j<numberOfTabs; j++){
//		    	 System.out.print(">");
//		     }
//		     System.out.print("With Move " + move1.toPrettyString());
		     
	         if (board_state.getTurnPlayer() == player_id) {  // mySeed (computer) is maximizing player
	            currentScore = minimax(depth - 1, cloned_board_state, numberOfTabs+1).getBestScore();
	            if (currentScore > bestScore) {
	               bestScore = currentScore;
	               bestMove = move1;
	            }
	         } else {  // oppSeed is minimizing player
	            currentScore = minimax(depth - 1, cloned_board_state, numberOfTabs+1).getBestScore();
	            if (currentScore < bestScore) {
	               bestScore = currentScore;
	               bestMove = move1;
	            }
	         }
	      }
	   }
	   
	   //System.out.println("BEST MOVE IS " + bestMove.toPrettyString());
	   return new Tuple<Integer, BohnenspielMove> (bestScore, bestMove);
	}
	
	private void printList (ArrayList<BohnenspielMove> list){
    	for (int i=0; i<list.size(); i++){
    		System.out.println(list.get(i).toPrettyString());
    	}
    }
}