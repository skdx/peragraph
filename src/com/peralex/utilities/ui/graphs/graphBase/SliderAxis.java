package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.peralex.utilities.ui.graphs.axisscale.NumberAxisScale;

/**
 * An axis component that draws a gradient-shaded background to indicate the "intensity" of the value
 * at that point.
 * 
 * This is a slider component, designed to fit in with the graph stuff. Unlike a normal slider, there is no
 * margin at either end of the slider, and instead the slider runs right up to the edge of its display area.
 * 
 * This component supports multiple sliders on the same axis, although they have to share the same min/max values.
 * 
 * @author Noel Grandin
 */
public class SliderAxis extends JComponent
{
	/**
	 * used for slider components and labels.
	 */
	public static final Color DEFAULT_SLIDER_COLOR = Color.BLACK;
	public static final Color DEFAULT_LABEL_COLOR = Color.BLACK;
	
	/**
	 * Some suggested colours for sliders
	 */
	public static final Color SLIDER_COLOUR_1 = new Color(180, 0, 0);
	public static final Color SLIDER_COLOUR_2 = new Color(180, 0, 180);
	
	private static final int SLIDER_TARGET_HEIGHT = 10;

	private static final class Slider {
		
		/** the String ID used to index the slider into the map */
		public String name;
		
		/** the text displayed next to the slider when the slider is dragged or moused-over*/
		public String sTitle;

		/** the value of the slider */
		public float fValue;
		
		/** position of the centre of the slider */
		public int iPositionY = 0;
		
		public Color oColor = DEFAULT_SLIDER_COLOR;
	}
	
	////////////////////////////////////////////////////////////
	// Axis action listener list
	////////////////////////////////////////////////////////////
	private final List<ISliderAxisListener> oSliderAxisListeners = new ArrayList<ISliderAxisListener>();
	
	private float fMinimum = 0;
	private float fMaximum = 0;
	private float fIncrement = 0;
	
	private boolean bLabelsVisible = false;

	/** I embed this component instead of extending it because I don't want to expose all of it's API */
	private final MyAxis axis = new MyAxis();
	
	private final Map<String, Slider> oSliderMap = new HashMap<String, Slider>();
	
	/** the selected, or currently "moused over" slider */
	private Slider oSelectedSlider = null;
	
	private static final String sBackgroundLabel = "Test";
	
