package com.peralex.utilities.fft;

import java.util.Arrays;

/**
 * 
 * History: 
 *   - From the Unix Version 2.4 by Steve Sampson, Public Domain, September 1988. 
 *     Adapted for Java by Ben Stoltz <stoltz@sun.com>, September 1997 
 *     Refer to http://www.pressurewave.com/~stoltz/cFft.html for updates and related resources. 
 *   - Modified by mmcgrath. Changed names and package as well as adding support for complex numbers. 
 *   - Further modified by David Lee.
 *   
 */
public class FFT
{
	/**
	 * FFT must be a power of two.
	 */
	private int iFFT_size = 0;

	/**
	 * The order of the FFT.
	 */
	private int iFFT_order = 0;

	/**
	 * Permutation LUT.
	 */
	private int[] aiPermLUT;

	/**
	 * Sine LUT.
	 */
	private double[] adSineLUT;

	/**
	 * Array used to store real part of result, not ordered.
	 */
	private double[] adRealData;

	/**
	 * Array used to store imag part of result, not ordered.
	 */
	private double[] adImagData;

	/**
	 * Output array containing spectra of FFT.
	 */
	private double[] adSpectra;

	private final double[] blackmanWindow;

	private final double[] fftResult;

	/**
	 * Constructor specifying FFT size.
	 * 
	 * @param FFT_size Number of samples to FFT at a time. Must be a power of two.
	 */
	public FFT(int FFT_size, boolean useBlackmanWindow)
	{
		setupFFT(FFT_size);
		if (useBlackmanWindow)
		{
			blackmanWindow = FFT.blackmanWindow(FFT_size);
		}
		else
		{
			blackmanWindow = new double[FFT_size];
			Arrays.fill(blackmanWindow, 1.0);
		}
		fftResult = new double[FFT_size];
	}

	/**
	 * Initialization routine precalculates information in order to speed up subsequent FFT calculations.
	 */
	private void setupFFT(int FFT_size)
	{
		if (iFFT_size == FFT_size)
		{
			return;
		}

		// Input data array length must be a power of two
		iFFT_order = (int) (Math.log(FFT_size) / Math.log(2.0));
		if ((1 << iFFT_order) != FFT_size)
		{
			iFFT_order = 0;
			throw new IllegalArgumentException("FFT size must be a power of 2. " + FFT_size);
		}

		iFFT_size = FFT_size;

		/*
		 * Build table of sines. The table is a sampling of sin(x) for x = 0 to 2pi step d, where d is 2pi/N. N is the total
		 * number of samples.
		 */
		adSineLUT = new double[iFFT_size];
		for (int i = 0; i < iFFT_size; i++)
		{
			adSineLUT[i] = Math.sin((i * (2 * Math.PI)) / iFFT_size);
		}

		// A place to hold the data
		adRealData = new double[iFFT_size];
		adImagData = new double[iFFT_size];

		// Resulting FFT is put in adSpectra
		adSpectra = new double[iFFT_size];

		// Build the bit reversal lookup table
		aiPermLUT = new int[iFFT_size];
		int result;
		for (int index = 0; index < iFFT_size; index++)
		{
			result = 0;
			for (int loop = 0; loop < iFFT_order; loop++)
			{
				if ((index & (1 << loop)) != 0)
				{
					result |= (1 << (iFFT_order - 1 - loop));
				}
			}
			aiPermLUT[index] = result;
		}
	}

	/**
	 * Get the size that the FFT processor has been setup for.
	 */
	public final int getFFT_size()
	{
		return iFFT_size;
	}

	/**
	 * Similar to calculate(double[] rdata, double[] idata), but rely on the external program to directly manipulate the
	 * internal arrays to save the time on creating arrays and moving the data around.
	 * 
	 * @return an array of spectrum data. This array will be reused for next calculation, in order to avoid the
	 *         unnecessary array-creation. Hence, don't modify and/or store the resultant array; rather copy the values
	 *         then process the copied values.
	 */
	public double[] calculate(double preFFTScalingFactor, double postFFTScalingFactor, double lostEnergy)
	{
		preFFTProcess(preFFTScalingFactor);

		runFFT();

		double[] spectra = calculateSpectra();

		postFFTProcess(spectra, postFFTScalingFactor, lostEnergy);

		return fftResult;
	}

