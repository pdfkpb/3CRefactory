/**
 *
 */

import java.awt.*;
import java.awt.print.*;
import java.awt.geom.*;
import java.awt.font.*;

public class PrintableText implements Printable {
	String text;
	int POINTS_PER_INCH;

	public PrintableText(String t) {
		POINTS_PER_INCH = 72;
		text = t;
	}

	public int print(Graphics graphic, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0) {
			return NO_SUCH_PAGE;
		}
		
		Graphics2D graphics2D = (Graphics2D) graphic; // Allow use of Java 2 graphics on

		graphics2D .translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		graphics2D .setPaint(Color.black);

		Point2D.Double pen = new Point2D.Double(0.25 * POINTS_PER_INCH, 0.25 * POINTS_PER_INCH);

		Font font = new Font ("courier", Font.PLAIN, 12);
   		FontRenderContext frc = graphics2D .getFontRenderContext();

		String lines[] = text.split("\n");

		for (int i=0; i < lines.length; i++) {		
			if (lines[i].length() > 0) {
				TextLayout layout = new TextLayout(lines[i], font, frc);
				layout.draw(graphics2D , (float) pen.x, (float) (pen.y + i*14));
			}
		}

		return PAGE_EXISTS;
	}

}
