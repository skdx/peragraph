package com.peralex.utilities.ui.graphs.graphBase;

public interface IGridListener
{
	
  /**
   * This is called every time the scaling factor of the graph has changed.
   */
  void scalingFactorChanged(GridDrawSurface surface, long lXScalingFactor, String sXScaleUnit, long lYScalingFactor, String sYScaleUnit);

  /**
   * @param axisType 0 for x, 1 for y
   */
	void gridChanged(int axisType, double dMinimum, double dMaximum, long lScalingFactor,
			boolean bProportional, double[] adGridValues, int[] aiGridCoordinates);
	
  /**
   * This is called every time the coordinates of the mouse has changed.
   */
  void mouseCoordinatesChanged(GridDrawSurface surface, double dXValue, double dYValue);
}
