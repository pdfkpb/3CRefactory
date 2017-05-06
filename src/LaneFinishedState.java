import java.util.Iterator;
import java.util.Vector;

public class LaneFinishedState extends LaneRunState {

    public LaneFinishedState(){

    }

    @Override
    public void run() {
        EndGamePrompt endGamePrompt = new EndGamePrompt( ((Bowler) party.getMembers().get(0)).getNickName() + "'s Party" );
        int result = endGamePrompt.getResult();
        endGamePrompt.destroy();

        //playAgain = true;

        System.out.println("result was: " + result);

        // TODO: send record of scores to control desk
        if (result == 1) {					// yes, want to play again
            resetScore();
            resetBowlerIterator();

        } else if (result == 2) {           // no, dont want to play another game
            //playAgain = false;

            Vector printVector;
            EndGameReport endGameReport = new EndGameReport( ((Bowler)party.getMembers().get(0)).getNickName() + "'s Party", party);
            printVector = endGameReport.getResult();
            partyAssigned = false;
            Iterator scoreIt = party.getMembers().iterator();
            party = null;
            partyAssigned = false;

            publish(lanePublish());

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
