import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Jethro Masangya on 5/1/2017.
 */
public abstract class RunState extends Thread
{
    protected Party party;
    protected boolean partyAssigned, gameFinished, gameIsHalted;
    protected Iterator bowlerIterator;
    protected int ball;
    protected int bowlIndex;
    protected int frameNumber;
    protected boolean tenthFrameStrike;

    protected int[] currentScores;
    protected int[][] cumulativeScores;
    protected boolean canThrowAgain;

    protected int[][] finalScores;
    protected int gameNumber;
    protected Bowler currentThrower;
    protected Pinsetter setter;
    protected HashMap scores;
    protected Vector subscribers;

    protected boolean playAgain;

    public void resetBowlerIterator(){
        this.bowlerIterator = (this.party.getMembers()).iterator();
    }

    public void resetScore(){
        Iterator bowlIt = (party.getMembers()).iterator();

        while ( bowlIt.hasNext() ) {
            int[] toPut = new int[25];
            for ( int i = 0; i != 25; i++){
                toPut[i] = -1;
            }
            this.scores.put( bowlIt.next(), toPut );
        }

        this.gameFinished = false;
        this.frameNumber = 0;
    }

    public void publish(LaneEvent laneEvent){
        if( subscribers.size() > 0 ) {
            Iterator eventIterator = subscribers.iterator();

            while ( eventIterator.hasNext() ) {
                ( (LaneObserver) eventIterator.next()).receiveLaneEvent( laneEvent );
            }
        }
    }

    public void setParty(Party party, boolean partyAssigned)
    {
        this.party = party;
        this.partyAssigned = partyAssigned;
    }

    public void setGameFlags(boolean gameFinished, boolean gameIsHalted)
    {
        this.gameFinished = gameFinished;
        this.gameIsHalted = gameIsHalted;
    }

    public void setBowlerInformation(Iterator bowlerIterator, Bowler currentThrower, boolean canThrowAgain, int bowlIndex)
    {
        this.bowlerIterator = bowlerIterator;
        this.currentThrower = currentThrower;
        this.canThrowAgain = canThrowAgain;
        this.bowlIndex = bowlIndex;
    }

    public void setFrameInformation(boolean tenthFrameStrike, int frameNumber)
    {
        this.tenthFrameStrike = tenthFrameStrike;
        this.frameNumber = frameNumber;
    }

    public void setScoreInformation(int[][] finalScores, int[][] cumulativeScores, HashMap scores)
    {
        this.finalScores = finalScores;
        this.cumulativeScores = cumulativeScores;
        this.scores = scores;
    }

    public void setLaneInformation(int ball, Pinsetter setter)
    {
        this.ball = ball;
        this.setter = setter;
    }

    public void setGameInformation(int gameNumber, Vector subscribers)
    {
        this.gameNumber = gameNumber;
        this.subscribers = subscribers;
    }

    public abstract void run(LaneEvent laneEvent);
}
