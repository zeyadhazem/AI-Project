package bohnenspiel;

//import student_player.StudentPlayer.heuristics;
//import student_player.StudentPlayer.heuristics;
//import student_player.StudentPlayer.heuristics;
import student_player.mytools.MyTools;
import student_player.mytools.Tuple;

import java.util.ArrayList;
import java.util.Random;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;

/** A random Bohnenspiel player. */
public class GreedyBohnenspielPlayer extends BohnenspielPlayer {
	
	enum heuristics {
		scoreDifference,
		marginForWinning,
		marginForLosing,
		seedsDifference;
	}
	
    Random rand = new Random();

    public GreedyBohnenspielPlayer() { super("GreedyPlayer"); }

    /** Choose moves randomly. */
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
        Tuple<Integer, BohnenspielMove> miniMax = minimax(11, board_state,0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        BohnenspielMove move1 = miniMax.getMove();
	    

        // But since this is a placeholder algorithm, we won't act on that information.
        return move1;
    }
    
    /** Recursive minimax at level of depth for either maximizing or minimizing player.s
 	 Return int[3] of {score, row, col}  */
	private Tuple<Integer,BohnenspielMove> minimax(int depth, BohnenspielBoardState board_state, int numberOfTabs, int alpha, int beta) {
	  // Generate possible next moves in a List of int[2] of {row, col}.
		ArrayList<BohnenspielMove> nextMoves = board_state.getLegalMoves();
	
	  // mySeed is maximizing; while oppSeed is minimizing
	  int bestScore = (board_state.getTurnPlayer() == player_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
	  int currentScore;
	  BohnenspielMove bestMove = new BohnenspielMove();
	  
	  
	  if (nextMoves.isEmpty() || depth == 0) {
	      // Gameover or depth reached, evaluate score
		   ArrayList <heuristics> heuristicsList = new ArrayList <heuristics>();
		   heuristicsList.add(heuristics.scoreDifference);
		   //heuristicsList.add(heuristics.seedsDifference);
		   //heuristicsList.add(heuristics.marginForWinning);	
		   //heuristicsList.add(heuristics.marginForLosing);
		   
		   bestScore = evaluate(board_state, heuristicsList);
	  } else {
		   for (int i=0; i<nextMoves.size(); i++) {
	        // Try this move for the current "player"
			 BohnenspielBoardState cloned_board_state = (BohnenspielBoardState) board_state.clone();
		     BohnenspielMove move1 = nextMoves.get(i);
		     cloned_board_state.move(move1);
		     
		     
	        if (board_state.getTurnPlayer() == player_id) {  // mySeed (computer) is maximizing player
	           currentScore = minimax(depth - 1, cloned_board_state, numberOfTabs+1, alpha, beta).getBestScore();
	           if (currentScore > bestScore) {
	              bestScore = currentScore;
	              bestMove = move1;
	           }
	           if(currentScore > alpha){
	           	alpha = currentScore;
	           	bestMove = move1;
	           }
	           if (beta <= alpha){
	           	return new Tuple<Integer, BohnenspielMove> (beta, new BohnenspielMove());	//Pruning
	           }
	           
	        } else {  // oppSeed is minimizing player
	           currentScore = minimax(depth - 1, cloned_board_state, numberOfTabs+1, alpha, beta).getBestScore();
	           if (currentScore < bestScore) {
	              bestScore = currentScore;
	              bestMove = move1;
	           }
	           if(currentScore < beta){
	           	beta = currentScore;
	           	bestMove = move1;
	           }
	           if (beta <= alpha){
	           	return new Tuple<Integer, BohnenspielMove> (alpha, new BohnenspielMove());	//Pruning
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
	
	private int evaluate (BohnenspielBoardState board_state, ArrayList<heuristics> heuristicsList){
int score = 0;
		
		for (heuristics heuristic : heuristicsList){
			if (heuristic.equals(heuristics.scoreDifference)){
				score += 10*(board_state.getScore(player_id) - board_state.getScore(opponent_id));
			}
			else if (heuristic.equals(heuristics.seedsDifference)){	
				int opponentSeeds = 0, mySeeds = 0;
		        int[][] pits2 = board_state.getPits();
		        for (int i=0; i<pits2.length; i++){
		        	for (int j=0; j<pits2[0].length; j++){
		        		if (i == opponent_id){ //Opponent Seeds
		        			opponentSeeds += pits2[i][j];
		        		}
		        		else{ //My seeds
		        			mySeeds += pits2[i][j];
		        		}
		        	}
		        }
		        score += (mySeeds - opponentSeeds);
			}
			else if (heuristic.equals(heuristics.marginForWinning)){
				score += -(36 - board_state.getScore(player_id));
			}
			else if (heuristic.equals(heuristics.marginForLosing)){
				score += 36 - board_state.getScore(opponent_id);
			}
//			else if (heuristic.equals(heuristics.opponentSeedsOverflow)){
//				int opponentSeedsOverflow = 0;
//		        int[][] pits2 = board_state.getPits();
//		        for (int j=0; j<pits2[opponent_id].length; j++){
//		        	int extraSeeds = pits2[opponent_id][j] - (6-j) + 1; //17 - 11
//		        	if (extraSeeds > 0){
//		        		double lapsOfSeeds = (double)extraSeeds/6;	//2.8333 - 1.83333
//		        		//One lap for me, one for the opponent
//		        		int floorLapsOfSeeds = (int) lapsOfSeeds; //2 - 1 - 3
//		        		
//		        		if (floorLapsOfSeeds%2 == 0){ //any extra goes to me
//		        			int seedMe = 0;
//		        			seedMe += (int) (6 * (double)(lapsOfSeeds - (double)floorLapsOfSeeds)); //0.83333
//		        			seedMe += (int) (6 * (floorLapsOfSeeds/2)); //6
//		        			opponentSeedsOverflow += seedMe;
//		        		}
//		        		else {
//		        			int seedMe = 0;
//		        			seedMe += 6 * ((floorLapsOfSeeds/2) + 1);
//		        			opponentSeedsOverflow += seedMe;
//		        		}
//		        	}
//		        }
//		        score -= opponentSeedsOverflow;
//			}
//			else if (heuristic.equals(heuristics.opponentSeedsOverflow)){
//				score += 36 - board_state.getScore(opponent_id);
//			}
			
		}
		return score;
	}
}
