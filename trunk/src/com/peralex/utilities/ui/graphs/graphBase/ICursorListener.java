package com.peralex.utilities.ui.graphs.graphBase;

/**
 * FIXME (Noel) add a cCursorDrawSurface parameter to the listener
 * CursorListener Interface
 *
 * @author Andre
 */
public interface ICursorListener
{
  /**
   * This method is implemented to receive the new value of the Cursor.
   */
  void cursorValueChanged(String sCursorID, double dCursorXValue, double dCursorYValue);
}
