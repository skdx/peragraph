package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.peralex.utilities.locale.ILocaleListener;
import com.peralex.utilities.locale.PeralexLibsBundle;
import com.peralex.utilities.ui.graphs.axisscale.NumberAxisScale;
import com.peralex.utilities.ui.images.IconManager;

/**
 * An axis component that draws a gradient-shaded background to indicate the "intensity" of the value
 * at that point.
 * 
 * @author Jaco Jooste
 */
public class IntensityAxis extends JPanel implements ILocaleListener
{
	/**
	 * used for slider components and labels.
	 */
	public static final Color DEFAULT_FOREGROUND_COLOR = new Color(50, 200, 50);
	
	private static final int SLIDER_LINE_HEIGHT = 2;
	
	private static final int SLIDER_TARGET_HEIGHT = 10;
	
	////////////////////////////////////////////////////////////
	// Axis action listener list
	////////////////////////////////////////////////////////////
	private final List<IIntensityAxisListener> oIntensityAxisListeners = new ArrayList<IIntensityAxisListener>();
	
	private float fMinThresholdValue = 0;
	private float fMaxThresholdValue = 0;
	
	/** position of the centre of each slider */
	private int iMinCompPositionY = 0;
	private int iMaxCompPositionY = 0;
	
	private float fMinThresholdLimit = 0;
	private float fMaxThresholdLimit = 0;
	
	private Color oGradientStartColor = Color.WHITE;
	private Color oGradientStopColor = Color.BLACK;
	private boolean bSelfMaintained = false;
	
	private boolean bColorScale = false;
	
	private final JPopupMenu oPopupMenu = new JPopupMenu();
	private final JCheckBoxMenuItem oColorScaleMenuItem;
	private final JMenuItem oResetScaleMenuItem;
	
	private final MyAxis axis = new MyAxis();
	private final JToggleButton oWaterfallOnOffButton = new JToggleButton();
	
	/** controls the drawing a value next to the slider while it is being dragged */
	private static final int SLIDER_LABEL_OFF = 1;
	private static final int SLIDER_LABEL_MIN = 2;
	private static final int SLIDER_LABEL_MAX = 3;
	private int iSliderLabelState = SLIDER_LABEL_OFF;
	
	private Color sliderColor = DEFAULT_FOREGROUND_COLOR;
	
	private boolean bSlidersEnabled = true;
	
