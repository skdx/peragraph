package com.peralex.utilities.ui.graphs.util;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.peralex.utilities.ValueFormatter;
import com.peralex.utilities.ui.graphs.graphBase.ICursorListener;
import com.peralex.utilities.ui.graphs.graphBase.Cursor;
import com.peralex.utilities.ui.graphs.lineGraph.AbstractLineGraph;

/**
 * Provides the standard pair of red and blue measure cursors that we use.
 * 
 * @author Noel Grandin
 */
public class StandardCursors
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
	
	private double fCurrentCursorOneFreq_Hz = Float.MIN_VALUE;
	private double fCurrentCursorOneAmpl_dBm = Float.MIN_VALUE;
	private double fCurrentCursorTwoFreq_Hz = Float.MIN_VALUE;
	private double fCurrentCursorTwoAmpl_dBm = Float.MIN_VALUE;
	
	private final AbstractLineGraph oLineGraph;
	
  private final List<ICursorListener> oCursorListeners = new ArrayList<ICursorListener>();  
  
	public StandardCursors(AbstractLineGraph oLineGraph)
	{
		this.oLineGraph = oLineGraph;
		
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
		
		setEnabled(true);
	}
	
	/**
	 * Enable/disable the primary and secondary cursors.
	 */
	public void setEnabled(boolean enabled)
	{
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
		oLineGraph.getCursor(sAmplitudeCursor_1).setHorizontalCursorEnabled(enabled);
		oLineGraph.getCursor(sAmplitudeCursor_1).setVerticalCursorEnabled(enabled);
	}
	
	/**
	 * Enable/disable the secondary cursor
	 */
	public void setSecondaryEnabled(boolean enabled)
	{
		oLineGraph.getCursor(sAmplitudeCursor_2).setHorizontalCursorEnabled(enabled);
		oLineGraph.getCursor(sAmplitudeCursor_2).setVerticalCursorEnabled(enabled);
	}

	private void setCursorValues(String sCursorID, double fCursorXValue, double fCursorYValue, boolean bFireCursor)
	{
		final boolean bFire1 = bFireCursor 
									&& (sCursorID.equals(sAmplitudeCursor_1))
									&& (fCursorXValue!=fCurrentCursorOneFreq_Hz || fCursorYValue!=fCurrentCursorOneAmpl_dBm);
			
		final boolean bFire2 = bFireCursor
								&& (sCursorID.equals(sAmplitudeCursor_2))
								&& (fCursorXValue!=fCurrentCursorTwoFreq_Hz || fCursorYValue!=fCurrentCursorTwoAmpl_dBm);
		
		if (sCursorID.equals(sAmplitudeCursor_1))
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
				
				oLineGraph.getCursor(sAmplitudeCursor_2).setValue(
						fCurrentCursorTwoFreq_Hz, fCurrentCursorTwoAmpl_dBm);
				oLineGraph.getCursor(sAmplitudeCursor_2).setXLabel(
						ValueFormatter.formatFrequency((long)Math.abs(fCurrentCursorOneFreq_Hz - fCurrentCursorTwoFreq_Hz)));
				oLineGraph.getCursor(sAmplitudeCursor_2).setYLabel(
						Math.abs(fCurrentCursorOneAmpl_dBm - fCurrentCursorTwoAmpl_dBm) + sdB);

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
	
	public void setFrequencyWindow(float fMin_x, float fMax_x, float fResolution_Hz)
	{
		_setFrequencyWindow(fMin_x, fMax_x, fResolution_Hz, true);
	}
	
	/**
	 * Set frequency window without firing cursor listeners.
	 */
	public void setFrequencyWindowWithoutFire(float fMin_x, float fMax_x, float fResolution_Hz)
	{
		_setFrequencyWindow(fMin_x, fMax_x, fResolution_Hz, false);
	}
	
	private void _setFrequencyWindow(float fMin_x, float fMax_x, float fResolution_Hz, boolean bFire)
	{
		oLineGraph.getCursor(sAmplitudeCursor_1).setXResolution(fResolution_Hz);
		oLineGraph.getCursor(sAmplitudeCursor_2).setXResolution(fResolution_Hz);
		
		fCurrentCursorOneFreq_Hz = fCurrentCursorOneFreq_Hz==Float.MIN_VALUE ? fMin_x : fCurrentCursorOneFreq_Hz;
		fCurrentCursorOneFreq_Hz = fCurrentCursorOneFreq_Hz<fMin_x ? fMin_x : fCurrentCursorOneFreq_Hz;
		fCurrentCursorOneFreq_Hz = fCurrentCursorOneFreq_Hz>fMax_x ? fMax_x : fCurrentCursorOneFreq_Hz;
		
		fCurrentCursorTwoFreq_Hz = fCurrentCursorTwoFreq_Hz==Float.MIN_VALUE ? fMin_x : fCurrentCursorTwoFreq_Hz;
		fCurrentCursorTwoFreq_Hz = fCurrentCursorTwoFreq_Hz<fMin_x ? fMin_x : fCurrentCursorTwoFreq_Hz;
		fCurrentCursorTwoFreq_Hz = fCurrentCursorTwoFreq_Hz>fMax_x ? fMax_x : fCurrentCursorTwoFreq_Hz;
		
		setCursorValues(sAmplitudeCursor_1, fCurrentCursorOneFreq_Hz, fCurrentCursorOneAmpl_dBm, bFire);
		setCursorValues(sAmplitudeCursor_2, fCurrentCursorTwoFreq_Hz, fCurrentCursorTwoAmpl_dBm, bFire);
	}
	
	public void setAmplitudeScale(float fAmplitudeMinimum_dBm, float fAmplitudeMaximum_dBm)
	{
		setCursorValues(sAmplitudeCursor_1, fCurrentCursorOneFreq_Hz,
										fCurrentCursorOneAmpl_dBm==Float.MIN_VALUE ? fAmplitudeMinimum_dBm : fCurrentCursorOneAmpl_dBm, true);
		setCursorValues(sAmplitudeCursor_2, fCurrentCursorTwoFreq_Hz,
										fCurrentCursorTwoAmpl_dBm==Float.MIN_VALUE ? fAmplitudeMaximum_dBm : fCurrentCursorTwoAmpl_dBm, true);
	}

	/**
	 * Set amplitude scale without firing cursor listeners.
	 */
	public void setAmplitudeScaleWithoutFire(float fAmplitudeMinimum_dBm, float fAmplitudeMaximum_dBm)
	{
		setCursorValues(sAmplitudeCursor_1, fCurrentCursorOneFreq_Hz,
										fCurrentCursorOneAmpl_dBm==Float.MIN_VALUE ? fAmplitudeMinimum_dBm : fCurrentCursorOneAmpl_dBm, false);
		setCursorValues(sAmplitudeCursor_2, fCurrentCursorTwoFreq_Hz,
										fCurrentCursorTwoAmpl_dBm==Float.MIN_VALUE ? fAmplitudeMaximum_dBm : fCurrentCursorTwoAmpl_dBm, false);
	}
	
	public Cursor getCursor1()
	{
		return oLineGraph.getCursor(sAmplitudeCursor_1);
	}
	
	public Cursor getCursor2()
	{
		return oLineGraph.getCursor(sAmplitudeCursor_2);
	}
	
	public void setCursor1Frequency_Hz(float f)
	{
		fCurrentCursorOneFreq_Hz = f;
		
		setCursorValues(sAmplitudeCursor_1, fCurrentCursorOneFreq_Hz, fCurrentCursorOneAmpl_dBm, true);
	}
	
	public double getCursor1Frequency_Hz()
	{
		return fCurrentCursorOneFreq_Hz;
	}
	
	public void setCursor2Frequency_Hz(float f)
	{
		fCurrentCursorTwoFreq_Hz = f;
		
		setCursorValues(sAmplitudeCursor_2, fCurrentCursorTwoFreq_Hz, fCurrentCursorTwoAmpl_dBm, true);
	}
	
	public double getCursor2Frequency_Hz()
	{
		return fCurrentCursorTwoFreq_Hz;
	}
	
	public void setCursor1Amplitude_dBm(float f)
	{
		fCurrentCursorOneAmpl_dBm = f;
		
		setCursorValues(sAmplitudeCursor_1, fCurrentCursorOneFreq_Hz, fCurrentCursorOneAmpl_dBm, true);
	}
	
	public void setCursor2Amplitude_dBm(float f)
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
      oCursorListeners.get(i).cursorValueChanged(sCursorID, (long)fCursorXValue, (long)fCursorYValue);
    }
  }
}
