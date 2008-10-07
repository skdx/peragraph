package com.peralex.utilities.ui.graphs.graphBase;

/**
 * 
 * @author  Andre
 */
public interface IGraphBaseListener
{
  
  /**
   * This is called every time the coordinates of the mouse has changed.
   */
  void mouseCoordinatesChanged(GraphBase graphBase, double fXValue, double fYValue);
}
