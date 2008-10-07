package com.peralex.utilities.ui.graphs.graphBase;

public interface IGridListener
{
	
  /**
   * This is called every time the scaling factor of the graph has changed.
   */
  void scalingFactorChanged(GridDrawSurface surface, long lXScalingFactor, String sXScaleUnit, long lYScalingFactor, String sYScaleUnit);

}
