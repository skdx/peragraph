package com.peralex.utilities.ui.helppopup;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;

/**
 * A factory class for creating buttons that display popup help messages.
 * 
 * @author Noel Grandin
 */
final class HelpPopup
{

	/** not meant to be instantiated */
	private HelpPopup()
	{
	}

	/**
	 * This create and returns a Help Popup with the given text.
	 */
	static JPopupMenu createHelpPopup(String sHelpText)
	{
		sHelpText = replaceLineBreaks(sHelpText);
		
		final JToolTip toolTip = new JToolTip();
		toolTip.setTipText(sHelpText);
		
		final JPopupMenu menu = new JPopupMenu();
		menu.setLayout(new BorderLayout());
		menu.setBorder(BorderFactory.createEmptyBorder());
		menu.add(toolTip, BorderLayout.CENTER);
		
		menu.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				menu.setVisible(false);
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				menu.setVisible(false);
			}			
		});
		return menu;
	}

	/**
	 * convert to an HTML string so that the line breaks are rendered correctly
	 */
	private static String replaceLineBreaks(String s)
	{
		// if it is already html, leave it alone
		if (s.startsWith("<html>") || s.startsWith("<Html>") || s.startsWith("<HTML>"))
		{
			return s;
		}
		
		// if there are no line breaks, do nothing
		if (s.indexOf("\n")==-1) return s;

		// convert to an HTML string
		return "<html>" + s.replaceAll("\n", "<br>") + "</html>";
	}

}
