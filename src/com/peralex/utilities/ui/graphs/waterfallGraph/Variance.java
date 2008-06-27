/**
 * Noel Grandin: This class was created by pulling bits of the Variance, FirstMoment, and SecondMoment classes from the
 * jakarata-commons-math source code tree.
 * 
 * I did it this way because (a) the library was still in alpha and (b) I don't need the rest of the library and (c) I
 * don't need it to be bug-free, only a reasonable approximation.
 * 
 */

/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.peralex.utilities.ui.graphs.waterfallGraph;

// package org.apache.commons.math.stat.descriptive.moment;

/**
 * Computes the first moment (arithmetic mean). Uses the definitional formula:
 * <p>
 * mean = sum(x_i) / n
 * <p>
 * where <code>n</code> is the number of observations.
 * <p>
 * To limit numeric errors, the value of the statistic is computed using the following recursive updating algorithm:
 * <p>
 * <ol>
 * <li>Initialize <code>m = </code> the first value</li>
 * <li>For each additional value, update using <br>
 * <code>m = m + (new value - m) / (number of observations)</code></li>
 * </ol>
 * <p>
 * Returns <code>Double.NaN</code> if the dataset is empty.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If multiple threads access an instance of this
 * class concurrently, and at least one of the threads invokes the <code>increment()</code> or <code>clear()</code>
 * method, it must be synchronized externally.
 */
/**
 * Computes a statistic related to the Second Central Moment. Specifically, what is computed is the sum of squared
 * deviations from the sample mean.
 * <p>
 * The following recursive updating formula is used:
 * <p>
 * Let
 * <ul>
 * <li> dev = (current obs - previous mean) </li>
 * <li> n = number of observations (including current obs) </li>
 * </ul>
 * Then
 * <p>
 * new value = old value + dev^2 * (n -1) / n.
 * <p>
 * Returns <code>Double.NaN</code> if no data values have been added and returns <code>0</code> if there is just one
 * value in the data set.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If multiple threads access an instance of this
 * class concurrently, and at least one of the threads invokes the <code>increment()</code> or <code>clear()</code>
 * method, it must be synchronized externally.
 */
class Variance
{

	/** Count of values that have been added */
	private long n;

	/** First moment of values that have been added */
	private double m1;

	/**
	 * Deviation of most recently added value from previous first moment. Retained to prevent repeated computation in
	 * higher order moments.
	 */
	private double dev;

	/**
	 * Deviation of most recently added value from previous first moment, normalized by previous sample size. Retained to
	 * prevent repeated computation in higher order moments
	 */
	private double nDev;

	/** second moment of values that have been added */
	private double m2;

	/**
	 * Create a FirstMoment instance
	 */
	public Variance()
	{
		n = 0;
		m1 = Double.NaN;
		dev = Double.NaN;
		nDev = Double.NaN;
		m2 = Double.NaN;
	}

	public void increment(final double d)
	{
		if (n < 1)
		{
			m1 = m2 = 0.0;
		}
		if (n == 0)
		{
			m1 = 0.0;
		}
		n++;
		double n0 = n;
		dev = d - m1;
		nDev = dev / n0;
		m1 += nDev;

		m2 += ((double) n - 1) * dev * nDev;
	}

	public void clear()
	{
		m1 = Double.NaN;
		n = 0;
		dev = Double.NaN;
		nDev = Double.NaN;
		m2 = Double.NaN;
	}

	public double getVariance()
	{
		if (n == 0)
		{
			return Double.NaN;
		}
		else if (n == 1)
		{
			return 0d;
		}
		else
		{
			return m2 / (n - 1d);
		}
	}
}