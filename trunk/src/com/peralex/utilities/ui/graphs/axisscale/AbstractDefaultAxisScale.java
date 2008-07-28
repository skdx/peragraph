package com.peralex.utilities.ui.graphs.axisscale;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A base class for providing axis-scale implementations.
 * 
 * FIXME paint() should be using font-relative values and not hard-coded pixel offsets.
 * 
 * FIXME: I should be using strings as labels, and letting the clients worry about formatting.
 * 
 * FIXME: switch to using a double value between 0 and 1 as the position value, so that clients
 *  don't know or worry about insets/borders/component_pixel_width/etc.
 *  
 * @author Roy Emmerich, Noel Grandin
 */
public abstract class AbstractDefaultAxisScale extends AbstractAxisScale
{
	
	/**
	 * These are the different Axis/orientation types.
	 */
	public static final int Y_AXIS = 0, X_AXIS = 1;
	
	/**
	 * Stores the type of the current Axis.
	 */
	private int iAxisOrientation;
	
	/**
	 * Map of pixel_position->label_value
	 */
	private final Map<Integer, Double> oLabelsHashMap = new LinkedHashMap<Integer, Double>();
	
	private boolean offsetFirstLabel = false;
	/**
	 * This is a hack to turn off painting. 
	 * Sometimes useful when I want this component to take up space, but
	 * not draw anything.
	 */
	private boolean bDrawLabels = true;
	
	public AbstractDefaultAxisScale(int iAxisType)
	{
		setOrientation(iAxisType);
	}
	
	public AbstractDefaultAxisScale()
	{
		this(Y_AXIS);
	}
	
	
	/**
	 * calculate my preferred size based on the font used
	 */
	@Override
	public Dimension getPreferredSize()
	{
		if (iAxisOrientation == X_AXIS)
		{
			// multiple by 1.2 to give a little extra space
			final int fontHeight = (int) (getFontMetrics(getFont()).getHeight() * 1.2);
			// only the height matters - my container should resize my width to fit the graph
			return new Dimension(1, fontHeight);
		}
		else
		{
			// base the width on the width of the format used
			final Rectangle2D rect = getFontMetrics(getFont()).getStringBounds(getSampleForPreferredSizing(), getGraphics());
			// only the width matters - my container should resize my height to fit the graph
			return new Dimension((int) (rect.getWidth() * 1.1), 1);
		}
	}

	/** 
	 * provide a default value for generating the preferred size
	 */
	protected abstract String getSampleForPreferredSizing();
	
	/**
	 * When any changes are made to the customisable axis this <code>paintComponent</code> method is called.
	 *
	 * @param g Graphics surface to be repainted after change has occurred
	 */
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
		if (!bDrawLabels || oLabelsHashMap.size() <= 1)
		{
			return;
		}
		
		// Set the tick and label color
		g.setColor(getForeground());
		g.setFont(getFont());
		
		// the distance from the top of the component to the baseline of the x-axis labels.
		final int xAxisVertical = g.getFontMetrics().getAscent() + g.getFontMetrics().getLeading();
		
		// Draw all the labels
		synchronized (oLabelsHashMap)
		{
			for (final Integer oPosition : oLabelsHashMap.keySet())
			{
				final int iPosition = oPosition.intValue();
				final double fLabelValue = oLabelsHashMap.get(oPosition);
				final String sLabel = formatValueAsLabel(fLabelValue);
				final Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(sLabel, g);
				final int stringWidth = (int) stringBounds.getWidth();
				
				if (iAxisOrientation == Y_AXIS)
				{
					final int iXCoord = getWidth() - stringWidth - 2;
					if (iPosition+4 <= 9 && iPosition >= 0)
					{
						g.drawString(sLabel, iXCoord, 9);
					}
					else if (iPosition+4 >= getHeight() && iPosition <= getHeight())
					{
						g.drawString(sLabel, iXCoord, getHeight());
					}
					else if (iPosition >= 0 && iPosition <= getHeight())
					{
						g.drawString(sLabel, iXCoord, iPosition+4);
					}
				}
				else
				{
					if (iPosition >= 0 && iPosition <= getWidth())
					{
						if (offsetFirstLabel)
						{
							if (iPosition==0)
							{
								// the very first label needs to be offset slightly
								g.drawString(sLabel, 0, xAxisVertical);
							}
							else
							{
								int xPos = iPosition - (stringWidth / 2);
								if (xPos+stringWidth>getWidth())
								{
									g.drawString(sLabel, iPosition - stringWidth, xAxisVertical);
								}
								else
								{
									g.drawString(sLabel, iPosition - (stringWidth / 2), xAxisVertical);
								}
							}
						}
						else
						{
							g.drawString(sLabel, 30 + iPosition - (stringWidth / 2), xAxisVertical);
						}
					}
				}
			}
		}
	}
	
	/**
	 * format a value the same way a label is formatted.
	 */
	@Override
	public abstract String formatValueAsLabel(double fVal);
	
	/**
	 * this mode is only meant to be turned on when AbstractDefaultAxisScale is used with GraphWrapper.
	 * It only applies to the x-axis.
	 */
	@Override
	public void setOffsetFirstLabel(boolean b)
	{
		this.offsetFirstLabel = b;
	}
	
	public boolean isOffsetFirstLabel()
	{
		return this.offsetFirstLabel;
	}
	
	/**
	 * Adds the label value and its x position.
	 */
	@Override
	public void addLabel(int iPosition, double fLabelValue)
	{
		synchronized (oLabelsHashMap)
		{
			oLabelsHashMap.put(Integer.valueOf(iPosition), fLabelValue);
		}
	}
	
	/**
	 * Clear the current collection of labels.
	 */
	@Override
	public void clear()
	{
		synchronized (oLabelsHashMap)
		{
			oLabelsHashMap.clear();
		}
	}
	
	/**
	 * This returns the type of the Axis.
	 *
	 * @return Y_AXIS or X_AXIS
	 */
	public int getOrientation()
	{
		return iAxisOrientation;
	}

	/**
	 * Add a label that will be draw at the end of the axis.
	 * This calculates the necessary pixel offset so that the label is visible.
	 */
	@Override
	public void addLabelAtEnd(double fLabelValue)
	{
		final String sLabel = formatValueAsLabel(fLabelValue).trim();
		final int iPos;
		if (iAxisOrientation==X_AXIS)
		{
			Rectangle2D rect = getFontMetrics(getFont()).getStringBounds(sLabel, getGraphics());
			// note: the /2 factor compensates for adjustments the painting method makes.
			iPos = (int) (getWidth() - (rect.getWidth()/2));
		}
		else
		{
			iPos = getHeight();
		}
		addLabel(iPos, fLabelValue);
	}
	
	/**
	 * 
	 * @param iAxisType Y_AXIS or X_AXIS
	 */
	public void setOrientation(int iAxisType)
	{
		this.iAxisOrientation = iAxisType;
	}

	/**
	 * this is a hack to turn off painting. 
	 * Sometimes useful when I want this component to take up space, but
	 * not draw anything.
	 */
	public void setDrawLabels(boolean bDraw)
	{
		this.bDrawLabels = bDraw;
	}
	
	public boolean isDrawLabels()
	{
		return this.bDrawLabels;
	}
}
