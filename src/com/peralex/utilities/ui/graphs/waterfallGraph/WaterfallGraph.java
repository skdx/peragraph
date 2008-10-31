package com.peralex.utilities.ui.graphs.waterfallGraph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

import com.peralex.sharedlibs.dsphostl.TimeStamp;
import com.peralex.utilities.ValueFormatter;
import com.peralex.utilities.ui.ScrollableBufferedImage;
import com.peralex.utilities.ui.graphs.graphBase.Cursor;
import com.peralex.utilities.ui.graphs.graphBase.ZoomDrawSurface;

/**
 * A base waterfall graph that uses a ScrollableBufferedImage for display.
 * 
 * @author Noel Grandin
 */
public class WaterfallGraph extends ZoomDrawSurface
{ 
	
	public static enum TimeDisplayPrecision {
		MICRO_SEC, MILLI_SEC, DECI_SEC;
	}
	
	/**
	 * Variables used in the drawing of the Waterfall.
	 */
	private final LinkedList<TimeStamp> oTimeStampList = new LinkedList<TimeStamp>();
	private final ScrollableBufferedImage oImage = new ScrollableBufferedImage();
	private TimeStamp oAmplitudeTimeStamp = new TimeStamp(100);
	private long lHalfAmpFramePeriod_usec = 100;
	/**
	 * Variables used for timestamp label drawing
	 */
	private Font oLabelFont;
	private int iTimeStampStringSpacer = 0;
	private boolean bPaintTimestampLabels = false;
	private TimeDisplayPrecision eTimeLabelPrecision = TimeDisplayPrecision.MILLI_SEC;
	/**
	 * Number of pixels gap between timestamp labels
	 */
	private final int iTimestampLabelGap_px;
	
	private static final float GRID_Y_MAX = 10000;
	
  /** 
   * Creates a new instance of cAmplitudeWaterfallGraph 
   */
  public WaterfallGraph()
  {
  	// set the Y axis limits. Using zero to one as a range makes certain calculations on cursors easier.
  	super.setGridYMinMax(0, GRID_Y_MAX);
  	
		this.oLabelFont = getFont().deriveFont(Font.BOLD);
		this.iTimestampLabelGap_px = getFontMetrics(oLabelFont).getHeight() * 3;
		
		setDrawingOrder(new Object[] { GRAPH_DRAWSURFACE, CURSOR_DRAWSURFACE });
		
		// By default, zoom is off. Normally if client code turns this on, it also links the zoom
		// on the waterfall with the zoom on a line graph.
		setZoomEnabled(false);

		// clear out the other components - we don't need them - they're related to line-graph stuff
		oPopupMenu.removeAll();
  }
  
  @Override
  public void setGridYMinMax(double fMinimumY, double fMaximumY)
  {
  	// don't mess with this because it upsets cursor position calculations
  	throw new IllegalStateException("you may not mess with a waterfall's grid Y min/max. You should probably be calling setAmplitudeValueRange()");
  }
  
  @Override
  public void setGridMinMax(double fMinimumX, double fMaximumX, double fMinimumY, double fMaximumY)
  {
  	// don't mess with this because it upsets cursor position calculations
  	throw new IllegalStateException("you may not mess with a waterfall's grid Y min/max");
  }
  
