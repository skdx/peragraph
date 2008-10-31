package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;

import com.peralex.utilities.ui.ListenerSupport;

/**
 * This is the draw surface for a graph. FIXME x,y grid spacing should be font-relative by default, not hard-coded
 * pixels.
 * 
 * @author Andre
 */
public class GridDrawSurface extends PopupMenuDrawSurface
{

	private final ListenerSupport<IGridListener> gridListeners = ListenerSupport
			.create(IGridListener.class);

	private static final class AxisValues
	{
		/**
		 * Default unit range values
		 */
		public double fMinimum, fMaximum;

		/**
		 * These arrays stores the steps at which the vertical and horizontal grids must be drawn.
		 */
		public int[] aiGridSteps;

		/**
		 * Stores the scaling unit that will be displayed for each axis.
		 */
		public String sScaleUnit = "";

		/**
		 * Stores the scaling factor buy which X and Y values must be multiplied when displayed.
		 */
		public long lScalingFactor = 1;

		/**
		 * Should the display show K°(engineering) or 1000's°(decimal).
		 */
		public boolean bDecimalUnitLabels = false;

		/**
		 * This stores the approximate number of pixels that will be between the X and Y grids.
		 */
		public int iGridSpacing;

		public AxisValues(double fMinimum, double fMaximum, int iGridSpacing) {
			this.fMinimum = fMinimum;
			this.fMaximum = fMaximum;
			this.iGridSpacing = iGridSpacing;
		}

	}

	private final AxisValues x = new AxisValues(0, 10000000, 60);
	private final AxisValues y = new AxisValues(-20, 20, 40);

	/**
	 * This Flag determines whether the Grid must be shown or not.
	 */
	private boolean bGridVisible = true;

	/**
	 * Stores the different UnitLabels. http://physics.nist.gov/cuu/Units/prefixes.html
	 * http://en.wikipedia.org/wiki/SI_prefix
	 */
	private static final String[] sUnitLabels = new String[] { "", "k", "M", "G", "T", "P", "E", "Z",
			"Y" };
	private static final String sUnknownUnitLabel = "?";

	/**
	 * This is the color used for the grid.
	 */
	private Color oGridColor = new Color(80, 80, 80);

	/**
	 * This is the CheckBox that gets added to the PopUpMenu.
	 */
	private final JCheckBoxMenuItem oShowGridItem;

