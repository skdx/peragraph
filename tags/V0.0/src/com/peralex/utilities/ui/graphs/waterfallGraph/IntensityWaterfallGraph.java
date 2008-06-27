package com.peralex.utilities.ui.graphs.waterfallGraph;


/**
 * A waterfall graph that displays amplitudes and detections, but uses an intensity axis
 * to calculate the necessary colors.
 * 
 * @author Noel Grandin
 */
public class IntensityWaterfallGraph extends AmplitudeDetectionWaterfallGraph
{ 
	private final WaterfallIntensityAxis oIntensityAxis;
  
	/**
	 * scale the intensity axis so that it just fits current min/max values in the data.
	 */
  private boolean bAutoScale = false;
  private final Variance autoScaleVariance = new Variance();
  private final ArithmeticMean autoScaleMean = new ArithmeticMean();
  private float fThresholdMin = Float.MIN_VALUE;
  private float fThresholdMax = Float.MIN_VALUE;
  
  /** 
   * Creates a new instance of cAmplitudeWaterfallGraph 
   */
  public IntensityWaterfallGraph()
  {
  	this.oIntensityAxis = new WaterfallIntensityAxis(this);
 }
  
  /**
   * set the intensity axis thresholds based on the auto scaling algorithm.
   */
  private void configureAutoScaleThreshold()
  {
  	/* 99% of the data will lie within 3 standard_deviations of the mean,
  	 * assuming the data is normally distributed.
  	 * (where standard_deviation = sqrt(variance))
  	 */
  	final double spread = Math.sqrt(getAutoScaleVariance()) * 3d;
  	oIntensityAxis.getAxis().setThresholdValues(
				(float) (getAutoScaleMean() - spread), 
				(float) (getAutoScaleMean() + spread));
  }
  
	public final void setColorScaleOn(boolean bColourScale)
	{
		oIntensityAxis.getAxis().setColorScale(bColourScale);
	}
	
  @Override
	protected void convertAmplitudeToLine(int[] aiImageData, float[] afAmplitudeFrame_dBm)
  {
		if (bAutoScale)
		{
			for (float amplitude_dbm : afAmplitudeFrame_dBm)
			{
				// ignore zeros, they indicate absense of data
				if (amplitude_dbm!=0)
				{
					autoScaleMean.increment(amplitude_dbm);
					autoScaleVariance.increment(amplitude_dbm);
				}
			}
			
			configureAutoScaleThreshold();
		}
		
		oIntensityAxis.getAxis().convertAmplitudeToLine(aiImageData, afAmplitudeFrame_dBm);
  }
  
  @Override
	protected void convertAmplitudeToLine(int[] aiImageData, short[] awAmplitude_cdBm)
  {
		if (bAutoScale)
		{
			for (short wAmplitude_cdBm : awAmplitude_cdBm)
			{
				// ignore zeros, they indicate absence of data
				if (wAmplitude_cdBm!=0)
				{
					autoScaleMean.increment(wAmplitude_cdBm/100f);
					autoScaleVariance.increment(wAmplitude_cdBm/100f);
				}
			}
			
			configureAutoScaleThreshold();
		}
		
		oIntensityAxis.getAxis().convertAmplitudeToLine(aiImageData, awAmplitude_cdBm);
  }
  
	protected final boolean isAutoScale()
	{
		return bAutoScale;
	}

	protected final double getAutoScaleVariance()
	{
		return autoScaleVariance.getVariance();		
	}
	
	protected final double getAutoScaleMean()
	{
		return autoScaleMean.getMean();		
	}

	void autoScaleSelected(boolean active)
	{
		if (active==bAutoScale) return;
		bAutoScale = active;
		if (bAutoScale)
		{
			// save old values
			fThresholdMin = oIntensityAxis.getAxis().getThresholdMin();
			fThresholdMax = oIntensityAxis.getAxis().getThresholdMax();
		}
		else
		{
			autoScaleMean.clear();
			autoScaleVariance.clear();
			oIntensityAxis.getAxis().setThresholdValues(fThresholdMin, fThresholdMax);
		}
		oIntensityAxis.setAutoScaleMode(bAutoScale);
	}
	
	public WaterfallIntensityAxis getWaterfallIntensityAxis()
	{
		return this.oIntensityAxis;
	}
	
	public void setThresholdLimits(float fMinThresholdLimit, float fMaxThresholdLimit)
	{
		oIntensityAxis.getAxis().setThresholdLimits(fMinThresholdLimit, fMaxThresholdLimit);
	}
	
	public void setThresholdValues(float fMinThresholdValue, float fMaxThresholdValue)
	{
		oIntensityAxis.getAxis().setThresholdValues(fMinThresholdValue, fMaxThresholdValue);
	}
}
