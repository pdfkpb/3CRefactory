import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Jethro Masangya on 5/1/2017.
 */
public class GameOngoingState extends RunState {

    @Override
    public void run(Party party, boolean partyAssigned, boolean gameFinished,
                    boolean gameIsHalted, Iterator bowlerIterator, Bowler currentThrower,
                    boolean canThrowAgain, boolean tenthFrameStrike, int ball,
                    Pinsetter setter, int frameNumber, int[][] finalScores, int bowlIndex,
                    int gameNumber, int[][] cumulativeScores, HashMap scores, LaneEvent laneEvent, Vector subscribers) {

        while (gameIsHalted) {
            try {
                sleep(10);
            } catch (Exception e) {}
        }

        if (bowlerIterator.hasNext()) {
            currentThrower = (Bowler)bowlerIterator.next();

            canThrowAgain = true;
            tenthFrameStrike = false;
            ball = 0;
            while (canThrowAgain) {
                setter.ballThrown();		// simulate the thrower's ball hiting
                ball++;
            }

            if (frameNumber == 9){
                finalScores[bowlIndex][gameNumber] = cumulativeScores[bowlIndex][9];
                try{
                    Date date = new Date();
                    String dateString = "" + date.getHours() + ":" + date.getMinutes() + " " + date.getMonth() + "/" + date.getDay() + "/" + (date.getYear() + 1900);
                    ScoreHistoryFile.addScore(currentThrower.getNickName(), dateString, new Integer(cumulativeScores[bowlIndex][9]).toString());
                } catch (Exception e) {System.err.println("Exception in addScore. "+ e );}
            }

            setter.reset();
            bowlIndex++;

        } else {
            frameNumber++;
            resetBowlerIterator(bowlerIterator, party);
            bowlIndex = 0;
            if (frameNumber > 9) {
                gameFinished = true;
                gameNumber++;
            }
        }
    }
}
