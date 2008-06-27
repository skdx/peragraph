package com.peralex.utilities.ui.graphs.constellationGraph;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

/**
 * A Layout manager that sizes it's component to be square, and in the centre of it's parent.
 *  
 * This is useful for the constellation graph because a constellation graph needs to be square.
 *  
 * @author Andre
 */
public class SquareLayout implements LayoutManager2
{
  
	private Component comp;
	
  /** 
   * Creates a new instance of SquareLayout 
   */
  public SquareLayout()
  {
  }  
  
  /**
   * Required by LayoutManager. 
   */
  public void addLayoutComponent(String name, Component _comp)
  {
  	if (this.comp!=null) throw new IllegalStateException("there can be one and only component in a SquareLayout");
  	this.comp = _comp;
  }
  
  /** 
   * Required by LayoutManager. 
   */
  public void removeLayoutComponent(Component _comp)
  {
  	if (this.comp==_comp) this.comp = null;
  }
  
  
  /**
   * Required by LayoutManager. 
   */
  public Dimension preferredLayoutSize(Container parent)
  {
    return new Dimension(0, 0);
  }
  
  /**
   * Required by LayoutManager. 
   */
  public Dimension minimumLayoutSize(Container parent)
  {
    return new Dimension(0, 0);
  }

  /**
   * Required by LayoutManager. 
   * This is called when the panel is first displayed,
   * and every time its size changes.
   * Note: You CAN'T assume preferredLayoutSize or
   * minimumLayoutSize will be called -- in the case
   * of applets, at least, they probably won't be.
   */
  public void layoutContainer(Container oParent)
  {
    final Insets oInsets = oParent.getInsets();
    final int iMaxWidth = oParent.getWidth() - (oInsets.left + oInsets.right);
    final int iMaxHeight = oParent.getHeight() - (oInsets.top + oInsets.bottom);
    final int iWidth, iHeight;
    
    if (iMaxWidth >= iMaxHeight)
    {
      iWidth = iMaxHeight;
      iHeight = iMaxHeight;
    }
    else
    {
      iWidth = iMaxWidth;
      iHeight = iMaxWidth; 
    }

    final int iXPos = ((iMaxWidth - iWidth) / 2) + oInsets.left;
    final int iYPos = ((iMaxHeight - iHeight) / 2) + oInsets.top;
    
    oParent.getComponent(0).setBounds(iXPos, iYPos, iWidth, iHeight);
  }

  /**
   * Required by LayoutManager2.
   */
  public void addLayoutComponent(Component _comp, Object constraints)
  {
  	if (this.comp!=null) throw new IllegalStateException("there can be one and only component in a SquareLayout");
  	this.comp = _comp;
  }
  
  /**
   * Required by LayoutManager2.
   */
  public float getLayoutAlignmentX(Container target)
  {
  	return 0.5f;
  }
  
  /**
   * Required by LayoutManager2.
   */
  public float getLayoutAlignmentY(Container target)
  {
  	return 0.5f;
  }
  
  /**
   * Required by LayoutManager2.
   */
  public void invalidateLayout(Container target)
  {
  }
  
  /**
   * Required by LayoutManager2.
   */
  public Dimension maximumLayoutSize(Container target)
  {
  	return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
}
