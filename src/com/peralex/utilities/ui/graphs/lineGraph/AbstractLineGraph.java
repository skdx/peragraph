package com.peralex.utilities.ui.graphs.lineGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.Timer;

import com.peralex.utilities.ui.graphs.graphBase.ZoomDrawSurface;

/**
 * Contains code common to the line-graph subclasses.
 * 
 * @author Noel Grandin
 */
public abstract class AbstractLineGraph extends ZoomDrawSurface
{
	/**
	 * A Timer that will limit the repainting of this graph
	 * FIXME this should not run continuously, rather make it 1-shot-trigger-style
	 */
	private final Timer oFrameRepaintTimer;
	
	/**
	 * True if frame limiting has been set activated.
	 */
	protected boolean bFrameRepaintLimited = false;
	
	/**
	 * scale the graph so that it just fits the displayed data.
	 */
  private final JMenuItem oAutoScaleMenuItem;
  
	/**
	 * Creates a new instance of cLineGraph
	 */
  protected AbstractLineGraph()
	{
    // Add the Reset zoom item to the PopUpMenu.
    oAutoScaleMenuItem = new JMenuItem(textRes.getString("AbstractLineGraph.Auto_scale"));
    oPopupMenu.add(oAutoScaleMenuItem);  
    oAutoScaleMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        autoScaleGraph();
      }
    });
    
		// Create the repaint timer but don't start it
		oFrameRepaintTimer = new Timer(50, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				repaint();
			}
		});
	}
	
	/**
	 * Start a Timer that will limit the repainting of this graph.
	 * The timer will use a default period of 50ms.
	 *
	 * @param bEnabled True if frame limiting should be performed.
	 */
	public void setFrameLimitingEnabled(boolean bEnabled)
	{
		setFrameLimitingEnabled(bEnabled, 50);
	}
	
	public boolean isFrameLimitingEnabled()
	{
		return bFrameRepaintLimited;
	}

	/**
	 * Set the period of the repaint-limiting timer
	 *
	 * @param iFramePeriod_ms how often repaint will be called on this component.
	 */
	public void setFrameLimitingPeriod(int iFramePeriod_ms)
	{
		setFrameLimitingEnabled(bFrameRepaintLimited, iFramePeriod_ms);
	}
	
	public int getFrameLimitingPeriod()
	{
		return oFrameRepaintTimer.getDelay();
	}
	
	/**
	 * Start a Timer that will limit the repainting of this graph.
	 *
	 * @param bEnabled True if frame limiting should be performed.
	 * @param iFramePeriod_ms how often repaint will be called on this component.
	 */
	public void setFrameLimitingEnabled(boolean bEnabled, int iFramePeriod_ms)
	{
		bFrameRepaintLimited = bEnabled;
		if (bEnabled)
		{
			oFrameRepaintTimer.setDelay(iFramePeriod_ms);
			oFrameRepaintTimer.start();
		}
		else
		{
			oFrameRepaintTimer.stop();
		}
	}
	
	/**
	 * scale the graph so that it just fits the displayed data.
	 */
	protected abstract void autoScaleGraph();
	
	@Override
	protected void localeChanged()
	{
		super.localeChanged();
		oAutoScaleMenuItem.setText(textRes.getString("Auto_scale"));
	}
	
}
