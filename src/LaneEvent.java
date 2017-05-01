/*  $Id$
 *
 *  Revisions:
 *    $Log: LaneEvent.java,v $
 *    Revision 1.6  2003/02/16 22:59:34  ???
 *    added mechnanical problem flag
 *
 *    Revision 1.5  2003/02/02 23:55:31  ???
 *    Many many changes.
 *
 *    Revision 1.4  2003/02/02 22:44:26  ???
 *    More data.
 *
 *    Revision 1.3  2003/02/02 17:49:31  ???
 *    Modified.
 *
 *    Revision 1.2  2003/01/30 21:21:07  ???
 *    *** empty log message ***
 *
 *    Revision 1.1  2003/01/19 22:12:40  ???
 *    created laneevent and laneobserver
 *
 *
 */

import java.util.HashMap;

public class LaneEvent {

	private Party party;
	private int frame;
	private int ball;
	private Bowler bowler;
	private int[][] cumulativeScores;
	private HashMap score;
	private int index;
	private int frameNum;
	private int[] currentScores;
	private boolean mechProb;
	
	public LaneEvent( Party party, int index, Bowler bowler, int[][] cumulativeScores, HashMap score, int frameNum, int[] currentScores, int ball, boolean mechProblem) {
		this.party = party;
		this.index = index;
		this.bowler = bowler;
		this.cumulativeScores = cumulativeScores;
		this.score = score;
		this.currentScores = currentScores;
		this.frameNum = frameNum;
		this.ball = ball;
		this.mechProb = mechProblem;
	}
	
	public boolean isMechanicalProblem() {
		return mechProb;
	}
	
	public int getFrameNum() {
		return frameNum;
	}
	
	public HashMap getScore( ) {
		return score;
	}


	public int[] getCurrentScores(){
		return currentScores;
	}
	
	public int getIndex() {
		return index;
	}

	public int getFrame( ) {
		return frame;
	}

	public int getBall( ) {
		return ball;
	}
	
	public int[][] getCumulativeScores(){
		return cumulativeScores;
	}

	public Party getParty() {
		return party;
	}
	
	public Bowler getBowler() {
		return bowler;
	}

}
 
