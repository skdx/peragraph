package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.peralex.utilities.locale.ILocaleListener;
import com.peralex.utilities.locale.PeralexLibsBundle;
import com.peralex.utilities.ui.SwingLib;
import com.peralex.utilities.ui.graphs.axisscale.AbstractAxisScale;
import com.peralex.utilities.ui.graphs.axisscale.AbstractDefaultAxisScale;
import com.peralex.utilities.ui.graphs.axisscale.NumberAxisScale;

/**
 * This wraps the graph proper and provides titles, x-axis and y-axis labels, and a co-ordinate panel.
 * 
 * This version tries to scale with system font settings. It also tries to
 * gracefully scale with resizing windows. It uses the default system font
 * settings.
 * 
 * FIXME: we have the names of some methods confused. e.g. setXAxisVisible() should be called setXAxisScaleVisible()
 *   and setXAxisScaleVisible() should be called setXAxisPanelVisible().
 *   
 * FIXME: de-couple the relationship between this and cGraphBase by using an interface.
 *    Gives me more options when creating new kinds of graphs.
 *    
 * FIXME rename the coordinates panel to something like MouseCoordinatesPanel to distinguish it from line-cursors.
 * 
 * @author Andre 
 * @author Noel Grandin
 */
public class GraphWrapper extends javax.swing.JPanel implements ILocaleListener
{
	/** The resource bundle used for multilingual support */
	private ResourceBundle textRes = PeralexLibsBundle.getResource();

	/**
   * Stores the reference to the Graph that is added.
   * 
   * Note: this can be null if we operating purely as a layout class.
   */
	private GraphBase oGraph;

	/**
   * These store the Titles and Units of the Axis'.
   */
	private String sXAxisTitle = "", sXAxisUnit = "", sYAxisTitle = "",
			sYAxisUnit = "";

	private AbstractAxisScale oXAxisScale = new NumberAxisScale(AbstractDefaultAxisScale.X_AXIS);

	private AbstractAxisScale oYAxisScale = new NumberAxisScale(AbstractDefaultAxisScale.Y_AXIS);
	
