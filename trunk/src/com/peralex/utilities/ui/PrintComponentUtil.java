package com.peralex.utilities.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;

/**
 * A simple utility class that lets you very simply print an arbitrary component. Just pass the component to the
 * printComponent() method. The component you want to print doesn't need a print method and doesn't have to implement
 * any interface or do anything special at all.
 * 
 * This class was pulled together from various bits of code on the net. 
 * Inspiration belongs to: 
 *   Rob MacGrogan http://www.developerdotstar.com/community/node/124 
 *   Marty Hall http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html 
 *   O'Reilly http://www.unix.org.ua/orelly/java-ent/jfc/ch05_02.htm
 * 
 * @author Rob MacGrogan, Marty Hall, Noel Grandin
 */
public class PrintComponentUtil
{

	private final Component componentToBePrinted;

	private final boolean scaleToFit;

	/**
	 * Create a component to render some HTML output. This is purely here because it took me so long to figure out the
	 * minimum magic invocation to make JEditorPane work properly.
	 */
	public static JEditorPane createHtmlComponent(InputStream in) throws IOException
	{
		JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		try
		{
			editorPane.getEditorKit().read(new InputStreamReader(in), editorPane.getDocument(), 0);
		}
		catch (BadLocationException ex)
		{
			// should never happen
			throw new RuntimeException(ex);
		}
		return editorPane;
	}

	/**
	 * Create a component to render some HTML output. This is purely here because it took me so long to figure out the
	 * minimum magic invocation to make JEditorPane work properly.
	 */
	public static JEditorPane createHtmlComponent(String html)
	{
		final JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setText(html);

		return editorPane;
	}

	public static void printComponent(Component c, boolean scaleToFit)
	{
		new PrintComponentUtil(c, scaleToFit).print((PageFormat) null);
	}

	public static void printComponent(Component c, boolean scaleToFit, PageFormat pageFormat)
	{
		new PrintComponentUtil(c, scaleToFit).print(pageFormat);
	}

	public PrintComponentUtil(Component componentToBePrinted, boolean scaleToFit)
	{
		this.componentToBePrinted = componentToBePrinted;
		this.scaleToFit = scaleToFit;

		if (componentToBePrinted.getHeight() == 0)
		{
			// component is "standalone" i.e. has not been show on the screen
			final Dimension d = componentToBePrinted.getPreferredSize();
			componentToBePrinted.setSize(d);
			componentToBePrinted.validate();
		}
	}

	/**
	 * Display the component in a dialog. Very useful when debugging what you want to print.
	 */
	public void debugComponent()
	{
		Frame activeFrame = SwingLib.findFrameParent(componentToBePrinted);
		final JDialog dialog = new JDialog(activeFrame, "Debug Printing Document", true);
		JScrollPane scrollPane = new JScrollPane(componentToBePrinted);
		dialog.setContentPane(scrollPane);

		dialog.pack();
		dialog.setLocationRelativeTo(activeFrame);
		dialog.setVisible(true);
	}

	public void print(PageFormat pageFormat)
	{
		final PrinterJob printJob = PrinterJob.getPrinterJob();
		if (pageFormat != null)
		{
			printJob.setPrintable(printable, pageFormat);
		}
		else
		{
			printJob.setPrintable(printable);
		}
		if (printJob.printDialog())
		{
			// run this on a background thread because it can take a while
			// and display a modal dialog while doing so.

			final JOptionPane optionPane = new JOptionPane("Printing...", JOptionPane.PLAIN_MESSAGE);
			optionPane.setOptions(new Object[] {}); // don't want any buttons
			final JDialog dialog = new JDialog(SwingLib.findFrameParent(componentToBePrinted), "Printing", true);
			dialog.setContentPane(optionPane);
			dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			dialog.setLocationRelativeTo(SwingLib.findFrameParent(componentToBePrinted));

			new Thread("Peralex - Print Component")
			{
				@Override public void run()
				{
					try
					{
						printJob.print();
					}
					catch (PrinterException ex)
					{
						dialog.setVisible(false);
						ex.printStackTrace();
						return;
					}
					dialog.setVisible(false);
				}
			}.start();

			dialog.pack();
			dialog.setVisible(true);
		}
	}

	private final Printable printable = new Printable() {
		/**
		 * @see Printable#print
		 */
		public int print(Graphics g, PageFormat pageFormat, int pageIndex)
		{
			final int response;
	
			final Graphics2D g2d = (Graphics2D) g;
	
			final double compHeightPx = componentToBePrinted.getHeight();
			final double compWidthPx = componentToBePrinted.getWidth();
			final double pageWidthPt = pageFormat.getImageableWidth(); // width of printer page
			final double pageHeightPt = pageFormat.getImageableHeight(); // height of printer page
	
			if (scaleToFit)
			{
				/* scale the component to fit on the page */
				if (pageIndex > 0)
				{
					return NO_SUCH_PAGE;
				}
				final double scaleX = pageWidthPt / compWidthPx;
				final double scaleY = pageHeightPt / compHeightPx;
				// pick the larger factor so that we maintain aspect ratio.
				final double scale = Math.min(scaleX, scaleY);
				g2d.scale(scale, scale);
	
				/* adjust the origin of the drawing surface */
				g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
	
				componentToBePrinted.printAll(g2d);
	
				return PAGE_EXISTS;
			}
	
			final double scale = pageWidthPt / compWidthPx;
			final int totalNumPages = (int) Math.ceil(scale * compHeightPx / pageHeightPt);
			// make sure we don't print empty pages
			if (pageIndex >= totalNumPages)
			{
				response = NO_SUCH_PAGE;
			}
			else
			{
				// shift Graphic to line up with beginning of print-imageable region
				g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
				// shift Graphic to line up with beginning of next page to print
				g2d.translate(0f, -pageIndex * pageHeightPt);
				// scale the page so the width fits...
				g2d.scale(scale, scale);
	
				componentToBePrinted.printAll(g2d);
				
				response = Printable.PAGE_EXISTS;
			}
			return response;
		}
	};

}
