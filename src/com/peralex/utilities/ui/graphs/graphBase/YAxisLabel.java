package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

/**
 * A component that draws a string from bottom to top.
 * 
 * @author  Roy Emmerich
 */
public class YAxisLabel extends JComponent
{
  private static final int DEGREES = -90;
  
  private String sYAxisLabel;
  
  /** 
	 * Creates new form sYAxisLabel 
	 */
  public YAxisLabel()
  {
    this("Y Axis");
  }
  
  /**
   * Create a new y axis label.
   *
   * @param label The string you want to display in the label.
   */
  public YAxisLabel(String label)
  {
    if (label == null)
    {
      sYAxisLabel = "null";
    }
    else
    {
      sYAxisLabel = label;
    }
  }
  
  @Override
  public Dimension getPreferredSize()
  {
  	Dimension d = super.getPreferredSize();
  	d.width = getFontMetrics(getFont()).getHeight();
  	return d;
  }
  
	@Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D)g;
    Dimension graphicsDimensions = getSize();
    
    AffineTransform at = new AffineTransform();
    at.setToRotation(Math.toRadians(DEGREES));    
    
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
    FontRenderContext frc = g2d.getFontRenderContext();
    // rotate the font
    Font derivedFont = getFont().deriveFont(at);
    TextLayout textLayout = new TextLayout(sYAxisLabel, derivedFont, frc);
    
    textLayout.draw(g2d, 9, (graphicsDimensions.height/2 + g.getFontMetrics().stringWidth(sYAxisLabel)/2));
  }
  
  /**
   * Change the label text
   *
   * @param sYAxisLabel The new label to display.
   */
  public void setText(String sYAxisLabel)
  {
		if (sYAxisLabel.equals("")) { sYAxisLabel = " "; }
    this.sYAxisLabel = sYAxisLabel;
    repaint();
  }
  
  /**
   * Retreive the label text
   *
   * @return The current label text
   */
  public String getText()
  {
    return sYAxisLabel;
  }
}
