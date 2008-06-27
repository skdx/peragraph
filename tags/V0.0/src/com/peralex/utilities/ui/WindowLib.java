package com.peralex.utilities.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * Utility routines for Swing windows and frames.
 * 
 * @author Noel Grandin
 */
public final class WindowLib
{
	private WindowLib() {}
	
	/**
	 * tile the windows on the desktop so that no windows overlap.
	 */
	public static void tile(Window ... frames)
	{
		final java.awt.Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(frames[0].getGraphicsConfiguration());
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		tile(frames, screenSize, screenInsets);
	}

	private static void tile(Window[] frames, Dimension screenSize, java.awt.Insets screenInsets)
	{
		final int cols = (int) Math.sqrt(frames.length);
		int rows = (int) (Math.ceil(((double) frames.length) / cols));
		int lastRow = frames.length - cols * (rows - 1);
		int width, height;

		/* calculate the area of the desktop we have to work with */
		final int desktopHeight = screenSize.height - screenInsets.top - screenInsets.bottom;
		final int desktopWidth = screenSize.width - screenInsets.left - screenInsets.right;

		/* if there is only one row */
		if (lastRow == 0)
		{
			rows--;
			height = desktopHeight / rows;
		}
		else
		{
			height = desktopHeight / rows;
			/* if the last row needs less columns than the other rows */
			if (lastRow < cols)
			{
				rows--;
				width = desktopWidth / lastRow;
				for (int i = 0; i < lastRow; i++)
				{
					frames[cols * rows + i].setBounds(screenInsets.left + (i * width), screenInsets.top + (rows * height), width, height);
					frames[cols * rows + i].validate();
				}
			}
		}

		width = desktopWidth / cols;
		for (int j = 0; j < rows; j++)
		{
			for (int i = 0; i < cols; i++)
			{
				frames[i + j * cols].setBounds(screenInsets.left + (i * width), screenInsets.top + (j * height), width, height);
				frames[i + j * cols].validate();
			}
		}
	}
}
