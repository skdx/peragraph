package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.peralex.utilities.locale.ILocaleListener;
import com.peralex.utilities.locale.PeralexLibsBundle;
import com.peralex.utilities.ui.ComponentDisabler;
import com.peralex.utilities.ui.graphs.axisscale.AbstractAxisScale;
import com.peralex.utilities.ui.graphs.axisscale.AbstractDefaultAxisScale;
import com.peralex.utilities.ui.graphs.axisscale.NumberAxisScale;

/**
 * This wraps the graph proper and provides titles, x-axis and y-axis labels, and a co-ordinate panel.
 * 
 * This version is meant to be stuck inside a cMultiGraphWrapperContainer class.
 * 
 * This class is meant to have all of the same methods as cGraphWrapper, so that it is basically a drop-in
 * replacement.
 * 
 * @author Andre, Noel Grandin
 */
public class MultiGraphWrapper implements ILocaleListener
{
	/** The resource bundle used for multilingual support */
	private ResourceBundle textRes = PeralexLibsBundle.getResource();

	/**
   * Stores the reference to the Graph that is added.
   */
	private ZoomDrawSurface oGraph;

	private final MultiGraphWrapperContainer parent;
	
	/**
   * These store the Titles and Units of the Axis'.
   */
	private String sXAxisTitle = "", sXAxisUnit = "", sYAxisTitle = "", sYAxisUnit = "";
  
  private JPanel genericCoordinatePanel = null;
  
  private final ComponentDisabler disabler = new ComponentDisabler();
  
  // Sometimes an oCoordinatePanel can be null (ie. when a genericCoordinatePanel is used), 
  // and a method call to it will throw exception. Instead of testing the null value all the time, 
  // it's easier to let this dummy panel handle everything (even when nothing is sent to screen.)
  private final com.peralex.utilities.ui.graphs.graphBase.CoordinatesPanel oDummyCoordinatesPanel = new CoordinatesPanel();
  
	private AbstractAxisScale oXAxisScale = new NumberAxisScale(AbstractDefaultAxisScale.X_AXIS);

	private AbstractAxisScale oYAxisScale = new NumberAxisScale(AbstractDefaultAxisScale.Y_AXIS);
	
	/**
   * Creates new form cMultiGraphWrapper
   */
	public MultiGraphWrapper(MultiGraphWrapperContainer container)
	{
		this(container, 1);
	}
	
	/**
   * Creates new form cMultiGraphWrapper
	 * @param weightY a scaling factor to influence the relative height of this graph
   */
	public MultiGraphWrapper(MultiGraphWrapperContainer container, float weightY)
	{
		this.parent = container;
		
		parent.add(this);
		
		initComponents(weightY);
    
    disabler.add(
               oBottomLeftCornerPanel,
               oGraphContainerPanel,
               oHeaderBottomExtraPanel,
               oHeaderLeftExtraPanel,
               oHeaderRightExtraPanel,
               oTitleLabel,
               oTitlePanel,
               oTopLeftCornerPanel,
               oXAxisContainerPanel,
               oXAxisExtraPanel,
               oXAxisLabel,
               oYAxisContainerPanel,
               oYAxisLabel );

		/*
		 * make the default title font be 20% bigger than the default font, and bold
		 */
		Font titleFont = oTitleLabel.getFont();
		titleFont = titleFont.deriveFont(titleFont.getSize() * 1.2f);
		titleFont = titleFont.deriveFont(Font.BOLD);
		
		setTitleFont(titleFont);
		
		PeralexLibsBundle.addLocaleListener(this); //do after components have been initialised
	}

	/**
   * Creates new form cGraphWrapper
   */
	public MultiGraphWrapper(MultiGraphWrapperContainer container, ZoomDrawSurface oGraph)
	{
		this(container);
		addGraph(oGraph);
	}

