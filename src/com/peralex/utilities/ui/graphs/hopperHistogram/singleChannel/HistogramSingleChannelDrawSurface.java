package com.peralex.utilities.ui.graphs.hopperHistogram.singleChannel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.peralex.utilities.ui.graphs.graphBase.PixelUnitConverter;

/**
 * 
 * @author Andre Esterhuizen
 */
class HistogramSingleChannelDrawSurface extends DrawSurface
{

	/**
	 * This stores the histogram data will be drawn in this graph.
	 */
	private float[] afHistogramData;

	/**
	 * These stores the Min and Max values of the data.
	 */
	private float fMinScaleValue = 0, fMaxScaleValue = 0;

	/**
	 * Creates a new instance of HistogramSingleChannelDrawSurface
	 */
	public HistogramSingleChannelDrawSurface()
	{
	}

	/**
	 * Updates the graph
	 * 
	 * @param g - The JPanel requires a Graphics surface on which to draw
	 */
	@Override
	public synchronized void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Draw the GridLines.
		drawVerticalGrid(g2d);
		drawHorizontalGrid(g2d);

		if (afHistogramData == null)
			return;

		g.setColor(Color.RED);

		// The input range = [fPowerMin, fPowerMax]
		final float fInputMin = (float) (Math.log10(fMinScaleValue));
		final float fInputMax = (float) (Math.log10(fMaxScaleValue));
		final float fInputIncrement = (fInputMax - fInputMin) / afHistogramData.length;

		// The X axis range
		final float fxAxisMin = (float) (Math.log10(dMinX));
		final float fxAxisMax = (float) (Math.log10(dMaxX));

		// Notice the unit value is (fxAxisMin + fInputIncrement) and not (fInputIncrement) by itself
		// Since fxAxisMin will be treated as the origin of the line in the conversion,
		// the actual length should be expressed as = fxAxisMin + fInputIncrement (i.e. fInputIncrement unit away from
		// origin)
		final int iBarWidth = PixelUnitConverter.unitToPixel(true, fxAxisMin + fInputIncrement, 0,
				getWidth(), fxAxisMin, fxAxisMax);

		for (int i = 0; i < afHistogramData.length; i++)
		{
			int iBarXPos = PixelUnitConverter.unitToPixel(true, fInputMin + (fInputIncrement * i), 0,
					getWidth(), fxAxisMin, fxAxisMax);
			int iBarHeight = PixelUnitConverter.unitToPixel(true, afHistogramData[i], 0, getHeight(),
					dMinY, dMaxY);

			g2d.fillRect(iBarXPos, getHeight() - iBarHeight, iBarWidth, iBarHeight);
		}
	}

	/**
	 * Passes new data into the Histogram.
	 * 
	 * @param _afHistogramData contains the new Data.
	 * @param _fMinScaleValue
	 * @param _fMaxScaleValue
	 */
	public synchronized void onDataReceived(float[] _afHistogramData, float _fMinScaleValue,
			float _fMaxScaleValue)
	{
		this.afHistogramData = _afHistogramData;
		this.fMinScaleValue = _fMinScaleValue;
		this.fMaxScaleValue = _fMaxScaleValue;

		repaint();
	}

	/**
	 * Clear the graph.
	 */
	public synchronized void clear()
	{
		afHistogramData = null;
		repaint();
	}
}