	private void preFFTProcess(double preFFTScalingFactor)
	{
		for (int i = 0, len = adRealData.length; i < len; ++i)
		{
			final double coefficient = blackmanWindow[i] * preFFTScalingFactor;
			adRealData[i] *= coefficient;
			adImagData[i] *= coefficient;
		}
	}

	private void postFFTProcess(double[] spectra, double postFFTScalingFactor, double lostEnergy)
	{
		for (int i = 0; i < spectra.length; ++i)
		{
			double m = spectra[i];
			if (m <= 0) // avoid zero, so the log of m won't be NaN
			{
				m = Double.MIN_VALUE;
			}

			// dMagnitude[i] = (Math.log( dMagnitude[i] ) / Math.log( 10 )) * scalingFactor;
			spectra[i] = Math.log(m) * postFFTScalingFactor + lostEnergy;
		}

		int half = spectra.length / 2;

		System.arraycopy(spectra, half, fftResult, 0, half);
		System.arraycopy(spectra, 0, fftResult, half, half);
	}

	public double[] getImagBuffer()
	{
		return adImagData;
	}

	public double[] getRealBuffer()
	{
		return adRealData;
	}

	public void runFFT()
	{
		// begin FFT
		int i1 = iFFT_size / 2;
		int i2 = 1;

		/* perform the butterfly's */
		for (int loop = 0; loop < iFFT_order; loop++)
		{
			int i3 = 0;
			int i4 = i1;
			int y;
			double z1;
			double z2;

			for (int loop1 = 0; loop1 < i2; loop1++)
			{
				y = aiPermLUT[i3 / i1];
				z1 = adSineLUT[((y) + (iFFT_size >> 2)) % iFFT_size]; // cosine
				z2 = -adSineLUT[y];

				double a1;
				double a2;
				double b1;
				double b2;
				for (int loop2 = i3; loop2 < i4; loop2++)
				{
					a1 = adRealData[loop2];
					a2 = adImagData[loop2];

					b1 = (z1 * adRealData[loop2 + i1]) - (z2 * adImagData[loop2 + i1]);
					b2 = (z2 * adRealData[loop2 + i1]) + (z1 * adImagData[loop2 + i1]);

					adRealData[loop2] = a1 + b1;
					adImagData[loop2] = a2 + b2;

					adRealData[loop2 + i1] = a1 - b1;
					adImagData[loop2 + i1] = a2 - b2;
				}

				i3 += (i1 << 1);
				i4 += (i1 << 1);
			}

			i1 >>= 1;
			i2 <<= 1;
		}
	}

	/**
	 * Calculate the spectra following an FFT calculation.
	 * 
	 * @return the spectra of the FFT
	 */
	public final double[] calculateSpectra()
	{
		for (int i = 0; i < iFFT_size; i++)
		{
			int p = aiPermLUT[i];

			double real = adRealData[p];
			double imaginary = adImagData[p];

			// Calculate power magnitude
			adSpectra[i] = Math.sqrt((real * real) + (imaginary * imaginary));
		}

		return adSpectra;
	}

	public static final double[] blackmanWindow(final int iSize)
	{
		double[] adBlackmanWindow = new double[iSize];
		final double i = iSize - 1;
		final double factorA = (2 * Math.PI) / i;
		final double factorB = (4 * Math.PI) / i;

		for (int n = 0; n < iSize; ++n)
		{
			// NP = resolution (FFT frame-size)
			// Blackman-Harris: W(n)=0.35875 -0.48829[cos(2*PI*n/(NP-1))] +0.14128[cos(4*PI*n/(NP-1))]
			// -0.01168[cos(6*PI*n/(NP-1))]
			// Blackman: W(n) = 0.42323 -0.49755[cos(2*PI*n/(NP-1))] +0.07922cos[cos(4*PI*n/(NP-1))]
			adBlackmanWindow[n] = 0.42323 - 0.49755 * Math.cos(factorA * n) + 0.07922 * Math.cos(factorB * n);
		}
		return adBlackmanWindow;
	}
}
