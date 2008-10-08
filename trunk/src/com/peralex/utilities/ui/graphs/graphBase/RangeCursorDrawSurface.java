package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Range cursors are solid blocks of colour, spanning a given horizontal range, which are drawn behind the line layer of
 * the graph.
 * 
 * Mostly useful for marking interesting ranges.
 * 
 * @author Andre
 */
public class RangeCursorDrawSurface extends GridDrawSurface
{

	/**
	 * This is the HashMap that stores all the RangeCursors added to this graph
	 */
	private final Map<String, RangeCursor> oRangeCursorsMap = new HashMap<String, RangeCursor>();

	private boolean bRangeCursorsChanged = false;

	/**
	 * This is the font used for the labels.
	 */
	private Font oLabelFont;

	private Color oLabelColor = Color.WHITE;

	/**
	 * This cursor will be drawn last to bring it to the front.
	 */
	private RangeCursor oCurrentFrontRangeCursor = null;

	/**
	 * the offset from the edge that the last range cursor label was drawn at
	 * 
	 * These 2 fields are protected because they are accessed by CursorDrawSurface which tries
	 * to make labels not paint on top of each other.
	 */
	protected int iRangeCursorLabelYOffset;

	/**
	 * true means the next label is at the top, false means the next label is at the bottom.
	 */
	protected boolean bRangeCursorLabelVerticalPosition;

	/**
	 * Creates a new instance of cRangeCursorDrawSurface
	 */
	public RangeCursorDrawSurface()
	{
		oLabelFont = getFont();
	}

	public void setRangeCursorLabelFont(Font oFont)
	{
		this.oLabelFont = oFont;
	}

	public void setRangeCursorLabelColor(Color oColor)
	{
		this.oLabelColor = oColor;
	}

	/**
	 * Paints all the cursors.
	 */
	@Override
	protected void paint(Graphics g, Object iDrawSurfaceID)
	{
		if (RANGE_CURSOR_DRAWSURFACE != iDrawSurfaceID)
		{
			super.paint(g, iDrawSurfaceID);
			return;
		}

		synchronized (oRangeCursorsMap)
		{
			if (oRangeCursorsMap.isEmpty())
				return;
			
			if (bRangeCursorsChanged) {
				calculateRangeCursors();
				bRangeCursorsChanged = false;
			}

			// Draw all the RangeCursors that is currently added
			for (RangeCursor oRangeCursor : oRangeCursorsMap.values())
			{
				g.setColor(oRangeCursor.getColor());

				if (oRangeCursor.isRangeCursorEnabled())
				{
					g.fillRect(oRangeCursor.getCoordinate() - (oRangeCursor.getPixelWidth() / 2), 0,
							oRangeCursor.getPixelWidth(), getHeight());
				}
			}

			// draw the top range cursor
			if (oCurrentFrontRangeCursor != null && oCurrentFrontRangeCursor.isRangeCursorEnabled())
			{
				g.setColor(oCurrentFrontRangeCursor.getColor());
				g.fillRect(oCurrentFrontRangeCursor.getCoordinate()
						- (oCurrentFrontRangeCursor.getPixelWidth() / 2), 0, oCurrentFrontRangeCursor
						.getPixelWidth(), getHeight());
			}

			// Draw Cursors labels that is currently added, ON TOP of all the cursors.
			g.setFont(oLabelFont);
			g.setColor(oLabelColor);
			for (RangeCursor oRangeCursor : oRangeCursorsMap.values())
			{
				if (oRangeCursor.isRangeCursorEnabled() && !oRangeCursor.getLabel().equals(""))
				{
					g.drawString(oRangeCursor.getLabel(), oRangeCursor.getCursorLabelXCoordinate(),
							oRangeCursor.getCursorLabelYCoordinate());
				}
			}
		}
	}

	/**
	 * mark range cursors for re-calculation
	 */
	void invalidateRangeCursors()
	{
		synchronized (oRangeCursorsMap)
		{
			bRangeCursorsChanged = true;
		}
		repaint();
	}

