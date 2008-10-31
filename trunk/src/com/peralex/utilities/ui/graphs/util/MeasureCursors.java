package com.peralex.utilities.ui.graphs.util;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.peralex.utilities.ValueFormatter;
import com.peralex.utilities.ui.graphs.graphBase.ICursorListener;
import com.peralex.utilities.ui.graphs.graphBase.Cursor;
import com.peralex.utilities.ui.graphs.graphBase.ZoomDrawSurface;
import com.peralex.utilities.ui.graphs.lineGraph.AbstractLineGraph;

/**
 * Provides a set of linked cursors on the waterfall and line graphs that help the user to measure
 * durations and frequency spans.
 * 
 * @author Noel Grandin
 */
public class MeasureCursors
{
	/**
	 * these are "virtual" cursor keys, and are used for listening to changes in the cursors.
	 */
	public static final String sCursor_1 =  "Cursor_1";
	public static final String sCursor_2 =  "Cursor_2";
	
	private static final String sdBm = " dBm";
	private static final String sdB = " dB";
	
	private static final String sAmplitudeCursor_1 =  "AmplitudeCursor_1";
	private static final String sAmplitudeCursor_2 =  "AmplitudeCursor_2";
	
	private static final String sWaterfallCursor_1 =  "WaterfallCursor_1";
	private static final String sWaterfallCursor_2 =  "WaterfallCursor_2";

	private double fCurrentCursorOneFreq_Hz = Double.MIN_VALUE;
	private double fCurrentCursorOneAmpl_dBm = Double.MIN_VALUE;
	private double fCurrentCursorTwoFreq_Hz = Double.MIN_VALUE;
	private double fCurrentCursorTwoAmpl_dBm = Double.MIN_VALUE;
	
	private final ZoomDrawSurface oWaterfallGraph;
	private final AbstractLineGraph oLineGraph;
	
  private final List<ICursorListener> oCursorListeners = new ArrayList<ICursorListener>();  
  
  private boolean bWaterfallHorizontalCursorEnabled = false;
  
	public MeasureCursors(ZoomDrawSurface oWaterfallGraph, AbstractLineGraph oLineGraph)
	{
		this.oWaterfallGraph = oWaterfallGraph;
		this.oLineGraph = oLineGraph;
		
		oWaterfallGraph.addCursor(sWaterfallCursor_1, -1, Color.RED, 1, 1, 0, 0, false, false);
		oWaterfallGraph.addCursorListener(new ICursorListener()
		{
			public void cursorValueChanged(String sCursorID, double dCursorXValue, double dCursorYValue)
			{
				setCursorValues(sCursorID, dCursorXValue, dCursorYValue, true);
			}
		});
		oWaterfallGraph.addCursor(sWaterfallCursor_2, KeyEvent.VK_SHIFT, Color.BLUE, 1, 1, 0, 0, false, false);
		oWaterfallGraph.addCursorListener(new ICursorListener()
		{
			public void cursorValueChanged(String sCursorID, double dCursorXValue, double dCursorYValue)
			{
				setCursorValues(sCursorID, dCursorXValue, dCursorYValue, true);
			}
		});
		
		oLineGraph.addCursor(sAmplitudeCursor_1, -1, Color.RED, 1, 1, 0, 0, false, false);
		oLineGraph.addCursorListener(new ICursorListener()
		{
			public void cursorValueChanged(String sCursorID, double dCursorXValue, double dCursorYValue)
			{
				setCursorValues(sCursorID, dCursorXValue, dCursorYValue, true);
			}
		});
		oLineGraph.addCursor(sAmplitudeCursor_2, KeyEvent.VK_SHIFT, Color.BLUE, 1, 1, 0, 0, false, false);
		oLineGraph.addCursorListener(new ICursorListener()
		{
			public void cursorValueChanged(String sCursorID, double dCursorXValue, double dCursorYValue)
			{
				setCursorValues(sCursorID, dCursorXValue, dCursorYValue, true);
			}
		});
	}
	
