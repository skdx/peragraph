package com.peralex.utilities.ui.graphs.barGraph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

import com.peralex.utilities.ui.graphs.graphBase.PixelUnitConverter;
import com.peralex.utilities.ui.graphs.graphBase.ZoomDrawSurface;

/**
 * FIXME (Noel) This does not need to extend cGraphBase at all. Rather just extend JComponent.
 * 
 * @author Andre
 */
public class VerticalBarGraph extends ZoomDrawSurface
{

	/**
   * This array stores the data for the bars that must be drawn.
   */
	private float[] afBarData;

	/**
   * This stores the number of bars.
   */
	private int iNumberOfBars = 10;

	/**
   * This is the color that will be used to draw the bars.
   */
	private Color oBarColor = Color.GREEN;

	/**
   * These store the Min and Max values of the Data.
   */
	private float fMinimumDataValue = 0, fMaximumDataValue = 100;

	/**
   * Creates a new instance of cBarGraph
   */
	public VerticalBarGraph()
	{
		setDrawingOrder(new Object [] {GRAPH_DRAWSURFACE});
		setZoomEnabled(false);
		setPopupMenuEnabled(false);

		afBarData = new float[iNumberOfBars];
	}

	/**
   * This will draw the Graph
   */
	@Override
	protected void drawGraph(Graphics2D g)
	{
		if (afBarData == null)
		{
			return;
		}
		g.setColor(oBarColor);
		float fBarHeightValue = (fMaximumDataValue - fMinimumDataValue)
				/ afBarData.length;
		int iBarHeightPixels = PixelUnitConverter.unitToPixel(true,
				getMinimumY() + fBarHeightValue, 0, getHeight(), getMinimumY(),
				getMaximumY());
		int iBarYPixels = 0, iBarLength = 0, iPreviousBarYPixels = getHeight();

		for (int i = 0; i < afBarData.length; i++)
		{
			iBarYPixels = getHeight()
					- PixelUnitConverter.unitToPixel(true, fMinimumDataValue
							+ (fBarHeightValue * (i + 1)), 0, getHeight(), getMinimumY(),
							getMaximumY());
			iBarLength = PixelUnitConverter.unitToPixel(true, afBarData[i], 0,
					getWidth(), getMinimumX(), getMaximumX());

			if ((iBarYPixels + iBarHeightPixels) < iPreviousBarYPixels)
			{
				g.fillRect(0, iBarYPixels, iBarLength, iBarHeightPixels
						- ((iBarYPixels + iBarHeightPixels) - iPreviousBarYPixels));
			} else
			{
				g.fillRect(0, iBarYPixels, iBarLength, iBarHeightPixels);
			}
			iPreviousBarYPixels = iBarYPixels;
		}
	}

	/**
   * This will add to the data that must be drawn.
   * 
   */
	public void addGraphData(float[] afDataValues)
	{
		if (afDataValues.length != getMaximumX())
		{
			setGridMinMax(0, afDataValues.length, fMinimumDataValue,
					fMaximumDataValue);
		}

		float fRatio = Math.abs(fMaximumDataValue - fMinimumDataValue)
				/ iNumberOfBars;
		Arrays.fill(afBarData, 0);
		for (float element : afDataValues)
		{
			int iHistogramIndex = (int) ((element - fMinimumDataValue) / fRatio);
			if (iHistogramIndex >= 0 && iHistogramIndex < afBarData.length) {
				afBarData[iHistogramIndex]++;
			}
		}

		repaint();
	}

	/**
   * This sets the Range of the data of the Graph.
   */
	public void setGraphDataRange(float fMinimumDataValue, float fMaximumDataValue)
	{
		this.fMinimumDataValue = fMinimumDataValue;
		this.fMaximumDataValue = fMaximumDataValue;

		setGridMinMax(0, getMaximumX(), fMinimumDataValue, fMaximumDataValue);
	}

	/**
   * This gets the Minimum Range of the data of the Graph.
   */
	public float getMinimumGraphDataRange()
	{
		return fMinimumDataValue;
	}

	/**
   * This gets the Maximum Range of the data of the Graph.
   */
	public float getMaximumGraphDataRange()
	{
		return fMaximumDataValue;
	}

	/**
   * This will set the number of bars.
   */
	public void setNumberOfBars(int iNumberOfBars)
	{
		this.iNumberOfBars = iNumberOfBars;
		afBarData = new float[iNumberOfBars];

		repaint();
	}

	/**
   * This will return the number of bars.
   */
	public int getNumberOfBars()
	{
		return iNumberOfBars;
	}

	/**
   * This will set the Color of the Bars.
   */
	public void setBarColor(Color oBarColor)
	{
		this.oBarColor = oBarColor;

		repaint();
	}

	/**
   * This will get the Color of the Bars.
   */
	public Color getBarColor()
	{
		return oBarColor;
	}

	/**
   * This methods sets the maximum and minimum on the Axis.
   * 
   * @param fMinimumX -
   *          will be set to 0
   * @param fMaximumX
   * @param fMinimumY
   * @param fMaximumY
   */
	@Override
	public void setGridMinMax(double fMinimumX, double fMaximumX, double fMinimumY,
			double fMaximumY)
	{
		super.setGridMinMax(0, fMaximumX, fMinimumY, fMaximumY);
	}

	/**
   * This sets the BarData that will be drawn.
   */
	public void setBarData(float[] afBarData)
	{
		this.afBarData = afBarData;
	}

	/**
   * This returns the BarData that will be drawn.
   */
	public float[] getBarData()
	{
		return afBarData;
	}

	/**
   * This method will cause the display to clear.
   */
	@Override
	public void clear()
	{
		setNumberOfBars(iNumberOfBars);

		repaint();
	}
}
