import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Jethro Masangya on 5/1/2017.
 */
public abstract class RunState extends Thread{

    public void resetBowlerIterator(Iterator bowlerIterator, Party party){
        bowlerIterator = (party.getMembers()).iterator();
    }

    public void resetScore(Party party, HashMap scores, Boolean gameFinished, int frameNumber){
        Iterator bowlIt = (party.getMembers()).iterator();

        while ( bowlIt.hasNext() ) {
            int[] toPut = new int[25];
            for ( int i = 0; i != 25; i++){
                toPut[i] = -1;
            }
            scores.put( bowlIt.next(), toPut );
        }

        gameFinished = false;
        frameNumber = 0;
    }

    public void publish(LaneEvent laneEvent, Vector subscribers){
        if( subscribers.size() > 0 ) {
            Iterator eventIterator = subscribers.iterator();

            while ( eventIterator.hasNext() ) {
                ( (LaneObserver) eventIterator.next()).receiveLaneEvent( laneEvent );
            }
        }
    }

    public abstract void run(Party party, boolean partyAssigned, boolean gameFinished, boolean gameIsHalted,
                             Iterator bowlerIterator, Bowler currentThrower, boolean canThrowAgain,
                             boolean tenthFrameStrike, int ball, Pinsetter setter, int frameNumber,
                             int[][] finalScores, int bowlIndex, int gameNumber, int[][] cumulativeScores);
}
