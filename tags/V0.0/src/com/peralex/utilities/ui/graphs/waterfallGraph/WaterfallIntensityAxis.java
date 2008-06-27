package com.peralex.utilities.ui.graphs.waterfallGraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import com.peralex.utilities.ui.graphs.graphBase.IntensityAxis;

/**
 * A specialised intensity axis for cIntensityWaterfallGraph
 * 
 * @author Noel Grandin
 */
public class WaterfallIntensityAxis extends JComponent
{
	private final IntensityAxis axis;
	private final JToggleButton autoModeButton;
	private final IntensityWaterfallGraph graph;
	private Color defaultButtonBackground;
	
	public WaterfallIntensityAxis(IntensityWaterfallGraph graph)
	{
		this.graph = graph;
		
		axis = new IntensityAxis();
		
		autoModeButton = new JToggleButton("Auto");
		autoModeButton.setHorizontalAlignment(JLabel.CENTER);
		autoModeButton.setMargin(new Insets(0,0,0,0));
		autoModeButton.setFocusPainted(false);
		autoModeButton.setToolTipText("Auto-scale the intensity");
		
		defaultButtonBackground = autoModeButton.getBackground();
		
		setLayout(new BorderLayout());
		this.add(axis, BorderLayout.CENTER);
		this.add(autoModeButton, BorderLayout.SOUTH);
		
		autoModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				boolean bAutoScale = autoModeButton.isSelected();
				axis.setSlidersEnabled(!bAutoScale);
				WaterfallIntensityAxis.this.graph.autoScaleSelected(bAutoScale);
				if (bAutoScale) {
					setButtonBackground(autoModeButton, true, new Color(0, 255, 0));
				} else {
					setButtonBackground(autoModeButton, false, null);
				}
			}
		});
	}

	public void setAutoScaleMode(boolean bAutoScale)
	{
		this.autoModeButton.setSelected(bAutoScale);
		axis.setSlidersEnabled(!bAutoScale);
	}
	
	public void resetScale()
	{
		axis.resetScale();
	}
	
	public IntensityAxis getAxis()
	{
		return this.axis;
	}
	
	/**
	 * Setting the button background under WindowsXP is a little tricky.
	 * 
	 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4880747
	 */
	private final void setButtonBackground(JToggleButton button, boolean active, Color activeColor)
	{
		if (active) {
			button.setContentAreaFilled(false);
			button.setOpaque(true);
			button.setBackground(activeColor);
		} else {
			button.setBackground(defaultButtonBackground);
			button.setContentAreaFilled(true);
			button.setOpaque(true);
		}
	}
}
