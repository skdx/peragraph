package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FIXME rename this to something like LineCursorDrawSurface to distinguish it from the mouse-cursor.
 * 
 * @author Andre
 */
public abstract class CursorDrawSurface extends GraphDrawSurface
{
  	
  /**
   * This is the HashMap that stores all the Cursors added to this graph
   */
  private final Map<String, Cursor> oCursorsMap = new LinkedHashMap<String, Cursor>();
  
  private boolean bCursorsChanged = true;
  
  /**
   * This list stores the list of current CursorListeners.
   */
  private final List<ICursorListener> oCursorListeners = new CopyOnWriteArrayList<ICursorListener>();  
  
  /**
   * Stores the KeyCode of the key that is currently down.
   */
  private int iCurrentKeyDown = -1;
  
  /**
   * This is the font used for the labels.
   */
  private Font oLabelFont;
    
  private Color oLabelColor = Color.WHITE;
	
	/** If label orientation is set to this, all labels will be at the top of the graph */
	public static final int iTOP = 0;
	/** If label orientation is set to this, all labels will be at the bottom of the graph */
	public static final int iBOTTOM = 1;
	/** If label orientation is set to this, labels will be alternated between top and bottom of the graph */
	public static final int iALTERNATING = 2;
	/** Current label orientation */
	private int iXCursorLabelOrientation = iALTERNATING;
  
  /** 
   * Creates a new instance of cCursorDrawSurface 
   */
	protected CursorDrawSurface()
  {
  	oLabelFont = getFont();
  }
  
  public void setCursorLabelFont(Font oFont) {
  	this.oLabelFont = oFont;
  }

  public void setCursorLabelColor(Color oColor) {
  	this.oLabelColor = oColor;
  }
  
  /**
   * Paints all the cursors.
   */
	@Override
  protected void paint(Graphics g, Object iDrawSurfaceID)
	{
		if (CURSOR_DRAWSURFACE != iDrawSurfaceID)
		{
			super.paint(g, iDrawSurfaceID);
			return;
		}

		if (oCursorsMap.isEmpty())
		{
			return;
		}

		synchronized (oCursorsMap)
		{
			if (bCursorsChanged)
			{
				doCalculateCursors();
			}
			
			// Draw all the Cursors that is currently added
			for (Cursor oCursor : oCursorsMap.values())
			{
				g.setColor(oCursor.getColor());

				if (oCursor.isVerticalCursorEnabled())
				{
					g.drawLine(oCursor.getXCoordinate(), 0, oCursor.getXCoordinate(), getHeight());
				}
				if (oCursor.isHorizontalCursorEnabled())
				{
					g.drawLine(0, oCursor.getYCoordinate(), getWidth(), oCursor.getYCoordinate());
				}
			}

			// Draw all the Cursors that is currently added, ON TOP of all the cursors.
			g.setFont(oLabelFont);
			g.setColor(oLabelColor);
			for (Cursor oCursor : oCursorsMap.values())
			{
				if (oCursor.isVerticalCursorEnabled() && !oCursor.getXLabel().equals(""))
				{
					g.drawString(oCursor.getXLabel(), oCursor.getVerticalCursorLabelXCoordinate(), oCursor
							.getVerticalCursorLabelYCoordinate());
				}
				if (oCursor.isHorizontalCursorEnabled() && !oCursor.getYLabel().equals(""))
				{
					g.drawString(oCursor.getYLabel(), oCursor.getHorizontalCursorLabelXCoordinate(), oCursor
							.getHorizontalCursorLabelYCoordinate());
				}
			}
		}
	}

  /**
   * This will set a flag which will force a recalculate of label positions on the next repaint().
   * We do this because this method can get called before the component has access to a Graphics object.
   * 
	 * Note: the position of the labels interacts with the position of the range cursor labels.
	 */
  protected final void invalidateCursors()
  {
  	// check for null because we get called by a superclass constructor
  	if (oCursorsMap==null) return;
  	
		synchronized (oCursorsMap)
		{
			bCursorsChanged = true;
		}
		
		repaint();
  }

