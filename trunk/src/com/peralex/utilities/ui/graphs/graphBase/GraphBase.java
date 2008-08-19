package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Sets up cursors and fire mouse coordinate listeners.
 * 
 * FIXME (Noel) This class does not contain enough stuff to warrant it's own class in the hierarchy.
 *  Also, this functionality needs to be higher up the hierarchy than cZoomDrawSurface because
 *  some graph classes need this, but don't want zooming. This can safely be moved up into cCursorDrawSurface.
 *  
 * @author Andre
 */
public abstract class GraphBase extends ZoomDrawSurface
{
	
	/**
	 * This is the cursor used when the mouse entered the graph area.
	 */
	private Cursor oCursor;

  /** 
   * Creates a new instance of cGraphBase 
   */
	protected GraphBase()
  {		
  	/* We create our own cursor because the default cross-hair cursor has some weird display
  	 * issues - it seems to fade into some waterfall graphs, and seems to interact with the
  	 * green FFT line to produce red pixels. Weird.
  	 */
  	this.oCursor = createCursor(Color.WHITE);
		setCursor(this.oCursor);
  }

  private static Cursor createCursor(Color color)
  {
		BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(color);
		
		graphics.drawLine(15, 6,15,12);
		graphics.drawLine(15,15,15,15);
		graphics.drawLine(15,18,15,24);
		
		graphics.drawLine( 6,15,12,15);
		graphics.drawLine(18,15,24,15);
		
		// Create the new mouse Cursor.
		Toolkit oToolkit = Toolkit.getDefaultToolkit();
		Cursor crossHairCursor = oToolkit.createCustomCursor(image, new Point(15, 15), "Cross");
		return crossHairCursor;
	}

  /**
   * Event for mouseDragged.
   */  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    super.mouseDragged(e);
    
    final double fMouseXValue = pixelToUnitX(e.getX());
    final double fMouseYValue = pixelToUnitY(e.getY());
    fireMouseCoordinatesListeners(fMouseXValue, fMouseYValue);
  }
  
  /**
   * Event for mouseMoved.
   */  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    super.mouseMoved(e);
    
    final double fMouseXValue = pixelToUnitX(e.getX());
    final double fMouseYValue = pixelToUnitY(e.getY());
    fireMouseCoordinatesListeners(fMouseXValue, fMouseYValue);
  }

  public void setGraphCursorColor(Color oColor)
  {
  	this.oCursor = createCursor(oColor);
		setCursor(oCursor);
  }
  
  /**
   * Set the cursor displayed.
   * Setting it to null resets to the default graph cursor.
   * 
   * @see #setGraphCursorColor
   */
  public void setGraphCursor(Cursor oCursor) {
  	if (oCursor==null)
  	{
    	this.oCursor = createCursor(Color.WHITE);
  	}
  	else
  	{
  		this.oCursor = oCursor;
  	}
		setCursor(this.oCursor);
  }
  
	public Cursor getGraphCursor()
	{
		return oCursor;
	}
}