	/**
   * Creates new form cGraphWrapper
   */
	public GraphWrapper()
	{
		initComponents();

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
	public GraphWrapper(GraphBase oGraph)
	{
		this();
		setGraph(oGraph);
	}

	/**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents()
  {
    java.awt.GridBagConstraints gridBagConstraints;

    oTitlePanel = new javax.swing.JPanel();
    oHeaderLeftExtraPanel = new javax.swing.JPanel();
    oTitleLabel = new javax.swing.JLabel();
    oHeaderRightExtraPanel = new javax.swing.JPanel();
    oCoordinatesPanel = new com.peralex.utilities.ui.graphs.graphBase.CoordinatesPanel();
    oHeaderBottomExtraPanel = new javax.swing.JPanel();
    oGraphContainerPanel = new javax.swing.JPanel();
    oXAxisContainerPanel = new javax.swing.JPanel();
    oYAxisLabel = new com.peralex.utilities.ui.graphs.graphBase.YAxisLabel();
    oYAxisContainerPanel = new javax.swing.JPanel();
    oXAxisLabel = new javax.swing.JLabel();
    oTopLeftCornerPanel = new javax.swing.JPanel();
    oBottomLeftCornerPanel = new javax.swing.JPanel();
    oXAxisExtraPanel = new javax.swing.JPanel();

    setLayout(new java.awt.GridBagLayout());

    oTitlePanel.setLayout(new java.awt.GridBagLayout());

    oHeaderLeftExtraPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    oTitlePanel.add(oHeaderLeftExtraPanel, gridBagConstraints);

    oTitleLabel.setText("Graph Title");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
    oTitlePanel.add(oTitleLabel, gridBagConstraints);

    oHeaderRightExtraPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    oTitlePanel.add(oHeaderRightExtraPanel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    oTitlePanel.add(oCoordinatesPanel, gridBagConstraints);

    oHeaderBottomExtraPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    oTitlePanel.add(oHeaderBottomExtraPanel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    add(oTitlePanel, gridBagConstraints);

    oGraphContainerPanel.setLayout(new java.awt.BorderLayout());

    oGraphContainerPanel.setBackground(new java.awt.Color(153, 153, 153));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    add(oGraphContainerPanel, gridBagConstraints);

    oXAxisContainerPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    add(oXAxisContainerPanel, gridBagConstraints);

    oYAxisLabel.setLayout(new java.awt.FlowLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0;
    add(oYAxisLabel, gridBagConstraints);

    oYAxisContainerPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0;
    add(oYAxisContainerPanel, gridBagConstraints);

    oXAxisLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    oXAxisLabel.setText(textRes.getString("X-Axis"));
    oXAxisLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    oXAxisLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    add(oXAxisLabel, gridBagConstraints);

    oTopLeftCornerPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    add(oTopLeftCornerPanel, gridBagConstraints);

    oBottomLeftCornerPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    add(oBottomLeftCornerPanel, gridBagConstraints);

    oXAxisExtraPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    add(oXAxisExtraPanel, gridBagConstraints);

  }// </editor-fold>//GEN-END:initComponents

  private class MyGridListener implements IGridListener
	{
		public void mouseCoordinatesChanged(GridDrawSurface surface, final double fXValue,
				final double fYValue)
		{
			if (oCoordinatesPanel != null)
			{
				oCoordinatesPanel.setCoordinates(fXValue / oGraph.getXScalingFactor(), fYValue
						/ oGraph.getYScalingFactor());
			}
		}

		public void scalingFactorChanged(GridDrawSurface surface, long lXScalingFactor,
				String sXScaleUnit, long lYScalingFactor, String sYScaleUnit)
		{
			updateCoordinatesPanelSuffixes();
			if (!sXAxisUnit.equals(""))
			{
				oXAxisLabel.setText(sXAxisTitle + " (" + sXScaleUnit + sXAxisUnit + ")");
			}
			if (!sYAxisUnit.equals(""))
			{
				oYAxisLabel.setText(sYAxisTitle + " (" + sYScaleUnit + sYAxisUnit + ")");
			}
		}
		public void gridChanged(int axis, double minimum, double maximum, long scalingFactor,
				boolean proportional, double[] afGridValues, int[] aiGridCoordinates) {
		}
	}
  private final MyGridListener gridListener = new MyGridListener();

	/**
   * This will add the graph to the Wrapper.
   */
	public final void setGraph(final GraphBase _oGraph)
	{
		this.oGraph = _oGraph;
		oXAxisScale.linkToX(this.oGraph);
		oYAxisScale.linkToY(this.oGraph);
		
		oGraph.addGridListener(gridListener);
		oGraphContainerPanel.add(oGraph);
		oXAxisContainerPanel.add(oXAxisScale);
		oXAxisScale.setOffsetFirstLabel(true);
		oYAxisContainerPanel.add(oYAxisScale);
		updateCoordinatesPanelSuffixes();
	}

	/**
	 * This is a very raw method for adding stuff to a GraphWrapper.
	 * It means that you are using the wrapper purely as a layout class, 
	 * and none of the axis and cursor/coordinate stuff is in operation.
	 */
	public final void setGraph(Component _oGraph, Component oXAxis, Component oYAxis)
	{
	  oGraphContainerPanel.add(_oGraph);
		oXAxisContainerPanel.add(oXAxis);
		oYAxisContainerPanel.add(oYAxis);
	}
	
	/**
	 * Remove the current Graph component.
	 */
	public final void removeGraph()
	{
		oGraph.removeGridListener(gridListener);
		oGraphContainerPanel.remove(oGraph);
		oXAxisContainerPanel.remove(oXAxisScale);
		oYAxisContainerPanel.remove(oYAxisScale);
		this.oGraph = null;
	}

	/**
	 * This will set the visibility of the x-scale i.e. the thing with numbers and marks on it.
	 */
	public void setXAxisVisible(boolean bVisible)
	{
		oXAxisScale.setVisible(bVisible);
	}
	
	public boolean isXAxisVisible()
	{
		return oXAxisScale.isVisible();
	}
	
	/**
	 * This will set the visibility of the y-scale i.e. the thing with numbers and marks on it.
	 */
	public void setYAxisVisible(boolean bVisible)
	{
		oYAxisScale.setVisible(bVisible);
	}
	
	public boolean isYAxisVisible()
	{
		return oYAxisScale.isVisible();
	}
	
	/**
	 * sets visibility for both x- and y-axis labels
	 */
	public void setAxisLabelsVisible(boolean bVisible)
	{
		setXAxisLabelVisible(bVisible);
		setYAxisLabelVisible(bVisible);
	}
	
	public boolean isAxisLabelsVisible()
	{
		return isXAxisLabelVisible() && isYAxisLabelVisible();
	}

	/**
   * This will set the X- and Y-Axis container panel visibility.
   */
	public void setAxisScalesVisible(boolean bVisible)
	{
		setXAxisScaleVisible(bVisible);
		setYAxisScaleVisible(bVisible);
	}
	
	public boolean isAxisScalesVisible()
	{
		return isXAxisScaleVisible() && isYAxisScaleVisible();
	}

	/**
   * This will set the XAxis Label visibility.
   */
	public void setXAxisLabelVisible(boolean bVisible)
	{
		oXAxisLabel.setVisible(bVisible);
	}
	
	public boolean isXAxisLabelVisible()
	{
		return oXAxisLabel.isVisible();
	}

	/**
   * This will set the XAxis container panel visibility.
   */
	public void setXAxisScaleVisible(boolean bVisible)
	{
		oXAxisContainerPanel.setVisible(bVisible);
	}
	
	public boolean isXAxisScaleVisible()
	{
		return oXAxisContainerPanel.isVisible();
	}

	/**
   * This will set the YAxis Label visibility.
   */
	public void setYAxisLabelVisible(boolean bVisible)
	{
		oYAxisLabel.setVisible(bVisible);
	}

	public boolean isYAxisLabelVisible()
	{
		return oYAxisLabel.isVisible();
	}
	
	/**
   * This will set the YAxis container panel visibility.
   */
	public void setYAxisScaleVisible(boolean bVisible)
	{
		oYAxisContainerPanel.setVisible(bVisible);
	}
	
	public boolean isYAxisScaleVisible()
	{
		return oYAxisContainerPanel.isVisible();
	}

	/**
	 * set the visibility of the cursor co-ordinates panel.
	 */
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

	public void setCursorCoordinatesFont(Font oFont)
	{
		oCoordinatesPanel.setFont(oFont);
	}
	
	/**
	 * sets fonts for both x- and y- axes
	 */
	public void setAxisLabelFont(Font oFont)
	{
		oXAxisLabel.setFont(oFont);
		oYAxisLabel.setFont(oFont);
	}

	/**
	 * sets label background for both x- and y-axes
	 */
	public void setAxisLabelForeground(Color oColor)
	{
		oXAxisLabel.setForeground(oColor);
		oYAxisLabel.setForeground(oColor);
	}

	/**
	 * FIXME: need a better name for this method
	 */
	public void setGraphContainerPanelBackground(Color oColor)
	{
		oGraphContainerPanel.setBackground(oColor);
	}

	public void setCursorCoordinatesBackground(Color oColor)
	{
		oCoordinatesPanel.setBackground(oColor);
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
	public void setAxisTitlesAndUnits(String sXAxisTitle,
			String sXAxisUnit, String sYAxisTitle, String sYAxisUnit)
	{
		this.sXAxisTitle = sXAxisTitle;
		this.sXAxisUnit = sXAxisUnit;
		this.sYAxisTitle = sYAxisTitle;
		this.sYAxisUnit = sYAxisUnit;
		
		updateCoordinatesPanelSuffixes();
		
		SwingLib.invokeAndWaitSafe(new Runnable()
			{
				public void run()
				{
					internalSetAxisTitlesAndUnits();
				}
			});
  }
  
  private void internalSetAxisTitlesAndUnits()
	{
		if (GraphWrapper.this.sXAxisUnit != null && !GraphWrapper.this.sXAxisUnit.equals(""))
		{
			final String xScaleUnit = oGraph==null ? "" : oGraph.getXScaleUnit();
			oXAxisLabel.setText(GraphWrapper.this.sXAxisTitle + " (" + xScaleUnit
					+ GraphWrapper.this.sXAxisUnit + ")");
		}
		else
		{
			oXAxisLabel.setText(GraphWrapper.this.sXAxisTitle);
		}
		if (GraphWrapper.this.sYAxisUnit != null && !GraphWrapper.this.sYAxisUnit.equals(""))
		{
			final String yScaleUnit = oGraph==null ? "" : oGraph.getYScaleUnit();
			oYAxisLabel.setText(GraphWrapper.this.sYAxisTitle + " (" + yScaleUnit
					+ GraphWrapper.this.sYAxisUnit + ")");
		}
		else
		{
			oYAxisLabel.setText(GraphWrapper.this.sYAxisTitle);
		}
	}


	private void updateCoordinatesPanelSuffixes()
	{
		if (oGraph!=null && oCoordinatesPanel!=null)
		{
			final String sXSuffix = oGraph.getXScaleUnit() + (sXAxisUnit==null ? "" : sXAxisUnit);
			final String sYSuffix = oGraph.getYScaleUnit() + (sYAxisUnit==null ? "" : sYAxisUnit);
			oCoordinatesPanel.setCoordinateSuffixes(sXSuffix, sYSuffix);
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
	 * Add an extra component to the right of the graph draw surface.
	 * This component is sized to the same height as the graph draw
	 * surface, but is positioned to the right of the graph and 
	 * the title area.
	 * 
	 * FIXME we should check that only one component gets added
	 */
	public void setGraphRightExtra(JComponent oExtraComponent)
	{
		GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.weighty = 1.0;
    add(oExtraComponent, gridBagConstraints);
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
	 * This will add the given component to the HeaderPanel at the top, 
	 * spanning the full width of the header panel, sitting above the title and co-ordinates panel.
	 */
	public void addAboveHeaderPanel(JComponent oExtraComponent)
	{
    GridBagConstraints oGridBagConstraints = new GridBagConstraints();
    oGridBagConstraints.gridx = 0;
    oGridBagConstraints.gridy = 0;
    oGridBagConstraints.gridwidth = 4;
    oGridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    oTitlePanel.add(oExtraComponent, oGridBagConstraints);		
	}
	
	/**
	 * This will remove the current co-ordinates panel, and disconnect the auto-updating
	 * of the coordinates that normally happens.
	 */
	public void replaceCoordinatesPanel(JPanel newCoordinatesPanel)
	{
		if (oCoordinatesPanel==null) throw new IllegalStateException("panel already been replaced");
		
		oTitlePanel.remove(oCoordinatesPanel);
		oCoordinatesPanel = null;
		
		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
	  gridBagConstraints.gridx = 3;
	  gridBagConstraints.gridy = 0;
	  gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
	  oTitlePanel.add(newCoordinatesPanel, gridBagConstraints);
	  
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
		oCoordinatesPanel.setXCoordinateDecimalFormat(oFormat);
	}
	
	public void setYCoordinateDecimalFormat(String oFormat)
	{
		oCoordinatesPanel.setYCoordinateDecimalFormat(oFormat);
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
