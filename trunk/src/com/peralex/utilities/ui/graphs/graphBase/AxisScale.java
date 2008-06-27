package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

/**
 * FIXME: I should be using strings as labels, and letting the clients worry about formatting.
 * 
 * FIXME: switch to using a float value between 0 and 1 as the position value, so that clients
 *  don't know or worry about insets/borders/component_pixel_width/etc.
 *  
 * FIXME: dump getLabelFont()/setLabelFont(). Just use the value stored in setFont()/getFont()
 *  
 * @author Roy Emmerich, Noel Grandin
 */
public class AxisScale extends JPanel
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
	 * DecimalFormat used for drawing the labels.
	 */
	private DecimalFormat oNumberFormat;
	
	/**
	 * LinkedHashMap that stores all the Labels.
	 */
	private final Map<Integer, Float> oLabelsHashMap = new LinkedHashMap<Integer, Float>();
	
	private Color oLabelColor = Color.BLACK;
	private Font oLabelFont;
	
	private boolean offsetFirstLabel = false;
	/**
	 * This is a hack to turn off painting. 
	 * Sometimes useful when I want this component to take up space, but
	 * not draw anything.
	 */
	private boolean bDrawLabels = true;
	
	/**
	 * Creates new form Axis.
	 */
	public AxisScale()
	{
		this(Y_AXIS);
		oLabelFont = getFont();
	}
	
	/**
	 * Create an axis of your choice.
	 */
	public AxisScale(int iAxisType)
	{
		oLabelFont = getFont();
		setOrientation(iAxisType);
	}
	
	/**
	 * set the number format. Setting it to null will reset to the default.
	 * @param df
	 */
	public void setNumberFormat(DecimalFormat df)
	{
		if (df==null)
		{
			oNumberFormat = defaultNumberFormat(iAxisOrientation);
		}
		else
		{
			this.oNumberFormat = df;
		}
	}
	
	public void setLabelColor(Color oColor)
	{
		this.oLabelColor = oColor;
	}
	
	public Color getLabelColor()
	{
		return this.oLabelColor;
	}
	
	public void setLabelFont(Font oFont)
	{
		this.oLabelFont = oFont;
	}
	
	public Font getLabelFont()
	{
		return this.oLabelFont;
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
			final int fontHeight = (int) (getFontMetrics(oLabelFont).getHeight() * 1.2);
			// only the height matters - my container should resize my width to fit the graph
			return new Dimension(1, fontHeight);
		}
		else
		{
			// base the width on the width of the format used
			final Rectangle2D rect = getFontMetrics(oLabelFont).getStringBounds("-" + oNumberFormat.toPattern(), getGraphics());
			// only the width matters - my container should resize my height to fit the graph
			return new Dimension((int) (rect.getWidth() * 1.1), 1);
		}
	}
	
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
		g.setColor(oLabelColor);
		g.setFont(oLabelFont);
		
		// the distance from the top of the component to the baseline of the x-axis labels.
		final int xAxisVertical = g.getFontMetrics().getAscent() + g.getFontMetrics().getLeading();
		
		// Draw all the labels
		synchronized (oLabelsHashMap)
		{
			for (final Integer oPosition : oLabelsHashMap.keySet())
			{
				final int iPosition = oPosition.intValue();
				final float fLabelValue = oLabelsHashMap.get(oPosition).floatValue();
				final String sLabel = oNumberFormat.format(fLabelValue);
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
	public String formatValueAsLabel(float fVal)
	{
		return oNumberFormat.format(fVal);
	}
	
	/**
	 * this mode is only meant to be turned on when cAxis is used with cGraphWrapper.
	 * It only applies to the x-axis.
	 */
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
	public void addLabel(int iPosition, float fLabelValue)
	{
		synchronized (oLabelsHashMap)
		{
			oLabelsHashMap.put(Integer.valueOf(iPosition), new Float(fLabelValue));
		}
	}
	
	/**
	 * Clear the current collection of labels.
	 */
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
	public void addLabelAtEnd(float fLabelValue)
	{
		final String sLabel = oNumberFormat.format(fLabelValue).trim();
		final int iPos;
		if (iAxisOrientation==X_AXIS)
		{
			Rectangle2D rect = getFontMetrics(oLabelFont).getStringBounds(sLabel, getGraphics());
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
	public final void setOrientation(int iAxisType)
	{
		this.iAxisOrientation = iAxisType;

		oNumberFormat = defaultNumberFormat(iAxisType);
	}

	private static DecimalFormat defaultNumberFormat(int iAxisType)
	{
		if (iAxisType==X_AXIS)
		{
			return new DecimalFormat("###0.###");
		}
		else
		{
			// most of the time, the Y-axis is decibels, and we only need a range of 0.0-140.0
			return new DecimalFormat("##0.#");
		}
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