	/**
	 * This will draw the Graph
	 */
  @Override
  protected void drawGraph(Graphics2D g)
  {		
		final AffineTransform prevTransform = g.getTransform();

		if (isZoomed())
		{
			/* Notes
			 * (1) We are both transforming this image such that we take a section of the data and scale
			 *    it to fit the whole available display width.
			 * (2) We are only transforming along the x-axis
			 */
			
			final double min = getMinimumX();
			final double max = getMaximumX();
			final double minZoomLimit = getMinimumXZoomLimit();
			final double maxZoomLimit = getMaximumXZoomLimit();
			final double totalXRange = maxZoomLimit - minZoomLimit;
			
			final float imageWidth_px = oImage.getWidth();
			final float displayWidth_px = getWidth();
			final float halfBinWidth_px = 0.5f;
			
			// move the image such the zoomed portion ends up with it's left edge on the left of the screen.
			// note: we adjust by half a bin width so that the points on the FFT line up nicely with the
			// centre of bins on the waterfall when zooming.
			final double translate_px = - ((min - minZoomLimit) / totalXRange) * imageWidth_px 
												- halfBinWidth_px;

			/* Scale zoomed portion to fit available space */
			final double zoomedWidthBeforeScaling_px = (max - min) / totalXRange * imageWidth_px;
			final double scaling = displayWidth_px / zoomedWidthBeforeScaling_px;
			
			/* The Graphics2D class has the rule "last-specified-first-applied".
			 * So in order to translate and then scale, we need to first call scale() and then translate(). */
      g.scale(scaling, 1.0);
      g.translate(translate_px, 0);
    }
		else
		{
			/* Scale image to fit available space - useful when the width of the
			 * data we get from the server does not match the screen width. */
			if (getWidth() != oImage.getWidth())
			{
				g.scale(getWidth() / (float) oImage.getWidth(), 1.0);
			}
		}

		oImage.drawImageTo(g, this);

		// restore previous transform
		g.setTransform(prevTransform);
		
		// Draw all the timestamps on the left of the waterfall---------------------
		if (bPaintTimestampLabels)
		{
			g.setColor(Color.YELLOW);
			g.setFont(oLabelFont);
			
			final int iCurrentHeight = getHeight();
			int iStringYIndex = 0;
			synchronized (oTimeStampList)
			{
				while ((iStringYIndex + iTimeStampStringSpacer) < oTimeStampList.size()
						&& (iStringYIndex + iTimeStampStringSpacer) < iCurrentHeight)
				{
					TimeStamp oTimeStamp = oTimeStampList.get(iStringYIndex + iTimeStampStringSpacer);
					String sTimeString = formatTimeString(oTimeStamp, eTimeLabelPrecision);
					g.drawString(sTimeString, 1, iCurrentHeight - (iStringYIndex + iTimeStampStringSpacer + 1));
					iStringYIndex += iTimestampLabelGap_px;
				}
			}
		}
		// --------------------------------------------------------------------------
  }

  protected static final String formatTimeString(TimeStamp oTimeStamp, TimeDisplayPrecision iPrecision)
  {
		String sTimeString;
		switch (iPrecision) {
		case MICRO_SEC: 
			sTimeString = ValueFormatter.formatTime(oTimeStamp.getTime_msec(), oTimeStamp.getTime_usec());
			break;
		case MILLI_SEC:
			sTimeString = ValueFormatter.formatTime(oTimeStamp.getTime_msec());
			break;
		case DECI_SEC:
			sTimeString = ValueFormatter.formatTime(oTimeStamp.getTime_msec());
			sTimeString = sTimeString.substring(0, sTimeString.length()-2); // strip the last 2 digits off
			break;
		default : throw new IllegalStateException("unknown precision " + iPrecision);
		}
  	return sTimeString;
  }
  
  protected final void addWaterfallLine(TimeStamp oTimeStamp, int[] aiImageData)
  {
  	addWaterfallLine(oTimeStamp, aiImageData, aiImageData.length);
  }
  
