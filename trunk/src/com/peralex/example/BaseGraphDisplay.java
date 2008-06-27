package com.peralex.example;

import java.awt.BorderLayout;

import com.peralex.sharedlibs.dsphostl.TimeStamp;
import com.peralex.utilities.ui.graphs.graphBase.MultiGraphWrapper;
import com.peralex.utilities.ui.graphs.graphBase.MultiGraphWrapperContainer;
import com.peralex.utilities.ui.graphs.lineGraph.MultiLineGraph;
import com.peralex.utilities.ui.graphs.lineGraph.GeneratedLineData;
import com.peralex.utilities.ui.graphs.waterfallGraph.IntensityWaterfallGraph;

/**
 *
 * @author Jaco Jooste
 */
abstract class BaseGraphDisplay extends javax.swing.JPanel
{
	
	private static final String GRAPH_DATA_AMPLITUDE = "Amplitude";
	private static final String GRAPH_DATA_NOISE_FLOOR = "Noise";
	private static final String GRAPH_DATA_NOISE_FLOOR_OFFSET = "Offset";
	
	////////////////////////////////////////////////////////////
	// Graphs and other visual objects
	////////////////////////////////////////////////////////////
  protected com.peralex.utilities.ui.graphs.graphBase.MultiGraphWrapperContainer oGraphContainer;
  protected com.peralex.utilities.ui.graphs.graphBase.MultiGraphWrapper oAmplitudeGraphWrapper;
  protected com.peralex.utilities.ui.graphs.graphBase.MultiGraphWrapper oWaterfallGraphWrapper;
	protected final IntensityWaterfallGraph oWaterfallGraph;
	
	////////////////////////////////////////////////////////////
	// General variables
	////////////////////////////////////////////////////////////
	protected float[] afAmplitudeFrame_dBm;
	private float[] afNoiseFloor_dBm;
	private float[] afNoiseFloorOffset_dBm;
	protected final GeneratedLineData oAmpLineData;
	private final GeneratedLineData oNoiseFloorData;
	private final GeneratedLineData oNoiseOffsetData;
	protected short wCurrentNoiseFloorValue_dB = 1;
	private boolean bNoiseFloorCurrentlyActive = false;
	protected boolean bSampleActive = true;
	
	/** Creates new form BaseGraphDisplay */
	protected BaseGraphDisplay()
	{
		initComponents();
		
		// Do setup for waterfall graph
		oWaterfallGraph = new IntensityWaterfallGraph();
		oWaterfallGraphWrapper.addGraph(oWaterfallGraph);
		oWaterfallGraphWrapper.replaceYAxis(oWaterfallGraph.getWaterfallIntensityAxis());
		oWaterfallGraphWrapper.setXAxisLabelVisible(false);
		oWaterfallGraphWrapper.setXAxisScaleVisible(false);
		oWaterfallGraphWrapper.setTitle("Amplitude");
		oWaterfallGraphWrapper.setAxisTitles("", "Waterfall Time Axis");
		oWaterfallGraphWrapper.setCursorCoordinatesVisible(false);
		
		// setup line data objects
		oAmpLineData = new GeneratedLineData(0, 0, 0, new float[0]);
		oNoiseFloorData = new GeneratedLineData(0, 0, 0, new float[0]);
		oNoiseOffsetData = new GeneratedLineData(0, 0, 0, new float[0]);
		
		//setup the graphs
		setNoiseFloorStatus(bNoiseFloorCurrentlyActive);
	}

  private void initComponents()
  {
  	oGraphContainer = new MultiGraphWrapperContainer();

  	initGraphWrappers();
  	
    setLayout(new BorderLayout());
    
    add(oGraphContainer, BorderLayout.CENTER);
  }

  protected void initGraphWrappers()
  {
    oWaterfallGraphWrapper = new com.peralex.utilities.ui.graphs.graphBase.MultiGraphWrapper(oGraphContainer);
    oAmplitudeGraphWrapper = new com.peralex.utilities.ui.graphs.graphBase.MultiGraphWrapper(oGraphContainer);
  }
  
	public void setFrequencyWindow(long lCentreFreq_Hz, long lBandwidth_Hz)
	{
		final float fMin_x = lCentreFreq_Hz - (lBandwidth_Hz/2);
		final float fMax_x = lCentreFreq_Hz + (lBandwidth_Hz/2);
		oWaterfallGraph.setGridXMinMax(fMin_x, fMax_x);
	}
	
	public void setAmplitudeScale(float fAmplitudeMinimum_dBm, float fAmplitudeMaximum_dBm)
	{
		getAmplitudeLineGraph().setGridYMinMax(fAmplitudeMinimum_dBm,
																				fAmplitudeMaximum_dBm);
		oWaterfallGraph.setThresholdLimits(fAmplitudeMinimum_dBm, fAmplitudeMaximum_dBm);
		oWaterfallGraph.setThresholdValues(fAmplitudeMinimum_dBm + 10, fAmplitudeMaximum_dBm - 10);
	}

	public void setSampleActive(boolean bSampleActive)
	{
		this.bSampleActive = bSampleActive;
	}
	
	public void setAmplitudeData(long lTimeValue_msec, short wTimeValue_usec, long lFirstFrequency_Hz, long lFrequencyResolution_cHz, short[] awAmplitude_cdBm)
	{
		if (!bSampleActive)
		{
			return;
		}
		
		showAmplitudeData(lFirstFrequency_Hz, lFrequencyResolution_cHz, awAmplitude_cdBm);
		oWaterfallGraph.setAmplitudeData(new TimeStamp(lTimeValue_msec, wTimeValue_usec), afAmplitudeFrame_dBm);
	}

