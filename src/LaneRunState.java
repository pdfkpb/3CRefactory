import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Jethro Masangya on 5/1/2017.
 */
public abstract class LaneRunState extends Thread implements PinsetterObserver
{
    protected Party party;
    protected Pinsetter setter;
    protected HashMap scores;
    protected Vector subscribers;

    protected boolean gameIsHalted;

    protected boolean partyAssigned;
    protected boolean gameFinished;
    protected Iterator bowlerIterator;
    protected int ball;
    protected int bowlIndex;
    protected int frameNumber;
    protected boolean tenthFrameStrike;

    protected int[] currentScores;
    protected int[][] cumulativeScores;
    protected boolean canThrowAgain;

    protected int[][] finalScores;
    protected int gameNumber = 0;

    protected Bowler currentThrower;


    protected boolean playAgain;


    public abstract void run();

    public void receivePinsetterEvent(PinsetterEvent pe) {
        if (pe.pinsDownOnThisThrow() >=  0) {			// this is a real throw
            markScore(currentThrower, frameNumber + 1, pe.getThrowNumber(), pe.pinsDownOnThisThrow());

            // next logic handles the ?: what conditions dont allow them another throw?
            // handle the case of 10th frame first
            if (frameNumber == 9) {
                if (pe.totalPinsDown() == 10) {
                    setter.resetPins();
                    if(pe.getThrowNumber() == 1) {
                        tenthFrameStrike = true;
                    }
                }

                if (((pe.totalPinsDown() != 10) && (pe.getThrowNumber() == 2 && !tenthFrameStrike)) || (pe.getThrowNumber() == 3)) {
                    canThrowAgain = false;
                    //publish( lanePublish() );
                }

//					if (pe.getThrowNumber() == 3) {
//						canThrowAgain = false;
                //publish( lanePublish() );
//					}
            } else { // its not the 10th frame

                if ((pe.pinsDownOnThisThrow() == 10) || (pe.getThrowNumber() == 2)) {		// threw a strike
                    canThrowAgain = false;
                    //publish( lanePublish() );
//					} else if (pe.getThrowNumber() == 2) {
//						canThrowAgain = false;
//						//publish( lanePublish() );
                } else if (pe.getThrowNumber() == 3)
                    System.out.println("I'm here...");
            }
        }
//            else {								//  this is not a real throw, probably a reset
//			}
    }

    public void resetBowlerIterator(){
        this.bowlerIterator = (party.getMembers()).iterator();
    }

