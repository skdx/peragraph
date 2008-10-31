package com.peralex.utilities.ui.graphs.lineGraph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.peralex.utilities.objectpool.GraphObjectPool;
import com.peralex.utilities.ui.graphs.graphBase.ZoomAdapter;
import com.peralex.utilities.ui.graphs.graphBase.PixelUnitConverter;

/**
 * A line graph that can draw multiple different colored lines, 
 * each with it's own Graphics2D stroke.
 * 
 * This is a development of cLineGraph, but now using a key to identify
 * each line.
 * 
 * @author Andre
 * @author Noel Grandin
 */
public class MultiLineGraph extends AbstractLineGraph
{

	/**
	 * A set of simple strokes for clients to use in their graphs.
	 */
	public static final BasicStroke STROKE_1 = new BasicStroke(1.0f);
	public static final BasicStroke STROKE_2 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {18.0f, 6.0f}, 0.0f);
	public static final BasicStroke STROKE_3 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {18.0f, 6.0f, 1.0f, 5.0f}, 0.0f);
	public static final BasicStroke STROKE_4 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {8.0f, 5.0f}, 0.0f);
	public static final BasicStroke STROKE_5 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {8.0f, 5.0f, 1.0f, 5.0f}, 0.0f);
	
	private static final class LineState {
		public AbstractLineData data;
		public boolean visible = true;
		public Color color;
		public Stroke stroke = STROKE_1;
		/**
		 * cache of calculated coordinate values for drawing
		 */
		public CoordinateCacheValue cache;
	}

	/**
	 * This stores the data of all the lines that must be drawn.
	 */
	private final Map<Object, LineState> aoLineData = new HashMap<Object, LineState>();
	
	private final Object cacheLock = new Object();
	
	/**
	 * the width of the component when the coordinate cache data was created
	 */
	private int iCachedWidth = -1;
	
	/**
	 * the height of the component when the coordinate cache data was created
	 */
	private int iCachedHeight = -1;
	
	/**
	 * the height of the component when the coordinate cache data was created
	 */
	private double iCachedMinX = -1;
	
	/**
	 * the height of the component when the coordinate cache data was created
	 */
	private double iCachedMaxX = -1;
	
	/**
	 * the height of the component when the coordinate cache data was created
	 */
	private double iCachedMinY = -1;
	
	/**
	 * the height of the component when the coordinate cache data was created
	 */
	private double iCachedMaxY = -1;
	
	/**
	 * active optimised drawing mode
	 */
	private boolean bOptimizedDrawMode = false;

	private boolean bCacheChanged = false;

  
	/**
	 * Creates a new instance of cLineGraph
	 */
	public MultiLineGraph()
	{
		addZoomListener(new ZoomAdapter()
		{
			@Override
			public void graphZoomChanged(double fMinimumX, double fMaximumX, double fMinimumY, double fMaximumY)
			{
				graphDataChanged();
			}
		});
		
	}
	
	/**
	 * This will draw the Graph
	 */
	@Override
	protected void drawGraph(Graphics2D g)
	{
		if (bOptimizedDrawMode)
		{
			drawGraphOpt(g);
			return;
		}
		
		if (aoLineData == null)
		{
			return;
		}
		
		/* Noel - this method performs the bulk of the work, so I optimised it a little */
		
		final int iHeight = getHeight();
		final PixelUnitConverter.UnitToPixel xUnitToPixel = defaultXUnitToPixel();
		final PixelUnitConverter.UnitToPixel yUnitToPixel = defaultYUnitToPixel();
		
		synchronized (aoLineData)
		{
			for (LineState state : aoLineData.values())
			{
				final AbstractLineData oLineData = state.data;
				if (oLineData==null) continue;
				if (!state.visible) continue;
				
				final int [] aiXCoordinates = GraphObjectPool.checkOutIntArray(oLineData.getNumberOfPoints());
				final int [] aiYCoordinates = GraphObjectPool.checkOutIntArray(oLineData.getNumberOfPoints());
				
				/* Only draw the data that is going to be visible.
				 * Note: the +2,-2 calculations are to allow for high zoom factors. If we are
				 *  highly zoomed in, the first line on the left may start off-screen.
				 */
				final int startIndex = Math.max(0, Math.abs(oLineData.binarySearchXValues(getMinimumX())) - 2);
				// find the index where the maxX would be (the value may not necessarily be in the array, but the index
				// will index where it _would_ be
				int endIndex = Math.abs(oLineData.binarySearchXValues(getMaximumX()));
				// adjust index for high zoom factors where the end-point of a line may line off screen
				endIndex = endIndex+2;
				// make sure we don't run off the edge of the array
				endIndex = Math.min(oLineData.getNumberOfPoints(), endIndex);
				int cnt = 0;
				for (int a=startIndex; a<endIndex; a++)
				{
					aiXCoordinates[cnt] = xUnitToPixel.compute(oLineData.getXValue(a));
					aiYCoordinates[cnt] = iHeight - yUnitToPixel.compute(oLineData.getYValue(a));
					cnt++;
				}
				
				g.setColor(state.color);
				g.setStroke(state.stroke);
				
				final Rectangle clip = g.getClipBounds();
				if (clip==null) {
					g.drawPolyline(aiXCoordinates, aiYCoordinates, cnt);
				} else {
					// only draw up till the end of the clip region
					final int endX = clip.x + clip.width;
					int clippedCnt = Math.abs(Arrays.binarySearch(aiXCoordinates, endX));
					// make sure we don't run off the end of the array
					clippedCnt = Math.min(clippedCnt, cnt);
					g.drawPolyline(aiXCoordinates, aiYCoordinates, clippedCnt);
				}
				
				GraphObjectPool.checkIn(aiXCoordinates);
				GraphObjectPool.checkIn(aiYCoordinates);
			}
		}
	}
	
	/**
	 * Notify the graph that the data has changed.
	 * 
	 * Note that this tells the graph that the Y data has changed, but the graph will assume that the 
	 * X data has remained the same.
	 */
	public void graphDataChanged()
	{
		synchronized (cacheLock)
		{
			bCacheChanged = true;
		}
		if (!bFrameRepaintLimited && isVisible())
		{
			repaint();
		}
	}
	
	/**
	 * Activate optimised drawing mode. 
	 * This requires that you notify the graph that the data has changed by calling
	 * graphDataChanged().
	 * It also requires that the x-coordinate data be sorted.
	 */
	public void setOptimizedMode(boolean bModeOn)
	{
		bOptimizedDrawMode = bModeOn;
	}
	
	/**
	 * Optimised drawing mode
	 */
	private void drawGraphOpt(Graphics2D g)
	{
		final int iHeight = getHeight();
		final int iWidth = getWidth();
		final PixelUnitConverter.UnitToPixel xUnitToPixel = defaultXUnitToPixel();
		final PixelUnitConverter.UnitToPixel yUnitToPixel = defaultYUnitToPixel();
		
		boolean bCalculateX = false;
		boolean bCalculateY = false;
		synchronized (cacheLock)
		{
			if (bCacheChanged
					|| iCachedWidth!=iWidth
					|| iCachedMinX!=getMinimumX() || iCachedMaxX!=getMaximumX())
			{
				bCalculateX = true;
				iCachedWidth = iWidth;
				iCachedMinX = getMinimumX();
				iCachedMaxX = getMaximumX();
			}
			if (bCalculateX || bCacheChanged
					|| iCachedHeight!=iHeight
					|| iCachedMinY!=getMinimumY() || iCachedMaxY!=getMaximumY())
			{
				bCalculateY = true;
				iCachedHeight = iHeight;
				iCachedMinY = getMinimumY();
				iCachedMaxY = getMaximumY();
			}
			bCacheChanged = false;
		}
		synchronized (aoLineData)
		{
			for (LineState state : aoLineData.values()) {
				if (!state.visible) continue;
				drawLineDataOpt(g, iHeight, iWidth, xUnitToPixel, yUnitToPixel, 
					state,
					bCalculateX, bCalculateY);
			}
		}
	}
	
	/**
	 * Optimised-mode drawing of a single line
	 */
	private void drawLineDataOpt(
							Graphics2D g, 
							final int iHeight, 
							final int iWidth, 
							final PixelUnitConverter.UnitToPixel xUnitToPixel, 
							final PixelUnitConverter.UnitToPixel yUnitToPixel, 
							LineState lineState,
							boolean bCalculateX,
							boolean bCalculateY)
	{
		/* The biggest chunk of work is calculating the coordinates, so
		 * cache the data
		 */
		final CoordinateCacheValue coords;
		
		if (lineState.cache!=null) {
			coords = lineState.cache;
			if (bCalculateX || bCalculateY)
			{
				coords.compute(iHeight, iWidth, xUnitToPixel, yUnitToPixel, lineState.data, getMinimumX(), getMaximumX(), bCalculateX, bCalculateY);
			}
		} else {
			// compute co-ordinate data
			coords = new CoordinateCacheValue();
			lineState.cache = coords;
			// have to calculate both X and Y
			coords.compute(iHeight, iWidth, xUnitToPixel, yUnitToPixel, lineState.data, getMinimumX(), getMaximumX(), true, true);
		}
		
		coords.paint(g, lineState.color, lineState.stroke);
	}

	/**
	 * This will set the data that must be drawn.
	 * Note: this method can be called from off the event thread.
	 */
	public void setGraphData(Object key, AbstractLineData oLineData)
	{
		if (oLineData==null) throw new IllegalStateException("line data may not be null");
		synchronized (aoLineData)
		{
			final LineState line = ensureKeyExists(key);
			line.data = oLineData;
			line.cache = null;
		}
		if (bOptimizedDrawMode)
		{
			synchronized (cacheLock)
			{
				bCacheChanged = true;
			}
		}
		
		if (!bFrameRepaintLimited && isVisible())
		{
			repaint();
		}
	}
	
	/**
	 * This will set the data for one line that must be drawn.
	 * Note: this method can be called from off the event thread.
	 */
	public void setGraphData(Object key, double[] afXValues, double[] afYValues)
	{
		setGraphData(key, new LineDataDouble(afXValues, afYValues));
	}
	
	/**
	 * This will set the data for one line that must be drawn.
	 * Note: this method can be called from off the event thread.
	 */
	public void setGraphData(Object key, float[] afXValues, float[] afYValues)
	{
		setGraphData(key, new LineData(afXValues, afYValues));
	}
	
	/** Redraw the line graph when the data list is received from the server.
	 * @param newData - Data from the server
	 */
	public void setGraphData(Map<? extends Object, ? extends AbstractLineData> newData)
	{
  	synchronized (aoLineData)
  	{
			for (Object key : newData.keySet()) {
				final LineState line = ensureKeyExists(key);
				final AbstractLineData newLine = newData.get(key);
				if (newLine==null) throw new IllegalStateException("illegal: line data for key " + key + " is null");
				line.data = newLine;
			}
  	}
  	
		if (bOptimizedDrawMode)
		{
			synchronized (cacheLock)
			{
				bCacheChanged = true;
			}
		}
		
		if (!bFrameRepaintLimited && isVisible())
		{
			repaint();
		}
	}
	
	/**
	 * Sets the color of a line.
	 */
	public void setLineColor(Object key, Color color)
	{
		synchronized (aoLineData)
		{
			final LineState line = ensureKeyExists(key);
			line.color = color;
		}
		if (!bFrameRepaintLimited && isVisible())
		{
			repaint();
		}
	}
	
	public void setLineVisible(Object key, boolean bVisible)
	{
		synchronized (aoLineData)
		{
			final LineState line = ensureKeyExists(key);
			line.visible = bVisible;
		}
		if (!bFrameRepaintLimited && isVisible())
		{
			repaint();
		}
	}
	
	/**
	 * set the visibility of all the lines
	 */
	public void setLinesVisible(boolean bVisible)
	{
		synchronized (aoLineData)
		{
			for (LineState lineState : aoLineData.values()) {
				lineState.visible = bVisible;
			}
		}
		if (!bFrameRepaintLimited && isVisible())
		{
			repaint();
		}
	}
	
	/**
	 * This method will cause the display to clear.
   * Note: this method should be called from the event thread.
	 */
	@Override
	public void clear()
	{
		synchronized (aoLineData)
		{
			for (LineState state : aoLineData.values()) {
				state.cache = null;
				state.data = NULL_LINE_DATA;
			}
		}
		synchronized (cacheLock)
		{
			bCacheChanged = true;
		}
		repaint();
	}
	
	/**
	 * sets the Graphics2D stroke that is used to draw the line.
	 */
	public final void setLineStroke(Object key, Stroke oStroke)
	{
		final LineState line = ensureKeyExists(key);
		line.stroke = oStroke;
		
		if (!bFrameRepaintLimited && isVisible())
		{
			repaint();
		}
	}
	
	private LineState ensureKeyExists(Object key)
	{
		synchronized (aoLineData)
		{
			LineState state = aoLineData.get(key);
			if (state!=null) {
				return state;
			}
			state = new LineState();
			state.color = allocateLineColor(aoLineData.size());
			state.data = NULL_LINE_DATA;
			aoLineData.put(key, state);
			return state;
		}
	}
	
	@Override
	protected void autoScaleGraph()
	{
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		boolean foundOne = false;
		for (LineState lineState : aoLineData.values())
		{
			if (!lineState.visible) continue;
			final AbstractLineData oLineData = lineState.data; 
			final int numPoints = oLineData.getNumberOfPoints();
			if (numPoints>0)
			{
				foundOne = true;
				minX = Math.min(minX, oLineData.getXValue(0));
				maxX = Math.max(maxX, oLineData.getXValue(oLineData.getNumberOfPoints()-1));
				for (int j=0; j<numPoints; j++)
				{
					minY = Math.min(minY, oLineData.getYValueDouble(j));
					maxY = Math.max(maxY, oLineData.getYValueDouble(j));
				}
			}
		}
		if (foundOne) 
		{
	    zoomIn(minX, maxX, minY, maxY);
		}
		else
		{
			resetZoom();
		}
	}
	
	/**
	 * FIXME move this into a better location, maybe create a GraphDefaults class.
	 * 
	 * allocate a color from the array. If the index is larger than
	 * the array of colors it is wrapped around.
	 */
	public static Color allocateLineColor(int i)
	{
		/* Note: I don't expose the array directly because the array is read-write
		 * and I don't want anybody messing with it.
		 */
		return DEFAULT_LINE_COLORS[i % DEFAULT_LINE_COLORS.length];
	}
	
	private static final Color [] DEFAULT_LINE_COLORS = new Color[]
  		{
  			new Color(0,255,0),         //light green
  			new Color(254,187,75),      //light orange
  			new Color(255,255,255),     //white
  			new Color(0,0,255),         //blue
  			new Color(255,0,255),       //magenta
  			new Color(63,171,135),      //green blue
  			new Color(153,255,255),     //baby blue
  			new Color(255,0,0),         //red
  			new Color(255,102,0),       //dark orange
  			new Color(255,149,149),     //pink
  			new Color(137,41,1),        //dark brown
  			new Color(102,0,153),       //purple
  			new Color(153,102,0),       //light brown
  			new Color(220,118,254),     //violet
  			new Color(117,135,255),     //light blue
  			new Color(133,5,54),        //dark purple
  			new Color(200,204,115),     //olive
  			new Color(204,204,204),     //gray
  			new Color(1,20,148),        //dark blue
  			new Color(0,74,0)           //dark green
  		};
	
		/** useful so that we never have to cope with null line data values */
		private static final AbstractLineData NULL_LINE_DATA = new AbstractLineData() {
			@Override
			public int binarySearchXValues(double key)
			{
				return -1;
			}
			@Override
			public int getNumberOfPoints()
			{
				return 0;
			}
			@Override
			public float getXValue(int idx)
			{
				return 0;
			}
			@Override
			public double getXValueDouble(int idx)
			{
				return 0;
			}
			@Override
			public float getYValue(int idx)
			{
				return 0;
			}
			@Override
			public double getYValueDouble(int idx)
			{
				return 0;
			}
		};
}
