package com.peralex.utilities.ui.graphs.hopperHistogram;

import java.awt.Color;

/**
 *
 * @author  Andre
 */
public interface IHopperHistogram
{
  
  /**
   * This method passes new data into the Histogram.
   *
   * @param asDimensionName the name of the dimension
   * @param asDimensionUnitName the name of units for the dimension
   * @param awNumPoints the number of points in the histogram for this dimension (ns)
   * @param afMinScaleValue minimum value for the scale of the dimension
   * @param afMaxScaleValue maximum value for the scale of the dimension
   * @param abIsLogarithmic is the dimension's scale logarithmic?
   * @param afHistogramData histogram data 
   */
  void onDataReceived(String[] asDimensionName, String[] asDimensionUnitName, short[] awNumPoints, float[] afMinScaleValue, float[] afMaxScaleValue, boolean[] abIsLogarithmic, float[] afHistogramData);  
  
  /**
   * This methods sets the XAxisRange.
   *
   * @param dXAxisMinimum contains the minimum
   * @param dXAxisMaximum contains the maximum
   * @param dYAxisMinimum contains the minimum
   * @param dYAxisMaximum contains the maximum
   */
  void setAxisRanges(double dXAxisMinimum, double dXAxisMaximum, double dYAxisMinimum, double dYAxisMaximum);
  
  /**
   * This methods sets the XAxisRange.
   *
   * @param dXAxisMinimum contains the minimum
   * @param dXAxisMaximum contains the maximum
   */
  void setXAxisRange(double dXAxisMinimum, double dXAxisMaximum);
  
  /**
   * This methods sets the YAxisRange.
   *
   * @param dYAxisMinimum contains the minimum
   * @param dYAxisMaximum contains the maximum
   */
  void setYAxisRange(double dYAxisMinimum, double dYAxisMaximum);
  
  /**
   * Get the XAxis Minimum.
   *
   * @return dMinX.
   */
  double getXAxisMinimum();
  
  /**
   * Get the XAxis Maximum.
   *
   * @return dMaxX.
   */
  double getXAxisMaximum();

  /**
   * Get the YAxis Minimum.
   *
   * @return dMinY.
   */
  double getYAxisMinimum();
  
  /**
   * Get the YAxis Maximum.
   *
   * @return dMaxY.
   */
  double getYAxisMaximum();

  /**
   * Set the Title of the Graph.
   *
   * @param sTitleText
   */
  void setTitle(final String sTitleText);
  
  /**
   * Get the Title of the Graph.
   *
   * @return sTitleText
   */
  String getTitle();
  
  /**
   * Set the Text of the X-Axis.
   *
   * @param sXAxisText
   */
  void setXAxisText(final String sXAxisText);
  
  /**
   * Get the Text of the X-Axis.
   *
   * @return XAxisTitle
   */
  String getXAxisText();
  
  /**
   * Set the Text of the Y-Axis.
   *
   * @param sYAxisText
   */
  void setYAxisText(final String sYAxisText);
  
  /**
   * Get the Text of the Y-Axis.
   *
   * @return YAxisTitle
   */
  String getYAxisText();
  
  /**
   * This method is used to Clear the graph.
   */
  void clear();
  
  /**
   * This method sets the Background color of this graph.
   *
   * @param oBackgroundColor contains the new color.
   */
  void setBackgroundColor(Color oBackgroundColor);
  
  /**
   * This method sets the Grid Color of this graph.
   *
   * @param oGridColor contains the new color.
   */
  void setGridColor(Color oGridColor);
  
  /**
   * This method sets whether the XAxis is Logarithmic or not.
   */  
  void setXAxisLogarithmic(boolean bXAxisLogarithmic);  
  
  /**
   * This method returns whether the XAxis is Logarithmic or not.
   *
   * @return bXAxisLogarithmic.
   */  
  boolean isXAxisLogarithmic();
}