	/**
	 * Creates a new DrawSurface with only a y axis.
	 */
	public GridDrawSurface() {
		// Add the Show grid item to the PopUpMenu.
		oShowGridItem = new JCheckBoxMenuItem(textRes.getString("GridDrawSurface.Show_grid"), true);
		oPopupMenu.add(oShowGridItem);
		oShowGridItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setGridVisible(oShowGridItem.isSelected());
			}
		});

		setFocusable(true);
		_setGridMinMax(x.fMinimum, x.fMaximum, y.fMinimum, y.fMaximum);
	}

	/**
	 * This will paint the grid.
	 */
	@Override
	protected void paint(Graphics g, Object iDrawSurfaceID) {
		if (GRID_DRAWSURFACE != iDrawSurfaceID) {
			return;
		}
		if (!bGridVisible) {
			return;
		}

		g.setColor(oGridColor);

		// Draw the Horizontal Grid
		if (y.aiGridSteps == null) {
			calculateHorizontalGridAndAxis();
		}
		for (int element : y.aiGridSteps) {
			if (element != 0 && element != getHeight()) {
				g.drawLine(0, element, getWidth(), element);
			}
		}

		// Draw the Vertical Grid
		if (x.aiGridSteps == null) {
			calculateVerticalGridAndAxis();
		}
		for (int element : x.aiGridSteps) {
			if (element != 0 && element != getWidth()) {
				g.drawLine(element, 0, element, getHeight());
			}
		}
	}

	/**
	 * This method will calculate the Horizontal Grid lines and the Axis labels.
	 */
	private void calculateHorizontalGridAndAxis() {
		// If no spacing, or the width is too small to be drawn sensibly, don't draw at all.
		if (!areInputValuesValid(y.iGridSpacing, y.fMinimum, y.fMaximum)) {
			return;
		}

		double[] afGridValues = calculateGridAndAxis(getHeight(), y, false);

		gridListeners.fire().gridChanged(1, y.fMinimum, y.fMaximum, y.lScalingFactor,
				false/* proportional */, afGridValues, y.aiGridSteps);
	}

	/**
	 * This method will recalculate the Vertical Grid lines and the Axis labels.
	 */
	private void calculateVerticalGridAndAxis() {
		if (!areInputValuesValid(x.iGridSpacing, x.fMinimum, x.fMaximum)) {
			return;
		}

		double[] adGridValues = calculateGridAndAxis(getWidth(), x, true);

		gridListeners.fire().gridChanged(0, x.fMinimum, x.fMaximum, x.lScalingFactor,
				true/* proportional */, adGridValues, x.aiGridSteps);
	}

	/**
	 * @param bProportional If the unit axis is inversely proportional to the pixel axis then this must be false
	 *          (Inversely proportional: units get smaller as pixels get bigger)
	 */
	private static double[] calculateGridAndAxis(int iAxisLength_px, AxisValues axis,
			boolean bProportional) {
		// The step size between the vertical gridlines on the x axis
		final double dStepSize_Hz = calculateStepSize(iAxisLength_px / axis.iGridSpacing,
				axis.fMinimum, axis.fMaximum);
		// The first unit value on an axis
		final double fFirstGridValue = Math.floor(axis.fMinimum / dStepSize_Hz) * dStepSize_Hz;
		// The number of steps along the x axis
		final int iNumSteps = (int) ((axis.fMaximum - fFirstGridValue) / dStepSize_Hz) + 1;

		final double[] adGridValues;
		if (iNumSteps > 3) {
			axis.aiGridSteps = new int[iNumSteps];
			adGridValues = new double[iNumSteps];
			for (int i = 0; i < iNumSteps; i++) {
				adGridValues[i] = fFirstGridValue + (i * dStepSize_Hz);
				axis.aiGridSteps[i] = PixelUnitConverter.unitToPixel(bProportional, adGridValues[i], 0,
						iAxisLength_px, axis.fMinimum, axis.fMaximum);
			}
		}
		else if (iNumSteps == 3) {
			/*
			 * Put a label at the beginning, at the end, and in the middle. Create a single grid line in the middle.
			 */
			axis.aiGridSteps = new int[1];
			adGridValues = new double[1];
			adGridValues[0] = (axis.fMinimum + axis.fMaximum) / 2;
			axis.aiGridSteps[0] = PixelUnitConverter.unitToPixel(bProportional, adGridValues[0], 0,
					iAxisLength_px, axis.fMinimum, axis.fMaximum);
		}
		else {
			/*
			 * Put a label at the beginning and at the end. Create no grid lines.
			 */
			axis.aiGridSteps = new int[0];
			adGridValues = new double[0];
		}
		return adGridValues;
	}

	/**
	 * If no spacing, or the width is too small to be drawn sensibly, don't draw at all.
	 */
	private static boolean areInputValuesValid(int iGridSpacing, double fMinimum, double fMaximum) {
		if (iGridSpacing == 0 || Math.abs(fMinimum - fMaximum) < 1e-300) {
			return false;
		}
		return true;
	}

