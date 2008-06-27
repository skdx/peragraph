package com.peralex.utilities.ui.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

/**
 * A line border that only paints some of it's borders.
 * 
 * @author Noel Grandin
 * @author Pieter De Vos
 */
public final class PartialLineBorder extends AbstractBorder
{

	private final Color lineColor;

	private final boolean drawLeft;
	private final boolean drawBottom;
	private final boolean drawTop;
	private final boolean drawRight;

	/* some factory methods because using boolean constructors is not very readable */
	
	public static PartialLineBorder createTopLeft(Color c) {
		return new PartialLineBorder(c, true, true, false, false);
	}
	
	public static PartialLineBorder createLeftBottomRight(Color c) {
		return new PartialLineBorder(c, false, true, true, true);
	}
	
	public static PartialLineBorder createTopBottomRight(Color c) {
		return new PartialLineBorder(c, true, false, true, true);
	}
	
	public static PartialLineBorder createBottomRight(Color c) {
		return new PartialLineBorder(c, false, false, true, true);
	}
	
	public static PartialLineBorder createLeft(Color c) {
		return new PartialLineBorder(c, false, true, false, false);
	}
	
	public static PartialLineBorder createTop(Color c) {
		return new PartialLineBorder(c, true, false, false, false);
	}
	
	private PartialLineBorder(Color c, boolean drawTop, boolean drawLeft, boolean drawBottom, boolean drawRight)
	{
		this.lineColor = c;
		this.drawLeft = drawLeft;
		this.drawTop = drawTop;
		this.drawBottom = drawBottom;
		this.drawRight = drawRight;
	}

	@Override
	public Insets getBorderInsets(Component c)
	{
		return new Insets(
				drawTop ? 1 : 0, 
				drawLeft ? 1 : 0, 
				drawBottom ? 1 : 0, 
				drawRight ? 1 : 0);
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets)
	{
		insets.left = drawLeft ? 1 : 0;
		insets.top = drawTop ? 1 : 0;
		insets.right = drawRight ? 1 : 0;
		insets.bottom = drawBottom ? 1 : 0;
		return insets;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		Color oldColor = g.getColor();

		g.setColor(lineColor);

		// left
		if (drawLeft)
			g.drawLine(x, y, x, y + height);

		// right
		if (drawRight)
			g.drawLine(x + width - 1, y, x + width - 1, y + height);

		// top
		if (drawTop)
			g.drawLine(x, y, x + width, y);

		// bottom
		if (drawBottom)
			g.drawLine(x, y + height - 1, x + width, y + height - 1);

		g.setColor(oldColor);
	}

}

