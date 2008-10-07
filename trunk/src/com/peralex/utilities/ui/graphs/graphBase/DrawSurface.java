package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.peralex.utilities.locale.ILocaleListener;
import com.peralex.utilities.locale.PeralexLibsBundle;

/**
 * @author  Andre
 */
public abstract class DrawSurface extends JComponent implements ILocaleListener
{
	
	/** The resource bundle used for multilingual support */
	protected ResourceBundle textRes = PeralexLibsBundle.getResource();
	
	/**
	 * These are the static variables that identifies each of the DrawSurfaces. 
	 * FIXME: these should be of type Object rather than type int, to prevent accidental collisions.
	 */
	public static final Object ZOOM_DRAWSURFACE = new Object();
	public static final Object CURSOR_DRAWSURFACE = new Object();
	public static final Object GRAPH_DRAWSURFACE = new Object();	
	public static final Object GRID_DRAWSURFACE = new Object();
	public static final Object RANGE_CURSOR_DRAWSURFACE = new Object();	
	
	/**
	 * This array stores the current drawing order of the DrawSurfaces.
	 */
	private Object[] aiDrawingOrder;
	
	/** 
	 * Creates a new instance of DrawSurface 
	 */
	protected DrawSurface()
	{
		aiDrawingOrder = new Object[] {
			RANGE_CURSOR_DRAWSURFACE,
			GRID_DRAWSURFACE,
			GRAPH_DRAWSURFACE,
			CURSOR_DRAWSURFACE,
			ZOOM_DRAWSURFACE,
		};
		
		// we know that we paint all our own pixels.
		setOpaque(false);
		
		// default background
		setBackground(Color.BLACK);

		// give us the same default front as a JPanel
    setFont(UIManager.getFont("Panel.font"));
    
		PeralexLibsBundle.addLocaleListener(this); //do after components have been initialised
	}

  /**
   * This will paint the entire graph in the correct order.
   */
	@Override
  public void paint(Graphics g)
  {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		// Draw each of the DrawSurfaces in the correct order.
		for (Object element : aiDrawingOrder)
		{
			paint(g, element);
		}
	}
	
	/**
	 * This will draw the DrawSurface with the given ID.
	 */
	protected abstract void paint(Graphics g, Object iDrawSurfaceID);

	/**
	 * This method will set the Drawing order of the graph.
	 */
	public void setDrawingOrder(Object... aiDrawingOrder)
	{
		this.aiDrawingOrder = aiDrawingOrder;
	}
	
  /**
   * This method is called when the locale has been changed. The listener should
   * then update the visual components.
   */
  public void componentsLocaleChanged()
  {
    textRes = PeralexLibsBundle.getResource();
    
		localeChanged();
		repaint();
	}

	/**
	 * override this method to handle local changed events - it will be running on the event thread.
	 */
	protected void localeChanged() {}
}
