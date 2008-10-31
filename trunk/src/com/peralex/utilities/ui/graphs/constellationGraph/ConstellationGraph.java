package com.peralex.utilities.ui.graphs.constellationGraph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;

import com.peralex.utilities.ui.graphs.graphBase.PixelUnitConverter;
import com.peralex.utilities.ui.graphs.graphBase.ZoomDrawSurface;

/**
 * 
 * @author Andre
 */
public class ConstellationGraph extends ZoomDrawSurface
{

	/**
   * This is the color used for drawing the Points.
   */
	private Color oPointColor = Color.GREEN;

	/**
   * This array stores the X Values of all the points.
   */
	private float[] afXValues;

	/**
   * This array stores the Y Values of all the points.
   */
	private float[] afYValues;

	/**
   * Creates a new instance of cConstellationGraph
   */
	public ConstellationGraph()
	{
		setSquareZoomEnabled(true);
	}

	/**
   * This will set the data that must be drawn.
   */
	@Override
	protected void drawGraph(Graphics2D g)
	{
		if (afXValues != null && afYValues != null)
		{
			final PixelUnitConverter.UnitToPixel xUnitToPixel = defaultXUnitToPixel();
			final PixelUnitConverter.UnitToPixel yUnitToPixel = defaultYUnitToPixel();
			g.setColor(oPointColor);
			for (int i = 0; i < afXValues.length; i++)
			{
				final int iXCoordinate = xUnitToPixel.compute(afXValues[i]);
				final int iYCoordinate = getHeight()
						- yUnitToPixel.compute(afYValues[i]);
				g.fillRect(iXCoordinate - 1, iYCoordinate - 1, 2, 2);
			}
		}
	}

	/**
   * This will set the data for one line that must be drawn.
   */
	public void setGraphData(float[] afXValues, float[] afYValues)
	{
		this.afXValues = afXValues;
		this.afYValues = afYValues;

		repaint();
	}

	/**
   * This sets the color of the points.
   */
	public void setPointColor(Color oPointColor)
	{
		this.oPointColor = oPointColor;
	}
	
	/**
   * Event for componentResized.
   */
	@Override
	public void componentResized(ComponentEvent e)
	{
		setGridSpacing(getWidth() / 4, getHeight() / 4);
		super.componentResized(e);
	}

	/**
   * This method will cause the display to clear.
   */
	@Override
	public void clear()
	{
		afXValues = null;
		afYValues = null;

		repaint();
	}
}
