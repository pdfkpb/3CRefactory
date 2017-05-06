import java.util.Date;

public class LaneOngoingState extends LaneRunState {

    public LaneOngoingState(){

    }

    @Override
    public void run() {
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
            resetBowlerIterator();
            bowlIndex = 0;
            if (frameNumber > 9) {
                gameFinished = true;
                gameNumber++;
            }
        }
    }
}
