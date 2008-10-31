package com.peralex.utilities.ui.graphs.waterfallGraph;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.peralex.sharedlibs.dsphostl.TimeStamp;

/**
 * A waterfall graph to draw amplitude data only on.
 * 
 * @author Andre
 */
public class AmplitudeWaterfallGraph extends AmplitudeDetectionWaterfallGraph
{
	//////////////////////////////////////////////////////////////////////////////
	//  Variables used in the drawing of the Waterfall.
	//////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////////////////////////////////////////////////
	//  Variables used in the drawing of the time cursors and timestamps.
	//////////////////////////////////////////////////////////////////////////////
	private final Polygon oCursorArrow1;
	private final Polygon oCursorArrow2;
	private boolean bCursorArrow1Selected = false;
	private boolean bCursorArrow2Selected = false;
	private boolean bTimeCursorsEnabled = false;
	private TimeDisplayPrecision eTimeCursorPrecision = TimeDisplayPrecision.MILLI_SEC;
  
  /** 
   * Creates a new instance of cAmplitudeWaterfallGraph 
   */
  public AmplitudeWaterfallGraph()
  {
		int iCursorY = 10;
		oCursorArrow1 = new Polygon(
				new int[]{15, 8, 8, 0, 0, 8, 8},
				new int[]{iCursorY, iCursorY-7, iCursorY-3, iCursorY-3, iCursorY+3, iCursorY+3, iCursorY+7},
				7);
		iCursorY = 20;
		oCursorArrow2 = new Polygon(
				new int[]{15, 8, 8, 0, 0, 8, 8},
				new int[]{iCursorY, iCursorY-7, iCursorY-3, iCursorY-3, iCursorY+3, iCursorY+3, iCursorY+7},
				7);
  }
  
	/**
   * This will draw the Graph.
   * @param g The graphics object.
   */
	@Override
	protected void drawGraph(Graphics2D g)
  {	
		super.drawGraph(g);
		
		if (bTimeCursorsEnabled)
		{
			final int iHeight = getHeight();
			final int iWidth = getWidth();
			
			drawArrow(g, Color.RED, oCursorArrow1, iWidth);
			int iCursorYPosition_1 = oCursorArrow1.ypoints[0];
			
			drawArrow(g, Color.BLUE, oCursorArrow2, iWidth);
			int iCursorYPosition_2 = oCursorArrow2.ypoints[0];

			g.setColor(Color.WHITE);
			g.setFont(getLabelFont());
			
			final int iFrameCount = getTimeStampListSize();
			final int iTimeStampIndex_1 = iHeight-iCursorYPosition_1-1;
			
			if (iFrameCount > iTimeStampIndex_1 && iTimeStampIndex_1 > -1)
			{
				final int fontHeight = g.getFontMetrics().getHeight();
				
				// get the selected timestamp and format it
				final TimeStamp oTime_1 = getTimeStampListEntry(iTimeStampIndex_1);
				drawTimeStampString(g, iWidth, fontHeight, iCursorYPosition_1, oTime_1);
				
				// draw the difference timestamp
				final int iTimeStampIndex_2 = iHeight-iCursorYPosition_2-1;
				if (iFrameCount > iTimeStampIndex_2)
				{
					TimeStamp oTime_2 = getTimeStampListEntry(iTimeStampIndex_2);
					oTime_2 = oTime_1.after(oTime_2) ? oTime_1.subtract(oTime_2) : oTime_2.subtract(oTime_1);
					drawTimeStampString(g, iWidth, fontHeight, iCursorYPosition_2, oTime_2);
				}
			}
		}
  }
	
	private void drawTimeStampString(Graphics2D g, int iWidth, int fontHeight, int iCursorYPosition, TimeStamp oTime)
	{
		// get the selected timestamp and format it
		String sTimeString = formatTimeString(oTime, eTimeCursorPrecision);
		final int iStringLength_1 = g.getFontMetrics().stringWidth(sTimeString);
		// move it below the line if it's drawn off screen
		iCursorYPosition = iCursorYPosition<fontHeight ? iCursorYPosition+fontHeight-1 : iCursorYPosition-1;
		g.drawString(sTimeString, iWidth-iStringLength_1, iCursorYPosition);
	}
	
	private static void drawArrow(Graphics2D g, Color color, Polygon arrowPoly, int iWidth)
	{
		g.setColor(color);
		g.fillPolygon(arrowPoly);
		final int iCursorYPosition = arrowPoly.ypoints[0];
		g.drawLine(0, iCursorYPosition, iWidth-1, iCursorYPosition);
	}
	
  /**
   * Event for componentResized.
   */  
	@Override
  public void componentResized(ComponentEvent e)
	{
		super.componentResized(e);
		int iHeight = getHeight() < 1 ? 1 : getHeight();

		// reset the cursor locations if the resizing puts them out of display range
		if (oCursorArrow1.ypoints[0] < iHeight || oCursorArrow2.ypoints[0] < iHeight) {
			setYCoordinateArrow_1(10);
			setYCoordinateArrow_2(20);
		}
	}	
	
