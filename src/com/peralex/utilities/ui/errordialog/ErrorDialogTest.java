package com.peralex.utilities.ui.errordialog;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class ErrorDialogTest
{

	private ErrorDialogTest() {}
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ignoreEx)
		{
		}


		final JFrame frame = new JFrame("Test Frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(400, 300);
		frame.setVisible(true);
		
		ErrorDialog.showFatal(new IllegalStateException());
	}
}
