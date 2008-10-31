package com.peralex.utilities.ui.graphs.scrollingline;

import java.awt.Color;
import java.awt.Graphics2D;

import com.peralex.utilities.locale.ILocaleListener;
import com.peralex.utilities.ui.graphs.graphBase.ZoomDrawSurface;

/**
 * A left-to-right scrolling line graph that only handles one line.
 * 
 * @author Roy Emmerich
 * @author Noel Grandin
 */
public class SingleScrollingLineGraph extends ZoomDrawSurface implements ILocaleListener
{

	private final ScrollingLineGraph.LineState lineState = new ScrollingLineGraph.LineState();

	public SingleScrollingLineGraph()
	{
	}

	/**
	 * Updates the straight line graph
	 */
	@Override
	protected void drawGraph(Graphics2D g)
	{
		final int currentHeight_pixels = getSize().height;

		/** Conversion ratio between units and pixels */
		final double yPlotRatio = currentHeight_pixels / (getMaximumY() - getMinimumY());
		synchronized (lineState)
		{
			ScrollingLineGraph.paintLine(this, g, currentHeight_pixels, yPlotRatio, lineState);
		}
	}

	/**
	 * Method for adding data for a specific line.
	 */
	public void addLineValue(float yValue)
	{
		synchronized (lineState)
		{
			lineState.data.addFirst(yValue);
			if (lineState.data.size() > getWidth())
			{
				lineState.data.removeLast();
			}
		}
		repaint();
	}

	/**
	 * Multiple graphs can be drawn on this DrawSurface. Each graph or signal must be able to be made in/visible upon
	 * request.
	 * 
	 * @param visible - True to see the signal, false to make it invisible
	 */
	public void setLineVisible(boolean visible)
	{
		synchronized (lineState)
		{
			lineState.visible = visible;
		}
		repaint();
	}

	public void setLineColor(Color color)
	{
		synchronized (lineState)
		{
			lineState.color = color;
		}
		repaint();
	}

	/**
	 * Clear the current data of the graph.
	 */
	@Override
	public void clear()
	{
		synchronized (lineState)
		{
			lineState.data.clear();
		}
		repaint();
	}

}
