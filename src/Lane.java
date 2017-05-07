
/* $Id$
 *
 * Revisions:
 *   $Log: Lane.java,v $
 *   Revision 1.52  2003/02/20 20:27:45  ???
 *   Fouls disables.
 *
 *   Revision 1.51  2003/02/20 20:01:32  ???
 *   Added things.
 *
 *   Revision 1.50  2003/02/20 19:53:52  ???
 *   Added foul support.  Still need to update laneview and test this.
 *
 *   Revision 1.49  2003/02/20 11:18:22  ???
 *   Works beautifully.
 *
 *   Revision 1.48  2003/02/20 04:10:58  ???
 *   Score reporting code should be good.
 *
 *   Revision 1.47  2003/02/17 00:25:28  ???
 *   Added disbale controls for View objects.
 *
 *   Revision 1.46  2003/02/17 00:20:47  ???
 *   fix for event when game ends
 *
 *   Revision 1.43  2003/02/17 00:09:42  ???
 *   fix for event when game ends
 *
 *   Revision 1.42  2003/02/17 00:03:34  ???
 *   Bug fixed
 *
 *   Revision 1.41  2003/02/16 23:59:49  ???
 *   Reporting of sorts.
 *
 *   Revision 1.40  2003/02/16 23:44:33  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.39  2003/02/16 23:43:08  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.38  2003/02/16 23:41:05  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.37  2003/02/16 23:00:26  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.36  2003/02/16 21:31:04  ???
 *   Score logging.
 *
 *   Revision 1.35  2003/02/09 21:38:00  ???
 *   Added lots of comments
 *
 *   Revision 1.34  2003/02/06 00:27:46  ???
 *   Fixed a race condition
 *
 *   Revision 1.33  2003/02/05 11:16:34  ???
 *   Boom-Shacka-Lacka!!!
 *
 *   Revision 1.32  2003/02/05 01:15:19  ???
 *   Real close now.  Honest.
 *
 *   Revision 1.31  2003/02/04 22:02:04  ???
 *   Still not quite working...
 *
 *   Revision 1.30  2003/02/04 13:33:04  ???
 *   Lane may very well work now.
 *
 *   Revision 1.29  2003/02/02 23:57:27  ???
 *   fix on pinsetter hack
 *
 *   Revision 1.28  2003/02/02 23:49:48  ???
 *   Pinsetter generates an event when all pins are reset
 *
 *   Revision 1.27  2003/02/02 23:26:32  ???
 *   ControlDesk now runs its own thread and polls for free lanes to assign queue members to
 *
 *   Revision 1.26  2003/02/02 23:11:42  ???
 *   parties can now play more than 1 game on a lane, and lanes are properly released after games
 *
 *   Revision 1.25  2003/02/02 22:52:19  ???
 *   Lane compiles
 *
 *   Revision 1.24  2003/02/02 22:50:10  ???
 *   Lane compiles
 *
 *   Revision 1.23  2003/02/02 22:47:34  ???
 *   More observering.
 *
 *   Revision 1.22  2003/02/02 22:15:40  ???
 *   Add accessor for pinsetter.
 *
 *   Revision 1.21  2003/02/02 21:59:20  ???
 *   added conditions for the party choosing to play another game
 *
 *   Revision 1.20  2003/02/02 21:51:54  ???
 *   LaneEvent may very well be observer method.
 *
 *   Revision 1.19  2003/02/02 20:28:59  ???
 *   fixed sleep thread bug in lane
 *
 *   Revision 1.18  2003/02/02 18:18:51  ???
 *   more changes. just need to fix scoring.
 *
 *   Revision 1.17  2003/02/02 17:47:02  ???
 *   Things are pretty close to working now...
 *
 *   Revision 1.16  2003/01/30 22:09:32  ???
 *   Worked on scoring.
 *
 *   Revision 1.15  2003/01/30 21:45:08  ???
 *   Fixed speling of received in Lane.
 *
 *   Revision 1.14  2003/01/30 21:29:30  ???
 *   Fixed some MVC stuff
 *
 *   Revision 1.13  2003/01/30 03:45:26  ???
 *   *** empty log message ***
 *
 *   Revision 1.12  2003/01/26 23:16:10  ???
 *   Improved thread handeling in lane/controldesk
 *
 *   Revision 1.11  2003/01/26 22:34:44  ???
 *   Total rewrite of lane and pinsetter for R2's observer model
 *   Added Lane/Pinsetter Observer
 *   Rewrite of scoring algorythm in lane
 *
 *   Revision 1.10  2003/01/26 20:44:05  ???
 *   small changes
 *
 * 
 */

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;

public class Lane extends Thread implements PinsetterObserver {	
	private Party party;
	private Pinsetter setter;
	private HashMap scores;
	private Vector subscribers;

	private boolean gameIsHalted;

	private boolean partyAssigned;
	private boolean gameFinished;
	private Iterator bowlerIterator;
	private int ball;
	private int bowlIndex;
	private int frameNumber;
	private boolean tenthFrameStrike;

