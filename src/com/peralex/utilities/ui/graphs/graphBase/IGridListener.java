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
	void gridChanged(int axisType, double fMinimum, double fMaximum, long lScalingFactor,
			boolean bProportional, double[] afGridValues, int[] aiGridCoordinates);
}
