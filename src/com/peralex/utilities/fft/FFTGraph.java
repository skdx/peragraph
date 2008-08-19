package com.peralex.utilities.fft;

import java.util.ArrayList;
import java.util.List;

import com.peralex.utilities.ui.graphs.lineGraph.SingleLineGraph;

/**
 * This class performs an FFT on incoming data, and then displays the data as a graph.
 * 
 * @author Noel Grandin
 */
public class FFTGraph extends SingleLineGraph
{
	private static final short[] RESOLUTION_LIST = new short[] { 256, 512, 1024, 2048, 4096, 8192, 16384 };

	private static final List<Short> resolutionList = new ArrayList<Short>(20);
	static
	{
		for (int i = 0, len = RESOLUTION_LIST.length; i < len; ++i)
		{
			resolutionList.add(Short.valueOf(RESOLUTION_LIST[i]));
		}
	}

	private short currentFFTResolution = resolutionList.get(0).shortValue();

	private FFT oFFT;

	private int fftBufferIndex = 0;

	// these are used to dump data if we exceed MAX_DATA_RATE to limit CPU usage
	private int dumpingCounter = 0;

	private boolean reInitialiseFFT_Buffer = false;

	private int dumpingCounterMax = 1;

	private float [] displayData;
	
	public FFTGraph()
	{
	}

	/**
	 * Set the number of frames to ignore before doing an FFT.
	 * Used to limit the CPU usage when data rates are high.
	 */
	public void setNoFramesToDrop(int dumpingCounterMax)
	{
		this.dumpingCounterMax = dumpingCounterMax;
	}
	
	/**
	 * Convert input from a double[] into a float[] of subset of input data
	 * 
	 * @return a (newly created) float array based on the input double array
	 */
	private float[] convertToDoubleArray(double[] inputData)
	{
		// Cache the array to reduce GC overhead.
		if (displayData==null || displayData.length!=inputData.length)
		{
			displayData = new float[inputData.length];
		}
		for (int i = 0; i < inputData.length; ++i)
		{
			displayData[i] = (float) inputData[i];
		}
		return displayData;
	}

	/**
	 * @param input an array containing real and imaginary value alternating (ie, [real] [imag] [real] [imag]...)
	 */
	public void computeFFT(short[] input)
	{
		// Ensure input size is correct
		final int inputLen = input.length;
		if ((inputLen % 2) == 1)
		{
			throw new IllegalArgumentException("the sample counts for the reals, differs from the counts for imaginaries");
		}

		// The GUI thread might change the current resolution.
		// Make a local copy of it (instead of synchronization)
		final int currentResolution = this.currentFFTResolution;

		// Initialise oFFT buffer if it's empty
		if (oFFT == null)
		{
			initialiseFFT_Buffer(currentResolution);
		}
		else if (oFFT.getFFT_size() != currentResolution)
		{
			// if it's currently filling up data, only change oFFT size when the filling-up process finishes.
			// (otherwise oFFT will start at a random point which may contain discontinuity.)
			reInitialiseFFT_Buffer = true;
		}

		// Set up length and get oFFT's internal buffer
		final int sampleSize = inputLen / 2;
		final int bufferLen = oFFT.getRealBuffer().length;

		for (int i = 0; i < sampleSize; i++)
		{
			final boolean isBufferFull = fftBufferIndex >= bufferLen;
			// Do oFFT computation if there is sufficient data
			if (isBufferFull)
			{
				if (reInitialiseFFT_Buffer)
				{
					initialiseFFT_Buffer(currentResolution);
					fftBufferIndex = 0;
					reInitialiseFFT_Buffer = false;
					continue;
				}

				// if we exceed the max data rate, we start dumping frames to keep the CPU usage reasonable
				++dumpingCounter;
				if (dumpingCounter > dumpingCounterMax)
				{
					doFFT(currentResolution);

					dumpingCounter = 0;
				}

				// reset bufferindex to start filling up again
				fftBufferIndex = 0;
			}
      
      final int index = i * 2;
      oFFT.getRealBuffer()[fftBufferIndex] = input[index];
      oFFT.getImagBuffer()[fftBufferIndex] = input[index + 1];
      ++fftBufferIndex;
		}
	}

	private void initialiseFFT_Buffer(final int currentResolution)
	{
		oFFT = new FFT(currentResolution, true);
		// when we resize the oFFT, we need to reset the fftBufferIndex
		fftBufferIndex = 0;
		// set this in such a way that we will do an oFFT as soon as possible
		dumpingCounter = dumpingCounterMax;
	}

	private void doFFT(final int currentResolution)
	{
		final double fullScaleAmplitude_dBm = getMaximumYZoomLimit();
		final double preFFTScalingFactor = Math.pow(10.0, fullScaleAmplitude_dBm / 20.0)
				/ ((long) Short.MAX_VALUE * currentResolution);

		final double postFFTScalingFactor = 20.0 / Math.log(10);

		// This is just an estimate of the difference between PXGViewer and PXGAnalysis
		final double lostEnergy = 6.87;

		/*
		 * Calculate oFFT and get the spectra. 
		 * Returning a temporary array that will be reused for next calculation, in
		 * order to avoid the unnecessary array-creation. 
		 * Hence, don't modify and/or store the resultant array; rather copy
		 * the values then process the copied values.
		 */
		final double[] dMagnitude = oFFT.calculate(preFFTScalingFactor, postFFTScalingFactor, lostEnergy);

		final double bandwidth_Hz = getMaximumXZoomLimit() - getMinimumXZoomLimit();
		final double centreFrequency_Hz = getMinimumXZoomLimit() + (bandwidth_Hz/2f);
		setGraphData(centreFrequency_Hz - (bandwidth_Hz / 2), centreFrequency_Hz + (bandwidth_Hz / 2),
				convertToDoubleArray(dMagnitude));
	}

	public static boolean isValidResolution(short resolution)
	{
		return resolutionList.contains(Short.valueOf(resolution));
	}

	public short getFFTResolution()
	{
		return currentFFTResolution;
	}

	public void setFFTResolution(short currentResolution)
	{
		if (!resolutionList.contains(Short.valueOf(currentResolution)))
		{
			throw new IllegalArgumentException("resolution not in valid list");
		}
		this.currentFFTResolution = currentResolution;
	}
}
