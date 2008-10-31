package com.peralex.utilities.ui.graphs.scrollingline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.peralex.utilities.locale.ILocaleListener;
import com.peralex.utilities.objectpool.GraphObjectPool;
import com.peralex.utilities.ui.graphs.graphBase.ZoomDrawSurface;
import com.peralex.utilities.ui.graphs.lineGraph.AbstractLineGraph;
import com.peralex.utilities.ui.graphs.lineGraph.MultiLineGraph;

/**
 * A left-to-right scrolling line graph.
 * 
 * @author Roy Emmerich
 * @author Noel Grandin
 */
public class ScrollingLineGraph extends AbstractLineGraph implements ILocaleListener
{

	static final class LineState
	{
		// a linked list for each channel
		public final LinkedList<Float> data = new LinkedList<Float>();

		public boolean visible = true;

		public Color color;
	}

	private final Map<Object, LineState> lineMap = new HashMap<Object, LineState>();

	public ScrollingLineGraph()
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
		synchronized (lineMap)
		{
			for (LineState state : this.lineMap.values())
			{
				paintLine(this, g, currentHeight_pixels, yPlotRatio, state);
			}
		}
	}

	static void paintLine(ZoomDrawSurface comp, Graphics2D g, final int currentHeight_pixels, final double yPlotRatio, LineState state)
	{
		if (state.visible)
		{
			final LinkedList<Float> currentList = state.data;

			if (! currentList.isEmpty())
			{
				final int [] aiXCoordinates = GraphObjectPool.checkOutIntArray(currentList.size());
				final int [] aiYCoordinates = GraphObjectPool.checkOutIntArray(currentList.size());
				
				int j = 0;
				for (Float temp : currentList)
				{
					aiXCoordinates[j] = j;
					aiYCoordinates[j] = (int) (currentHeight_pixels - (float) ((temp.floatValue() - comp.getMinimumY()) * yPlotRatio));
					j++;
				}

				g.setColor(state.color);
				g.drawPolyline(aiXCoordinates, aiYCoordinates, currentList.size());
				
				GraphObjectPool.checkIn(aiXCoordinates);
				GraphObjectPool.checkIn(aiYCoordinates);
			}
		}
	}

	/**
	 * Method for adding data for a specific line.
	 */
	public void addLineValue(Object key, float yValue)
	{
		synchronized (lineMap)
		{
			ensureKeyExists(key);
			final LineState state = lineMap.get(key);

			state.data.addFirst(yValue);
			if (state.data.size() > getWidth())
			{
				state.data.removeLast();
			}
		}
		repaint();
	}

	/**
	 * clear the data for a line.
	 */
	public void clearLine(Object key)
	{
		boolean repaintNeeded = false;
		synchronized (lineMap)
		{
			ensureKeyExists(key);
			final LineState state = lineMap.get(key);
			if (state.data.size()>0) {
				repaintNeeded = true;
			}
			state.data.clear();
		}
		if (repaintNeeded) repaint();
	}

	/**
	 * Method for pushing a whole map of lines.
	 */
	public void addLineValues(Map<Object, Float> newData)
	{
		synchronized (lineMap)
		{
			for (Object key : newData.keySet())
			{
				ensureKeyExists(key);
				final LineState state = lineMap.get(key);

				final Float val = newData.get(key);
				state.data.addFirst(val);
				if (state.data.size() > getWidth())
				{
					state.data.removeLast();
				}
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
	public void setLineVisible(Object key, boolean visible)
	{
		boolean repaintNeeded = false;
		synchronized (lineMap)
		{
			ensureKeyExists(key);
			final LineState state = lineMap.get(key);
			if (!state.visible == visible) {
				repaintNeeded = true;
			}
			state.visible = visible;
		}
		if (repaintNeeded) repaint();
	}

	/**
	 * sets the visibility of all the lines
	 */
	public void setLinesVisible(boolean visible)
	{
		boolean repaintNeeded = false;
		synchronized (lineMap)
		{
			for (LineState state : lineMap.values())
			{
				if (!state.visible == visible) {
					repaintNeeded = true;
				}
				state.visible = visible;
			}
		}
		if (repaintNeeded) repaint();
	}
	
	public void setLineColor(Object key, Color color)
	{
		boolean repaintNeeded = false;
		synchronized (lineMap)
		{
			ensureKeyExists(key);
			final LineState state = lineMap.get(key);
			if (!state.color.equals(color)) {
				repaintNeeded = true;
			}
			state.color = color;
		}
		if (repaintNeeded) repaint();
	}

	/**
	 * Clear the current data of the graph.
	 */
	@Override
	public void clear()
	{
		boolean repaintNeeded = false;
		synchronized (lineMap)
		{
			for (LineState state : lineMap.values())
			{
				if (state.data.size()>0) repaintNeeded = true;
				state.data.clear();
			}
		}
		if (repaintNeeded) repaint();
	}

	private void ensureKeyExists(Object key)
	{
		synchronized (lineMap)
		{
			if (!lineMap.containsKey(key))
			{
				lineMap.put(key, new LineState());
				lineMap.get(key).color = MultiLineGraph.allocateLineColor(lineMap.size());
			}
		}
	}
	
	@Override
	protected void autoScaleGraph()
	{
		float minX = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		
		synchronized (lineMap)
		{
			if (lineMap.isEmpty()) 
			{
				resetZoom();
				return;
			}
			for (LineState lineState : lineMap.values())
			{
				final LinkedList<Float> oLineData = lineState.data; 
				final int numPoints = oLineData.size();
				if (numPoints>0)
				{
					minX = Math.min(minX, oLineData.getFirst());
					maxX = Math.max(maxX, oLineData.getLast());
					for (Float f : oLineData)
					{
						minY = Math.min(minY, f);
						maxY = Math.max(maxY, f);
					}
				}
			}
		}
		
    zoomIn(minX, maxX, minY, maxY);
		
	}
}
