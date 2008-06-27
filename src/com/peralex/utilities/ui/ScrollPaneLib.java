package com.peralex.utilities.ui;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.ViewportLayout;

/**
 * Some utility methods for JScrollPane.
 * 
 * @author Noel Grandin
 */
public final class ScrollPaneLib
{
	private ScrollPaneLib() {}
	
  /** 
   * Use this method to fix the unhelpful behaviour when you stick a JScrollPane inside a GridBagLayout
   * and the display starts running low on space.
   * 
   * Override getMinimumSize() and make preferred_width=min_width.
   * Otherwise when GridBagLayout goes into "minimum" mode the JScrollPane will disappear.
   */
	public static void fixMinimumWidth(JScrollPane oScrollPane)
	{
		oScrollPane.getViewport().setLayout(new ViewportLayout()
		{
			@Override
			public Dimension minimumLayoutSize(Container parent)
			{
	      Dimension d = super.minimumLayoutSize(parent);
	      Dimension d2 = super.preferredLayoutSize(parent);
	      d.width = d2.width;
	      return d;
			}
		});
	}
}
