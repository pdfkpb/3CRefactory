/**
 * 
 * SMTP implementation based on code by R�al Gagnon mailto:real@rgagnon.com
 * 
 */


import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import java.net.*;
import java.awt.print.*;

public class ScoreReport {

	private String content;
	
	public ScoreReport( Bowler bowler, int[] scores, int games ) {
		String nick = bowler.getNickName();
		String full = bowler.getFullName();
		Vector v = null;
		try{
			v = ScoreHistoryFile.getScores(nick);
		} catch (Exception e){System.err.println("Error: " + e);}
		
		Iterator scoreIt = v.iterator();
		
		content = "";
		content += "--Lucky Strike Bowling Alley Score Report--\n";
		content += "\n";
		content += "Report for " + full + ", aka \"" + nick + "\":\n";
		content += "\n";
		content += "Final scores for this session: ";
		content += scores[0];
		for (int i = 1; i < games; i++){
			content += ", " + scores[i];
		}
		content += ".\n";
		content += "\n";
		content += "\n";
		content += "Previous scores by date: \n";
		while (scoreIt.hasNext()){
			Score score = (Score) scoreIt.next();
			content += "  " + score.getDate() + " - " +  score.getScore();
			content += "\n";
		}
		content += "\n\n";
		content += "Thank you for your continuing patronage.";

	}

	public void sendEmail(String recipient) {
		try {
			System.out.println("Subject: Bowling Score Report ");
			System.out.println("From: <Lucky Strikes Bowling Club>");
			System.out.println("Content-Type: text/plain; charset=\"us-ascii\"\r\n");
			System.out.println(content + "\n\n");
			System.out.println("\r\n");

			System.out.println(".");
			System.out.println("QUIT");
			//s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendPrintout() {
		PrinterJob job = PrinterJob.getPrinterJob();

		PrintableText printobj = new PrintableText(content);

		job.setPrintable(printobj);

		if (job.printDialog()) {
			try {
				job.print();
			} catch (PrinterException e) {
				System.out.println(e);
			}
		}

	}

	public void sendln(BufferedReader in, BufferedWriter out, String s) {
		try {
			out.write(s + "\r\n");
			out.flush();
			// System.out.println(s);
			s = in.readLine();
			// System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendln(BufferedWriter out, String s) {
		try {
			out.write(s + "\r\n");
			out.flush();
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