	/**
	 * This will calculate the coordinates of the cursors and their labels. Note: the position of normal cursor labels
	 * interacts with the position of the range cursor labels.
	 */
	private void calculateRangeCursors()
	{
		iRangeCursorLabelYOffset = 2;
		bRangeCursorLabelVerticalPosition = true;
		synchronized (oRangeCursorsMap)
		{
			if (oRangeCursorsMap.isEmpty())
			{
				return;
			}
			// Calculate position of RangeCursors
			for (RangeCursor oRangeCursor : oRangeCursorsMap.values())
			{
				oRangeCursor.setCoordinate(PixelUnitConverter.unitToPixel(true, oRangeCursor.getValue(), 0,
						getWidth(), getMinimumX(), getMaximumX()));
				oRangeCursor.setPixelWidth(PixelUnitConverter.unitToPixel(true, getMinimumX()
						+ oRangeCursor.getWidth(), 0, getWidth(), getMinimumX(), getMaximumX()));
			}

			// Calculate each label's position
			for (RangeCursor oRangeCursor : oRangeCursorsMap.values())
			{
				if (!oRangeCursor.getLabel().equals(""))
				{
					final int iRightBoundry = oRangeCursor.getCoordinate()
							+ (oRangeCursor.getPixelWidth() / 2);
					final int iLeftBoundry = oRangeCursor.getCoordinate()
							- (oRangeCursor.getPixelWidth() / 2);
					Graphics graphics = getGraphics();
					final int iLabelWidth = graphics == null ? 0 : graphics.getFontMetrics().stringWidth(
							oRangeCursor.getLabel());
					final int iXPos;
					if (iRightBoundry > (getWidth() - iLabelWidth - 3))
					{
						iXPos = iLeftBoundry - iLabelWidth;
					}
					else
					{
						iXPos = iRightBoundry + 2;
					}

					final int iLabelHeight = graphics == null ? 0 : graphics.getFontMetrics().getHeight();
					// alternate the positions of the labels between the top and bottom of the graph,
					// increasing the distance from the the edge as we go, to prevent the labels overlapping.
					if (bRangeCursorLabelVerticalPosition)
					{
						oRangeCursor.setCursorLabelCoordinates(iXPos, iRangeCursorLabelYOffset + iLabelHeight);
						bRangeCursorLabelVerticalPosition = false;
					}
					else
					{
						oRangeCursor.setCursorLabelCoordinates(iXPos, getHeight() - iRangeCursorLabelYOffset);
						bRangeCursorLabelVerticalPosition = true;
						iRangeCursorLabelYOffset += iLabelHeight;
					}
				}
			}
		}
	}

	/**
	 * Add a RangeCursor to the graph
	 */
	public void addRangeCursor(String sRangeCursorID, Color oColor, float fResolution, double fWidth)
	{
		addRangeCursor(sRangeCursorID, oColor, fResolution, fWidth, (long) getMinimumX());
	}

	/**
	 * Add a RangeCursor to the graph
	 */
	public void addRangeCursor(String sRangeCursorID, Color oColor, float fResolution, double fWidth,
			double fValue)
	{
		addRangeCursor(new RangeCursor(sRangeCursorID, oColor, fResolution, fWidth,
				fValue));
	}

	/**
	 * Add a RangeCursor to the graph
	 */
	public void addRangeCursor(String sRangeCursorID, Color oColor, float fResolution, double fWidth,
			double fValue, boolean bEnabled)
	{
		addRangeCursor(new RangeCursor(sRangeCursorID, oColor, fResolution, fWidth,
				fValue));
		getRangeCursor(sRangeCursorID).setRangeCursorEnabled(bEnabled);
	}

	/**
	 * Add a RangeCursor to the graph
	 */
	public void addRangeCursor(RangeCursor oRangeCursor)
	{
		oRangeCursor.setRangeCursorDrawSurface(this);
		synchronized (oRangeCursorsMap)
		{
			final String rangeCursorID = oRangeCursor.getRangeCursorID();
			if (oRangeCursorsMap.containsKey(rangeCursorID)) {
				throw new IllegalStateException("already contains range cursor with ID " + rangeCursorID);
			}
			oRangeCursorsMap.put(rangeCursorID, oRangeCursor);
		}
		invalidateRangeCursors();
	}