  /**
	 * This will calculate the coordinates of the cursors and their labels. 
	 * Note: the position of the labels interacts
	 * with the position of the range cursor labels.
	 */
  private void doCalculateCursors()
  {
		synchronized (oCursorsMap)
		{
			// Calculate all of the Cursors positions
			for (Cursor oCursor : oCursorsMap.values()) 
			{
				oCursor.setXCoordinate(PixelUnitConverter.unitToPixel(true, oCursor.getXValue(), 0, getWidth(), getMinimumX(), getMaximumX()));
				oCursor.setYCoordinate(getHeight() - PixelUnitConverter.unitToPixel(true, oCursor.getYValue(), 0, getHeight(), getMinimumY(), getMaximumY()));
			}

			// Calculate the Labels positions

			int iYOffset = iRangeCursorLabelYOffset, iLeftXOffset = 2, iRightXOffset = 2;
			boolean bVerticalLabelPosition = bRangeCursorLabelVerticalPosition;
			boolean bHorizontalLabelPosition = true;
			final int iLabelHeight = getGraphics().getFontMetrics().getHeight();
			for (Cursor oCursor : oCursorsMap.values()) 
			{
				if (!oCursor.getXLabel().equals(""))
				{
					final int iLabelWidth = getGraphics().getFontMetrics().stringWidth(oCursor.getXLabel());
					final int iXPos;
					if (oCursor.getXCoordinate() > (getWidth() - iLabelWidth - 2))
					{
						iXPos = oCursor.getXCoordinate() - iLabelWidth - 2;
					}
					else
					{
						iXPos = oCursor.getXCoordinate() + 2;
					}

					final int iYPos;
					switch (iXCursorLabelOrientation)
					{
						case iTOP:
							iYOffset += iLabelHeight;
							iYPos = iYOffset;
							break;
							
						case iBOTTOM:
							iYPos = getHeight() - iYOffset;
							iYOffset += iLabelHeight;
							break;
							
						case iALTERNATING:
							if (bVerticalLabelPosition)
							{
								iYPos = iYOffset + iLabelHeight;
							}
							else
							{
								iYPos = getHeight() - iYOffset;
								iYOffset += iLabelHeight;
							}
							bVerticalLabelPosition = !bVerticalLabelPosition;
							break;
						default:
							throw new IllegalStateException("unknown cursor label orientation " + iXCursorLabelOrientation);
					}
					oCursor.setVerticalCursorLabelCoordinates(iXPos, iYPos);
				}
				
				if (!oCursor.getYLabel().equals(""))
				{
					// if the cursor is near the top of the screen, draw the label beneath the cursor line
					final int iYPos;
					if (oCursor.getYCoordinate() < (iLabelHeight + 2))
					{
						iYPos = oCursor.getYCoordinate() + iLabelHeight + 2;
					}
					else
					{
						iYPos = oCursor.getYCoordinate() - 2;
					}

					if (bHorizontalLabelPosition)
					{
						// draw on the left-hand side
						oCursor.setHorizontalCursorLabelCoordinates(iLeftXOffset, iYPos);
						iLeftXOffset += getGraphics().getFontMetrics().stringWidth(oCursor.getYLabel()) + 2;
					}
					else
					{
						// draw on the right-hand side
						iRightXOffset += getGraphics().getFontMetrics().stringWidth(oCursor.getYLabel());
						oCursor.setHorizontalCursorLabelCoordinates(getWidth() - iRightXOffset, iYPos);
					}
					bHorizontalLabelPosition = !bHorizontalLabelPosition;
				}
			}
			
			bCursorsChanged = false;
		}
  } 
  
  /**
	 * This will calculate the coordinates of the cursors and their labels.
	 */
  @Override
	void invalidateRangeCursors()
	{
		super.invalidateRangeCursors();
		// when range cursors are re-calculated, we need to re-calculate the position of labels.
		invalidateCursors();
	}
    
   
  /**
	 * This method adds a cursor to the graph
	 * 
   * @param iKeyBinding the KeyEvent.VK_* code of the keybinding to position the cursor, -1 is "no key pressed", -2 means "not positionable" 
	 */
  public void addCursor(String sCursorID, int iKeyBinding, Color oCursorColor, float fXResolution, float fYResolution)
  {    
    addCursor(sCursorID, iKeyBinding, oCursorColor, fXResolution, fYResolution, getMinimumX(), getMaximumY());
  }
  
