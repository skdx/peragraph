package com.peralex.utilities.ui;

import java.awt.event.InputEvent;

/**
 * 
 * @author Noel Grandin
 */
public final class InputEventLib
{
	/** not meant to be instantiated */
	
	private InputEventLib() {}
	
  /**
   * Utility method for checking event modifier keys/buttons.
   * @see InputEvent#getModifiersEx
   * 
   * @param downModifiersMask these are the *_MASK values in InputEvent and MouseEvent
   */
  public static boolean isDown(InputEvent e, int downModifiersMask)
  {
  	return (e.getModifiersEx() & downModifiersMask) == downModifiersMask;
  }
  
  /**
   * Utility method for checking event modifier keys/buttons.
   * @see InputEvent#getModifiersEx
   * 
   * @param upModifiersMask these are the *_MASK values in InputEvent and MouseEvent
   */
  public static boolean isUp(InputEvent e, int upModifiersMask)
  {
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
  	return (e.getModifiersEx() & (downModifiersMask | upModifiersMask)) == downModifiersMask;
  }
}