	/**
	 * returns the first enabled range cursor that overlaps with the given x-coordinate.
	 */
	public RangeCursor getRangeCursorForCoordinate(int x)
	{
		synchronized (oRangeCursorsMap)
		{
			// Calculate all of the RangeCursors
			for (RangeCursor oRangeCursor : oRangeCursorsMap.values())
			{
				if (oRangeCursor.isRangeCursorEnabled())
				{
					final int tmp = oRangeCursor.getPixelWidth() / 2;
					if (oRangeCursor.getCoordinate() - tmp <= x && x <= oRangeCursor.getCoordinate() + tmp)
					{
						return oRangeCursor;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Remove a range cursor from the graph
	 */
	public void removeRangeCursor(String sRangeCursorID)
	{
		if (oCurrentFrontRangeCursor != null
				&& oCurrentFrontRangeCursor.equals(oRangeCursorsMap.get(sRangeCursorID)))
		{
			oCurrentFrontRangeCursor = null;
		}
		synchronized (oRangeCursorsMap)
		{
			oRangeCursorsMap.remove(sRangeCursorID);
		}
		repaint();
	}

	/**
	 * Remove all range cursors from the graph
	 */
	public void removeRangeCursors()
	{
		synchronized (oRangeCursorsMap)
		{
			oRangeCursorsMap.clear();
		}
		repaint();
	}

	/**
	 * This method will return the given RangeCursor.
	 */
	public RangeCursor getRangeCursor(String sRangeCursorID)
	{
		synchronized (oRangeCursorsMap)
		{
			return oRangeCursorsMap.get(sRangeCursorID);
		}
	}

	/**
	 * This method will enable or disable the RangeCursors.
	 */
	public void setRangeCursorsEnabled(boolean bRangeCursorsEnabled)
	{
		synchronized (oRangeCursorsMap)
		{
			for (RangeCursor oRangeCursor : oRangeCursorsMap.values())
			{
				oRangeCursor.setRangeCursorEnabled(bRangeCursorsEnabled);
			}
		}
	}

	public void setRangeCursorToFront(RangeCursor oFrontRangeCursor)
	{
		this.oCurrentFrontRangeCursor = oFrontRangeCursor;
		repaint();
	}

	/**
	 * Return the number of cursors that is currently added to the graph.
	 * 
	 * @return oRangeCursorsMap.size()
	 */
	public int getNumberOfRangeCursors()
	{
		return oRangeCursorsMap.size();
	}

	/**
	 * returns a copy of the set of range cursor keys
	 */
	public Set<String> getRangeCursorKeys()
	{
		synchronized (oRangeCursorsMap)
		{
			return new HashSet<String>(oRangeCursorsMap.keySet());
		}
	}

	/**
	 * This methods sets the maximum and minimum on the Axis.
	 */
	@Override
	public void setGridMinMax(double fMinimumX, double fMaximumX, double fMinimumY, double fMaximumY)
	{
		super.setGridMinMax(fMinimumX, fMaximumX, fMinimumY, fMaximumY);

		invalidateRangeCursors();
	}

	/**
	 * This methods sets the maximum and minimum on the X Axis.
	 */
	@Override
	public void setGridXMinMax(double fMinimumX, double fMaximumX)
	{
		super.setGridXMinMax(fMinimumX, fMaximumX);
		invalidateRangeCursors();
	}

	/**
	 * This methods sets the maximum and minimum on the Y Axis.
	 */
	@Override
	public void setGridYMinMax(double fMinimumY, double fMaximumY)
	{
		super.setGridYMinMax(fMinimumY, fMaximumY);
		invalidateRangeCursors();
	}

	/**
	 * Event for componentResized.
	 */
	@Override
	public void componentResized(ComponentEvent e)
	{
		super.componentResized(e);

		calculateRangeCursors();
	}
}