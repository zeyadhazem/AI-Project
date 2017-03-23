package student_player.mytools;
@SuppressWarnings("hiding")
public class Tuple<Integer,BohnenspielMove> {

  private final Integer bestScore;
  private final BohnenspielMove move;

  public Tuple(Integer bestScore, BohnenspielMove move) {
    this.bestScore = bestScore;
    this.move = move;
  }

  public Integer getBestScore() { 
	  return bestScore; 
  }
  public BohnenspielMove getMove() {
	  return move; 
  }
}