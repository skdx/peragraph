package com.peralex.utilities.ui.graphs.lineGraph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Arrays;

import com.peralex.utilities.ui.graphs.graphBase.PixelUnitConverter;

/**
 * Cache for computed co-ordinate values.
 * 
 * @author Noel Grandin
 */
final class CoordinateCacheValue {
	
	private int [] aiXCoordinates;
	private int [] aiYCoordinates;
	private int cnt; // sometimes, only part of the array is used
	
	CoordinateCacheValue()
	{
	}
	
	void compute(
						final int iHeight, 
						final int iWidth, 
						final PixelUnitConverter.UnitToPixel xUnitToPixel, 
						final PixelUnitConverter.UnitToPixel yUnitToPixel, 
						AbstractLineData oLineData,
						double fMinX,
						double fMaxX,
						boolean bCalculateX,
						boolean bCalculateY)
	{
		/* Only draw the data that is going to be visible.
		 * Note: the +2,-2 calculations are to allow for high zoom factors. If we are
		 *  highly zoomed in, the first line on the left/right may start/end off-screen.
		 */
		final int startIndex = Math.max(0, Math.abs(oLineData.binarySearchXValues(fMinX)) - 2);
		final int endIndex = Math.min(oLineData.getNumberOfPoints(), Math.abs(oLineData.binarySearchXValues(fMaxX)) + 2);
		final int size = endIndex-startIndex;
		final int pointsPerPixel = Math.max(1, size / iWidth);
		if (pointsPerPixel<=2) {
			computeNormal(iHeight, xUnitToPixel, yUnitToPixel, oLineData, startIndex, endIndex, size, bCalculateX, bCalculateY);
		} else {
			computeZoomedOut(iHeight, xUnitToPixel, yUnitToPixel, oLineData, startIndex, endIndex, size, pointsPerPixel);
		}
	}

	private void computeNormal(final int iHeight, final PixelUnitConverter.UnitToPixel xUnitToPixel, final PixelUnitConverter.UnitToPixel yUnitToPixel, AbstractLineData oLineData, final int startIndex, final int endIndex, final int size, boolean bCalculateX, boolean bCalculateY)
	{
    // I need to use a double computation here because some of the larger graphs have very high x-values
    // which will otherwise not get resolved to pixels properly.
		
		if (bCalculateX)
		{
			if (aiXCoordinates==null || aiXCoordinates.length!=size)
			{
				aiXCoordinates = new int[size];
			}
			cnt = 0;
			for (int a=startIndex; a<endIndex; a++)
			{
				aiXCoordinates[cnt] = xUnitToPixel.compute(oLineData.getXValueDouble(a));
				cnt++;
			}
		}
		
		if (bCalculateY)
		{
			if (aiYCoordinates==null || aiYCoordinates.length!=size)
			{
				aiYCoordinates = new int[size];
			}
			
			cnt = 0;
			for (int a=startIndex; a<endIndex; a++)
			{
				aiYCoordinates[cnt] = iHeight - yUnitToPixel.compute(oLineData.getYValueDouble(a));
				cnt++;
			}
		}
	}
	
	/**
	 * The zoomed-out case, where we need to combine several data points into one pixel.
	 * Note: We cannot split the X and Y computations here because of the combining we are doing.
	 */
	private void computeZoomedOut(final int iHeight, final PixelUnitConverter.UnitToPixel xUnitToPixel, final PixelUnitConverter.UnitToPixel yUnitToPixel, AbstractLineData oLineData, final int startIndex, final int endIndex, final int size, final int pointsPerPixel)
	{
		cnt = 0;
		
		// Combine data points that end up on a single pixel such that we will
		// draw along the min/max values.
		final int numCoords = (size / pointsPerPixel * 2 + 1) * 2;

		if (aiXCoordinates==null || aiXCoordinates.length!=numCoords)
		{
			aiXCoordinates = new int[numCoords];
		}
		
		if (aiYCoordinates==null || aiYCoordinates.length!=numCoords)
		{
			aiYCoordinates = new int[numCoords];
		}
		
		int x = xUnitToPixel.compute(oLineData.getXValue(0));
		float maxY = oLineData.getYValue(0);
		float minY = oLineData.getYValue(0);
		for (int a=startIndex; a<endIndex; a++)
		{
			final int newX = xUnitToPixel.compute(oLineData.getXValue(a));
			final float newY = oLineData.getYValue(a);
			if (x!=newX)
			{
				aiXCoordinates[cnt] = x;
				aiYCoordinates[cnt] = iHeight - yUnitToPixel.compute(maxY);
				cnt++;
		    aiXCoordinates[cnt] = x;
		    aiYCoordinates[cnt] = iHeight - yUnitToPixel.compute(minY);
		    cnt++;
				x = newX;
				maxY = newY;
		    minY = newY;
			}
			else
			{
				maxY = Math.max(maxY, newY);
		    minY = Math.min(minY, newY);
			}
		}
		aiXCoordinates[cnt] = x;
		aiYCoordinates[cnt] = iHeight - yUnitToPixel.compute(maxY);
		cnt++;
		aiXCoordinates[cnt] = x;
		aiYCoordinates[cnt] = iHeight - yUnitToPixel.compute(minY);
		cnt++;
	}

	void paint(Graphics2D g, Color oLineColor, Stroke oStroke)
	{
		if (oStroke!=null)
		{
			g.setStroke(oStroke);
		}
		g.setColor(oLineColor);
		final Rectangle clip = g.getClipBounds();
		if (clip==null) {
			System.out.println("clip null");
			g.drawPolyline(aiXCoordinates, aiYCoordinates, cnt);
		} else {
			// only draw up till the end of the clip region
			final int endX = clip.x + clip.width;
			int clippedCnt = Math.abs(Arrays.binarySearch(aiXCoordinates, endX));
			// adjust clippedCnt for high zoom factors where the end-point of a line-segment may line off screen
			clippedCnt++;
			// make sure we don't run off the end of the array
			clippedCnt = Math.min(clippedCnt, cnt);
			g.drawPolyline(aiXCoordinates, aiYCoordinates, clippedCnt);
		}
	}
	
}