	/**
	 * If enabled two time cursors will be draw, one showing the time of it's frame, the other showing the difference.
	 * @param bTimeCursorsEnabled True to enable. False by default.
	 */
	public void setTimeCursorsEnabled(boolean bTimeCursorsEnabled)
	{
		this.bTimeCursorsEnabled = bTimeCursorsEnabled;
	}
	
	/**
	 * Draw time stamps on the graph.
	 * @param bTimeLabelsEnabled True to enable. False by default.
	 */
	public void setTimeLabelsEnabled(boolean bTimeLabelsEnabled)
	{
		setPaintTimestampLabels(bTimeLabelsEnabled);
	}
	
	/**
	 * Set the cursor to show either millisecond or microsecond precision. Millisecond by default.
	 */
	public void setTimeCursorsPrecision(TimeDisplayPrecision eTimeCursorPrecision)
	{
		this.eTimeCursorPrecision = eTimeCursorPrecision;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//  Mouse and Key events for this component
	//////////////////////////////////////////////////////////////////////////////
	@Override
  public void mousePressed(MouseEvent e)
  {
		if (oCursorArrow1.contains(e.getPoint()) && bTimeCursorsEnabled)
		{
			bCursorArrow1Selected = true;
		}
		else if (oCursorArrow2.contains(e.getPoint()) && bTimeCursorsEnabled)
		{
			bCursorArrow2Selected = true;
		}
		super.mousePressed(e);
  }
	
	@Override
  public void mouseReleased(MouseEvent e)
  {
		bCursorArrow1Selected = false;
		bCursorArrow2Selected = false;
		super.mouseReleased(e);
	}
	
	@Override
  public void mouseMoved(MouseEvent e)
  {
		if (oCursorArrow1.contains(e.getPoint()) && bTimeCursorsEnabled)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		else if (oCursorArrow2.contains(e.getPoint()) && bTimeCursorsEnabled)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		else if (!getCursor().equals(getDefaultCrossHairCursor()) && bTimeCursorsEnabled)
		{
			setCursor(getDefaultCrossHairCursor());
		}
		super.mouseMoved(e);
  }
	
	@Override
  public void mouseDragged(MouseEvent e)
  {
		final int iCursorY = e.getY();
		if (bCursorArrow1Selected && bTimeCursorsEnabled)
		{
			setYCoordinateArrow_1(iCursorY);
		}
		else if (bCursorArrow2Selected && bTimeCursorsEnabled)
		{
			setYCoordinateArrow_2(iCursorY);
		}
		super.mouseDragged(e);
  }
	
	@Override
  public void keyPressed(KeyEvent e)
  {
    super.keyPressed(e);
		if (!bTimeCursorsEnabled)
		{
			return;
		}
		
		final int iKeyPressed = e.getKeyCode();
		
		// Move the Cursors with the arrow keys
		switch (iKeyPressed)
		{
			case KeyEvent.VK_UP:
				if (e.isShiftDown())
				{
					setYCoordinateArrow_2(oCursorArrow2.ypoints[0]-1);
				}
				else
				{
					setYCoordinateArrow_1(oCursorArrow1.ypoints[0]-1);
				}
				break;
			case KeyEvent.VK_DOWN:
				if (e.isShiftDown())
				{
					setYCoordinateArrow_2(oCursorArrow2.ypoints[0]+1);
				}
				else
				{
					setYCoordinateArrow_1(oCursorArrow1.ypoints[0]+1);
				}
				break;
		}
  }
	
	//////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////////////////////////////////////////////////
	// Methods for setting the coordinates of the cursors
	//////////////////////////////////////////////////////////////////////////////
	private void setYCoordinateArrow_1(int yCoordinate)
	{
		if (yCoordinate<0)
		{
			yCoordinate = 0;
		}
		else if (yCoordinate>=getHeight())
		{
			yCoordinate = getHeight()-1;
		}
		oCursorArrow1.ypoints[0] = yCoordinate;
		oCursorArrow1.ypoints[1] = yCoordinate-7;
		oCursorArrow1.ypoints[2] = yCoordinate-3;
		oCursorArrow1.ypoints[3] = yCoordinate-3;
		oCursorArrow1.ypoints[4] = yCoordinate+3;
		oCursorArrow1.ypoints[5] = yCoordinate+3;
		oCursorArrow1.ypoints[6] = yCoordinate+7;
		oCursorArrow1.invalidate();
		repaint();
	}
	
	private void setYCoordinateArrow_2(int yCoordinate)
	{
		if (yCoordinate<0)
		{
			yCoordinate = 0;
		}
		else if (yCoordinate>=getHeight())
		{
			yCoordinate = getHeight()-1;
		}
		oCursorArrow2.ypoints[0] = yCoordinate;
		oCursorArrow2.ypoints[1] = yCoordinate-7;
		oCursorArrow2.ypoints[2] = yCoordinate-3;
		oCursorArrow2.ypoints[3] = yCoordinate-3;
		oCursorArrow2.ypoints[4] = yCoordinate+3;
		oCursorArrow2.ypoints[5] = yCoordinate+3;
		oCursorArrow2.ypoints[6] = yCoordinate+7;
		oCursorArrow2.invalidate();
		repaint();
	}
	//////////////////////////////////////////////////////////////////////////////
}
