package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 *
 * @author Andre Esterhuizen
 */
public abstract class EventsSurface extends DrawSurface implements MouseListener, 
                                                      MouseMotionListener, 
                                                      MouseWheelListener,
                                                      KeyListener,
                                                      ComponentListener
{
  
  /**
   * This stores the X position of where the mouse was pressed.
   */
  public int iMousePressedX = 0;
  
  /**
   * This stores the Y position of where the mouse was pressed.
   */
  public int iMousePressedY = 0;
   
  /**
   * This stores the X position of where the mouse was released.
   */
  public int iMouseReleasedX = 0;
  
  /**
   * This stores the Y position of where the mouse was released.
   */
  public int iMouseReleasedY = 0;
  
  
  /** 
   * Creates a new instance of cEventsSurface 
   */
  protected EventsSurface()
  {
    addComponentListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    addMouseWheelListener(this);
    addKeyListener(this);
  }

  /**
   * Utility method for checking event modifier keys/buttons.
   * @see InputEvent#getModifiersEx
   * 
   * @param downModifiersMask these are the *_MASK values in InputEvent and MouseEvent
   */
  public static boolean isDown(InputEvent e, int downModifiersMask)
  {
  	validateModifiersExMask(downModifiersMask);
  	return (e.getModifiersEx() & downModifiersMask) == downModifiersMask;
  }
  
  private static void validateModifiersExMask(int modifiersMask)
  {
  	if ((modifiersMask & (InputEvent.SHIFT_DOWN_MASK-1)) > 0) {
  		throw new IllegalStateException("invalid event modifiers mask - you are probably using the wrong constants from InputEvent");
  	}
  }
  
  /**
   * Utility method for checking event modifier keys/buttons.
   * @see InputEvent#getModifiersEx
   * 
   * @param upModifiersMask these are the *_MASK values in InputEvent and MouseEvent
   */
  public static boolean isUp(InputEvent e, int upModifiersMask)
  {
  	validateModifiersExMask(upModifiersMask);
  	return (e.getModifiersEx() & upModifiersMask) == 0;
  }
  
  /**
   * Utility method for checking event modifier keys/buttons.
   * @see InputEvent#getModifiersEx
   * 
   * @param downModifiersMask values for keys/buttons that are down/on. these are the *_MASK values in InputEvent and MouseEvent
   * @param upModifiersMask values for keys/buttons that are up/off. these are the *_MASK values in InputEvent and MouseEvent
   */
  public static boolean isDownUp(InputEvent e, int downModifiersMask, int upModifiersMask)
  {
  	validateModifiersExMask(downModifiersMask);
  	validateModifiersExMask(upModifiersMask);
  	return (e.getModifiersEx() & (downModifiersMask | upModifiersMask)) == downModifiersMask;
  }
  
  /**
   * Event for mouseClicked.
   */
  public void mouseClicked(MouseEvent e)
  {
  }
  
  /**
   * Event for mouseDragged.
   */  
  public void mouseDragged(MouseEvent e)
  {
  }

  /**
   * Event for mouseEntered.
   */  
  public void mouseEntered(MouseEvent e)
  {
    /* we need the focus so that
     * (a) keyboard navigation around the graph works
     * (b) we pick up mouse wheel events
     * 
     * Note: requestFocusInWindow() will only give us the focus
     * if we are the the active window. I do this because it is rude
     * to grab the focus if you are not the active window.
     * Note: Once we become the active window, we will get focus.
     */ 
		requestFocusInWindow();
  }

  /**
   * Event for mouseExited.
   */    
  public void mouseExited(MouseEvent e)
  {
  }
  
  /**
   * Event for mouseMoved.
   */  
  public void mouseMoved(MouseEvent e)
  {
  }
  
  /**
   * Event for mousePressed.
   */  
  public void mousePressed(MouseEvent e)
  {
    iMousePressedX = e.getX();
    iMousePressedY = e.getY();
  }
  
  /**
   * Event for mouseReleased.
   */  
  public void mouseReleased(MouseEvent e)
  {    
    iMouseReleasedX = e.getX();
    iMouseReleasedY = e.getY();    
  }
  
  /**
   * Event for mouseWheelMoved.
   */  
  public void mouseWheelMoved(MouseWheelEvent e)
  {
  }
  
  /**
   * Event for keyPressed.
   */   
  public void keyPressed(KeyEvent e)
  {
  }
  
  /**
   * Event for keyReleased.
   */   
  public void keyReleased(KeyEvent e)
  {
  }
  
  /**
   * Event for keyTyped.
   */   
  public void keyTyped(KeyEvent e)
  {
  }
  
  /**
   * Event for componentHidden.
   */  
  public void componentHidden(ComponentEvent e)
  {
  }
  
  /**
   * Event for componentMoved.
   */  
  public void componentMoved(ComponentEvent e)
  {
  }
  
  /**
   * Event for componentResized.
   */  
  public void componentResized(ComponentEvent e)
  {
  }
  
  /**
   * Event for componentShown.
   */  
  public void componentShown(ComponentEvent e)
  {
  }
	
}