// /**
// * format a value the same way an X-axis label is formatted
// */
// public String formatXAxisLabel(double fVal)
// {
// return getXAxisScale().formatValueAsLabel(fVal / lXScalingFactor);
// }

	/**
	 * Calculate the no of spaces along the axis
	 * 
	 * @param approxSpaces - The number of spaces that would look nice for a particular size of drawSurface
	 * @param max - The maximum value on the axis
	 * @param min - The minimum value on the axis
	 * @return - The step size across the axis in the same unit as arguments min & max
	 */
	private static double calculateStepSize(int approxSpaces, double min, double max) {
		// Some input values make the calculation unpractical (like zero width.)
		// So check for zero or negative divisor, and possibly log 0.

		if (approxSpaces <= 0) {
			approxSpaces = 1;
		}

		double difference = (max - min);
		if (difference <= 0) {
			difference = 1;
		}

		double ratio = difference / approxSpaces;
		if (Double.isInfinite(ratio) || Double.isNaN(ratio)) {
			ratio = 1;
		}

		double m = Math.log10(ratio);
		double floorM = Math.floor(m);
		double remainder = m - floorM;
		int f = 0;

		if (remainder <= 0.15) {
			f = 1;
		}
		else if (remainder <= 0.5) {
			f = 2;
		}
		else if (remainder <= 0.85) {
			f = 5;
		}
		else {
			f = 10;
		}

		double result = f * Math.pow(10.0, floorM);

		// In case that all checks failed, return 1 (step) as result.
		if (result <= 0) {
			result = 1;
		}
		return result;
	}

	/**
	 * This methods sets the maximum and minimum on the Axis.
	 */
	public void setGridMinMax(double fMinimumX, double fMaximumX, double fMinimumY, double fMaximumY) {
		_setGridMinMax(fMinimumX, fMaximumX, fMinimumY, fMaximumY);
		repaint();
	}

	/**
	 * private copy that can't be overridden so I can safely call it from the constructor
	 */
	private void _setGridMinMax(double _fMinimumX, double _fMaximumX, double _fMinimumY,
			double _fMaximumY) {
		if (_fMinimumX > _fMaximumX)
			throw new IllegalStateException("minX>maxX " + _fMinimumX + " " + _fMaximumX);
		if (_fMinimumY > _fMaximumY)
			throw new IllegalStateException("minY>maxY " + _fMinimumY + " " + _fMaximumY);
		this.x.fMinimum = _fMinimumX;
		this.x.fMaximum = _fMaximumX;
		this.y.fMinimum = _fMinimumY;
		this.y.fMaximum = _fMaximumY;

		calculateAxisScalingFactor(x);
		calculateVerticalGridAndAxis();
		calculateAxisScalingFactor(y);
		calculateHorizontalGridAndAxis();
	}

	/**
	 * This methods sets the maximum and minimum on the X Axis.
	 */
	public void setGridXMinMax(double _fMinimumX, double _fMaximumX) {
		if (_fMinimumX > _fMaximumX)
			throw new IllegalStateException("minX>maxX " + _fMinimumX + " " + _fMaximumX);
		this.x.fMinimum = _fMinimumX;
		this.x.fMaximum = _fMaximumX;

		calculateAxisScalingFactor(x);
		calculateVerticalGridAndAxis();
		repaint();
	}

	/**
	 * This methods sets the maximum and minimum on the Y Axis.
	 */
	public void setGridYMinMax(double _fMinimumY, double _fMaximumY) {
		if (_fMinimumY > _fMaximumY)
			throw new IllegalStateException("minY>maxY " + _fMinimumY + " " + _fMaximumY);
		this.y.fMinimum = _fMinimumY;
		this.y.fMaximum = _fMaximumY;

		calculateAxisScalingFactor(y);
		calculateHorizontalGridAndAxis();
		repaint();
	}

	/**
	 * Get the max X value.
	 */
	public double getMaximumX() {
		return x.fMaximum;
	}

	/**
	 * Get the min X value.
	 */
	public double getMinimumX() {
		return x.fMinimum;
	}

	/**
	 * Get the max Y value.
	 */
	public double getMaximumY() {
		return y.fMaximum;
	}

	/**
	 * Get the min Y value.
	 */
	public double getMinimumY() {
		return y.fMinimum;
	}

	public void setGridVisible(boolean bGridVisible) {
		this.bGridVisible = bGridVisible;
		if (oShowGridItem.isSelected() != bGridVisible) {
			oShowGridItem.setSelected(bGridVisible);
		}

		repaint();
	}

	public boolean isGridVisible() {
		return bGridVisible;
	}

	/**
	 * Compute the corresponding SI prefix for values
	 */
	private static String computeUnitPrefix(boolean bDecimalUnitLabels, double fUnitDivisor) {
		if (bDecimalUnitLabels) {
			return computeDecimalUnitPrefix(fUnitDivisor);
		}

		final double digitCounts = Math.log10(fUnitDivisor);
		int iLabelIndex = (int) (Math.round(digitCounts / 3));

		if (iLabelIndex < 0) {
			iLabelIndex = 0;
		}

		if (iLabelIndex < sUnitLabels.length) {
			return sUnitLabels[iLabelIndex];
		}
		else {
			return sUnknownUnitLabel;
		}
	}

	/**
	 * This returns the unit to which a value will be converted, used for displaying purposes.
	 * 
	 * @param dValue contains the the value.
	 * @returns the unit to which it is converted.
	 */
	private static String computeDecimalUnitPrefix(double dValue) {
		if (dValue > 999999) {
			return "1 000 000's";
		}
		else if (dValue > 999 && dValue <= 999999) {
			return "1000's";
		}
		else if (dValue <= 999 && dValue >= -999) {
			return "";
		}
		else if (dValue < -999 && dValue >= -999999) {
			return "1000'th";
		}
		if (dValue < -999999) {
			return "1 000 000'th";
		}
		else {
			return "";
		}
	}

	/**
	 * This method will calculate the Scaling Factor for an axis.
	 */
	private void calculateAxisScalingFactor(AxisValues axis) {
		final double fUnitDivisor = Math.pow(10, (Math.floor(Math
				.floor((Math.log10(axis.fMaximum)) / 3)) * 3));
		axis.sScaleUnit = computeUnitPrefix(axis.bDecimalUnitLabels, fUnitDivisor);
		axis.lScalingFactor = (long) fUnitDivisor;
		if (axis.lScalingFactor < 1) {
			axis.lScalingFactor = 1;
		}

		fireScalingFactorListeners();
	}

