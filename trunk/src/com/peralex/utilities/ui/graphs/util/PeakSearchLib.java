package com.peralex.utilities.ui.graphs.util;

import com.peralex.utilities.ui.graphs.lineGraph.AbstractLineData;

/**
 * This implements a rather simple peak search function. 
 * This is only intended to be useful for hopping from peak to peak in a graph on a GUI.
 * 
 * Note: I do not smooth the data, because that would cause me to miss some single-pixel-wide peaks.
 * 
 * @author Noel Grandin
 */
public final class PeakSearchLib
{
	private PeakSearchLib()
	{
	}

	/**
	 * Find the first peak to our left
	 * 
	 * @param visibleMinX the minimum visible x-value.
	 * @return the next peak frequency.
	 */
	public static double firstPeakLeft(final double currentXValue, AbstractLineData lineData, double visibleMinX, double minY)
	{
		final int minIndex = findIndex(lineData, visibleMinX);

		// find the index of the current x value
		int index = findIndex(lineData, currentXValue);
		
		// make sure we move at least one point left
		index--;
		
		// Looking for minima
		while (index>minIndex) {
			if (lineData.getYValue(index) < minY
					|| lineData.getYValue(index) < lineData.getYValue(index - 1))
			{
				break;
			}
			index--;
		}
		
		// Looking for maxima
    while (index>minIndex) {
			if (lineData.getYValue(index) >= minY)
			{
				if (lineData.getYValue(index) > lineData.getYValue(index - 1))
					break;
			}
			index--;
		}
		
		index = Math.max(index, minIndex);
		return lineData.getXValue(index);
	}

	/**
	 * Find the first peak to our right
	 * 
	 * @param visibleMaxX the maximum visible x-value.
	 * @return the next peak frequency.
	 */
	public static double firstPeakRight(final double currentXValue, AbstractLineData lineData, double visibleMaxX, double minY)
	{
		final int maxIndex = findIndex(lineData, visibleMaxX);

		// find the index of the current x value
		int index = findIndex(lineData, currentXValue);
		
		// make sure we move at least one point right
		index++;
		
		// Looking for minima
		while (index<maxIndex) {
			if (lineData.getYValue(index) < minY
					|| lineData.getYValue(index) < lineData.getYValue(index + 1))
			{
				break;
			}
			index++;
		}
		
		// Looking for maxima
    while (index<maxIndex) {
			if (lineData.getYValue(index) >= minY
					&& lineData.getYValue(index) > lineData.getYValue(index + 1))
			{
				break;
			}
			index++;
		}
		
		index = Math.min(index, maxIndex);
		return lineData.getXValueDouble(index);
	}
	
	private static int findIndex(AbstractLineData lineData, double f)
	{
		final int i = Math.abs(lineData.binarySearchXValues(f));
		if (i>lineData.getNumberOfPoints()) return lineData.getNumberOfPoints()-1;
		return i;
	}

}
