package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author  Andre
 */
public abstract class GraphDrawSurface extends RangeCursorDrawSurface
{
	  
  /** 
   * Creates a new instance of cGraphDrawSurface 
   */
	protected GraphDrawSurface()
  {
  }
  
	@Override
  public void paint(Graphics g, int iDrawSurfaceID)
  {
		if (GRAPH_DRAWSURFACE == iDrawSurfaceID)
		{
			drawGraph((Graphics2D) g);
		}
		else
		{
			super.paint(g, iDrawSurfaceID);
		}		
  }
  
  /**
   * This method will do the actual drawing of the graph.
   *
   * @param g
   */
	public abstract void drawGraph(Graphics2D g);
	
	/**
	 * This method will cause the display to clear.
	 */
	public abstract void clear();
}