	/**
	 * @param weightY a scaling factor to influence the relative height of this graph
	 */
  private void initComponents(float weightY)
  {
  	final int gridYOffset = parent.getMaxGridY() + 1;
  	
    java.awt.GridBagConstraints gridBagConstraints;

    oTitlePanel = new javax.swing.JPanel();
    oTitleLabel = new javax.swing.JLabel();
    oHeaderRightExtraPanel = new javax.swing.JPanel();
    oCoordinatesPanel = new com.peralex.utilities.ui.graphs.graphBase.CoordinatesPanel();
    oHeaderLeftExtraPanel = new javax.swing.JPanel();
    oHeaderBottomExtraPanel = new javax.swing.JPanel();
    oGraphContainerPanel = new javax.swing.JPanel();
    oXAxisContainerPanel = new javax.swing.JPanel();
    oYAxisLabel = new com.peralex.utilities.ui.graphs.graphBase.YAxisLabel();
    oYAxisContainerPanel = new javax.swing.JPanel();
    oXAxisLabel = new javax.swing.JLabel();
    oTopLeftCornerPanel = new javax.swing.JPanel();
    oBottomLeftCornerPanel = new javax.swing.JPanel();
    oXAxisExtraPanel = new javax.swing.JPanel();

    oTitlePanel.setLayout(new java.awt.GridBagLayout());

    oTitleLabel.setText("Graph Title");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
    oTitlePanel.add(oTitleLabel, gridBagConstraints);

    oHeaderRightExtraPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    oTitlePanel.add(oHeaderRightExtraPanel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    oTitlePanel.add(oCoordinatesPanel, gridBagConstraints);

    oHeaderLeftExtraPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    oTitlePanel.add(oHeaderLeftExtraPanel, gridBagConstraints);

    oHeaderBottomExtraPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    oTitlePanel.add(oHeaderBottomExtraPanel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = gridYOffset;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    parent.add(oTitlePanel, gridBagConstraints);

    oGraphContainerPanel.setLayout(new java.awt.BorderLayout());

    oGraphContainerPanel.setBackground(new java.awt.Color(153, 153, 153));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = gridYOffset + 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0 * weightY;
    parent.add(oGraphContainerPanel, gridBagConstraints);

    oXAxisContainerPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = gridYOffset + 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    parent.add(oXAxisContainerPanel, gridBagConstraints);

    oYAxisLabel.setLayout(new java.awt.FlowLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = gridYOffset + 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0 * weightY;
    parent.add(oYAxisLabel, gridBagConstraints);

    oYAxisContainerPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = gridYOffset + 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0 * weightY;
    parent.add(oYAxisContainerPanel, gridBagConstraints);

    oXAxisLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    oXAxisLabel.setText(textRes.getString("X-Axis"));
    oXAxisLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    oXAxisLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = gridYOffset + 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    parent.add(oXAxisLabel, gridBagConstraints);

    oTopLeftCornerPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = gridYOffset;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    parent.add(oTopLeftCornerPanel, gridBagConstraints);

    oBottomLeftCornerPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = gridYOffset + 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    parent.add(oBottomLeftCornerPanel, gridBagConstraints);

    oXAxisExtraPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = gridYOffset + 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    parent.add(oXAxisExtraPanel, gridBagConstraints);
  }

  private class MyGridListener implements IGridListener
	{
		public void mouseCoordinatesChanged(GridDrawSurface surface, final double fXValue,
				final double fYValue)
		{
			getInternalCoordinatesPanel().setCoordinates(fXValue / oGraph.getXScalingFactor(),
					fYValue / oGraph.getYScalingFactor());
		}

		public void scalingFactorChanged(GridDrawSurface surface, long lXScalingFactor,
				String sXScaleUnit, long lYScalingFactor, String sYScaleUnit)
		{
			updateSuffixesInCoordinateLables();
			if (!sXAxisUnit.equals(""))
			{
				String text = String.format("%s (%s%s)", sXAxisTitle, sXScaleUnit, sXAxisUnit);

				oXAxisLabel.setText(text);
			}
			if (!sYAxisUnit.equals(""))
			{
				String text = String.format("%s (%s%s)", sYAxisTitle, sYScaleUnit, sYAxisUnit);

				oYAxisLabel.setText(text);
			}
		}
		public void gridChanged(int axis, double minimum, double maximum, long scalingFactor,
				boolean proportional, double[] afGridValues, int[] aiGridCoordinates) {
		}
	}

	private final MyGridListener gridListener = new MyGridListener();
  
	public com.peralex.utilities.ui.graphs.graphBase.CoordinatesPanel getCoordinatesPanel()
  {
    return this.oCoordinatesPanel;
  }
  
  private com.peralex.utilities.ui.graphs.graphBase.CoordinatesPanel getInternalCoordinatesPanel()
  {
    return oCoordinatesPanel != null ? oCoordinatesPanel : oDummyCoordinatesPanel;
  }
  
  public JPanel getGenericCoordinatePanel()
  {
    return this.genericCoordinatePanel;
  }
  
  public IGridListener getGraphListener()
  {
    return this.gridListener;
  }

  /**
   * This will add the graph to the Wrapper.
   */
	public final void addGraph(final ZoomDrawSurface _oGraph)
	{
		this.oGraph = _oGraph;
		oXAxisScale.linkToX(this.oGraph);
		oYAxisScale.linkToY(this.oGraph);
		
		oGraph.addGridListener(gridListener);
		oGraphContainerPanel.add(oGraph);
		oXAxisContainerPanel.add(oXAxisScale);
		oXAxisScale.setOffsetFirstLabel(true);
		oYAxisContainerPanel.add(oYAxisScale);
		updateSuffixesInCoordinateLables();
	}

	/**
	 * This is a very raw method for adding stuff to a GraphWrapper.
	 * It means that you are using the wrapper purely as a layout class, 
	 * and none of the axis and cursor/coordinate stuff is in operation.
	 */
	public final void addGraph(Component _oGraph, Component oXAxis, Component oYAxis)
	{
	  oGraphContainerPanel.add(_oGraph);
		oXAxisContainerPanel.add(oXAxis);
		oYAxisContainerPanel.add(oYAxis);
	}
	
	public final void removeGraph()
	{
		oGraph.removeGridListener(gridListener);
		oGraphContainerPanel.remove(oGraph);
		oXAxisContainerPanel.remove(oXAxisScale);
		oYAxisContainerPanel.remove(oYAxisScale);
		this.oGraph = null;
	}
	
	public void setXAxisVisible(boolean bVisible)
	{
		oXAxisScale.setVisible(bVisible);
	}
	
	public void setYAxisVisible(boolean bVisible)
	{
		oYAxisScale.setVisible(bVisible);
	}
	
	/**
   * This will set the Axis Labels visibility.
   */
	public void setAxisLabelsVisible(boolean bVisible)
	{
		setXAxisLabelVisible(bVisible);
		setYAxisLabelVisible(bVisible);
	}

	/**
   * This will set the Axis Scales visibility.
   */
	public void setAxisScalesVisible(boolean bVisible)
	{
		setXAxisScaleVisible(bVisible);
		setYAxisScaleVisible(bVisible);
	}

	/**
   * This will set the XAxis Label visibility.
   */
	public void setXAxisLabelVisible(boolean bVisible)
	{
		oXAxisLabel.setVisible(bVisible);
	}

	/**
   * This will set the XAxis Scale visibility.
   */
	public void setXAxisScaleVisible(boolean bVisible)
	{
		oXAxisContainerPanel.setVisible(bVisible);
	}

	/**
   * This will set the YAxis Label visibility.
   */
	public void setYAxisLabelVisible(boolean bVisible)
	{
		oYAxisLabel.setVisible(bVisible);
	}

	/**
   * This will set the YAxis Scale visibility.
   */
	public void setYAxisScaleVisible(boolean bVisible)
	{
		oYAxisContainerPanel.setVisible(bVisible);
	}

	/**
   * This will set the Axis Scales Enabled or Disabled.
   */
	public void setAxisScalesEnabled(boolean bEnabled)
	{
		setXAxisScaleEnabled(bEnabled);
		setYAxisScaleEnabled(bEnabled);
	}

	/**
   * This will set the YAxis Scale Enabled or Disabled.
   */
	public void setXAxisScaleEnabled(boolean bEnabled)
	{
		oXAxisScale.setVisible(bEnabled);
	}

	/**
   * This will set the YAxis Scale Enabled or Disabled.
   */
	public void setYAxisScaleEnabled(boolean bEnabled)
	{
		oYAxisScale.setVisible(bEnabled);
	}

	/**
   * This will set the Cursor Coordinates Enabled or Disabled.
   */
	public void setCursorCoordinatesEnabled(boolean bEnabled)
	{
    getInternalCoordinatesPanel().setVisible(bEnabled);
    
    if ( genericCoordinatePanel != null )
    {
      genericCoordinatePanel.setVisible( bEnabled );
    }
	}

	public void setCursorCoordinatesVisible(boolean bVisible)
	{
		oCoordinatesPanel.setVisible(bVisible);
	}
	
	/**
   * This will set the Title of the Graph.
   */
	public void setTitle(String sTitle)
	{
		oTitleLabel.setText(sTitle);
	}
	
	public String getTitle()
	{
		return oTitleLabel.getText();
	}

	public void setTitleFont(Font oFont)
	{
		oTitleLabel.setFont(oFont);
	}

	public Font getTitleFont()
	{
		return oTitleLabel.getFont();
	}

	/** this makes the title label centred on the graph contents, at the cost of potentially overlapping with
	 * the other side components when space is reduced.
	 */
	public void makeTitleCompletelyCentred()
	{
		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
	  gridBagConstraints.gridx = 0;
	  gridBagConstraints.gridy = 0;
	  gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
	  gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
	  gridBagConstraints.gridwidth = 4;
	  gridBagConstraints.weightx = 1.0;
	  gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
	  ((GridBagLayout) oTitlePanel.getLayout()).setConstraints(oTitleLabel, gridBagConstraints);

	  //since the TitlePanel is not soaking up excess space in the centre anymore, we need another component
	  JPanel spacer = new JPanel();
	  spacer.setOpaque(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
	  oTitlePanel.add(spacer, gridBagConstraints);
	}
  
	/**
	 * Sets the alignment of the title along the X axis.
	 * 
	 * @see javax.swing.SwingConstants
	 * 
   * @param alignment  One of the following constants
   *           defined in <code>SwingConstants</code>:
   *           <code>LEFT</code>,
   *           <code>CENTER</code> (the default for image-only labels),
   *           <code>RIGHT</code>,
   *           <code>LEADING</code> (the default for text-only labels) or
   *           <code>TRAILING</code>.
	 */
	public void setTitleHorizontalAlignment(int alignment)
	{
		oTitleLabel.setHorizontalAlignment(alignment);
	}
	
	public int getTitleHorizontalAlignment()
	{
		return oTitleLabel.getHorizontalAlignment();
	}
	
	public void setTitleForeground(Color oColor)
	{
		oTitleLabel.setForeground(oColor);
	}

	public void setCoordinatesFont(Font oFont)
	{
    getInternalCoordinatesPanel().setFont(oFont);
	}

	public void setAxisLabelFont(Font oFont)
	{
		oXAxisLabel.setFont(oFont);
		oYAxisLabel.setFont(oFont);
	}

	public void setAxisLabelForeground(Color oColor)
	{
		oXAxisLabel.setForeground(oColor);
		oYAxisLabel.setForeground(oColor);
	}

	public void setGraphContainerPanelBackground(Color oColor)
	{
		oGraphContainerPanel.setBackground(oColor);
	}

	public void setCoordinatesPanelBackground(Color oColor)
	{
    getInternalCoordinatesPanel().setBackground(oColor);
	}

	/**
   * This will set the Title of both the Axis of the Graph.
   */
	public void setAxisTitles(String sXAxisTitle, String sYAxisTitle)
	{
		setAxisTitlesAndUnits(sXAxisTitle, this.sXAxisUnit, sYAxisTitle, this.sYAxisUnit);
	}
	
	public void setYAxisTitle(String sYAxisTitle)
	{
		setAxisTitlesAndUnits(sXAxisTitle, sXAxisUnit, sYAxisTitle, sYAxisUnit);
	}

	public void setXAxisTitle(String sXAxisTitle)
	{
		setAxisTitlesAndUnits(sXAxisTitle, sXAxisUnit, sYAxisTitle, sYAxisUnit);
	}
	
	public void setXAxisUnit(String sXAxisUnit)
	{
		setAxisTitlesAndUnits(sXAxisTitle, sXAxisUnit, sYAxisTitle, sYAxisUnit);
	}
	
	public void setYAxisUnit(String sYAxisUnit)
	{
		setAxisTitlesAndUnits(sXAxisTitle, sXAxisUnit, sYAxisTitle, sYAxisUnit);
	}
  
	/**
   * This will set the Title and Units of both the Axis of the Graph.
   */
	public void setAxisTitlesAndUnits( String sXAxisTitle,
                                    String sXAxisUnit,
                                    String sYAxisTitle,
                                    String sYAxisUnit )
	{
		this.sXAxisTitle = sXAxisTitle;
		this.sXAxisUnit = sXAxisUnit;
		this.sYAxisTitle = sYAxisTitle;
		this.sYAxisUnit = sYAxisUnit;

		updateSuffixesInCoordinateLables();
		
		SwingUtilities.invokeLater( new Runnable()
    {
      public void run()
      {
        if ( oGraph == null )
        {
          return;
        }

        final String xScaleUnit = oGraph.getXScaleUnit();
        final String yScaleUnit = oGraph.getYScaleUnit();
        
        String text = MultiGraphWrapper.this.sXAxisTitle;
        if ( MultiGraphWrapper.this.sXAxisUnit != null && MultiGraphWrapper.this.sXAxisUnit.length() > 0
            && xScaleUnit != null )
        {
          text = String.format( "%s (%s%s)",
                                MultiGraphWrapper.this.sXAxisTitle,
                                xScaleUnit,
                                MultiGraphWrapper.this.sXAxisUnit );

        }
        oXAxisLabel.setText( text );

        text = MultiGraphWrapper.this.sYAxisTitle;
        if ( MultiGraphWrapper.this.sYAxisUnit != null && MultiGraphWrapper.this.sYAxisUnit.length() > 0
            && yScaleUnit != null )
        {
          text = String.format( "%s (%s%s)",
                                MultiGraphWrapper.this.sYAxisTitle,
                                yScaleUnit,
                                MultiGraphWrapper.this.sYAxisUnit );

        }
        oYAxisLabel.setText( text );
      }
    } );
  }

  
	public String getXAxisTitle()
  {
    return this.sXAxisTitle;
  }

  public String getXAxisUnit()
  {
    return this.sXAxisUnit;
  }

  public String getYAxisTitle()
  {
    return this.sYAxisTitle;
  }

  public String getYAxisUnit()
  {
    return this.sYAxisUnit;
  }

  private void updateSuffixesInCoordinateLables()
	{
		if (oGraph!=null)
		{
			final String sXSuffix = oGraph.getXScaleUnit() + (sXAxisUnit==null ? "" : sXAxisUnit);
			final String sYSuffix = oGraph.getYScaleUnit() + (sYAxisUnit==null ? "" : sYAxisUnit);
      getInternalCoordinatesPanel().setCoordinateSuffixes(sXSuffix, sYSuffix);
		}
	}

	/**
	 * Add an extra component below the x-axis panel.
	 * Useful for additional GUI elements that need to be the same width as the x-axis.
	 */
	public void addXAxisExtra(JComponent oExtraComponent)
	{
    oXAxisExtraPanel.add(oExtraComponent, BorderLayout.CENTER);		
	}

	/**
   * This panel is meant for adding extra functionality to the x-axis area.
   * By default, it has a BorderLayout.
   * 
	 * Useful for additional GUI elements that need to be the same width as the x-axis.
   */
	public JPanel getXAxisExtraPanel()
	{
		return oXAxisExtraPanel;
	}
	
	/**
   * This panel is meant for adding extra functionality to the header area, between the title
   * and the co-ordinates panel.
   * 
   * By default, it has a BorderLayout.
   */
	public JPanel getHeaderRightExtraPanel()
	{
		return oHeaderRightExtraPanel;
	}
	
	/**
   * This panel is meant for adding extra functionality to the header area, between the title
   * and the left edge of the graph.
   * 
   * By default, it has a BorderLayout.
   */
	public JPanel getHeaderLeftExtraPanel()
	{
		return oHeaderLeftExtraPanel;
	}
	
	/**
   * This panel is meant for adding extra functionality to the header area, between the title
   * and the top edge of the graph.
   * It spans the width of the graph.
   * Useful for adding components to the top of the graph that need to be exactly as wide as the
   * graph.
   * 
   * By default, it has a BorderLayout.
   */
	public JPanel getHeaderBottomExtraPanel()
	{
		return oHeaderBottomExtraPanel;
	}
	
	/**
	 * Get the top left corner panel.
	 * This method is so that clients of this class can add extra widgets to that space.
   * 
   * By default, it has a BorderLayout.
   */
	public JPanel getTopLeftCornerPanel()
	{
		return oTopLeftCornerPanel;
	}

	/**
	 * Get the bottom left corner panel.
	 * This method is so that clients of this class can add extra widgets to that space.
   * 
   * By default, it has a BorderLayout.
   */
	public JPanel getBottomLeftCornerPanel()
	{
		return oBottomLeftCornerPanel;
	}
  
	/**
	 * This will remove the current co-ordinates panel, and disconnect the auto-updating
	 * of the coordinates that normally happens.
	 */
	public void replaceCoordinatesPanel(JPanel newCoordinatesPanel)
	{
    if ( genericCoordinatePanel != null )
    {
      oTitlePanel.remove( genericCoordinatePanel );
    }
    
    genericCoordinatePanel = newCoordinatesPanel;
    
    if ( oCoordinatesPanel != null )
    {
      oTitlePanel.remove( oCoordinatesPanel );
      oCoordinatesPanel = null;
    }
		
		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
	  gridBagConstraints.gridx = 3;
	  gridBagConstraints.gridy = 0;
	  gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
	  oTitlePanel.add( genericCoordinatePanel,
                     gridBagConstraints );
	  
	  oTitlePanel.revalidate();
	  oTitlePanel.repaint();
	}
  
	public void replaceYAxis(JComponent newAxis)
	{
		oYAxisContainerPanel.removeAll();
		oYAxisContainerPanel.add(newAxis);
	}
	
	public void setXCoordinateDecimalFormat(String oFormat)
	{
    getInternalCoordinatesPanel().setXCoordinateDecimalFormat(oFormat);
	}

	public void setYCoordinateDecimalFormat(String oFormat)
	{
    getInternalCoordinatesPanel().setYCoordinateDecimalFormat(oFormat);
	}

  /**
   * This method is called when the locale has been changed. The listener should
   * then update the visual components.
   */
  public void componentsLocaleChanged()
  {
    textRes = PeralexLibsBundle.getResource();
    
    oXAxisLabel.setText(textRes.getString("X-Axis"));
	}
	
	public void setVisible(boolean bVisible)
	{
	  getInternalCoordinatesPanel().setVisible( bVisible );
	  disabler.setVisible( bVisible );
	}
	
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel oBottomLeftCornerPanel;
  private com.peralex.utilities.ui.graphs.graphBase.CoordinatesPanel oCoordinatesPanel;
  private javax.swing.JPanel oGraphContainerPanel;
  private javax.swing.JPanel oHeaderBottomExtraPanel;
  private javax.swing.JPanel oHeaderLeftExtraPanel;
  private javax.swing.JPanel oHeaderRightExtraPanel;
  private javax.swing.JLabel oTitleLabel;
  private javax.swing.JPanel oTitlePanel;
  private javax.swing.JPanel oTopLeftCornerPanel;
  private javax.swing.JPanel oXAxisContainerPanel;
  private javax.swing.JPanel oXAxisExtraPanel;
  private javax.swing.JLabel oXAxisLabel;
  private javax.swing.JPanel oYAxisContainerPanel;
  private com.peralex.utilities.ui.graphs.graphBase.YAxisLabel oYAxisLabel;
  // End of variables declaration//GEN-END:variables
}
