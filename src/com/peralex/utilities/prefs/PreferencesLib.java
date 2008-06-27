package com.peralex.utilities.prefs;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.util.Enumeration;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.TableColumn;

import com.peralex.utilities.ui.SwingLib;

/**
 * 
 * @author Noel Grandin
 */
public class PreferencesLib
{

	/** not meant to be instantiated */
	private PreferencesLib()
	{
	}

	/**
	 * Restore any previous stored setting, and configure a save listener
	 * 
	 * @param defaultDimension may be null
	 */
	public static void configure(final IPreferencesNode node, final String prefName,
			final JFrame frame, Dimension defaultDimension)
	{
		addSavePreferencesListener(frame, new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				storeFramePrefs(node, prefName, frame);
			}
		});

		if (!loadFramePrefs(node, prefName, frame))
		{
			if (defaultDimension != null)
			{
				frame.setSize(defaultDimension.width, defaultDimension.height);
			}
		}
	}

	/**
	 * Restore any previous stored setting, and configure a save listener
	 */
	public static void configure(IPreferencesNode node, String tableName, JTable table)
	{
		loadJTablePreferences(node, tableName, table);
		addSavePreferencesListener(node, tableName, table);
	}

	/**
	 * Restore any previous stored setting, and configure a save listener
	 */
	public static void configure(final IPreferencesNode node, final String prefName,
			final JTabbedPane tabbedPane)
	{
		loadTabbedPanePrefs(node, prefName, tabbedPane);
		addSavePreferencesListener(tabbedPane, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				storeTabbedPanePrefs(node, prefName, tabbedPane);
			}
		});
	}

	/**
	 * Restore any previous stored setting, and configure a save listener
	 * 
	 * @param defaultDimension may be null
	 */
	public static void configure(final IPreferencesNode node, final String prefName,
			final Window window, Dimension defaultDimension)
	{
		addSavePreferencesListener(window, new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				storeWindowPrefs(node, prefName, window);
			}
		});

		if (!loadWindowPrefs(node, prefName, window))
		{
			if (defaultDimension != null)
			{
				window.setSize(defaultDimension.width, defaultDimension.height);
			}
		}
	}

	public static void save(IPreferencesNode node, String prefName, JTextField textfield)
	{
		node.put(prefName, textfield.getText());
	}

	public static void save(IPreferencesNode node, String prefName, JCheckBox checkbox)
	{
		node.put(prefName, checkbox.isSelected());
	}

	public static void load(IPreferencesNode node, String prefName, JCheckBox checkbox,
			boolean defaultVal)
	{
		checkbox.setSelected(node.getBoolean(prefName, defaultVal));
	}

	public static void load(IPreferencesNode node, String prefName, JTextField textfield,
			String defaultVal)
	{
		textfield.setText(node.get(prefName, defaultVal));
	}

	/**
	 * configures a JComboBox by item name ( ie. a combobox that stores strings in it's item list)
	 */
	public static void loadByItemName(IPreferencesNode node, String prefName, JComboBox combobox,
			String defaultVal)
	{
		combobox.setSelectedItem(node.get(prefName, defaultVal));
	}

	/**
	 * configures a JComboBox by index
	 */
	public static void loadByItemIdx(IPreferencesNode node, String prefName, JComboBox combobox,
			int defaultIdx)
	{
		combobox.setSelectedIndex(node.getInt(prefName, defaultIdx));
	}

	private static void storeWindowPrefs(IPreferencesNode node, String prefName, Window window)
	{
		final Rectangle rect = window.getBounds();
		node.put(prefName + "_window_x", rect.x);
		node.put(prefName + "_window_y", rect.y);
		node.put(prefName + "_window_width", rect.width);
		node.put(prefName + "_window_height", rect.height);
	}

	private static boolean loadWindowPrefs(IPreferencesNode node, String prefName, Window window)
	{
		final int x = node.getInt(prefName + "_window_x", -1);
		if (x == -1)
		{
			return false;
		}
		final int y = node.getInt(prefName + "_window_y", 0);
		final int width = node.getInt(prefName + "_window_width", 0);
		final int height = node.getInt(prefName + "_window_height", 0);

		window.setBounds(new Rectangle(x, y, width, height));
		return true;
	}

	private static void storeFramePrefs(IPreferencesNode node, String prefName, Frame frame)
	{
		final Rectangle rect = frame.getBounds();
		node.put(prefName + "_window_x", rect.x);
		node.put(prefName + "_window_y", rect.y);
		node.put(prefName + "_window_width", rect.width);
		node.put(prefName + "_window_height", rect.height);
		node.put(prefName + "_window_maximized", (frame.getExtendedState() == Frame.MAXIMIZED_BOTH));
	}

	private static boolean loadFramePrefs(IPreferencesNode node, String prefName, Frame window)
	{
		final int x = node.getInt(prefName + "_window_x", -1);
		if (x == -1)
		{
			return false;
		}
		final int y = node.getInt(prefName + "_window_y", 0);
		final int width = node.getInt(prefName + "_window_width", 0);
		final int height = node.getInt(prefName + "_window_height", 0);
		final boolean maximized = node.getBoolean(prefName + "_window_maximized", false);

		window.setBounds(new Rectangle(x, y, width, height));
		if (maximized)
		{
			window.setExtendedState(Frame.MAXIMIZED_BOTH);
		}
		return true;
	}

	/**
	 * add the necessary listeners to save preferences for a JXTable's column preferences
	 */
	private static void addSavePreferencesListener(final IPreferencesNode node,
			final String tableName, final JTable table)
	{
		addSavePreferencesListener(table, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				storeJTablePrefs(node, tableName, table);
			}
		});
	}

	private static void storeJTablePrefs(IPreferencesNode node, final String tableName,
			final JTable table)
	{
		for (Enumeration<TableColumn> iter = table.getColumnModel().getColumns(); iter
				.hasMoreElements();)
		{
			final TableColumn column = iter.nextElement();
			final int modelIndex = column.getModelIndex();
			node.put(tableName + "_columnWidth" + modelIndex, column.getWidth());
		}
	}

	/**
	 * loads saved preferences for a JTable
	 */
	private static void loadJTablePreferences(IPreferencesNode node, String tableName,
			final JTable table)
	{
		for (Enumeration<TableColumn> iter = table.getColumnModel().getColumns(); iter
				.hasMoreElements();)
		{
			final TableColumn column = iter.nextElement();
			final int modelIndex = column.getModelIndex();
			final int width = node.getInt(tableName + "_columnWidth" + modelIndex, -1);
			if (width != -1)
			{
				column.setPreferredWidth(width);
			}
		}
	}

	private static final class SavePrefsAncestorListener implements AncestorListener
	{
		private final ActionListener realListener;

		public SavePrefsAncestorListener(ActionListener realListener)
		{
			this.realListener = realListener;
		}

		public void ancestorAdded(AncestorEvent event)
		{
		}

		public void ancestorMoved(AncestorEvent event)
		{
		}

		public void ancestorRemoved(AncestorEvent event)
		{
			realListener.actionPerformed(null);
		}
	}

	/**
	 * Use weak preferences. Since JFrame is a global object, attaching listeners to it can mean that some of my UI
	 * objects will "leak". So use a weak reference to break this leak.
	 */
	private static class SavePrefsWindowListener extends WindowAdapter
	{
		private final WeakReference<ActionListener> listenerRef;

		private final Frame src;

		public SavePrefsWindowListener(ActionListener listener, Frame src)
		{
			listenerRef = new WeakReference<ActionListener>(listener);
			this.src = src;
		}

		@Override
		public void windowClosing(WindowEvent e2)
		{
			final ActionListener listener = listenerRef.get();
			if (listener == null)
			{
				removeListener();
			}
			else
			{
				listener.actionPerformed(null);
			}
		}

		private void removeListener()
		{
			src.removeWindowListener(this);
		}
	}

	private static void loadTabbedPanePrefs(IPreferencesNode node, String prefName,
			JTabbedPane tabbedPane)
	{
		final int previousIdx = node.getInt(prefName, 0);
		if (previousIdx < tabbedPane.getTabCount())
		{
			tabbedPane.setSelectedIndex(previousIdx);
		}
	}

	private static void storeTabbedPanePrefs(IPreferencesNode node, String prefName,
			JTabbedPane tabbedPane)
	{
		node.put(prefName, tabbedPane.getSelectedIndex());
	}

	/**
	 * add the necessary listeners to save preferences
	 */
	public static void addSavePreferencesListener(final JComponent component,
			final ActionListener listener)
	{
		/*
		 * Sanity check to make sure that when I copy code I modify the save prefs calls.
		 */
		for (AncestorListener al : component.getAncestorListeners())
		{
			if (al != null && al.getClass() == SavePrefsAncestorListener.class)
			{
				throw new IllegalStateException("this component already has a SavePreferences listener");
			}
		}

		// update the preference store when we switch away from this panel
		component.addAncestorListener(new SavePrefsAncestorListener(listener));
		component.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentHidden(ComponentEvent e)
			{
				listener.actionPerformed(null);
			}
		});
		final Frame parent = SwingLib.findFrameParent(component);
		if (parent != null)
		{
			parent.addWindowListener(new SavePrefsWindowListener(listener, parent));
		}
		else
		{
			/*
			 * The first time this component is shown, add a close listener to the parent JFrame. I can't do this initially,
			 * because at the time addSavePreferencesListener is called, the component may not be added to a frame yet.
			 */
			component.addHierarchyListener(new HierarchyListener()
			{
				public void hierarchyChanged(HierarchyEvent e)
				{
					// find the JFrame parent
					final Frame parent2 = SwingLib.findFrameParent(component);

					if (parent2 != null)
					{
						parent2.addWindowListener(new SavePrefsWindowListener(listener, parent2));
						/* Remove myself so I don't add another WindowListener. */
						component.removeHierarchyListener(this);
					}
				}
			});
		}

	}

	/**
	 * add the necessary listeners to save preferences
	 */
	public static void addSavePreferencesListener(final java.awt.Window component,
			final ActionListener listener)
	{

		// update the preference store when we switch away from this panel
		component.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentHidden(ComponentEvent e)
			{
				listener.actionPerformed(null);
			}
		});
		component.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e2)
			{
				listener.actionPerformed(null);
			}
		});
	}

}
