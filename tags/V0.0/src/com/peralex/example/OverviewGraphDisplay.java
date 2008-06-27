package com.peralex.example;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import com.peralex.utilities.ui.graphs.graphBase.RangeCursor;
import com.peralex.utilities.ui.graphs.lineGraph.MultiLineGraph;

public class OverviewGraphDisplay extends BaseGraphDisplay
{
	////////////////////////////////////////////////////////////
	// Graphs and other visual objects
	////////////////////////////////////////////////////////////
	private final GraphWithCustomDrawSurface oAmplitudeLineGraph;
	private final JMenuItem oTuneHereItem;
	
	////////////////////////////////////////////////////////////
	// General variables
	////////////////////////////////////////////////////////////

	public OverviewGraphDisplay()
	{
		oAmplitudeLineGraph = new GraphWithCustomDrawSurface();
		oAmplitudeLineGraph.setFrameLimitingEnabled(true);
		oAmplitudeLineGraph.setOptimizedMode(true);
		oAmplitudeLineGraph.setGridVisible(false);
		oAmplitudeLineGraph.setZoomEnabled(false);
//		oAmplitudeLineGraph.setLineColours(new Color[] {Color.GREEN, Color.YELLOW, Color.BLUE});
		addAmplitudeGraphToWrapper(oAmplitudeLineGraph);
		
		oTuneHereItem = new JMenuItem("Custom Popup Menu Item");
		oTuneHereItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent oEvent)
			{
//				final float f = oAmplitudeLineGraph.pixelToUnitX(oAmplitudeLineGraph.iMousePressedX);
			}
		});
		oAmplitudeLineGraph.getPopupMenu().add(oTuneHereItem, 0);
		
		// call this to initialise the data arrays in the amplitude line graph
		setNoiseFloorStatus(false);
	}
	
	@Override
	public MultiLineGraph getAmplitudeLineGraph()
	{
		return oAmplitudeLineGraph;
	}
	
	void removeDemodCursor(String sCursorID)
	{
		oAmplitudeLineGraph.removeRangeCursor(sCursorID);
	}
	
	void setDemodCursorLabel(String sClientID, String sLabelValue)
	{
		oAmplitudeLineGraph.getRangeCursor(sClientID).setLabel(sLabelValue);
	}
	
	void setDemodCursor(String sCursorId, long lRangeCursorCenter_Hz, long lBandwidth_Hz, long lCurrentFrequencyIncrement_Hz)
	{
		RangeCursor oRangeCursor = oAmplitudeLineGraph.getRangeCursor(sCursorId);
		oRangeCursor.setWidth(lBandwidth_Hz);
		oRangeCursor.setResolution(lCurrentFrequencyIncrement_Hz);
		oRangeCursor.setValue(lRangeCursorCenter_Hz);
	}

	void addDemodCursor(String sCursorId, Color darkDemodColor, long lRangeCursorCenter_Hz, long lBandwidth_Hz, long lCurrentFrequencyIncrement_Hz)
	{
		oAmplitudeLineGraph.addRangeCursor(sCursorId, darkDemodColor, lCurrentFrequencyIncrement_Hz, lBandwidth_Hz,
				lRangeCursorCenter_Hz);
	}
	
	void setDemodCursorCentre(String sCursorId, long lValue)
	{
		RangeCursor oRangeCursor = oAmplitudeLineGraph.getRangeCursor(sCursorId);
		oRangeCursor.setValue(lValue);
	}
}