  /**
   * This method adds a cursor to the graph
   * 
   * @param iKeyBinding the KeyEvent.VK_* code of the keybinding to position the cursor, -1 is "no key pressed", -2 means "not positionable" 
   */
  public void addCursor(String sCursorID, int iKeyBinding, Color oCursorColor, float fXResolution, float fYResolution, double dXValue, double dYValue)
  {    
		addCursor(sCursorID, iKeyBinding, oCursorColor, fXResolution, fYResolution, dXValue, dYValue, true, true);
  }
	
  /**
   * This method adds a cursor to the graph
   * 
   * @param iKeyBinding the KeyEvent.VK_* code of the keybinding to position the cursor, -1 is "no key pressed", -2 means "not positionable" 
   * 
   */
  public void addCursor(String sCursorID, int iKeyBinding, Color oCursorColor, float fXResolution, float fYResolution, double dXValue, double dYValue, boolean bHorizontalCursorEnabled, boolean bVerticalCursorEnabled)
  {    
		Cursor oCursor = new Cursor(this, sCursorID, iKeyBinding, oCursorColor, fXResolution,  fYResolution, dXValue, dYValue, bHorizontalCursorEnabled, bVerticalCursorEnabled);
		synchronized (oCursorsMap)
		{
			oCursorsMap.put(sCursorID, oCursor);
		}
		invalidateCursors();
  }	
  
  /**
   * This method removes a cursor from the graph
   */
  public void removeCursor(String sCursorID)
  {
  	synchronized (oCursorsMap)
  	{
  		oCursorsMap.remove(sCursorID);
  	}
    repaint();
  }
  
  /**
   * This method removes all cursor from the graph
   */
  public void removeCursors()
  {
  	synchronized (oCursorsMap)
  	{
  		oCursorsMap.clear();
  	}
    repaint();
  }  
  
  /**
   * This method will return the given cursor.
   */
  public Cursor getCursor(String sCursorID)
  {
  	synchronized (oCursorsMap)
  	{
			if (oCursorsMap.containsKey(sCursorID))
			{
				 return oCursorsMap.get(sCursorID);
			}
			else
			{
				return null;
			}
  	}
  }  
	
	/**
	 * This method will return all the cursors.
	 */
	public Cursor[] getCursors()
	{
		synchronized (oCursorsMap)
		{
			Cursor [] aoCursors = new Cursor[oCursorsMap.size()];
			return oCursorsMap.values().toArray(aoCursors);
		}
	}	
	
  /**
   * This method will return the number of cursors that is currently added to the graph.
   *
   * @return oCursorsMap.size()
   */
  public int getNumberOfCursors()
  {
    return oCursorsMap.size();
  }	

  /**
   * This method will enable or disable the Horizontal cursors.
   */
  public void setHorizontalCursorsEnabled(boolean bHorizontalCursorsEnabled)
  {
		synchronized (oCursorsMap)
		{
			for (Cursor oCursor : oCursorsMap.values()) 
			{
				oCursor.setHorizontalCursorEnabled(bHorizontalCursorsEnabled);                  
			}
		}
    repaint();
  } 
  
  /**
   * This method will enable or disable the Vertical cursors.
   */
  public void setVerticalCursorsEnabled(boolean bVerticalCursorsEnabled)
  {
		synchronized(oCursorsMap)
		{
			for (Cursor oCursor : oCursorsMap.values()) 
			{
				oCursor.setVerticalCursorEnabled(bVerticalCursorsEnabled);                  
			}
		}
    repaint();
  }
 