  /**
   * Set the amplitude data for the graph that must be drawn.
	 * @param imageDataLen the length of data in the array to use
   */
  protected final void addWaterfallLine(TimeStamp oTimeStamp, int[] aiImageData, int imageDataLen)
  {
		if (!oImage.isImageCreated() || oImage.getWidth()!=imageDataLen)
		{
			oImage.resizeExisting(imageDataLen, Math.max(oImage.getHeight(), 1));
		}
		
		synchronized (oTimeStampList)
		{
			final int desiredSize = Math.max(getHeight()-1, 0);
			while (oTimeStampList.size()>desiredSize)
			{
				oTimeStampList.removeLast();
			}
			oTimeStampList.addFirst(oTimeStamp);
			
			iTimeStampStringSpacer++;
			if (iTimeStampStringSpacer > iTimestampLabelGap_px)
			{
				iTimeStampStringSpacer -= iTimestampLabelGap_px;
			}
		}
		
		
		if (oAmplitudeTimeStamp == null || oTimeStamp.after(oAmplitudeTimeStamp))
		{
			if (oAmplitudeTimeStamp != null)
			{
				lHalfAmpFramePeriod_usec = oTimeStamp.subtract(oAmplitudeTimeStamp).getPeriod_usec();
			}
			oAmplitudeTimeStamp = oTimeStamp;
			// Move the display one pixel up
			scrollUp();
		}
		else if (oTimeStamp.before(oAmplitudeTimeStamp))
		{
			oAmplitudeTimeStamp = oTimeStamp;
			// Move the display one pixel down
			scrollDown();
		}

		oImage.drawRGB_Bottom(aiImageData, imageDataLen);
		
		repaint();
  }

  protected void scrollUp()
  {
		// Move the display one pixel up
		oImage.scrollUp();
  }
  
  protected void scrollDown()
  {
		// Move the display one pixel down
		oImage.scrollDown();
  }
  
  /**
   * used to match up data arriving at different times. For example, we may get detection data
   * much later than amplitude data.
   */
  protected final boolean isTimeWithinOneFramePeriod(long lTime_usec)
  {
		final long lCurrentAmpTime_usec = oAmplitudeTimeStamp.getPeriod_usec();
		final boolean xxx = (lTime_usec >= (lCurrentAmpTime_usec-lHalfAmpFramePeriod_usec)	&&
				lTime_usec <= (lCurrentAmpTime_usec+lHalfAmpFramePeriod_usec));
		return xxx;
  }
  
  /**
   * used to match up data arriving at different times. For example, we may get detection data
   * much later than amplitude data.
   */
  protected final boolean isTimeWithinTwoFramePeriods(long lTime_usec)
  {
		final long lCurrentAmpTime_usec = oAmplitudeTimeStamp.getPeriod_usec();
		final boolean xxx = (lTime_usec >= (lCurrentAmpTime_usec-lHalfAmpFramePeriod_usec*2)	&&
				lTime_usec <= (lCurrentAmpTime_usec+lHalfAmpFramePeriod_usec));
		return xxx;
  }
  
  protected final void setBufferedImageColor(Color c)
  {
		oImage.setColor(c);
  }
  
  protected final void drawBufferedImagePixelBottom(int i)
  {
		oImage.drawPixelBottom(i);
  }
  
  protected final void drawBufferedImagePixelBottomRelative(int i, int relative)
  {
		oImage.drawPixelBottomRelative(i, relative);
  }
  
  /**
   * draws an extra marker line over the current bottom line (i.e. the newest data) of the waterfall .
   */
	public void drawMarker(long lStartFrequency_Hz, long lStopFrequency_Hz, Color color)
	{
		oImage.setColor(color);
		// remember that we are drawing on the un-zoomed image.
		final int xStart = frequencyHzToX(lStartFrequency_Hz); 
		final int xEnd =  frequencyHzToX(lStopFrequency_Hz);
		oImage.drawLineBottom(xStart, xEnd);
	}

	private final int frequencyHzToX(long lFrequency_Hz) {
		return (int) Math.round((lFrequency_Hz - getMinimumXZoomLimit()) / (getMaximumXZoomLimit() - getMinimumXZoomLimit()) * oImage.getWidth());
	}
	