	/**
	 * Enable/disable the primary and secondary cursors.
	 */
	public void setEnabled(boolean enabled)
	{
		oWaterfallGraph.getCursor(sWaterfallCursor_1).setVerticalCursorEnabled(enabled);
		oWaterfallGraph.getCursor(sWaterfallCursor_2).setVerticalCursorEnabled(enabled);
		
		oLineGraph.getCursor(sAmplitudeCursor_1).setHorizontalCursorEnabled(enabled);
		oLineGraph.getCursor(sAmplitudeCursor_1).setVerticalCursorEnabled(enabled);
		oLineGraph.getCursor(sAmplitudeCursor_2).setHorizontalCursorEnabled(enabled);
		oLineGraph.getCursor(sAmplitudeCursor_2).setVerticalCursorEnabled(enabled);
	}

	/**
	 * Enable/disable the primary cursor
	 */
	public void setPrimaryEnabled(boolean enabled)
	{
		oWaterfallGraph.getCursor(sWaterfallCursor_1).setVerticalCursorEnabled(enabled);
		oWaterfallGraph.getCursor(sWaterfallCursor_1).setHorizontalCursorEnabled(enabled && bWaterfallHorizontalCursorEnabled);
		
		oLineGraph.getCursor(sAmplitudeCursor_1).setHorizontalCursorEnabled(enabled);
		oLineGraph.getCursor(sAmplitudeCursor_1).setVerticalCursorEnabled(enabled);
	}
	
	public void setWaterfallHorizontalCursorEnabled(boolean bEnabled)
	{
		this.bWaterfallHorizontalCursorEnabled = bEnabled;
		oWaterfallGraph.getCursor(sWaterfallCursor_1).setHorizontalCursorEnabled(bEnabled);
	}
	
	/**
	 * Enable/disable the secondary cursor
	 */
	public void setSecondaryEnabled(boolean enabled)
	{
		oWaterfallGraph.getCursor(sWaterfallCursor_2).setVerticalCursorEnabled(enabled);
		
		oLineGraph.getCursor(sAmplitudeCursor_2).setHorizontalCursorEnabled(enabled);
		oLineGraph.getCursor(sAmplitudeCursor_2).setVerticalCursorEnabled(enabled);
	}

