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
	
	enum heuristics {
		scoreDifference,
		marginForWinning,
		marginForLosing,
		opponentSeedsOverflow,
		seedsDifference;
	}

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
        
        //Apply min-max
        Tuple<Integer, BohnenspielMove> miniMax = minimax(14, board_state,0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        BohnenspielMove move1 = miniMax.getMove();
        
        // Make sure move is not illegal
        if (move1.getMoveType().equals(BohnenspielMove.MoveType.NOTHING)){
        	System.out.println("Output is an Illegal move");
        	move1 = board_state.getLegalMoves().get(0);
        }
        
        //Applu move
        return move1;
    } 
    
    
	 /** Recursive minimax at level of depth for either maximizing or minimizing player. */
	private Tuple<Integer,BohnenspielMove> minimax(int depth, BohnenspielBoardState board_state, int numberOfTabs, int alpha, int beta) {
	   // Generate possible next moves
		ArrayList<BohnenspielMove> nextMoves = board_state.getLegalMoves();
	
	   // My turn is maximizing but opponent's turn is minimizing
	   int bestScore = (board_state.getTurnPlayer() == player_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
	   int currentScore;
	   BohnenspielMove bestMove = new BohnenspielMove();
	   
	   
	   if (nextMoves.isEmpty() || depth == 0) {
	      // Gameover or depth reached, evaluate score
		   ArrayList <heuristics> heuristicsList = new ArrayList <heuristics>();
		   heuristicsList.add(heuristics.scoreDifference);
		   heuristicsList.add(heuristics.seedsDifference);
		   heuristicsList.add(heuristics.opponentSeedsOverflow);
		   bestScore = evaluate(board_state, heuristicsList);
	   } 
	   else {
		   //For every legal move for the player
		   for (int i=0; i<nextMoves.size(); i++) {
	         // Try this move for the current "player"
			 BohnenspielBoardState cloned_board_state = (BohnenspielBoardState) board_state.clone();
		     BohnenspielMove move1 = nextMoves.get(i);
		     cloned_board_state.move(move1);
		     
	         if (board_state.getTurnPlayer() == player_id) {  // my Turn is maximizing
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
	            
	         } 
	         else {  // opponent is minimizing
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
	   
	   return new Tuple<Integer, BohnenspielMove> (bestScore, bestMove);
	}
	
	private void printList (ArrayList<BohnenspielMove> list){
    	for (int i=0; i<list.size(); i++){
    		System.out.println(list.get(i).toPrettyString());
    	}
    }
	
	private int evaluate (BohnenspielBoardState board_state, ArrayList<heuristics> heuristicsList){
		int score = 0;
		
		for (heuristics heuristic : heuristicsList){	//Check the list of heuristics to evaluate a state
			if (heuristic.equals(heuristics.scoreDifference)){	// myScore - oppScore
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
			else if (heuristic.equals(heuristics.opponentSeedsOverflow)){
				int opponentSeedsOverflow = 0;
		        int[][] pits2 = board_state.getPits();
		        for (int j=0; j<pits2[opponent_id].length; j++){
		        	int extraSeeds = pits2[opponent_id][j] - (6-j) + 1; //17 - 11
		        	if (extraSeeds > 0){
		        		double lapsOfSeeds = (double)extraSeeds/6;	//2.8333 - 1.83333
		        		//One lap for me, one for the opponent
		        		int floorLapsOfSeeds = (int) lapsOfSeeds; //2 - 1 - 3
		        		
		        		if (floorLapsOfSeeds%2 == 0){ //any extra goes to me
		        			int seedMe = 0;
		        			seedMe += (int) (6 * (double)(lapsOfSeeds - (double)floorLapsOfSeeds)); //0.83333
		        			seedMe += (int) (6 * (floorLapsOfSeeds/2)); //6
		        			opponentSeedsOverflow += seedMe;
		        		}
		        		else {
		        			int seedMe = 0;
		        			seedMe += 6 * ((floorLapsOfSeeds/2) + 1);
		        			opponentSeedsOverflow += seedMe;
		        		}
		        	}
		        }
		        score -= (int)((opponentSeedsOverflow));
			}			
		}
		return score;
	}
}
