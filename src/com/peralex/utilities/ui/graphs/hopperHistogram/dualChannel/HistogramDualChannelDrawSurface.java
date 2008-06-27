package com.peralex.utilities.ui.graphs.hopperHistogram.dualChannel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author Andre E
 */
class HistogramDualChannelDrawSurface extends DrawSurface
{
  
  /**
   * This stores the histogram data will be drawn in this graph.
   */
  private float[][] afHistogramData;

  
  /**
   * Creates a new instance of cHistogram2DDrawSurface
   */
  public HistogramDualChannelDrawSurface()
  {
  }
  
  /**
   * Updates the graph
   *
   * @param g - The JPanel requires a Graphics surface on which to draw
   */
	@Override
  public synchronized void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D)g;
        
    // Draw the GridLines.
    drawVerticalGrid(g2d);
    drawHorizontalGrid(g2d);
    
    if (afHistogramData != null)
    {
      float iBlockWidth_pixels = (float)(getLogUnitToPixel(dDrawMaxX, dMinX, dMaxX) - getLogUnitToPixel(dDrawMinX, dMinX, dMaxX)) / afHistogramData[0].length;
      float iBlockHeight_pixels = (float)getHeight() / afHistogramData.length;
 
      int iXpos = 0;      
      int iYpos = 0;
      int iXStartPos = getLogUnitToPixel(dDrawMinX, dMinX, dMaxX) + 1;
      
      for (int i=0; i<afHistogramData.length; i++)
      {
        for (int a=0; a<afHistogramData[i].length; a++)
        {
          if (afHistogramData[i][a] > 0)
          {
            iXpos = iXStartPos + (int)(iBlockWidth_pixels * a);
            iYpos = (int)((iBlockHeight_pixels * i)) + 1;

            if (afHistogramData[i][a] >= 0.7f)
            {
              g.setColor(Color.GREEN);
            }
            else 
            {
              g.setColor(new Color(0, 0, (int)(255 * (((int)(afHistogramData[i][a] * 10)) / 6f))));
            }            
            g2d.fillRect(iXpos, iYpos, ((int)iBlockWidth_pixels - 1), ((int)iBlockHeight_pixels) - 1);
          }
        }
      }            
    }
  }

  /**
   * This method passes new data into the Histogram.
   *
   * @param _afHistogramData contains the new Data.
   */  
  public synchronized void onDataReceived(float[][] _afHistogramData)
  {    
    this.afHistogramData = _afHistogramData;
    repaint();    
  }
  
  /**
   * This method is used to Clear the graph.
   */
  public synchronized void clear()
  {
    afHistogramData = null;
    repaint();
  }
}
