package com.peralex.utilities.ui.graphs.lineGraph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;

import com.peralex.utilities.objectpool.GraphObjectPool;
import com.peralex.utilities.ui.graphs.graphBase.PixelUnitConverter;
import com.peralex.utilities.ui.graphs.graphBase.ZoomAdapter;

/**
 * This is an optimized form of AbstractLineGraph which only handles a single line, which does not have changing X-values.
 * Specifically, this is optimized for drawing FFT-type graphs.
 *   
 * This requires that you notify the graph that the data has changed by calling
 * graphDataChanged().
 * 
 * @author Noel Grandin
 */
public class SingleLineGraph extends AbstractLineGraph
{
	
	/**
	 * This stores the data of all the lines that must be drawn.
	 */
	private GeneratedLineData oLineData;
	
	/**
	 * The line color.
	 */
	private Color oLineColor;
	
	/**
	 * cache of calculated coordinate values for drawing
	 */
	private final Object oCacheLock = new Object();
	private int [] aiCachedXCoordinates;
	private int iCachedCnt; // sometimes, only part of the array is used
	private boolean bCacheChanged = false;
	
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
	 * Creates a new instance of cLineGraph
	 */
	public SingleLineGraph()
	{
		oLineColor = new Color(0,255,0); // light green
		
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
		if (oLineData == null)
		{
			return;
		}
		
		final int iHeight = getHeight();
		final int iWidth = getWidth();
		final Rectangle clip = g.getClipBounds();

		boolean bCalculateX = false;
		synchronized (oCacheLock)
		{
			if (iCachedWidth!=iWidth
					|| iCachedMinX!=getMinimumX() || iCachedMaxX!=getMaximumX())
			{
				bCalculateX = true;
				iCachedWidth = iWidth;
				iCachedMinX = getMinimumX();
				iCachedMaxX = getMaximumX();
			}
			if (bCacheChanged
					|| iCachedHeight!=iHeight
					|| iCachedMinY!=getMinimumY() || iCachedMaxY!=getMaximumY())
			{
				iCachedHeight = iHeight;
				iCachedMinY = getMinimumY();
				iCachedMaxY = getMaximumY();
			}
			bCacheChanged = false;
		}

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
		
		if (bCalculateX)
		{
			final PixelUnitConverter.UnitToPixel xUnitToPixel = defaultXUnitToPixel();
			if (aiCachedXCoordinates==null || aiCachedXCoordinates.length!=oLineData.getNumberOfPoints())
			{
				aiCachedXCoordinates = new int[oLineData.getNumberOfPoints()];
			}
			int cnt = 0;
			for (int a=startIndex; a<endIndex; a++)
			{
				aiCachedXCoordinates[cnt] = xUnitToPixel.compute(oLineData.getXValue(a));
				cnt++;
			}
			iCachedCnt = cnt;
		}

		final PixelUnitConverter.UnitToPixel yUnitToPixel = defaultYUnitToPixel();
		final int [] aiYCoordinates = GraphObjectPool.checkOutIntArray(oLineData.getNumberOfPoints());
		final float [] yValues = oLineData.getYValues();
		int cnt = 0;
		for (int a=startIndex; a<endIndex; a++)
		{
			aiYCoordinates[cnt] = iHeight - yUnitToPixel.compute(yValues[a]);
			cnt++;
		}
		
		
		/* if there is a clip region set, take advantage of it and reduce the amount of data to be drawn
		 */
		if (clip==null) {
			g.setColor(oLineColor);
			g.drawPolyline(aiCachedXCoordinates, aiYCoordinates, iCachedCnt);
		} else {
			// only draw up till the end of the clip region
			final int endX = clip.x + clip.width;
			int idx = Math.abs(Arrays.binarySearch(aiCachedXCoordinates, endX));
			// make sure we don't run off the end of the array
			idx = Math.min(idx, iCachedCnt);
			g.setColor(oLineColor);
			g.drawPolyline(aiCachedXCoordinates, aiYCoordinates, idx);
		}
		
		GraphObjectPool.checkIn(aiYCoordinates);
	}
	
	/**
	 * Notify the graph that the data has changed.
	 * 
	 * Note that this tells the graph that the Y data has changed, but the graph will assume that the 
	 * X data has remained the same.
	 */
	public void graphDataChanged()
	{
		synchronized (oCacheLock)
		{
			bCacheChanged = true;
		}
		if (!bFrameRepaintLimited && isVisible())
		{
			repaint();
		}
	}
	
	/**
	 * This will set the data that must be drawn.
	 */
	public void setGraphData(GeneratedLineData oLineData)
	{
		this.oLineData = oLineData;
		synchronized (oCacheLock)
		{
			bCacheChanged = true;
		}
		if (!bFrameRepaintLimited)
		{
			repaint();
		}
	}
	
	/**
	 * This will set the data for one line that must be drawn.
	 */
	public void setGraphData(double startX, double endX, float [] afYValues)
	{
		setGraphData(new GeneratedLineData(startX, endX, afYValues.length, afYValues));
	}
	
	/**
	 * Set the line color.
	 */
	public void setLineColor(Color oLineColor)
	{
		this.oLineColor = oLineColor;
	}
	
	/**
	 * This method will cause the display to clear.
   * Note: this method should be called from the event thread.
	 */
	@Override
	public void clear()
	{
		oLineData = null;
		repaint();
	}
	
	@Override
	protected void autoScaleGraph()
	{
		if (oLineData==null || oLineData.getNumberOfPoints()==0) 
		{
			resetZoom();
			return;
		}
		final double minX = oLineData.getXValueDouble(0);
		final double maxX = oLineData.getXValueDouble(oLineData.getNumberOfPoints()-1);
		final float [] yValues = oLineData.getYValues();
		float minY = Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		for (float element : yValues)
		{
			minY = Math.min(minY, element);
			maxY = Math.max(maxY, element);
		}
    zoomIn(minX, maxX, minY, maxY);
	}
}