	private void setCursorValues(String sCursorID, double fCursorXValue, double fCursorYValue, boolean bFireCursor)
	{
		final boolean bFire1 = bFireCursor 
									&& (sCursorID.equals(sWaterfallCursor_1) || sCursorID.equals(sAmplitudeCursor_1))
									&& (fCursorXValue!=fCurrentCursorOneFreq_Hz || fCursorYValue!=fCurrentCursorOneAmpl_dBm);
			
		final boolean bFire2 = bFireCursor
								&& (sCursorID.equals(sWaterfallCursor_2) || sCursorID.equals(sAmplitudeCursor_2))
								&& (fCursorXValue!=fCurrentCursorTwoFreq_Hz || fCursorYValue!=fCurrentCursorTwoAmpl_dBm);
		
    if (sCursorID.equals(sWaterfallCursor_1))
		{
			fCurrentCursorOneFreq_Hz = fCursorXValue;
		}
		else if (sCursorID.equals(sWaterfallCursor_2))
		{
			fCurrentCursorTwoFreq_Hz = fCursorXValue;
		}
		else if (sCursorID.equals(sAmplitudeCursor_1))
		{
			fCurrentCursorOneFreq_Hz = fCursorXValue;
			fCurrentCursorOneAmpl_dBm = fCursorYValue;      
		}
		else if (sCursorID.equals(sAmplitudeCursor_2))
		{
			fCurrentCursorTwoFreq_Hz = fCursorXValue;
			fCurrentCursorTwoAmpl_dBm = fCursorYValue;
		}
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				oLineGraph.getCursor(sAmplitudeCursor_1).setValue(
						fCurrentCursorOneFreq_Hz, fCurrentCursorOneAmpl_dBm);
				oLineGraph.getCursor(sAmplitudeCursor_1).setXLabel(
						ValueFormatter.formatFrequency((long)fCurrentCursorOneFreq_Hz));
				oLineGraph.getCursor(sAmplitudeCursor_1).setYLabel(
						fCurrentCursorOneAmpl_dBm + sdBm);
				oWaterfallGraph.getCursor(sWaterfallCursor_1).setXValue(fCurrentCursorOneFreq_Hz);
				oWaterfallGraph.getCursor(sWaterfallCursor_1).setXLabel(
						ValueFormatter.formatFrequency((long)fCurrentCursorOneFreq_Hz));
				
				oLineGraph.getCursor(sAmplitudeCursor_2).setValue(
						fCurrentCursorTwoFreq_Hz, fCurrentCursorTwoAmpl_dBm);
				oLineGraph.getCursor(sAmplitudeCursor_2).setXLabel(
						ValueFormatter.formatFrequency((long)Math.abs(fCurrentCursorOneFreq_Hz - fCurrentCursorTwoFreq_Hz)));
				oLineGraph.getCursor(sAmplitudeCursor_2).setYLabel(
						Math.abs(fCurrentCursorOneAmpl_dBm - fCurrentCursorTwoAmpl_dBm) + sdB);
				oWaterfallGraph.getCursor(sWaterfallCursor_2).setXValue(fCurrentCursorTwoFreq_Hz);
				oWaterfallGraph.getCursor(sWaterfallCursor_2).setXLabel(
						ValueFormatter.formatFrequency((long)Math.abs(fCurrentCursorOneFreq_Hz - fCurrentCursorTwoFreq_Hz)));

				if (bFire1)
				{
					fireCursorListener(sCursor_1, fCurrentCursorOneFreq_Hz, fCurrentCursorOneAmpl_dBm);
				}
				if (bFire2)
				{
					fireCursorListener(sCursor_2, fCurrentCursorTwoFreq_Hz, fCurrentCursorTwoAmpl_dBm);
				}
			}
		});
	}
	
	public void setFrequencyWindow(double fMin_x, double fMax_x, float fResolution_Hz)
	{
		_setFrequencyWindow(fMin_x, fMax_x, fResolution_Hz, true);
	}
	
	/**
	 * Set frequency window without firing cursor listeners.
	 */
	public void setFrequencyWindowWithoutFire(double fMin_x, double fMax_x, float fResolution_Hz)
	{
		_setFrequencyWindow(fMin_x, fMax_x, fResolution_Hz, false);
	}
	
	private void _setFrequencyWindow(double fMin_x, double fMax_x, float fResolution_Hz, boolean bFire)
	{
		oLineGraph.getCursor(sAmplitudeCursor_1).setXResolution(fResolution_Hz);
		oLineGraph.getCursor(sAmplitudeCursor_2).setXResolution(fResolution_Hz);
		oWaterfallGraph.getCursor(sWaterfallCursor_1).setXResolution(fResolution_Hz);
		oWaterfallGraph.getCursor(sWaterfallCursor_2).setXResolution(fResolution_Hz);
		
		fCurrentCursorOneFreq_Hz = fCurrentCursorOneFreq_Hz==Double.MIN_VALUE ? fMin_x : fCurrentCursorOneFreq_Hz;
		fCurrentCursorOneFreq_Hz = fCurrentCursorOneFreq_Hz<fMin_x ? fMin_x : fCurrentCursorOneFreq_Hz;
		fCurrentCursorOneFreq_Hz = fCurrentCursorOneFreq_Hz>fMax_x ? fMax_x : fCurrentCursorOneFreq_Hz;
		
		fCurrentCursorTwoFreq_Hz = fCurrentCursorTwoFreq_Hz==Double.MIN_VALUE ? fMin_x : fCurrentCursorTwoFreq_Hz;
		fCurrentCursorTwoFreq_Hz = fCurrentCursorTwoFreq_Hz<fMin_x ? fMin_x : fCurrentCursorTwoFreq_Hz;
		fCurrentCursorTwoFreq_Hz = fCurrentCursorTwoFreq_Hz>fMax_x ? fMax_x : fCurrentCursorTwoFreq_Hz;
		
		setCursorValues(sAmplitudeCursor_1, fCurrentCursorOneFreq_Hz, fCurrentCursorOneAmpl_dBm, bFire);
		setCursorValues(sAmplitudeCursor_2, fCurrentCursorTwoFreq_Hz, fCurrentCursorTwoAmpl_dBm, bFire);
	}
	
	public void setAmplitudeScale(double fAmplitudeMinimum_dBm, double fAmplitudeMaximum_dBm)
	{
		setCursorValues(sAmplitudeCursor_1, fCurrentCursorOneFreq_Hz,
										fCurrentCursorOneAmpl_dBm==Double.MIN_VALUE ? fAmplitudeMinimum_dBm : fCurrentCursorOneAmpl_dBm, true);
		setCursorValues(sAmplitudeCursor_2, fCurrentCursorTwoFreq_Hz,
										fCurrentCursorTwoAmpl_dBm==Double.MIN_VALUE ? fAmplitudeMaximum_dBm : fCurrentCursorTwoAmpl_dBm, true);
	}

	/**
	 * Set amplitude scale without firing cursor listeners.
	 */
	public void setAmplitudeScaleWithoutFire(double fAmplitudeMinimum_dBm, double fAmplitudeMaximum_dBm)
	{
		setCursorValues(sAmplitudeCursor_1, fCurrentCursorOneFreq_Hz,
										fCurrentCursorOneAmpl_dBm==Double.MIN_VALUE ? fAmplitudeMinimum_dBm : fCurrentCursorOneAmpl_dBm, false);
		setCursorValues(sAmplitudeCursor_2, fCurrentCursorTwoFreq_Hz,
										fCurrentCursorTwoAmpl_dBm==Double.MIN_VALUE ? fAmplitudeMaximum_dBm : fCurrentCursorTwoAmpl_dBm, false);
	}
	
	public Cursor getCursor1()
	{
		return oLineGraph.getCursor(sAmplitudeCursor_1);
	}
	
	public Cursor getCursor2()
	{
		return oLineGraph.getCursor(sAmplitudeCursor_2);
	}
	
	/** When the waterfall horizontal cursor is enabled, it is not linked to the horizontal value
	 *  on the amplitude graph.
	 */
	public int getWaterfallCursorYCoordinate()
	{
		return oWaterfallGraph.getCursor(sWaterfallCursor_1).getYCoordinate();
	}
	
	public void setWaterfallCursorYValue(double fYValue)
	{
		oWaterfallGraph.getCursor(sWaterfallCursor_1).setYValue(fYValue);
	}
	
	public void setCursor1Frequency_Hz(double f)
	{
		fCurrentCursorOneFreq_Hz = f;
		
		setCursorValues(sAmplitudeCursor_1, fCurrentCursorOneFreq_Hz, fCurrentCursorOneAmpl_dBm, true);
	}
	
	public double getCursor1Frequency_Hz()
	{
		return fCurrentCursorOneFreq_Hz;
	}
	
	public void setCursor2Frequency_Hz(double f)
	{
		fCurrentCursorTwoFreq_Hz = f;
		
		setCursorValues(sAmplitudeCursor_2, fCurrentCursorTwoFreq_Hz, fCurrentCursorTwoAmpl_dBm, true);
	}
	
	public double getCursor2Frequency_Hz()
	{
		return fCurrentCursorTwoFreq_Hz;
	}
	
	public void setCursor1Amplitude_dBm(double f)
	{
		fCurrentCursorOneAmpl_dBm = f;
		
		setCursorValues(sAmplitudeCursor_1, fCurrentCursorOneFreq_Hz, fCurrentCursorOneAmpl_dBm, true);
	}
	
	public void setCursor2Amplitude_dBm(double f)
	{
		fCurrentCursorTwoAmpl_dBm = f;
		
		setCursorValues(sAmplitudeCursor_2, fCurrentCursorTwoFreq_Hz, fCurrentCursorTwoAmpl_dBm, true);
	}
	
	public double getCursor2Amplitude_dBm()
	{
		return fCurrentCursorTwoAmpl_dBm;
	}
	
	public double getCursor1Amplitude_dBm()
	{
		return fCurrentCursorOneAmpl_dBm;
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
   * Fire the CursorListeners
   */
  private void fireCursorListener(String sCursorID, double fCursorXValue, double fCursorYValue)
  {
    for (int i=0; i<oCursorListeners.size() ; i++)
    {
      oCursorListeners.get(i).cursorValueChanged(sCursorID, fCursorXValue, fCursorYValue);
    }
  }
}
