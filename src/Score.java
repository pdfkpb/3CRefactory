/**
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

public class Score {

    private String nickName;
    private String date;
    private String score;

    public Score( String nickName, String date, String score ) {
		this.nickName = nickName;
		this.date = date;
		this.score = score;
    }

    public String getNickName() {
        return nickName;
    }

	public String getDate() {
		return date;
	}
	
	public String getScore() {
		return score;
	}

	public String toString() {
		return nickName + "\t" + date + "\t" + score;
	}

}