	/** 
	 * Creates a new instance of cSliderAxis.
	 */
	public SliderAxis()
	{
		this.setLayout(new BorderLayout());
		this.setOpaque(false);

		axis.setLayout(null);
		axis.setOpaque(false);
		// the number format is used to size the width, so make it shorter to reduce the width
		axis.setFormat(new DecimalFormat("0"));
		
		this.add(axis, BorderLayout.CENTER);
		
		axis.addComponentListener(new ComponentAdapter()
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
		
		// set up some defaults.
		setBackground(Color.WHITE);
		setForeground(DEFAULT_LABEL_COLOR);
		axis.setForeground(DEFAULT_LABEL_COLOR);
		setBorder(new LineBorder(Color.BLACK));
		axis.setFont(axis.getFont().deriveFont(Font.BOLD));
	}

	@Override
	public void setBackground(Color bg)
	{
		/* Override this and pass the setting on to the axis
		 */
		super.setBackground(bg);
		// this is called during construction, so we have to check for null
		if (axis!=null) axis.setBackground(bg);
	}
	
	@Override
	public void setForeground(Color fg)
	{
		/* Override this and pass the setting on to the axis
		 */
		super.setForeground(fg);
		// this is called during construction, so we have to check for null
		if (axis!=null) axis.setForeground(fg);
	}
	
	/**
	 * Set minimum and maximum axis limits.
	 */
	public void setMinMax(float fMinimum, float fMaximum)
	{
		if (this.fMinimum==fMinimum 
				&& this.fMaximum==fMaximum)
		{
			return;
		}
		this.fMinimum = fMinimum;
		this.fMaximum = fMaximum;
		calculateLabels();
		repaint();
		
		// clamp the slider values
		for (Slider slider : oSliderMap.values())
		{
			if (fMinimum>slider.fValue)
			{
				setSliderValue(slider, fMinimum);
			}
			if (fMaximum<slider.fValue)
			{
				setSliderValue(slider, fMaximum);
			}
		}
	}

	/**
	 * Set maximum axis limit.
	 */
	public void setMinimum(float fMin)
	{
		setMinMax(fMin, this.fMaximum);
	}

	/**
	 * Set maximum axis limit.
	 */
	public void setMaximum(float fMax)
	{
		setMinMax(this.fMinimum, fMax);
	}
	
	/**
	 * Get the minimum value of the slider axis.
	 */
	public float getMinimum()
	{
		return this.fMinimum;
	}
	
	/**
	 * Get the maximum value of the slider axis.
	 */
	public float getMaximum()
	{
		return this.fMaximum;
	}

	/**
	 * Get the increment value. Zero indicates that it is continuously variable.
	 * Any other value indicates that slider values will get snapped to the nearest increment.
	 */
	public float getIncrement()
	{
		return this.fIncrement;
	}
	
	/**
	 * Set the increment value. Zero indicates that it is continuously variable.
	 * Any other value indicates that slider values will get snapped to the nearest increment.
	 */
	public void setIncrement(float increment)
	{
		this.fIncrement = increment;
	}
	
	/**
	 * Set the floating point value of a slider.
	 * 
	 * @param sSliderName the string ID that used to identifier the slider
	 * @param fSliderValue the floating point value of the slider
	 */
	public void setSliderValue(String sSliderName, float fSliderValue)
	{
		ensureSliderExists(sSliderName);
		setSliderValue(oSliderMap.get(sSliderName), fSliderValue);
	}
	
	/**
	 * Get the floating point value of a slider.
	 * 
	 * @param sSliderName the string ID that used to identifier the slider
	 */
	public float getSliderValue(String sSliderName)
	{
		return oSliderMap.get(sSliderName).fValue;
	}

	private void ensureSliderExists(String sSliderName)
	{
		if (!oSliderMap.containsKey(sSliderName))
		{
			Slider slider = new Slider();
			slider.name = sSliderName;
			oSliderMap.put(sSliderName, slider);
		}
	}
	
	private void setSliderValue(Slider oSlider, float fSliderValue)
	{
		if (fSliderValue<fMinimum) fSliderValue = fMinimum;
		if (fSliderValue>fMaximum) fSliderValue = fMaximum;
		if (fIncrement!=0) {
			fSliderValue = Math.round(fSliderValue/fIncrement) * fIncrement;
		}
		oSlider.fValue = fSliderValue;
		
		final int iComponentHeight = axis.getHeight();
		
		oSlider.iPositionY = PixelUnitConverter.unitToPixel(false, fSliderValue, 0, iComponentHeight-1, fMinimum, fMaximum);
		
		axis.repaint();
		fireSliderValueChanged(oSlider.name, fSliderValue);
	}

	/**
	 * Set the color of a slider.
	 * 
	 * @param sSliderName the string ID that used to identifier the slider
	 * @param oColor the color of the slider
	 */
	public void setSliderColor(String sSliderName, Color oColor)
	{
		ensureSliderExists(sSliderName);
		oSliderMap.get(sSliderName).oColor = oColor;
		axis.repaint();
	}

	/**
	 * Set the title of a slider.
	 * 
	 * @param sSliderName the string ID that used to identifier the slider
	 * @param sTitle the text that is displayed next to the slider when it is dragged
	 */
	public void setSliderTitle(String sSliderName, String sTitle)
	{
		ensureSliderExists(sSliderName);
		oSliderMap.get(sSliderName).sTitle = sTitle;
		axis.repaint();
	}

	/**
	 * Add a slider.
	 * 
	 * @param sSliderName the string ID that used to identifier the slider
	 * @param sTitle the text that is displayed next to the slider when it is dragged
	 * @param oColor the color of the slider
	 * @param fVal the floating point value of the slider
	 */
	public void addSlider(String sSliderName, String sTitle, Color oColor, float fVal)
	{
		ensureSliderExists(sSliderName);
		oSliderMap.get(sSliderName).sTitle = sTitle;
		oSliderMap.get(sSliderName).oColor = oColor;
		oSliderMap.get(sSliderName).fValue = fVal;
		axis.repaint();
	}
	
	/**
	 * set the axis labels visibility, the default is false
	 */
	public void setLabelsVisible(boolean bVisible)
	{
		this.bLabelsVisible = bVisible;
		if (bLabelsVisible)
		{
			// reset the axis number format to default.
			axis.setFormat(null);
		}
		else
		{
			// the number format is used to size the width, so make it shorter to reduce the width
			axis.setFormat(new DecimalFormat("0"));
		}
		calculateLabels();
		repaint();
	}
	
	private void setCursor(int type)
	{
		super.setCursor(Cursor.getPredefinedCursor(type));
	}


	private void intensityAxisComponentResized()
	{
		final int iComponentHeight = axis.getHeight();
		
		for (Slider slider : oSliderMap.values())
		{
			slider.iPositionY = PixelUnitConverter.unitToPixel(false, slider.fValue, 0, iComponentHeight-1, fMinimum, fMaximum);
		}
		
		calculateLabels();
	}

	private void calculateLabels()
	{
		axis.clear();
		if (bLabelsVisible)
		{
			final int iComponentHeight = axis.getHeight();
			final double dYStepSize_Hz = calculateStepSize(iComponentHeight/40, fMinimum, fMaximum);
			final double fFirstGridValue = Math.floor(fMinimum/dYStepSize_Hz) * dYStepSize_Hz;
			final int iNumYSteps = (int)((fMaximum - fFirstGridValue)/dYStepSize_Hz) + 1;

			for (int i=0; i<iNumYSteps+1; i++)
			{
				final double val = fFirstGridValue + (i * dYStepSize_Hz);
				final int iPosition = PixelUnitConverter.unitToPixel(false, val, 0, getHeight()-1, fMinimum, fMaximum);
				axis.addLabel(iPosition, (float) val);
			}
		}
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
	
	//////////////////////////////////////////////////////////////////////////////
	// Slider Axis Action Listener, notifier methods and Interface
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Add slider axis listener.
	 */
	public void addSliderAxisListener(ISliderAxisListener oIntensityAxisListener)
	{
		oSliderAxisListeners.add(oIntensityAxisListener);
	}
	
	/**
	 * Remove slider axis listener.
	 */
	public void removeSliderAxisListener(ISliderAxisListener oIntensityAxisListener)
	{
		oSliderAxisListeners.remove(oIntensityAxisListener);
	}
	
	private void fireSliderValueChanged(String sSliderName, float fMinThreshold)
	{
		for (ISliderAxisListener listener : oSliderAxisListeners)
		{
			listener.sliderValueChanged(this, sSliderName, fMinThreshold);
		}
	}
	//////////////////////////////////////////////////////////////////////////////

	private final class MyAxis extends NumberAxisScale
	{
		
		/**
		 * paint the sliders
		 */
		@Override
		public void paint(Graphics oGraphics)
		{
			// make a copy
			final Graphics2D g = (Graphics2D)(oGraphics.create());
			// turn anti-alias on
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			final GradientPaint oGradientPaint = new GradientPaint(0, 0, getBackground().darker(),
					getWidth(), 0, getBackground());
			g.setPaint(oGradientPaint);
			g.fillRect(0, 0, getWidth(), getHeight());

			if (sBackgroundLabel!=null) {
				g.rotate(Math.PI/2);
				g.setColor(getForeground());
				Rectangle2D rect = g.getFontMetrics().getStringBounds(sBackgroundLabel, g);
				g.drawString(sBackgroundLabel, 
						(int) (getHeight() - rect.getWidth()) / 2, 
						- (int) (getWidth() - rect.getHeight()) / 2);
				g.rotate(-Math.PI/2);
			}
			
			/* Paint these in reverse order, so that the one on top is the same one that will get
			 * selected by the mouseover code.*/
			final List<Slider> sliderList = new ArrayList<Slider>(oSliderMap.values());
			Collections.reverse(sliderList);
			for (Slider slider : sliderList)
			{
				if (slider!=oSelectedSlider)
				{
					paintSlider(g, slider);
				}
			}
			
			// paint the selected Slider last, so that it paints on top of the other sliders
			if (oSelectedSlider!=null)
			{
				paintSlider(g, oSelectedSlider);
				String s = "";
				if (oSelectedSlider.sTitle!=null)
				{
					s += oSelectedSlider.sTitle + " ";
				}
				s += axis.formatValueAsLabel(oSelectedSlider.fValue);
				final int arrowHeight = g.getFontMetrics().getHeight();
				g.setColor(getForeground());
				g.drawString(s, 1, oSelectedSlider.iPositionY - (arrowHeight/2) - 1);
			}
			
			// paint the tick labels
			super.paint(oGraphics);
		}
		
		// paint a slider
		private void paintSlider(Graphics2D o2DG, Slider slider)
		{
			// scale the arrow to be as wide as the slider, and as high as a line of text
			final int arrowHeight = o2DG.getFontMetrics().getHeight();
			final int arrowWidth = getWidth() - 4;
			
			// create a new graphics so the translate/transform does not mess anything else up
			Graphics2D arrowGraphics = (Graphics2D) o2DG.create();
			// translate the new graphics so that the arrow is in the correct place
			arrowGraphics.translate(4, slider.iPositionY + 4);
			
			final int CORNER_ARC = arrowHeight;
			
			// paint shadow
			arrowGraphics.setColor(arrowGraphics.getBackground().darker());
			arrowGraphics.fillRoundRect(0, -arrowHeight/2, arrowWidth, arrowHeight, CORNER_ARC, CORNER_ARC);

			// create a new graphics so the translate/transform does not mess anything else up
			arrowGraphics = (Graphics2D) o2DG.create();
			// translate the new graphics so that the arrow is in the correct place
			arrowGraphics.translate(1, slider.iPositionY);
			
			// paint the slider background
			arrowGraphics.setPaint(slider.oColor.brighter());
			arrowGraphics.fillRoundRect(0, -arrowHeight/2, arrowWidth, arrowHeight, CORNER_ARC, CORNER_ARC);
			// draw 2 borders
			arrowGraphics.setPaint(slider.oColor);
			arrowGraphics.drawRoundRect(0, -arrowHeight/2, arrowWidth, arrowHeight, CORNER_ARC, CORNER_ARC); 
			arrowGraphics.drawRoundRect(1, -arrowHeight/2+1, arrowWidth-2, arrowHeight-2, CORNER_ARC, CORNER_ARC);
		}
		
	}
	
	private class AxisMouseHandling implements MouseListener, MouseMotionListener
	{
		
		private boolean bMousingOver = false;
		
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
			if (oSelectedSlider!=null)
			{
				// trigger snapping
				setSliderValue(oSelectedSlider, oSelectedSlider.fValue);
				// fire notification
				fireSliderValueChanged(oSelectedSlider.name, oSelectedSlider.fValue);
				oSelectedSlider = null;
			}
		}

		public void mouseMoved(MouseEvent e)
		{
			boolean foundOne = false;
			
			for (Slider slider : oSliderMap.values())
			{
				if (e.getY() < (slider.iPositionY + SLIDER_TARGET_HEIGHT / 2)
						&& e.getY() > (slider.iPositionY - SLIDER_TARGET_HEIGHT / 2))
				{
					if (!foundOne && !bMousingOver)
					{
						bMousingOver = true;
						setCursor(Cursor.N_RESIZE_CURSOR);
						oSelectedSlider = slider;
						repaint();
					}
					foundOne = true;
				}
			}
			
			if (!foundOne && bMousingOver)
			{
				bMousingOver = false;
				setCursor(Cursor.DEFAULT_CURSOR);
				oSelectedSlider = null;
				repaint();
			}
			
		}

		public void mouseDragged(MouseEvent mouseEvent)
		{
			if (oSelectedSlider==null) return;
			
			final int iComponentHeight = axis.getHeight();
			final int iY = mouseEvent.getY();
			
			oSelectedSlider.iPositionY = iY;
			// make sure we don't go out of range
			if (oSelectedSlider.iPositionY+1 > iComponentHeight)
			{
				oSelectedSlider.iPositionY = iComponentHeight-1;
			}
			if (oSelectedSlider.iPositionY-1 <= 0)
			{
				oSelectedSlider.iPositionY = 1;
			}
			
			oSelectedSlider.fValue = (float)PixelUnitConverter.pixelToUnit(false, oSelectedSlider.iPositionY,
					0, iComponentHeight-1, fMinimum, fMaximum);
			
			axis.repaint();
		}
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
		catch (Exception ignoreEx) {}
		
		final SliderAxis oSlider = new SliderAxis();
		oSlider.setMinMax(-150, 10);
		oSlider.setIncrement(10);
		oSlider.addSlider("XXX", "Slider 1 Title", SliderAxis.SLIDER_COLOUR_1, -140);
		oSlider.setSliderValue("YYY", -50);
		oSlider.setSliderColor("YYY", SliderAxis.SLIDER_COLOUR_2);
		oSlider.setSliderTitle("YYY", "Slider 2 Title");
		oSlider.setLabelsVisible(true);
		oSlider.addSliderAxisListener(new ISliderAxisListener()
		{
			public void sliderValueChanged(SliderAxis control, String sSliderName, float fSliderValue)
			{
				System.out.println("fSliderValue: "+fSliderValue);
			}
		});

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		final JCheckBox labelsVisible = new JCheckBox("Labels Visible", true);
		labelsVisible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				oSlider.setLabelsVisible(labelsVisible.isSelected());
			}
		});
		controlPanel.add(labelsVisible);
		
		JFrame oFrame = new JFrame();
		JSplitPane splitPane = new JSplitPane();
		splitPane.setRightComponent(controlPanel);
		splitPane.setLeftComponent(oSlider);
		splitPane.setDividerLocation(100);
		
		oFrame.getContentPane().add(splitPane);
		oFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		oFrame.setBounds(300, 200, 500, 800);
		oFrame.setVisible(true);
	}
}
