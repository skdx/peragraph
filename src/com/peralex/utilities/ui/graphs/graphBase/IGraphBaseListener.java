package com.peralex.utilities.ui.graphs.graphBase;

/**
 * 
 * FIXME (Noel) add cGraphBase parameters to the listener methods
 *
 * @author  Andre
 */
public interface IGraphBaseListener
{
  
  /**
   * This is called every time the coordinates of the mouse has changed.
   */
  void mouseCoordinatesChanged(double fXValue, double fYValue);
	
  /**
   * This is called every time the ScalingFactor of the graph has changed.
   */
  void scalingFactorChanged(long lXScalingFactor, String sXScaleUnit, long lYScalingFactor, String sYScaleUnit);
}