// /**
// * This will return the X Axis.
// */
// public AbstractAxisScale getXAxisScale()
// {
// return oXAxis;
// }
//  
// public void setXAxisScale(AbstractAxisScale scale)
// {
// this.oXAxis = scale;
// calculateHorizontalGridAndAxis();
// }

// /**
// * This will return the Y Axis.
// */
// public AbstractAxisScale getYAxisScale()
// {
// return oYAxis;
// }
//  
// public void setYAxisScale(AbstractAxisScale scale)
// {
// this.oYAxis = scale;
// calculateVerticalGridAndAxis();
// }

	/**
	 * Event for componentResized.
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		super.componentResized(e);

		calculateHorizontalGridAndAxis();
		calculateVerticalGridAndAxis();
	}

	/**
	 * @return the X scale unit e.g "k" or "G" or "1 000 000's"
	 */
	public String getXScaleUnit() {
		return x.sScaleUnit;
	}

	/**
	 * Getter for property lXScalingFactor.
	 */
	public long getXScalingFactor() {
		return x.lScalingFactor;
	}

	/**
	 * @return the Y scale unit e.g "k" or "G" or "1 000 000's"
	 */
	public String getYScaleUnit() {
		return y.sScaleUnit;
	}

	/**
	 * Getter for property lYScalingFactor.
	 */
	public long getYScalingFactor() {
		return y.lScalingFactor;
	}

	/**
	 * the space between the vertical lines of the grid
	 */
	public int getXGridSpacing() {
		return x.iGridSpacing;
	}

	/**
	 * the space between the vertical lines of the grid
	 */
	public void setXGridSpacing(int x) {
		this.x.iGridSpacing = x;
		calculateHorizontalGridAndAxis();
		calculateVerticalGridAndAxis();
		repaint();
	}

	/**
	 * the space between the horizontal lines of the grid
	 */
	public int getYGridSpacing() {
		return y.iGridSpacing;
	}

	/**
	 * the space between the horizontal lines of the grid
	 */
	public void setYGridSpacing(int y) {
		this.y.iGridSpacing = y;
		calculateHorizontalGridAndAxis();
		calculateVerticalGridAndAxis();
		repaint();
	}

	/**
	 * This sets the iXGridSpacing and the iYGridSpacing.
	 * 
	 * @param iXGridSpacing the space between the vertical lines of the grid
	 * @param iYGridSpacing the space between the horizontal lines of the grid
	 */
	public void setGridSpacing(int iXGridSpacing, int iYGridSpacing) {
		this.x.iGridSpacing = iXGridSpacing;
		this.y.iGridSpacing = iYGridSpacing;

		calculateHorizontalGridAndAxis();
		calculateVerticalGridAndAxis();
		repaint();
	}

	/**
	 * Should the display show K(engineering) or 1000's(decimal).
	 */
	public void setXDecimalUnitLabels(boolean bXDecimalUnitLabels) {
		this.x.bDecimalUnitLabels = bXDecimalUnitLabels;
		calculateAxisScalingFactor(x);
	}

	public boolean isXDecimalUnitLabels() {
		return x.bDecimalUnitLabels;
	}

	/**
	 * Should the display show K(engineering) or 1000's(decimal).
	 */
	public void setYDecimalUnitLabels(boolean bYDecimalUnitLabels) {
		this.y.bDecimalUnitLabels = bYDecimalUnitLabels;
		calculateAxisScalingFactor(y);
	}

	public boolean isYDecimalUnitLabels() {
		return y.bDecimalUnitLabels;
	}

	/**
	 * This will set the color of the grid.
	 */
	public void setGridColor(Color oGridColor) {
		this.oGridColor = oGridColor;
		repaint();
	}

	/**
	 * This returns the color of the grid.
	 */
	public Color getGridColor() {
		return oGridColor;
	}

	/**
	 * Utility function for converting X from pixel value to unit value
	 */
	public final double pixelToUnitX(int pixelValue) {
		return PixelUnitConverter.pixelToUnit(true, pixelValue, 0, getWidth(), getMinimumX(),
				getMaximumX());
	}

	/**
	 * Utility function for converting X from pixel value to unit value
	 */
	public final double pixelToUnitY(int pixelValue) {
		return PixelUnitConverter.pixelToUnit(false, pixelValue, 0, getHeight(), getMinimumY(),
				getMaximumY());
	}

	@Override
	protected void localeChanged() {
		super.localeChanged();
		oShowGridItem.setText(textRes.getString("GridDrawSurface.Show_grid"));
	}

	/**
	 * Event for mouseDragged.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);

		final double fMouseXValue = pixelToUnitX(e.getX());
		final double fMouseYValue = pixelToUnitY(e.getY());
		gridListeners.fire().mouseCoordinatesChanged(this, fMouseXValue, fMouseYValue);
	}

	/**
	 * Event for mouseMoved.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);

		final double fMouseXValue = pixelToUnitX(e.getX());
		final double fMouseYValue = pixelToUnitY(e.getY());
		gridListeners.fire().mouseCoordinatesChanged(this, fMouseXValue, fMouseYValue);
	}

	/**
	 * Adds a GraphBase Listener.
	 */
	public final void addGridListener(IGridListener oGraphBaseListener) {
		gridListeners.add(oGraphBaseListener);
	}

	/**
	 * Removes a GraphBase Listener.
	 */
	public final void removeGridListener(IGridListener oGraphBaseListener) {
		gridListeners.remove(oGraphBaseListener);
	}

	/**
	 * This is called every time the ScalingFactor of the graph has changed.
	 */
	private void fireScalingFactorListeners() {
		gridListeners.fire().scalingFactorChanged(this, x.lScalingFactor, x.sScaleUnit,
				y.lScalingFactor, y.sScaleUnit);
	}

	protected final PixelUnitConverter.UnitToPixel defaultXUnitToPixel() {
		return new PixelUnitConverter.UnitToPixel(true, 0, getWidth(), getMinimumX(), getMaximumX());
	}

	protected final PixelUnitConverter.UnitToPixel defaultYUnitToPixel() {
		return new PixelUnitConverter.UnitToPixel(true, 0, getHeight(), getMinimumY(), getMaximumY());
	}
}