	/** 
	 * Creates a new instance of cIntensityAxis.
	 * This will be a Y-Axis.
	 */
	public IntensityAxis()
	{
		// Internationalisation code start. ****************************************
		// Register for locale change events.
		PeralexLibsBundle.addLocaleListener(this);
		// Internationalisation code end. ******************************************
		
		
		oWaterfallOnOffButton.setIcon(IconManager.getSizedFor("/com/peralex/utilities/ui/images/colour_waterfall_off.png", this, 1.2f));
		oWaterfallOnOffButton.setSelectedIcon(IconManager.getSizedFor("/com/peralex/utilities/ui/images/colour_waterfall.png", this, 1.2f));
		oWaterfallOnOffButton.setFocusPainted(false);
		oWaterfallOnOffButton.setMargin(new Insets(0,0,0,0));
		oWaterfallOnOffButton.setToolTipText(PeralexLibsBundle.getString("IntensityAxis.Colour_scale"));
		oWaterfallOnOffButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
        setColorScale(oWaterfallOnOffButton.isSelected());
			}
		});
		
		this.setLayout(new BorderLayout());
		this.setOpaque(false);

		axis.setOpaque(false);
		// the number format is used to size the width, so make it shorter to reduce the width
		axis.setFormat(new DecimalFormat("0"));
		
		this.add(oWaterfallOnOffButton, BorderLayout.NORTH);
		this.add(axis, BorderLayout.CENTER);
		
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				intensityAxisComponentResized();
			}
		});
		
		final AxisMouseHandling mouseHandling = new AxisMouseHandling();
		axis.addMouseListener(mouseHandling);
		axis.addMouseMotionListener(mouseHandling);
		
    // Add the event for displaying the popup.
    axis.addMouseListener(new MouseAdapter()
    {
    	@Override
      public void mouseClicked(MouseEvent e)
      {
        if (e.getButton() == MouseEvent.BUTTON3)
        {
          oPopupMenu.show(axis, e.getX(), e.getY());
        }
      }
    });
    
    // Add the Show grid item to the PopUpMenu.
    oColorScaleMenuItem = new JCheckBoxMenuItem(PeralexLibsBundle.getString("IntensityAxis.Colour_scale"), false);
    oPopupMenu.add(oColorScaleMenuItem);
    oColorScaleMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        setColorScale(oColorScaleMenuItem.isSelected());
      }
    });
    
    oResetScaleMenuItem = new JMenuItem(PeralexLibsBundle.getString("IntensityAxis.Reset_scale"));
    oPopupMenu.add(oResetScaleMenuItem);
    oResetScaleMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
      	resetScale();
      }
    });
    
		// set up some defaults.
		axis.setForeground(DEFAULT_FOREGROUND_COLOR);
		setMonochromeGradientColors(new Color(240, 240, 240), new Color(25, 25, 25));
		setBorder(new LineBorder(Color.BLACK));
		axis.setFont(axis.getFont().deriveFont(Font.BOLD));
	}

	/** when doing auto-scaling, this resets the thresholds to the default limits */
	public void resetScale()
	{
 		setThresholdValues(fMinThresholdLimit, fMaxThresholdLimit);
	}
	
	public void setThresholdLimits(float fMinThresholdLimit, float fMaxThresholdLimit)
	{
		if (this.fMinThresholdLimit==fMinThresholdLimit 
				&& this.fMaxThresholdLimit==fMaxThresholdLimit)
		{
			return;
		}
		this.fMinThresholdLimit = fMinThresholdLimit;
		this.fMaxThresholdLimit = fMaxThresholdLimit;
		calculateLabels();
		repaint();
		
		if (fMinThresholdLimit>fMinThresholdValue || fMaxThresholdLimit<fMaxThresholdValue)
		{
			setThresholdValues(
					Math.max(fMinThresholdLimit, fMinThresholdValue), 
					Math.min(fMaxThresholdLimit, fMaxThresholdValue));
		}
	}
	
	public float getThresholdLimitMin()
	{
		return this.fMinThresholdLimit;
	}
	
	public float getThresholdLimitMax()
	{
		return this.fMaxThresholdLimit;
	}
	
	public float getThresholdMin()
	{
		return this.fMinThresholdValue;
	}
	
	public float getThresholdMax()
	{
		return this.fMaxThresholdValue;
	}
	
	public void setThresholdValues(float fMinThresholdValue, float fMaxThresholdValue)
	{
		if (this.fMinThresholdValue==fMinThresholdValue 
				&& this.fMaxThresholdValue==fMaxThresholdValue)
		{
			return;
		}
		if (fMinThresholdValue<fMinThresholdLimit) fMinThresholdValue = fMinThresholdLimit;
		this.fMinThresholdValue = fMinThresholdValue;
		if (fMaxThresholdValue>fMaxThresholdLimit) fMaxThresholdValue = fMaxThresholdLimit;
		this.fMaxThresholdValue = fMaxThresholdValue;
		
		final int iComponentHeight = axis.getHeight();
		
		iMinCompPositionY = PixelUnitConverter.unitToPixel(false, fMinThresholdValue, 0, iComponentHeight-1, fMinThresholdLimit, fMaxThresholdLimit);
		iMaxCompPositionY = PixelUnitConverter.unitToPixel(false, fMaxThresholdValue, 0, iComponentHeight-1, fMinThresholdLimit, fMaxThresholdLimit);
		
		axis.repaint();
		notifyIntensityThresholdChangedListener(fMinThresholdValue, fMaxThresholdValue);
	}

	/**
	 * Sets the colors that get used for the gradient when in monochrome (i.e. not color)
	 * mode.
	 */
	public void setMonochromeGradientColors(Color oStartColor, Color oStopColor)
	{
		oGradientStartColor = oStartColor;
		oGradientStopColor = oStopColor;
		axis.repaint();
	}
	
	public void setSliderColor(Color oSliderColor)
	{
		this.sliderColor = oSliderColor;
		axis.repaint();
	}
	
	/**
	 * if true, use a rainbow color scale, else use a greyscale.
	 */
	public void setColorScale(boolean bColorScale)
	{
		oColorScaleMenuItem.setSelected(bColorScale);
		oWaterfallOnOffButton.setSelected(bColorScale);
		this.bColorScale = bColorScale;
		axis.repaint();
	}
	
	/**
	 * if true, use a rainbow colour scale, else use a greyscale.
	 */
	public boolean isColorScale()
	{
		return bColorScale;
	}
	
	/**
	 * Convert amplitude data into an array of RGB data.
	 * Mostly used to feed data into images.
	 */
	public void convertAmplitudeToLine(int [] aiImageData, float[] afAmplitudeFrame_dBm)
	{
		if (!bColorScale)
		{
			final PixelUnitConverter.UnitToPixel2 converter = new PixelUnitConverter.UnitToPixel2(true, 0, 255, fMinThresholdValue, fMaxThresholdValue);
		
			for (int i = 0; i < afAmplitudeFrame_dBm.length; i++)
			{
				int colour = converter.compute(afAmplitudeFrame_dBm[i]);
				colour = (colour < 0 ? 0 : (colour > 255 ? 255 : colour));
				aiImageData[i] = new Color(colour, colour, colour).getRGB();
			}
		}
		else
		{
			final PixelUnitConverter.UnitToPixel2 converter = new PixelUnitConverter.UnitToPixel2(true, 0, 0xffffff,
					fMinThresholdValue, fMaxThresholdValue);
			for (int i = 0; i < afAmplitudeFrame_dBm.length; i++)
			{
				int colour = converter.compute(afAmplitudeFrame_dBm[i]);
				float fColour = colour / ((float) 0xffffff);

				aiImageData[i] = floatToRainbow(fColour).getRGB();
		  }
		}
	}

	/**
   * @param awAmplitude_cdBm amplitudes in hundredth's of a dBm
	 */
	public void convertAmplitudeToLine(int[] aiImageData, short[] awAmplitude_cdBm)
	{
		if (!bColorScale)
		{
			final PixelUnitConverter.UnitToPixel2 converter = new PixelUnitConverter.UnitToPixel2(true, 0, 255, fMinThresholdValue, fMaxThresholdValue);
		
			for (int i = 0; i < awAmplitude_cdBm.length; i++)
			{
				int colour = converter.compute(awAmplitude_cdBm[i]/100f);
				colour = (colour < 0 ? 0 : (colour > 255 ? 255 : colour));
				aiImageData[i] = new Color(colour, colour, colour).getRGB();
			}
		}
		else
		{
			final PixelUnitConverter.UnitToPixel2 converter = new PixelUnitConverter.UnitToPixel2(true, 0, 0xffffff,
					fMinThresholdValue, fMaxThresholdValue);
			for (int i = 0; i < awAmplitude_cdBm.length; i++)
			{
				int colour = converter.compute(awAmplitude_cdBm[i]/100f);
				float fColour = colour / ((float) 0xffffff);

				aiImageData[i] = floatToRainbow(fColour).getRGB();
		  }
		}
	}
	
	/**
	 * Convert a floating point value in the intensity range to a greyscale RGB value.
	 */
	public int convertToGreyscale(float fVal)
	{
		int iGreyScale = PixelUnitConverter.unitToPixel(true, fVal, 0, 255, fMinThresholdValue, fMaxThresholdValue);
		iGreyScale = (iGreyScale < 0 ? 0 : (iGreyScale > 255 ? 255 : iGreyScale));
		return iGreyScale;
	}

	/**
	 * Convert a floating point value in the intensity range to an RGB value.
	 */
	public int convertToPixel(float fVal)
	{
		if (bColorScale)
		{
			final PixelUnitConverter.UnitToPixel2 converter = new PixelUnitConverter.UnitToPixel2(true, 0, 0xffffff,
					fMinThresholdValue, fMaxThresholdValue);
			int colour = converter.compute(fVal);
			float fColour = colour / ((float) 0xffffff);
	
			return floatToRainbow(fColour).getRGB();
		}
		else
		{
			final int iGreyScale = convertToGreyscale(fVal);
			return new Color(iGreyScale, iGreyScale, iGreyScale).getRGB();
		}
	}

	private static final Color [] RAINBOW = new Color []
	  {
			new Color(0, 0, 0),
			new Color(48, 48, 96),
			new Color(0, 64, 248),
			new Color(0, 136, 248),
			new Color(0, 216, 248),
			new Color(0, 248, 208),
			new Color(0, 248, 112),
			new Color(136, 248, 24),
			new Color(200, 248, 8),
			new Color(240, 248, 8),
			new Color(248, 224, 8),
			new Color(248, 184, 8),
			new Color(248, 152, 8),
			new Color(248, 96, 8),
			new Color(248, 0, 0),
	  };
	
	/** 
	 * Convert a float in the range (0.0,1.0) to a rainbow colour
	 */
	private static Color floatToRainbow(float fColour)
	{
		if (fColour<0) return RAINBOW[0];
		if (fColour>=1) return RAINBOW[RAINBOW.length-1];

		return RAINBOW[Math.round(fColour*(RAINBOW.length-1))];
	}
	
	/**
	 * If this Axis is self maintained it will calculate and draw it's own labels.
	 * The default is false, which means that no labels are drawn.
	 */
	public void setSelfMaintained(boolean bSelfMaintained)
	{
		this.bSelfMaintained = bSelfMaintained;
		if (bSelfMaintained)
		{
			// reset the axis number format to default.
			axis.setFormat(null);
		}
		else
		{
			// the number format is used to size the width, so make it shorter to reduce the width
			axis.setFormat(new DecimalFormat("0"));
		}
		repaint();
	}
	
	private void intensityAxisComponentResized()
	{
		final int iComponentHeight = axis.getHeight();
		
		iMinCompPositionY = PixelUnitConverter.unitToPixel(false, fMinThresholdValue, 0, iComponentHeight-1, fMinThresholdLimit, fMaxThresholdLimit);
		iMaxCompPositionY = PixelUnitConverter.unitToPixel(false, fMaxThresholdValue, 0, iComponentHeight-1, fMinThresholdLimit, fMaxThresholdLimit);
		
		calculateLabels();
	}

	private void calculateLabels()
	{
		if (bSelfMaintained)
		{
			final int iComponentHeight = axis.getHeight();
			axis.clear();
			final double dYStepSize_Hz = calculateStepSize(iComponentHeight/40, fMinThresholdLimit, fMaxThresholdLimit);
			final double fFirstGridValue = Math.floor(fMinThresholdLimit/dYStepSize_Hz) * dYStepSize_Hz;
			final int iNumYSteps = (int)((fMaxThresholdLimit - fFirstGridValue)/dYStepSize_Hz) + 1;

			for (int i=0; i<iNumYSteps+1; i++)
			{
				final double val = fFirstGridValue + (i * dYStepSize_Hz);
				final int iPosition = PixelUnitConverter.unitToPixel(false, val, 0, getHeight()-1, fMinThresholdLimit, fMaxThresholdLimit);
				axis.addLabel(iPosition, (float) val);
			}
		}
	}
	
	public void setSlidersEnabled(boolean active)
	{
		this.bSlidersEnabled = active;
	}
	
	public boolean isSlidersEnabled()
	{
		return this.bSlidersEnabled;
	}

  /** 
   * Calculate the no of spaces along the axis.
   *
   * @param approxSpaces - The number of spaces that would look nice for a particular size of drawSurface
   * @param max - The maximum value on the axis
   * @param min - The minimum value on the axis
   * @return - The step size across the axis in the same unit as arguments min & max 
   */
  private static double calculateStepSize(int approxSpaces, double min, double max)
  {
    double m = Math.log10((max - min)/approxSpaces);
    double floorM = Math.floor(m);
    double remainder = m - floorM;
    int f = 0;
		
    if (remainder <= 0.15)  { f = 1; }
    else if (remainder <= 0.5) { f = 2; }
    else if (remainder <= 0.85) { f = 5; }
    else { f = 10; }
		
    return f * Math.pow(10.0, floorM);
  }
	
	public void componentsLocaleChanged()
	{
		oWaterfallOnOffButton.setToolTipText(PeralexLibsBundle.getString("Colour_scale"));
		oColorScaleMenuItem.setText(PeralexLibsBundle.getString("Colour_scale"));
    oResetScaleMenuItem.setText(PeralexLibsBundle.getString("Reset_scale"));
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Intensity Axis Action Listener, notifier methods and Interface
	//////////////////////////////////////////////////////////////////////////////
	
	public interface IIntensityAxisListener
	{
		/**
		 * Called every time the Intensity threshold changed.
		 */
		void intensityThresholdChanged(IntensityAxis source, float fMinThreshold, float fMaxThreshold);
	}
	
	public void addIntensityAxisListener(IIntensityAxisListener oIntensityAxisListener)
	{
		oIntensityAxisListeners.add(oIntensityAxisListener);
	}
	
	public void removeIntensityAxisListener(IIntensityAxisListener oIntensityAxisListener)
	{
		oIntensityAxisListeners.remove(oIntensityAxisListener);
	}
	
	private void notifyIntensityThresholdChangedListener(float fMinThreshold, float fMaxThreshold)
	{
		for (IIntensityAxisListener listener : oIntensityAxisListeners)
		{
			listener.intensityThresholdChanged(this, fMinThreshold, fMaxThreshold);
		}
	}
	//////////////////////////////////////////////////////////////////////////////

	private final class MyAxis extends NumberAxisScale
	{
		
		/**
		 * Fill the component with the threshold gradient.
		 */
		@Override
		public void paint(Graphics oGraphics)
		{
			final Graphics2D o2DG = (Graphics2D)oGraphics;
			final int iComponentHeight = getHeight();
			final int iComponentWidth = getWidth();
			if (!bColorScale)
			{
				// paint the gradient between the min and max intensity markers
				GradientPaint oGradientPaint = new GradientPaint(0, iMaxCompPositionY, oGradientStartColor,
						0, iMinCompPositionY, oGradientStopColor);
				o2DG.setPaint(oGradientPaint);
				o2DG.fillRect(0, iMaxCompPositionY, iComponentWidth, iMinCompPositionY-iMaxCompPositionY);
				// paint the space above the max intensity marker
				o2DG.setPaint(oGradientStartColor);
				o2DG.fillRect(0, 0, iComponentWidth, iMaxCompPositionY);
				// paint the space below the min intensity marker
				o2DG.setPaint(oGradientStopColor);
				o2DG.fillRect(0, iMinCompPositionY, iComponentWidth, iComponentHeight-iMinCompPositionY);
			}
			else
			{
				// paint the gradient between the min and max intensity markers
				final float iIncrement = (iMinCompPositionY-iMaxCompPositionY)/(float)(RAINBOW.length-1);
				int rainbowIdx = RAINBOW.length-1;
				for (int i = 0; i<(RAINBOW.length-1) ; i++)
				{
					// The calculations here are important to avoid rounding errors which could leave us
					// not painting part of the range.
					final int startRegion = (int) Math.floor(iMaxCompPositionY + (i * iIncrement));
					final int endRegion = (int) Math.ceil(iMaxCompPositionY + ((i+1) * iIncrement));
				  GradientPaint oGradientPaint = new GradientPaint(
				  		0, startRegion, RAINBOW[rainbowIdx],
				  		0, endRegion, RAINBOW[rainbowIdx-1]);
					o2DG.setPaint(oGradientPaint);
					o2DG.fillRect(0, startRegion, iComponentWidth, (endRegion - startRegion));
					rainbowIdx--;
				}	
				
				// paint the space above the max intensity marker
				o2DG.setPaint(RAINBOW[RAINBOW.length-1]);
				o2DG.fillRect(0, 0, iComponentWidth, iMaxCompPositionY);
				
				// paint the space below the min intensity marker
				o2DG.setPaint(RAINBOW[0]);
				o2DG.fillRect(0, iMinCompPositionY, iComponentWidth, iComponentHeight-iMinCompPositionY);
			}

			// paint the sliders
			o2DG.setColor(sliderColor);
			o2DG.fillRect(0, iMinCompPositionY - (SLIDER_LINE_HEIGHT/2), getWidth(), SLIDER_LINE_HEIGHT);
			o2DG.fillRect(0, iMaxCompPositionY - (SLIDER_LINE_HEIGHT/2), getWidth(), SLIDER_LINE_HEIGHT);
			if (iSliderLabelState==SLIDER_LABEL_MIN)
			{
				o2DG.drawString(axis.formatValueAsLabel(fMinThresholdValue), 1, iMinCompPositionY - (SLIDER_LINE_HEIGHT/2) - 1);
			}
			else if (iSliderLabelState==SLIDER_LABEL_MAX)
			{
				o2DG.drawString(axis.formatValueAsLabel(fMaxThresholdValue), 1, iMaxCompPositionY - (SLIDER_LINE_HEIGHT/2) - 1);
			}
			
			super.paint(oGraphics);
		}
		
	}
	
	private class AxisMouseHandling implements MouseListener, MouseMotionListener
	{
		
		private static final int STATE_NONE = 0;
		private static final int STATE_MIN_SLIDER_SELECTED = 1;
		private static final int STATE_MAX_SLIDER_SELECTED = 2;
		private int state = STATE_NONE;
		
		public AxisMouseHandling()
		{
		}
		
		public void mouseClicked(MouseEvent e)	 {}

		public void mousePressed(MouseEvent e)	 {}

		public void mouseEntered(MouseEvent mouseEvent) {}

		public void mouseExited(MouseEvent mouseEvent)
		{
		}

		public void mouseReleased(java.awt.event.MouseEvent mouseEvent)
		{
			iSliderLabelState = SLIDER_LABEL_OFF;
			notifyIntensityThresholdChangedListener(fMinThresholdValue, fMaxThresholdValue);
		}

		public void mouseMoved(MouseEvent e)
		{
			if (!bSlidersEnabled) return;
			
			if (e.getY() < (iMinCompPositionY + SLIDER_TARGET_HEIGHT / 2)
					&& e.getY() > (iMinCompPositionY - SLIDER_TARGET_HEIGHT / 2))
			{
				if (state!=STATE_MIN_SLIDER_SELECTED)
				{
					state = STATE_MIN_SLIDER_SELECTED;
					setCursor(Cursor.N_RESIZE_CURSOR);
					iSliderLabelState = SLIDER_LABEL_MIN;
					repaint();
				}
			}
			else if (e.getY() < (iMaxCompPositionY + SLIDER_TARGET_HEIGHT / 2)
					&& e.getY() > (iMaxCompPositionY - SLIDER_TARGET_HEIGHT / 2))
			{
				if (state!=STATE_MAX_SLIDER_SELECTED)
				{
					state = STATE_MAX_SLIDER_SELECTED;
					setCursor(Cursor.N_RESIZE_CURSOR);
					iSliderLabelState = SLIDER_LABEL_MAX;
					repaint();
				}
			}
			else
			{
				if (state!=STATE_NONE)
				{
					state = STATE_NONE;
					setCursor(Cursor.DEFAULT_CURSOR);
					iSliderLabelState = SLIDER_LABEL_OFF;
					repaint();
				}
			}
		}

		public void mouseDragged(MouseEvent mouseEvent)
		{
			final int iComponentHeight = axis.getHeight();
			final int iY = mouseEvent.getY();
			if (state==STATE_MIN_SLIDER_SELECTED)
			{
				iMinCompPositionY = iY;
				// make sure we don't go out of range
				if (iMinCompPositionY+1 > iComponentHeight)
				{
					iMinCompPositionY = iComponentHeight-1;
				}
				// make sure we don't pull min slider past max slider
				if (iMinCompPositionY-20 <= iMaxCompPositionY)
				{
					iMinCompPositionY = iMaxCompPositionY+20;
				}
				fMinThresholdValue = (float)PixelUnitConverter.pixelToUnit(false, iMinCompPositionY,
						0, iComponentHeight-1, fMinThresholdLimit, fMaxThresholdLimit);
			}
			else if (state==STATE_MAX_SLIDER_SELECTED)
			{
				iMaxCompPositionY = iY;
				// make sure we don't go out of range
				if (iMaxCompPositionY < 0)
				{
					iMaxCompPositionY = 0;
				}
				// make sure we don't pull max slider past min slider
				if (iMinCompPositionY-20 <= iMaxCompPositionY)
				{
					iMaxCompPositionY = iMinCompPositionY-20;
				}
				fMaxThresholdValue = (float)PixelUnitConverter.pixelToUnit(false, iMaxCompPositionY,
						0, iComponentHeight-1, fMinThresholdLimit, fMaxThresholdLimit);
			}
			axis.repaint();
		}
		
		private void setCursor(int type)
		{
			axis.setCursor(Cursor.getPredefinedCursor(type));
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
		catch (Exception ignoreEx) {}
		
		JFrame oFrame = new JFrame();
		IntensityAxis oIAxis = new IntensityAxis();
		oIAxis.setThresholdLimits(-150, 10);
		oIAxis.setThresholdValues(-140, 0);
		oIAxis.setSelfMaintained(true);
		oIAxis.setSliderColor(Color.GREEN);
		oIAxis.setMonochromeGradientColors(new Color(255, 255, 255), new Color(0, 0, 0));
		//oIAxis.setLabelColor(new Color(50, 200, 50));
		//oIAxis.setLabelFont(new Font("Arial", Font.BOLD, 10));
		oIAxis.addIntensityAxisListener(new IIntensityAxisListener()
		{
			public void intensityThresholdChanged(IntensityAxis axis, float fMinThreshold, float fMaxThreshold)
			{
				System.out.println("fMinThreshold: "+fMinThreshold + ", fMaxThreshold: "+fMaxThreshold);
			}
		});
		oFrame.getContentPane().add(oIAxis);
		oFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		oFrame.setBounds(300, 200, 100, 800);
		oFrame.setVisible(true);
	}
}
