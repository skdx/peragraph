package com.peralex.utilities.ui.graphs.waterfallGraph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;

import com.peralex.sharedlibs.dsphostl.TimeStamp;
import com.peralex.utilities.objectpool.GraphObjectPool;
import com.peralex.utilities.ui.graphs.DirectionLib;

/**
 * 
 * @author Jaco Jooste
 */
public class AmplitudeDetectionWaterfallGraph extends WaterfallGraph
{
	private static final class DetectionData {
		public TimeStamp oTimeStamp = new TimeStamp(100);
		public long lFirstBinFrequency_Hz;
		public long lFrequencyResolution_cHz;
		public short[] awDetection_cdBm;
		
		public boolean isDetected(int index) {
			// -32768 means "blocked" and -32767 means "not detected"
			return awDetection_cdBm[index] > -32767;
		}
	}
	
	/**
	 * Colour used to display detections
	 */
	private Color oDetectionColor = Color.GREEN;

	/**
	 * Keeps a reference to the DetectionData.
	 */
	private DetectionData oDetectionData = null;


	private final ImageDataConverter imageDataConverter = new ImageDataConverter();
	
	private final WaterfallDetectionsLayer detectionsLayer = new WaterfallDetectionsLayer();

	public AmplitudeDetectionWaterfallGraph()
	{
	}

	/**
	 * This will draw the Graph
	 */
  @Override
  protected void drawGraph(Graphics2D g)
  {		
  	super.drawGraph(g);
  	
  	// draw the detections layer
		detectionsLayer.drawImageTo(g, this, isZoomed(), 
				getMinimumX(), getMaximumX(), 
				getMinimumXZoomLimit(), getMaximumXZoomLimit(),
				getWidth());
  }

  @Override
  protected void clearImage()
  {
  	super.clearImage();
		detectionsLayer.clearImage();
  }

  @Override
  public void componentResized(ComponentEvent e)
	{
		super.componentResized(e);
		
		if (!detectionsLayer.isImageCreated())
		{
			detectionsLayer.setDisplaySizeAndClear(getWidth(), getHeight());
		}
		else if (detectionsLayer.getHeight() != getHeight())
		{
			detectionsLayer.resizeHeight(getHeight());
		}
		else if (detectionsLayer.getDisplayWidth() != getWidth())
		{
			detectionsLayer.resizeDisplayWidth(getWidth());
		}
	}
  
  @Override
  protected void scrollUp()
  {
  	super.scrollUp();
  	// if we haven't received any detection data yet, there is nothing to scroll
  	if (detectionsLayer.isImageCreated())
  	{
  		detectionsLayer.scrollUp();
  	}
  }
  
  @Override
  protected void scrollDown()
  {
  	super.scrollDown();
  	// if we haven't received any detection data yet, there is nothing to scroll
  	if (detectionsLayer.isImageCreated())
  	{
  		detectionsLayer.scrollDown();
  	}
  }
  
	public void setAmplitudeValueRange(float min, float max)
	{
		imageDataConverter.setRange(min, max);
	}

	public void setAmplitudeMin(float min)
	{
		imageDataConverter.setMin(min);
	}
	
	public float getAmplitudeMin()
	{
		return imageDataConverter.getMin();
	}
	
	public void setAmplitudeMax(float max)
	{
		imageDataConverter.setMax(max);
	}
	
	public float getAmplitudeMax()
	{
		return imageDataConverter.getMax();
	}
	
	/**
	 * Set the azimuth direction data for the graph that must be drawn.
	 */
	public void setDirectionAzimuthData(TimeStamp oTimeStamp, short[] awAmplitude_cdBm, short [] awAzimuth_cdeg)
	{
		final int [] aiImageData = GraphObjectPool.checkOutIntArray(awAmplitude_cdBm.length);
		convertDirectionAzimuthToLine(aiImageData, awAmplitude_cdBm, awAzimuth_cdeg);

		addWaterfallLine(oTimeStamp, aiImageData, awAmplitude_cdBm.length);

		GraphObjectPool.checkIn(aiImageData);
		
		// Draw the detection on top of the amplitudes. First check if it goes on
		// top of the current frame and then if it goes on top of the previous one.
		drawDetections();

		repaint();
	}
	