	private int[] currentScores;
	private int[][] cumulativeScores;
	private boolean canThrowAgain;
	
	private int[][] finalScores;
	private int gameNumber;
	
	private Bowler currentThrower;			// = the thrower who just took a throw

	private HashMap<String, LaneRunState> states;
    private LaneRunState currentState;

	/**
	 * Lane Constructor, creates a new lane object while initializing the necessary variables
	 */
	public Lane() { 
		setter = new Pinsetter();
		scores = new HashMap();
		subscribers = new Vector();

		gameIsHalted = false;
		partyAssigned = false;

		gameNumber = 0;

		states = new HashMap<>();
		states.put("ongoing", new LaneOngoingState());
		states.put("finished", new LaneFinishedState());

		currentState = states.get("ongoing");
        setLaneSatesInfo();

		setter.subscribe( this );

        getLaneStateInfo();
		this.start();
	}

	/**
	 * Is the main running loop for the system, and chooses between different states of the party to run
	 */
	public void run() {
        currentState.setPlayAgain(true);
        while (true) {
            //System.out.println(isPartyAssigned());
            getLaneStateInfo();
            if (isPartyAssigned()) {
                if (!isGameFinished()) {
                    currentState = states.get("ongoing");
                } else {
                    currentState = states.get("finished");
                }
				setLaneSatesInfo();
                currentState.run();
                getLaneStateInfo();

            }

            try {
                sleep(10);
            } catch (Exception e) {
            }
        }
	}

	/**
	 * Passes a pinsetter event to the current runnning state of the system
	 * @param pe - a pinsetter event
	 */
	public void receivePinsetterEvent(PinsetterEvent pe) {
		currentState.receivePinsetterEvent(pe);
	}

	/**
	 * Passes the party in question to the current state to be assigned
	 * @param theParty - a party object full of bowlers
	 */
	public void assignParty( Party theParty ) {
		currentState.assignParty(theParty);
	}

	/**
	 * Gets a lane event from the current Running state and returns the event
	 * @return - a laneEvent
	 */
	private LaneEvent lanePublish(  ) {
		return currentState.lanePublish();
	}



	/**
	 * Gets if a party is assigned from the current state
	 * @return true if party assigned, false otherwise
	 */
	public boolean isPartyAssigned() {
		return currentState.isPartyAssigned();
	}
	
	/**
	 * Gets if the game is finished from the current state
	 * @return true if the game is done, false otherwise
	 */
	public boolean isGameFinished() {
		return currentState.isGameFinished();
	}

	/**
	 * Passes a laneObserver to the current state
	 * @param laneObserver - Observer that is to be added
	 */
	public void subscribe( LaneObserver laneObserver ) {
		currentState.subscribe(laneObserver);
	}

	/**
	 * Method to pass a laneObserver to remove to the current state
	 * @param removing - The observer to be removed
	 */
	public void unsubscribe( LaneObserver removing ) {
		currentState.unsubscribe(removing);
	}

	/**
	 * Calls the publish method of the current state
	 * @param event	Event that is to be published
	 */
	public void publish( LaneEvent event ) {
		currentState.publish(event);
	}

	/**
	 * Gets the pinsetter from the current state
	 * @return - A reference to this lane's pinsetter
	 */
	public Pinsetter getPinsetter() {
		return currentState.getPinsetter();
	}

	/**
	 * Pause the execution of this game by calling pause in the current state
	 */
	public void pauseGame() {
		currentState.pauseGame();
	}
	
	/**
	 * Resume the execution of this game by calling unPause in the current state
	 */
	public void unPauseGame() {
		currentState.unPauseGame();
	}

    /**
     * Sets the information that's in states equal to the information in the class.
     */
    private void setLaneSatesInfo(){
        currentState.setInfo(party, setter, scores, subscribers, gameIsHalted, partyAssigned, gameFinished,
                bowlerIterator, ball, bowlIndex, frameNumber, tenthFrameStrike, currentScores, cumulativeScores,
                canThrowAgain, finalScores, gameNumber, currentThrower);
    }

    /**
     * Extracts all the information from the state.
     */
    private void getLaneStateInfo(){
        this.party = currentState.getParty();
        this.partyAssigned = currentState.getPartyAssigned();
        this.gameFinished = currentState.getGameFinished();
        this.gameIsHalted = currentState.getGameIsHalted();
        this.bowlerIterator = currentState.getBowlerIterator();
        this.ball = currentState.getBall();
        this.bowlIndex = currentState.getBowlIndex();
        this.frameNumber = currentState.getFrameNumber();
        this.tenthFrameStrike = currentState.getTenthFrameStrike();

        this.currentScores = currentState.getCurrentScores();
        this.cumulativeScores = currentState.getCumulativeScores();
        this.canThrowAgain = currentState.getCanThrowAgain();

        this.finalScores = currentState.getFinalScores();
        this.gameNumber = currentState.getGameNumber();
        this.currentThrower = currentState.getCurrentThrower();
        this.setter = currentState.getSetter();
        this.scores = currentState.getScores();
        this.subscribers = currentState.getSubscribers();
    }

}
