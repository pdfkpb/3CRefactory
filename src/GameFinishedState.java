import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class GameFinishedState extends RunState
{
    @Override
    public void run(Party party, boolean partyAssigned, boolean gameFinished, boolean gameIsHalted, Iterator bowlerIterator, Bowler currentThrower, boolean canThrowAgain, boolean tenthFrameStrike, int ball, Pinsetter setter, int frameNumber, int[][] finalScores, int bowlIndex, int gameNumber, int[][] cumulativeScores, HashMap scores, LaneEvent laneEvent, Vector subscribers)
    {
        EndGamePrompt endGamePrompt = new EndGamePrompt( ((Bowler) party.getMembers().get(0)).getNickName() + "'s Party" );
        int result = endGamePrompt.getResult();
        endGamePrompt.destroy();
        endGamePrompt = null;


        System.out.println("result was: " + result);

        // TODO: send record of scores to control desk
        if (result == 1) {					// yes, want to play again
            resetScore(party, scores, gameFinished, frameNumber);
            resetBowlerIterator(bowlerIterator, party);

        } else if (result == 2) {           // no, dont want to play another game
            Vector printVector;
            EndGameReport endGameReport = new EndGameReport( ((Bowler)party.getMembers().get(0)).getNickName() + "'s Party", party);
            printVector = endGameReport.getResult();
            partyAssigned = false;
            Iterator scoreIt = party.getMembers().iterator();
            party = null;
            partyAssigned = false;

            publish(laneEvent, subscribers);

            int myIndex = 0;
            while (scoreIt.hasNext()){
                Bowler thisBowler = (Bowler)scoreIt.next();
                ScoreReport sr = new ScoreReport( thisBowler, finalScores[myIndex++], gameNumber );
                //sr.sendEmail(thisBowler.getEmail());
                Iterator printIt = printVector.iterator();
                while (printIt.hasNext()){
                    if (thisBowler.getNickName().equals(printIt.next())){
                        System.out.println("Printing " + thisBowler.getNickName());
                        sr.sendPrintout();
                    }
                }

            }
        }
    }
}