	/**
	 * Set the direction data for the graph that must be drawn.
	 */
	public void setDirectionElevationData(TimeStamp oTimeStamp, short[] awAmplitude_cdBm, short [] awElevation_cdeg)
	{
		final int [] aiImageData = GraphObjectPool.checkOutIntArray(awAmplitude_cdBm.length);
		convertDirectionElevationToLine(aiImageData, awAmplitude_cdBm, awElevation_cdeg);

		addWaterfallLine(oTimeStamp, aiImageData, awAmplitude_cdBm.length);

		GraphObjectPool.checkIn(aiImageData);
		
		// Draw the detection on top of the amplitudes. First check if it goes on
		// top of the current frame and then if it goes on top of the previous one.
		drawDetections();

		repaint();
	}

	/**
	 * Set the amplitude data for the graph that must be drawn.
	 */
	public void setAmplitudeData(TimeStamp oTimeStamp, float[] afAmplitudeFrame_dBm)
	{
		final int dataLength = afAmplitudeFrame_dBm.length;
		// Be careful here, the int array returned might be longer than requested
		final int [] aiImageData = GraphObjectPool.checkOutIntArray(dataLength);
		convertAmplitudeToLine(aiImageData, afAmplitudeFrame_dBm);

		addWaterfallLine(oTimeStamp, aiImageData, dataLength);
		
		GraphObjectPool.checkIn(aiImageData);
		
		// Draw the detection on top of the amplitudes. First check if it goes on
		// top of the current frame and then if it goes on top of the previous one.
		drawDetections();

		repaint();
	}

	/**
	 * Set the amplitude data for the graph that must be drawn.
	 * 
	 * @param awAmplitude_cdBm amplitudes in hundredth's of a dBm
	 */
	public void setAmplitudeData(TimeStamp oTimeStamp, short[] awAmplitude_cdBm)
	{
		final int [] aiImageData = GraphObjectPool.checkOutIntArray(awAmplitude_cdBm.length);
		convertAmplitudeToLine(aiImageData, awAmplitude_cdBm);

		addWaterfallLine(oTimeStamp, aiImageData, awAmplitude_cdBm.length);

		GraphObjectPool.checkIn(aiImageData);
		
		// Draw the detection on top of the amplitudes. First check if it goes on
		// top of the current frame and then if it goes on top of the previous one.
		drawDetections();

		repaint();
	}

	protected void convertAmplitudeToLine(int[] aiImageData, float[] afAmplitudeFrame_dBm)
	{
		for (int i = 0; i < afAmplitudeFrame_dBm.length; i++)
		{
			aiImageData[i] = imageDataConverter.computeLinePixel(afAmplitudeFrame_dBm[i]);
		}
	}

	/**
	 * @param awAmplitude_cdBm amplitudes in hundredth's of a dBm
	 */
	protected void convertAmplitudeToLine(int[] aiImageData, short[] awAmplitude_cdBm)
	{
		for (int i = 0; i < awAmplitude_cdBm.length; i++)
		{
			aiImageData[i] = imageDataConverter.computeLinePixel(awAmplitude_cdBm[i] / 100f);
		}
	}

	/**
	 * @param awAmplitude_cdBm amplitudes in hundredth's of a dBm
	 */
	protected void convertDirectionAzimuthToLine(int [] aiImageData, short[] awAmplitude_cdBm, short [] awAzimuth_cdeg)
	{
		for (int i=0; i<awAmplitude_cdBm.length; i++) {
      if (awAmplitude_cdBm[i] > -32768)
      {
      	aiImageData[i] = DirectionLib.degreesToRGB(awAzimuth_cdeg[i]/100f);
      }
		}
	}
	
	/**
	 * @param awAmplitude_cdBm amplitudes in hundredth's of a dBm
	 */
	protected void convertDirectionElevationToLine(int [] aiImageData, short[] awAmplitude_cdBm, short [] awElevation_cdeg)
	{
		for (int i=0; i<awAmplitude_cdBm.length; i++) {
      if (awAmplitude_cdBm[i] > -32768)
      {
        final int degree = (awElevation_cdeg[i] / 100) % 360;
        final int color;
        if ( degree < 0 || degree > 180 )
        {
          color = 0xff000000; // black
        }
        else if ( degree > 90 )
        {
        	// remap the 90-180 range to the 0-90 range, since in elevation terms, it is the same thing.
          color = DirectionLib.degreesToRGB(180 - degree);
        }
        else 
        {
          color = DirectionLib.degreesToRGB( degree );
        }
      	aiImageData[i] = color;
      }
		}
	}
	
