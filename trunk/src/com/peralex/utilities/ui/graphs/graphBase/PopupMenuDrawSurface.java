package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 *
 * @author  Andre
 */
public abstract class PopupMenuDrawSurface extends EventsSurface
{

  /**
   * This is the PopupMenu that appears when the user right-clicks.
   */
  protected final JPopupMenu oPopupMenu = new JPopupMenu();

	/**
	 * This flag indicates whether the PopupMenu is enabled or disabled.
	 */
	private boolean bPopupMenuEnabled = true;
	
  
  /** 
   * Creates a new instance of cPopupMenuDrawSurface 
   */
	protected PopupMenuDrawSurface()
  {   
    // Add the event for displaying the popup.
    addMouseListener(new MouseAdapter()
    {
    	@Override
    	public void mousePressed(MouseEvent e)
    	{
    		triggerPopup(e);
    	}
    	@Override
    	public void mouseReleased(MouseEvent e)
    	{
    		triggerPopup(e);
    	}
      private void triggerPopup(MouseEvent e)
      {
    		/* Show the menu if (a) it is enabled (b) the correct button was pressed
    		 * and (c) someone has actually added something to it to show */ 
        if (bPopupMenuEnabled && e.isPopupTrigger() && oPopupMenu.getComponentCount()>0)
        {
          oPopupMenu.show(PopupMenuDrawSurface.this, iMouseReleasedX, iMouseReleasedY);
        }
      }
    });
  }
	
	/**
	 * This method will set the PopupMenu enabled or disabled.
	 */
	public void setPopupMenuEnabled(boolean bEnabled)
	{
		this.bPopupMenuEnabled = bEnabled;
	}
	
	/**
	 * This method will return the PopupMenu.
	 */
	public JPopupMenu getPopupMenu()
	{
		return oPopupMenu;
	}	
}