  /**
   * Event for mouseReleased.
   */     
	@Override
  public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);

		if (e.getButton() != MouseEvent.BUTTON1)
			return;

		// ignore dragging clicks
		if (Math.abs(iMousePressedX - iMouseReleasedX) >= 4
				|| Math.abs(iMousePressedY - iMouseReleasedY) >= 4)
			return;

		// Fire the event of the Cursor that has just been moved
		synchronized (oCursorsMap)
		{
			for (Cursor oCursor : oCursorsMap.values())
			{
				if (oCursor.getKeyBinding() == iCurrentKeyDown
						&& (oCursor.isHorizontalCursorEnabled() || oCursor.isVerticalCursorEnabled()))
				{
					final double fXValue = pixelToUnitX(iMouseReleasedX);
					final double fYValue = pixelToUnitY(iMouseReleasedY);
					// set value will trigger a repaint
					oCursor.setValue(fXValue, fYValue);

					doCalculateCursors();
					fireCursorListener(oCursor.getCursorID(), oCursor.getXValue(), oCursor.getYValue());
					break;
				}
			}
		}
	}
  
  /**
   * Event for keyPressed.
   */ 
	@Override
  public void keyPressed(KeyEvent e)
  {
    super.keyPressed(e);

    // this could be either a modifier keypress to select a cursor, or a cursor movement key

    // if it's not a movement key, store the modifier and return
		final int iKeyPressed = e.getKeyCode();		
		if (iKeyPressed != KeyEvent.VK_UP && iKeyPressed != KeyEvent.VK_DOWN && iKeyPressed != KeyEvent.VK_LEFT && iKeyPressed != KeyEvent.VK_RIGHT)
		{
			iCurrentKeyDown = iKeyPressed;
			return;
		}		
		
		// Move a cursor with the arrow keys
		synchronized (oCursorsMap)
		{
			for (Cursor oCursor : oCursorsMap.values()) 
			{
				if (oCursor.getKeyBinding() == iCurrentKeyDown 
						&& (oCursor.isHorizontalCursorEnabled() || oCursor.isVerticalCursorEnabled()))
				{
					switch (iKeyPressed)
					{
						case KeyEvent.VK_UP:
							oCursor.moveUp();
							break;
						case KeyEvent.VK_DOWN:
							oCursor.moveDown();
							break;
						case KeyEvent.VK_LEFT:
							oCursor.moveLeft();
							break;
						case KeyEvent.VK_RIGHT:
							oCursor.moveRight();
							break;					
					}

					doCalculateCursors();
					fireCursorListener(oCursor.getCursorID(), oCursor.getXValue(), oCursor.getYValue());
					break;
				}
			}
    }
  }

  /**
   * Event for keyReleased.
   */ 
	@Override
  public void keyReleased(KeyEvent e)
  {
    super.keyReleased(e);

    if (e.getKeyCode() == iCurrentKeyDown)
    {
      iCurrentKeyDown = -1;
    }
  }  
  
  /**
   * Register a new CursorListener.
   */
  public void addCursorListener(ICursorListener iCursorListener)
  {
		if (!oCursorListeners.contains(iCursorListener))
		{
			oCursorListeners.add(iCursorListener);
		}
  }
  
  /**
   * De-register a CursorListener.
   */
  public void removeCursorListener(ICursorListener iCursorListener)
  {
    oCursorListeners.remove(iCursorListener);
  }
  
  /**
   * Fire the CursorListeners.
   * Note: the ONLY reason this is public is because of a long-standing hack in PXGAnalysis.
   */
  public void fireCursorListener(String sCursorID, double dCursorXValue, double dCursorYValue)
  {
    for (int i=0; i<oCursorListeners.size() ; i++)
    {
      oCursorListeners.get(i).cursorValueChanged(sCursorID, dCursorXValue, dCursorYValue);
    }
  }
  
  /**
   * Set the maximum and minimum on the Axis.
   */
	@Override
  public void setGridMinMax(double fMinimumX, double fMaximumX, double fMinimumY, double fMaximumY)
  {
    super.setGridMinMax(fMinimumX, fMaximumX, fMinimumY, fMaximumY);
		
    invalidateCursors();
  }
	
  /**
   * Set the maximum and minimum on the X Axis.
   */
	@Override
  public void setGridXMinMax(double fMinimumX, double fMaximumX)
  {
		super.setGridXMinMax(fMinimumX, fMaximumX);
		
		invalidateCursors();
  }
	
  /**
   * Set the maximum and minimum on the Y Axis.
   */
	@Override
  public void setGridYMinMax(double fMinimumY, double fMaximumY)
  {
		super.setGridYMinMax(fMinimumY, fMaximumY);
		
		invalidateCursors();
  }	
  
  /**
   * Event for componentResized.
   */
	@Override
  public void componentResized(ComponentEvent e)
  {
    super.componentResized(e);
    
    invalidateCursors();
  }
	
	/**
	 * Set the vertical cursor's label orientation to one of the following:
	 *
	 * - cCursorDrawSurface.iTOP 
	 * - cCursorDrawSurface.iBOTTOM
	 * - cCursorDrawSurface.iALTERNATING (default)
	 */
	public void setXCursorLabelOrientation(int iXCursorLabelOrientation)
	{
		this.iXCursorLabelOrientation = iXCursorLabelOrientation;
	}
}