	private void drawDetections()
	{
		if (oDetectionData == null)
		{
			return;
		}

		final int imageDataLen = oDetectionData.awDetection_cdBm.length;
		if (!detectionsLayer.isImageCreated() || detectionsLayer.getDataWidth()!=imageDataLen)
		{
			detectionsLayer.resizeDataWidth(imageDataLen);
		}
		
		final long lCurrentDetectionTime_usec = oDetectionData.oTimeStamp.getPeriod_usec();

		if (isTimeWithinOneFramePeriod(lCurrentDetectionTime_usec))
		{
			detectionsLayer.setColor(oDetectionColor);
			for (int i = 0; i < oDetectionData.awDetection_cdBm.length; i++)
			{
				if (oDetectionData.isDetected(i))
				{
					detectionsLayer.drawPixelBottom(i);
				}
			}
		}
		else if (isTimeWithinOneFramePeriod(lCurrentDetectionTime_usec))
		{
			detectionsLayer.setColor(oDetectionColor);
			for (int i = 0; i < oDetectionData.awDetection_cdBm.length; i++)
			{
				if (oDetectionData.isDetected(i))
				{
					detectionsLayer.drawPixelBottomRelative(i, -1);
				}
			}
		}
	}

	/**
	 * Set the detection data for the graph that must be drawn.
	 */
	public void setDetectionData(long lTimeValue_msec, short wTimeValue_usec, long lFirstBinFrequency_Hz, long lFrequencyResolution_cHz, short[] awDetection_cdBm)
	{
		oDetectionData = new DetectionData();
		oDetectionData.oTimeStamp = new TimeStamp(lTimeValue_msec, wTimeValue_usec);
		oDetectionData.lFirstBinFrequency_Hz = lFirstBinFrequency_Hz;
		oDetectionData.lFrequencyResolution_cHz = lFrequencyResolution_cHz;
		oDetectionData.awDetection_cdBm = awDetection_cdBm;
	}
	
	public final float getDetectionAmplitude_dBm(double fFrequency_Hz)
	{
		if (oDetectionData == null || oDetectionData.awDetection_cdBm.length < 2)
		{
			return 0;
		}

		final double fBinWidth_Hz = oDetectionData.lFrequencyResolution_cHz/100f;
		final int idx = (int) Math.round((fFrequency_Hz - oDetectionData.lFirstBinFrequency_Hz) / fBinWidth_Hz);
		
		if (idx < 0)
			return 0;
		return oDetectionData.awDetection_cdBm[idx]/100f;
	}

	public final void setDetectionDisplayColour(Color oDetectionDisplayColour)
	{
		this.oDetectionColor = oDetectionDisplayColour;
	}

	private static class ImageDataConverter
	{
		private float max;

		private float min;

		private float slope;

		private float yIntercept;

		public void setRange(float min, float max)
		{
			this.min = min;
			this.max = max;
			final boolean equalZero = Float.compare(max - min, 0) == 0;
			if (equalZero)
			{
				return;
			}
			this.slope = 255 / (max - min);
			this.yIntercept = -slope * min;
		}
		
		public void setMin(float min) {
			this.min = min;
			// force re-calculation
			setRange(min, max);
		}

		public void setMax(float max) {
			this.max = max;
			// force re-calculation
			setRange(min, max);
		}
		
		public float getMin() {
			return this.min;
		}
		
		public float getMax() {
			return this.max;
		}
		
		public int computeLinePixel(float fCurrentAmplitude_dBm)
		{
			if (fCurrentAmplitude_dBm >= min && fCurrentAmplitude_dBm <= max)
			{
				int colour = (int) ((slope * fCurrentAmplitude_dBm) + yIntercept);

				// clamp it to useful range
				if (colour < 0)
				{
					colour = 0;
				}
				else if (colour > 255)
				{
					colour = 255;
				}

				final int colourByte = colour & 0xFF;
				return 0xFF000000 | (colourByte << 16) | (colourByte << 8) | colourByte;
			} else {
				return 0;
			}
		}
	}
}
