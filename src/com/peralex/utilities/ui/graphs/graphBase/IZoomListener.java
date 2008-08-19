package com.peralex.utilities.ui.graphs.graphBase;

/**
 * ZoomListener Interface
 *
 * FIXME (Noel) add cZoomDrawSurface parameters to the listener methods
 * FIXME (Noel) roll these 2 methods into one method
 * 
 * @author Andre Esterhuizen
 */
public interface IZoomListener
{
  /**
   * This is called whenever the zoom of the graph changes.
   */
  void graphZoomChanged(double fMinimumX, double fMaximumX, double fMinimumY, double fMaximumY);
  
  /**
   * This is called whenever the zoom status of the graph changes.
   */
  void graphZoomStatusChanged(boolean bIsCurrentlyZoomed);
  
  /**
   * Called when we start animating the zoom - these values are the values we will zoom TO.
   */
  void zoomAnimationStart(double fMinimumX, double fMaximumX, double fMinimumY, double fMaximumY);
}