	@Override
	public void setGridXMinMax(double fMinimumX, double fMaximumX)
	{
		final boolean bNeedToClear = getMinimumXZoomLimit()!=fMinimumX || getMaximumXZoomLimit()!=fMaximumX;
		super.setGridXMinMax(fMinimumX, fMaximumX);
		// if the x axis changes, the image needs to be cleared because the preceding data won't
		// match the new data
		if (bNeedToClear)
		{
			clearImage();
			synchronized (oTimeStampList)
			{
				oTimeStampList.clear();
				iTimeStampStringSpacer = 0;
			}
		}
	}

  /**
   * Event for componentResized.
   */
  @Override
  public void componentResized(ComponentEvent e)
	{
		super.componentResized(e);
		
		if (!oImage.isImageCreated())
		{
			oImage.setSizeAndClear(getWidth(), getHeight());
			synchronized (oTimeStampList)
			{
				oTimeStampList.clear();
				iTimeStampStringSpacer = 0;
			}
		}
		else if (oImage.getHeight() != getHeight())
		{
			// we only care about height changes - we can scale in the painting code for width changes
			oImage.resizeExisting(oImage.getWidth(), getHeight());
		}
    
		repaint();
	}
	
	/**
	 * Clear the display.
	 */
	@Override
	public void clear()	
	{
		if (isShowing())
		{
			clearImage();
			synchronized (oTimeStampList)
			{
				oTimeStampList.clear();
				iTimeStampStringSpacer = 0;
			}
		}
	
		repaint();
	}	
	
	protected void clearImage()
	{
		oImage.clearImage();
	}
	
	/**
	 * translate a co-ordinate into a timestamp.
	 * 
	 * @return time in milliseconds, -1 if nothing at that coordinate
	 */
	public final long getTimeForYCoordinate(int iYCoord)
	{
		TimeStamp ts = getTimeStampForYCoordinate(iYCoord);
		if (ts==null) {
			return -1;
		} else {
			return ts.getTime_msec();
		}
	}
	
	/**
	 * translate a co-ordinate into a timestamp.
	 * 
	 * @return null if nothing at that coordinate
	 */
	public final TimeStamp getTimeStampForYCoordinate(int iYCoord)
	{
		int iYCoordinate = getHeight() - iYCoord;
		
		synchronized (oTimeStampList)
		{
			if (iYCoordinate >= 0 && iYCoordinate < oTimeStampList.size())
			{
				return oTimeStampList.get(iYCoordinate);
			}
		}
		return null;
	}
	
	/**
	 * translate a <code>cCursor</code> y-co-ordinate into a timestamp.
	 * 
	 * @return time in milliseconds, -1 if nothing at that coordinate
	 */
	public final long getTimeForCursor(Cursor cursor)
	{
		return getTimeForYCoordinate((int) (Math.round(cursor.getYValue() / GRID_Y_MAX * getHeight())));
	}
	
	public final void setPaintTimestampLabels(boolean bPaint)
	{
		this.bPaintTimestampLabels = bPaint;
	}
	
	public final boolean isPaintTimestampLabels()
	{
		return this.bPaintTimestampLabels;
	}
	
	public final Font getLabelFont()
	{
		return this.oLabelFont;
	}
	
	public final void setLabelFont(Font f)
	{
		this.oLabelFont = f;
	}
	
	/**
	 * Set the labels to show either millisecond or microsecond precision. Millisecond by default.
	 */
	public final void setTimeLabelPrecision(TimeDisplayPrecision eTimeLabelPrecision)
	{
		this.eTimeLabelPrecision = eTimeLabelPrecision;
	}

	public final TimeDisplayPrecision getTimeLabelPrecision()
	{
		return this.eTimeLabelPrecision;
	}
	
	protected final TimeStamp getTimeStampListEntry(int idx)
	{
		synchronized (oTimeStampList)
		{
			return oTimeStampList.get(idx);
		}
	}
	
	protected final int getTimeStampListSize()
	{
		synchronized (oTimeStampList)
		{
			return oTimeStampList.size();
		}
	}
}
