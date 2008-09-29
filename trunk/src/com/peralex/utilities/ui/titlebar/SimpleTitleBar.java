package com.peralex.utilities.ui.titlebar;

import javax.swing.JFrame;
import javax.swing.UIManager;


/**
 * Simple version of the cTitleBar class without any buttons.
 * Useful as a grouping label.
 *  
 * @author Noel Grandin
 */
public class SimpleTitleBar extends TitleBar
{
	public SimpleTitleBar()
	{
		this("Title");
	}
	
	public SimpleTitleBar(String sTitle)
	{
		super(sTitle);
		setCloseable(false);
		setLockable(false);
		setMinimizable(false);
	}

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
		catch (Exception ignoreEx) {}
		
		final SimpleTitleBar blockControl = new SimpleTitleBar();

		final JFrame frame = new JFrame("cSimpleTitleBar Test Frame");
		frame.getContentPane().add(blockControl);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(400, 300);
		frame.setVisible(true);
	}
}
