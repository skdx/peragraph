package com.peralex.utilities.ui;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

/**
 * A collection of few useful functions for JTabbedPane
 * 
 * @author Noel Grandin
 */
public class TabbedPaneLib
{
	/** not meant to be instantiated */
	private TabbedPaneLib()
	{
	}

	
	public static void setTitleAt(JTabbedPane tabbedPane, JComponent comp, String title)
	{
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(comp), title);
	}
	
	public static String getTitleAt(JTabbedPane tabbedPane, JComponent comp)
	{
		return tabbedPane.getTitleAt(tabbedPane.indexOfComponent(comp));
	}
	
	public static void setIconAt(JTabbedPane tabbedPane, JComponent comp, Icon icon)
	{
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(comp), icon);
	}
}