	protected final void showAmplitudeData(long lFirstFrequency_Hz, long lFrequencyResolution_cHz, short[] awAmplitude_cdBm)
	{
		if (afAmplitudeFrame_dBm == null || afAmplitudeFrame_dBm.length!=awAmplitude_cdBm.length)
		{
			afAmplitudeFrame_dBm = new float[awAmplitude_cdBm.length];
			for (int i = 0; i < afAmplitudeFrame_dBm.length; i++)
			{
				afAmplitudeFrame_dBm[i] = awAmplitude_cdBm[i]/100f;
			}
		}
		else
		{
			for (int i = 0; i < afAmplitudeFrame_dBm.length; i++)
			{
				afAmplitudeFrame_dBm[i] = awAmplitude_cdBm[i]/100f;
			}
		}
		final float endX = lFirstFrequency_Hz + ((lFrequencyResolution_cHz/100f)*awAmplitude_cdBm.length);
		/* We don't trust onFrequencyWindow() to do this job because the FFT data and the FrequencyWindowResponse packet are not
		 * always in sync for a few frames after we change centre frequency.
		 */
		if (getAmplitudeLineGraph().getMinimumXZoomLimit() != lFirstFrequency_Hz
		     || getAmplitudeLineGraph().getMaximumXZoomLimit() != endX)
		{
			getAmplitudeLineGraph().setGridXMinMax(lFirstFrequency_Hz, endX);
		}
		oAmpLineData.setXValues(lFirstFrequency_Hz, endX, awAmplitude_cdBm.length);
		oAmpLineData.setYValues(afAmplitudeFrame_dBm);
		getAmplitudeLineGraph().graphDataChanged();
	}
	
	public void setDetectionData(long lTimeValue_msec, short wTimeValue_usec, long lFirstBinFrequency_Hz, long lFrequencyResolution_cHz, short[] awDetection_cdBm)
	{
		if (!bSampleActive)
		{
			return;
		}
		
		oWaterfallGraph.setDetectionData(lTimeValue_msec, wTimeValue_usec, lFirstBinFrequency_Hz, lFrequencyResolution_cHz, awDetection_cdBm);
	}
	
	public void setNoiseFloorData(long lFirstBinFrequency_Hz, long lFrequencyResolution_cHz, short[] awNoiseFloorBins_cdBm)
	{
		if (!bSampleActive)
		{
			return;
		}
		if (afNoiseFloor_dBm==null || afNoiseFloor_dBm.length!=awNoiseFloorBins_cdBm.length)
		{
			afNoiseFloor_dBm = new float[awNoiseFloorBins_cdBm.length];
			afNoiseFloorOffset_dBm = new float[awNoiseFloorBins_cdBm.length];
		}
		
		for (int i = 0; i < afNoiseFloor_dBm.length; i++)
		{
			afNoiseFloor_dBm[i] = awNoiseFloorBins_cdBm[i]/100f;
			afNoiseFloorOffset_dBm[i] = afNoiseFloor_dBm[i]+wCurrentNoiseFloorValue_dB;
		}
		
		final float endX = lFirstBinFrequency_Hz + (lFrequencyResolution_cHz/100f)*afNoiseFloor_dBm.length;
		oNoiseFloorData.setXValues(lFirstBinFrequency_Hz, endX, afNoiseFloor_dBm.length);
		oNoiseOffsetData.setXValues(lFirstBinFrequency_Hz, endX, afNoiseFloor_dBm.length);
		oNoiseFloorData.setYValues(afNoiseFloor_dBm);
		oNoiseOffsetData.setYValues(afNoiseFloorOffset_dBm);
		getAmplitudeLineGraph().graphDataChanged();
	}
	
	public void setNoiseFloorStatus(boolean bActive)
	{
		this.bNoiseFloorCurrentlyActive = bActive;
		if (bNoiseFloorCurrentlyActive)
		{
			getAmplitudeLineGraph().setGraphData(GRAPH_DATA_AMPLITUDE, oAmpLineData);
			getAmplitudeLineGraph().setGraphData(GRAPH_DATA_NOISE_FLOOR, oNoiseFloorData);
			getAmplitudeLineGraph().setGraphData(GRAPH_DATA_NOISE_FLOOR_OFFSET, oNoiseOffsetData);
		}
		else
		{
			getAmplitudeLineGraph().setGraphData(GRAPH_DATA_AMPLITUDE, oAmpLineData);
			getAmplitudeLineGraph().setGraphData(GRAPH_DATA_NOISE_FLOOR, null);
			getAmplitudeLineGraph().setGraphData(GRAPH_DATA_NOISE_FLOOR_OFFSET, null);
		}
	}
	
	public int getGraphWidth()
	{
		return getAmplitudeLineGraph().getWidth();
	}
	
	public abstract MultiLineGraph getAmplitudeLineGraph();

	
	protected final void addAmplitudeGraphToWrapper(MultiLineGraph oLineGraph)
	{
		oAmplitudeGraphWrapper.addGraph(oLineGraph);
		
		// Do setup for line graph
	  oAmplitudeGraphWrapper.setAxisTitlesAndUnits(
				"Amplitude_Frequency", "Amplitude_Hz", 
				"Amplitude", "Amplitude_dBm");
		oAmplitudeGraphWrapper.setTitle("Amplitude_FFT");
	}
	
	
	protected final MultiGraphWrapper getAmplitudeGraphWrapper()
	{
		return oAmplitudeGraphWrapper;
	}
	
	
}
