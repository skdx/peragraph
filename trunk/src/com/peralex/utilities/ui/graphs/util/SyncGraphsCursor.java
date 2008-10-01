package com.peralex.utilities.ui.graphs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.peralex.utilities.ui.graphs.graphBase.ICursorListener;
import com.peralex.utilities.ui.graphs.graphBase.Cursor;
import com.peralex.utilities.ui.graphs.graphBase.CursorDrawSurface;

/**
 * Used for synchronized cursor positions across multiple graphs.
 * 
 * @author Noel Grandin
 */
public class SyncGraphsCursor
{
	private static class SyncEntry
	{
		public final CursorDrawSurface surface;

		public final String cursorID;

		public SyncEntry(String cursorID, CursorDrawSurface surface)
		{
			this.surface = surface;
			this.cursorID = cursorID;
		}
	}

	private static enum ESyncWhat
	{
		X, Y, BOTH;
	}

	private final List<SyncEntry> relatedGraphs = new ArrayList<SyncEntry>();

	private final ESyncWhat syncWhat;

	/**
	 * Synchronise a pair of graphs cursor X values
	 */
	public static void syncX(String cursor1, CursorDrawSurface surface1, String cursor2, CursorDrawSurface surface2)
	{
		new SyncGraphsCursor(ESyncWhat.X, new SyncEntry[] { new SyncEntry(cursor1, surface1), new SyncEntry(cursor2, surface2), });
	}

	/**
	 * Synchronise a set of graphs cursor X values
	 * @param cursorID shared cursor ID
	 */
	public static void syncX(String cursorID, CursorDrawSurface ... surfaces)
	{
		sync(ESyncWhat.X, cursorID, surfaces);
	}
	
	/**
	 * Synchronise a pair of graphs cursor Y values
	 */
	public static void syncY(String cursor1, CursorDrawSurface surface1, String cursor2, CursorDrawSurface surface2)
	{
		new SyncGraphsCursor(ESyncWhat.Y, new SyncEntry[] { new SyncEntry(cursor1, surface1), new SyncEntry(cursor2, surface2), });
	}

	/**
	 * Synchronise a set of graphs cursor Y values
	 * @param cursorID shared cursor ID
	 */
	public static void syncY(String cursorID, CursorDrawSurface ... surfaces)
	{
		sync(ESyncWhat.Y, cursorID, surfaces);
	}
	
	/**
	 * Synchronise a pair of graphs cursor X and Y values
	 */
	public static void syncXY(String cursor1, CursorDrawSurface surface1, String cursor2, CursorDrawSurface surface2)
	{
		new SyncGraphsCursor(ESyncWhat.BOTH, new SyncEntry[] { new SyncEntry(cursor1, surface1), new SyncEntry(cursor2, surface2), });
	}

	/**
	 * Synchronise a set of graphs cursor X and Y values
	 * @param cursorID shared cursor ID
	 */
	public static void syncXY(String cursorID, CursorDrawSurface ... surfaces)
	{
		sync(ESyncWhat.BOTH, cursorID, surfaces);
	}
	
	private static void sync(ESyncWhat syncWhat, String cursorID, CursorDrawSurface ... surfaces)
	{
		final SyncEntry [] entries = new SyncEntry[surfaces.length];
		int i = 0;
		for (CursorDrawSurface surface : surfaces) {
			entries[i] = new SyncEntry(cursorID, surface);
			i++;
		}
		
		new SyncGraphsCursor(syncWhat, entries);
	}
	
	/** only meant to be instantiated from the static factory constructor methods */
	private SyncGraphsCursor(ESyncWhat syncWhat, SyncEntry[] relatedGraphs)
	{
		this.syncWhat = syncWhat;
		this.relatedGraphs.addAll(Arrays.asList(relatedGraphs));

		for (SyncEntry entry : relatedGraphs)
		{
			entry.surface.addCursorListener(new MyCursorListener(entry));
		}
	}

	private final class MyCursorListener implements ICursorListener
	{
		private final SyncEntry listenerEntry;

		public MyCursorListener(SyncEntry listenerEntry)
		{
			this.listenerEntry = listenerEntry;
		}

		public void cursorValueChanged(String cursorID, double cursorXValue, double cursorYValue)
		{
			if (!cursorID.equals(this.listenerEntry.cursorID))
				return;

			for (SyncEntry entry : relatedGraphs)
			{
				if (entry == listenerEntry)
					continue;
				Cursor cursor = entry.surface.getCursor(entry.cursorID);
				// Check if the cursor is different.
				// This should prevent the feedback-loop where A and B notify each other continuously.
				final boolean xDifferent = cursor.getXValue() != cursorXValue;
				final boolean yDifferent = cursor.getYValue() != cursorYValue;
				switch (syncWhat)
				{
				case X:
					if (xDifferent)
					{
						cursor.setXValue(cursorXValue);
					}
					break;
				case Y:
					if (yDifferent)
					{
						cursor.setYValue(cursorYValue);
					}
					break;
				case BOTH:
					if (xDifferent || yDifferent)
					{
						cursor.setValue(cursorXValue, cursorYValue);
					}
					break;
				default: throw new IllegalStateException(""+syncWhat);
				}
			}
		}
	}

}