    public void resetScore(){
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

    public void assignParty( Party theParty ) {
        party = theParty;
        resetBowlerIterator();
        partyAssigned = true;

        currentScores = new int[party.getMembers().size()];
        cumulativeScores = new int[party.getMembers().size()][10];
        finalScores = new int[party.getMembers().size()][128]; //Hardcoding a max of 128 games, bite me.
        gameNumber = 0;

        resetScore();
    }

    private void markScore( Bowler currentBowler, int frame, int ball, int score ){
        int[] currentScore;
        int index =  ( (frame - 1) * 2 + ball);

        currentScore = (int[]) scores.get(currentBowler);

        currentScore[index - 1] = score;
        scores.put(currentBowler, currentScore);
        getScore(currentBowler, frame);
        publish(lanePublish());
    }

    protected LaneEvent lanePublish(  ) {
        return new LaneEvent(party, bowlIndex, currentThrower, cumulativeScores, scores, frameNumber+1, currentScores, ball, gameIsHalted);
    }

    private int getScore( Bowler bowler, int frame) {
        int[] curScore;
        int strikeBalls = 0;
        int totalScore = 0;
        curScore = (int[]) scores.get(bowler);

        for (int i = 0; i != 10; i++){
            cumulativeScores[bowlIndex][i] = 0;
        }

        int current = 2*(frame - 1)+ball-1;

        //Iterate through each ball until the current one.
        for (int i = 0; i != current+2; i++){
            //Spare:
            if( (i%2 == 1) && (curScore[i - 1] + curScore[i] == 10) && (i < current - 1) && (i < 19)){
                //This ball was a the second of a spare.
                //Also, we're not on the current ball.
                //Add the next ball to the ith one in cumul.
                cumulativeScores[bowlIndex][(i/2)] += curScore[i+1] + curScore[i];
            }
            else if( (i < current) && (i%2 == 0) && (curScore[i] == 10)  && (i < 18)){
                strikeBalls = 0;
                //This ball is the first ball, and was a strike.
                //If we can get 2 balls after it, good add them to cumul.
                if (curScore[i+2] != -1) {
                    strikeBalls = 1;
                    if( (curScore[i+3] != -1) || (curScore[i+4] != -1) ) {
                        //Still got em.
                        strikeBalls = 2;
                    }
                }
                if (strikeBalls == 2){
                    //Add up the strike.
                    //Add the next two balls to the current cumulativeScores
                    cumulativeScores[bowlIndex][i/2] += 10;
                    if(curScore[i+1] != -1) {
                        cumulativeScores[bowlIndex][i/2] += curScore[i+1] + cumulativeScores[bowlIndex][(i/2)-1];
                        if ( (curScore[i+2] != -1) && ( curScore[i+2] != -2) ){
                            cumulativeScores[bowlIndex][(i/2)] += curScore[i+2];
                        }
                        else if( curScore[i+3] != -2) {
                            cumulativeScores[bowlIndex][(i/2)] += curScore[i+3];
                        }
                    }
                    else {
                        if ( i/2 > 0 ){
                            cumulativeScores[bowlIndex][i/2] += curScore[i+2] + cumulativeScores[bowlIndex][(i/2)-1];
                        } else {
                            cumulativeScores[bowlIndex][i/2] += curScore[i+2];
                        }

                        if ( (curScore[i+3] != -1) && (curScore[i+3] != -2) ){
                            cumulativeScores[bowlIndex][(i/2)] += curScore[i+3];
                        }
                        else {
                            cumulativeScores[bowlIndex][(i/2)] += curScore[i+4];
                        }
                    }
                }
                else {
                    break;
                }
            }
            else {
                //We're dealing with a normal throw, add it and be on our way.
                if( (i%2 == 0) && (i < 18)){
                    if ( (i/2 == 0) && (curScore[i] != -2) ) {
                        //First frame, first ball.  Set his cumul score to the first ball
                        cumulativeScores[bowlIndex][i/2] += curScore[i];
                    }
                    else if (i/2 != 9){
                        //add his last frame's cumul to this ball, make it this frame's cumul.
                        if(curScore[i] != -2){
                            cumulativeScores[bowlIndex][i/2] += cumulativeScores[bowlIndex][i/2 - 1] + curScore[i];
                        }
                        else {
                            cumulativeScores[bowlIndex][i/2] += cumulativeScores[bowlIndex][i/2 - 1];
                        }
                    }
                }
                else if (i < 18){
                    if( (curScore[i] != -1 && i > 2) && (curScore[i] != -2) ){
                        cumulativeScores[bowlIndex][i/2] += curScore[i];
                    }
                }
                if (i/2 == 9){
                    if (i == 18){
                        cumulativeScores[bowlIndex][9] += cumulativeScores[bowlIndex][8];
                    }
                    if(curScore[i] != -2){
                        cumulativeScores[bowlIndex][9] += curScore[i];
                    }
                }
                else if ( (i/2 == 10) && (curScore[i] != -2) ) {
                    cumulativeScores[bowlIndex][9] += curScore[i];
                }
            }
        }
        return totalScore;
    }

    public boolean isPartyAssigned() {
        return partyAssigned;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void subscribe( LaneObserver laneObserver ) {
        subscribers.add( laneObserver );
    }

    public void unsubscribe( LaneObserver removing ) {
        subscribers.remove( removing );
    }

    public void publish(LaneEvent laneEvent){
        if( subscribers.size() > 0 ) {
            Iterator eventIterator = subscribers.iterator();

            while ( eventIterator.hasNext() ) {
                ( (LaneObserver) eventIterator.next()).receiveLaneEvent( laneEvent );
            }
        }
    }

    public Pinsetter getPinsetter() {
        return setter;
    }

    public void pauseGame() {
        gameIsHalted = true;
        publish(lanePublish());
    }

    public void unPauseGame() {
        gameIsHalted = false;
        publish(lanePublish());
    }

    public void setInfo(Party party, Pinsetter setter, HashMap scores, Vector subscribers,
                        boolean gameIsHalted, boolean partyAssigned, boolean gameFinished,
                        Iterator bowlerIterator, int ball, int bowlIndex, int frameNumber,
                        boolean tenthFrameStrike, int[] currentScores, int[][] cumulativeScores,
                        boolean canThrowAgain, int[][] finalScores, int gameNumber,
                        Bowler currentThrower){
        this.party = party;
        this.setter = setter;
        this.scores = scores;
        this.subscribers = subscribers;

        this.gameIsHalted = gameIsHalted;

        this.partyAssigned = partyAssigned;
        this.gameFinished = gameFinished;
        this.bowlerIterator = bowlerIterator;
        this.ball = ball;
        this.bowlIndex = bowlIndex;
        this.frameNumber = frameNumber;
        this.tenthFrameStrike = tenthFrameStrike;

        this.currentScores = currentScores;
        this.cumulativeScores = cumulativeScores;
        this.canThrowAgain = canThrowAgain;

        this.finalScores = finalScores;
        this.gameNumber = gameNumber;

        this.currentThrower = currentThrower;
    }

    public Party getParty(){return party;}
    public boolean getPartyAssigned(){return partyAssigned;}
    public boolean getGameFinished(){return gameFinished;}
    public boolean getGameIsHalted(){return gameIsHalted;}
    public Iterator getBowlerIterator(){return bowlerIterator;}
    public int getBall(){return ball;}
    public int getBowlIndex(){return bowlIndex;}
    public int getFrameNumber(){return frameNumber;}
    public boolean getTenthFrameStrike(){return tenthFrameStrike;}

    public int[] getCurrentScores(){return currentScores;}
    public int[][] getCumulativeScores(){return cumulativeScores;}
    public boolean getCanThrowAgain(){return canThrowAgain;}

    public int[][] getFinalScores(){return finalScores;}
    public int getGameNumber(){return gameNumber;}
    public Bowler getCurrentThrower(){return currentThrower;}
    public Pinsetter getSetter(){return setter;}
    public HashMap getScores(){return scores;}
    public Vector getSubscribers(){return subscribers;}

    public boolean getPlayAgain(){return playAgain;}
    public void setPlayAgain(boolean value){playAgain = value;}


//    public void setParty(Party party, boolean partyAssigned)
//    {
//        this.party = party;
//        this.partyAssigned = partyAssigned;
//    }
//
//    public void setGameFlags(boolean gameFinished, boolean gameIsHalted)
//    {
//        this.gameFinished = gameFinished;
//        this.gameIsHalted = gameIsHalted;
//    }
//
//    public void setBowlerInformation(Iterator bowlerIterator, Bowler currentThrower, boolean canThrowAgain, int bowlIndex)
//    {
//        this.bowlerIterator = bowlerIterator;
//        this.currentThrower = currentThrower;
//        this.canThrowAgain = canThrowAgain;
//        this.bowlIndex = bowlIndex;
//    }
//
//    public void setFrameInformation(boolean tenthFrameStrike, int frameNumber)
//    {
//        this.tenthFrameStrike = tenthFrameStrike;
//        this.frameNumber = frameNumber;
//    }
//
//    public void setScoreInformation(int[][] finalScores, int[][] cumulativeScores, HashMap scores)
//    {
//        this.finalScores = finalScores;
//        this.cumulativeScores = cumulativeScores;
//        this.scores = scores;
//    }
//
//    public void setLaneInformation(int ball, Pinsetter setter)
//    {
//        this.ball = ball;
//        this.setter = setter;
//    }
//
//    public void setGameInformation(int gameNumber, Vector subscribers)
//    {
//        this.gameNumber = gameNumber;
//        this.subscribers = subscribers;
//    }
//
//    public Party getParty(){return this.party;}
//    public boolean getPartyAssigned(){return this.partyAssigned;}
//    public boolean getGameFinished(){return this.gameFinished;}
//    public boolean getGameIsHalted(){return this.gameIsHalted;}
//    public Iterator getBowlerIterator(){return this.bowlerIterator;}
//    public int getBall(){return this.ball;}
//    public int getBowlIndex(){return this.bowlIndex;}
//    public int getFrameNumber(){return this.frameNumber;}
//    public boolean getTenthFrameStrike(){return this.tenthFrameStrike;}
//
//    public int[] getCurrentScores(){return this.currentScores;}
//    public int[][] getCumulativeScores(){return this.cumulativeScores;}
//    public boolean getCanThrowAgain(){return this.canThrowAgain;}
//
//    public int[][] getFinalScores(){return this.finalScores;}
//    public int getGameNumber(){return this.gameNumber;}
//    public Bowler getCurrentThrower(){return this.currentThrower;}
//    public Pinsetter getSetter(){return this.setter;}
//    public HashMap getScores(){return this.scores;}
//    public Vector getSubscribers(){return this.subscribers;}
//
//    public boolean getPlayAgain(){return this.playAgain;}
//
//    public abstract void run(LaneEvent laneEvent);
}
