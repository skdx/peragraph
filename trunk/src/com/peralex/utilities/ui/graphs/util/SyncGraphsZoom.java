package com.peralex.utilities.ui.graphs.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.peralex.utilities.ui.graphs.graphBase.IZoomListener;
import com.peralex.utilities.ui.graphs.graphBase.ZoomDrawSurface;

/**
 * Synchronise a set of graphs zoom status
 * 
 * @author David Lee
 * @author Noel Grandin
 */
public final class SyncGraphsZoom implements IZoomListener
{
	private final Set<ZoomDrawSurface> relatedGraphs = new HashSet<ZoomDrawSurface>();

	private static enum ESyncWhat
	{
		X, Y, BOTH;
	}

	private final ESyncWhat syncWhat;

	/**
	 * Synchronise a set of graphs x-zoom status
	 */
	public static void syncX(ZoomDrawSurface... surfaces)
	{
		for (ZoomDrawSurface graph : surfaces)
		{
			new SyncGraphsZoom(ESyncWhat.X, graph, surfaces);
		}
	}

	/**
	 * Synchronise a set of graphs y-zoom status
	 */
	public static void syncY(ZoomDrawSurface... surfaces)
	{
		for (ZoomDrawSurface graph : surfaces)
		{
			new SyncGraphsZoom(ESyncWhat.Y, graph, surfaces);
		}
	}

	/**
	 * Synchronise a set of graphs x,y-zoom status
	 */
	public static void syncXY(ZoomDrawSurface... surfaces)
	{
		for (ZoomDrawSurface graph : surfaces)
		{
			new SyncGraphsZoom(ESyncWhat.BOTH, graph, surfaces);
		}
	}

	/** only meant to be instantiated from the static factory constructor methods */
	private SyncGraphsZoom(ESyncWhat syncWhat, ZoomDrawSurface me, ZoomDrawSurface[] relatedGraphs)
	{
		this.syncWhat = syncWhat;
		this.relatedGraphs.addAll(Arrays.asList(relatedGraphs));
		this.relatedGraphs.remove(me);

		me.addZoomListener(this);
	}

	public void graphZoomChanged(double minimumX, double maximumX, double minimumY, double maximumY)
	{
	}

	public void zoomAnimationStart(double fMinimumX, double fMaximumX, double fMinimumY, double fMaximumY)
	{
		for (ZoomDrawSurface graph : relatedGraphs)
		{
			switch (syncWhat)
			{
			case X:
				graph.zoomXRange(fMinimumX, fMaximumX);
				break;
			case Y:
				graph.zoomYRange(fMinimumY, fMaximumY);
				break;
			case BOTH:
				graph.zoomIn(fMinimumX, fMaximumX, fMinimumY, fMaximumY);
				break;
			}
		}
	}

	public void graphZoomStatusChanged(boolean bIsCurrentlyZoomed)
	{
		if (!bIsCurrentlyZoomed)
		{
			for (ZoomDrawSurface graph : relatedGraphs)
			{
				graph.resetZoom();
			}
		}
	}
}
