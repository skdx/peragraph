package com.peralex.utilities.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;

/**
 * This can disable a set of components (and later restored). The status of all components in the set will be firstly
 * stored, so it can be restored properly.
 * 
 * The components can be anywhere (and even not visible), although it's recommended to use this on a logically-related
 * set of components to avoid surprises (e.g. if something in another tab panel B is disabled, and later the user can't
 * find the button in panel B to enable it.)
 * 
 * This class can also disable the child nodes - typically it's useful when you want to disable a JPanel and the
 * components inside.
 * 
 * Note (Noel). This class currently has a weakness in that if we add component A and it's children to the disabler, and
 * then later on add additional children to component A, the disabler will not know about the new children. This could
 * be fixed by doing the recursive search at enable/disable time. But then we'd have to maintain an extra Set of
 * components to ignore (see method
 * 
 * @author David Lee
 */
public class ComponentDisabler
{
	private final Set<Component> components = new HashSet<Component>();

	// This stores all components' enabled states
	private final Map<Component, Boolean> enabledStates = new HashMap<Component, Boolean>();

	// This stores all components' visibility
	private final Map<Component, Boolean> visibleStates = new HashMap<Component, Boolean>();

	// Some JComponents act like containers (like JPanel) while others don't
	// When encounter a container-like JComponent, look into it.
	private final List<Class<?>> containerJComponentClasses = new ArrayList<Class<?>>();

	// Some JComponent extends the container-like JComponent, but itself is not intended
	// to be a container-like JComponent
	private final Set<Class<?>> excludingJComponentClasses = new HashSet<Class<?>>();

	public ComponentDisabler()
	{
		// A list of known container-like classes.
		// Add to it if you find some objects' enable-status isn't restored properly.
		// A list of the known classes: Class[] classes = { JPanel.class, JScrollPane.class, JDesktopPane.class,
		// JSplitPane.class, JTabbedPane.class, JToolBar.class, JLayeredPane.class, JInternalFrame.class, JRootPane.class };
		//
		// But only the currently used ones are listed
		final Class<?>[] containerClasses = { JPanel.class, JScrollPane.class, JSplitPane.class,
				JTabbedPane.class, JViewport.class, JFileChooser.class, JToolBar.class };

		this.containerJComponentClasses.addAll(Arrays.asList(containerClasses));

		final Class<?>[] excludingClasses = { JLabel.class /* no need to bother */};
		this.excludingJComponentClasses.addAll(Arrays.asList(excludingClasses));
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public ComponentDisabler(boolean includeChildren, JComponent... components)
	{
		this();
		addAll(includeChildren, components);
	}

	public ComponentDisabler(JComponent... components)
	{
		this();
		add(components);
	}

	/**
	 * Some JComponent can embed other components (e.g. JPanel, JScrollPane) To look into the components it contains, add
	 * the class of the outer component (e.g. JPanel) to this list.
	 * 
	 * @deprecated rather call addExcludedClass()/removeExcludedClass()
	 */
	@Deprecated
	public List<Class<?>> getListOfContainerJComponentClasses()
	{
		return this.containerJComponentClasses;
	}

	/**
	 * Some JComponent can embed other components (e.g. JPanel, JScrollPane) To look into the components it contains, add
	 * the class of the outer component (e.g. JPanel) to this list.
	 */
	public void addContainerClass(Class<?> clazz)
	{
		this.containerJComponentClasses.add(clazz);
	}

	public void removeContainerClass(Class<?> clazz)
	{
		this.containerJComponentClasses.add(clazz);
	}

	/**
	 * Get the list of the classes of excluded JComponents. You can add or remove new classes.
	 * 
	 * @deprecated rather call addExcludedClass()/removeExcludedClass()
	 */
	@Deprecated
	public Set<Class<?>> getListOfExcludedJComponentClasses()
	{
		return this.excludingJComponentClasses;
	}

	public void addExcludedClass(Class<?> clazz)
	{
		this.excludingJComponentClasses.add(clazz);
	}

	public void removeExcludedClass(Class<?> clazz)
	{
		this.excludingJComponentClasses.add(clazz);
	}

	/**
	 * A convenience function to add all of parent component's children.
	 * 
	 * @deprecated rather call addChildren()
	 */
	@Deprecated
	public void addOnlyChildren(JComponent parent)
	{
		this.internalAdd(parent, true, false);
		this.remove(parent);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void addAll(boolean includeChildren, JComponent... componentList)
	{
		for (JComponent component : componentList)
		{
			internalAdd(component, includeChildren, true);
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void add(JComponent c, boolean includeChildren)
	{
		internalAdd(c, includeChildren, true);
	}

	/**
	 * add components and all their children
	 */
	public void add(JComponent... componentList)
	{
		for (JComponent component : componentList)
		{
			internalAdd(component, true, true);
		}
	}

	/**
	 * add a component and all it's children
	 */
	public void add(JComponent c)
	{
		internalAdd(c, true, true);
	}

	/**
	 * Add all of parent component's children.
	 */
	public void addChildren(JComponent parent)
	{
		this.internalAdd(parent, true, false);
		this.remove(parent);
	}

	private void internalAdd(JComponent c, boolean includeChildren, boolean checkSetEnabled)
	{
		components.add(c);

		if (!includeChildren || excludingJComponentClasses.contains(c.getClass())
				|| !isContainerClass(c))
		{
			return;
		}

		/*
		 * If the component overrides setEnabled(), we can assume it manages it's own enabled state, including that of its
		 * children.
		 */
		if (checkSetEnabled && overridesSetEnabled(c.getClass()))
		{
			return;
		}

		for (Component child : c.getComponents())
		{
			if (child instanceof JComponent)
			{
				internalAdd((JComponent) child, true, true);
			}
		}
	}

	private static boolean overridesSetEnabled(Class<?> clazz)
	{
		try
		{
			clazz.getDeclaredMethod("setEnabled", new Class[] { Boolean.TYPE });
		}
		catch (NoSuchMethodException ex)
		{
			return false;
		}
		return true;
	}

	private boolean isContainerClass(JComponent c)
	{
		for (Class<?> containerClass : containerJComponentClasses)
		{
			if (containerClass.isInstance(c))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove some components from the component set
	 */
	public void remove(JComponent... componentList)
	{
		for (JComponent component : componentList)
		{
			// Firstly, restore the visual status.
			final Boolean enabled = enabledStates.get(component);
			final Boolean visible = visibleStates.get(component);
			if (enabled != null)
			{
				component.setEnabled(enabled.booleanValue());
			}

			if (visible != null)
			{
				component.setVisible(visible.booleanValue());
			}

			// Then remove it from the controlled component set
			components.remove(component);
		}
	}

	public void setEnabled(final boolean enabled)
	{
		synchronized (enabledStates)
		{
			if (enabled)
			{
				// If nothing to restore, return now.
				if (enabledStates.isEmpty())
				{
					return;
				}
	
				for (Component comp : enabledStates.keySet())
				{
					final boolean booleanValue = enabledStates.get(comp).booleanValue();
					comp.setEnabled(booleanValue);
				}
	
				enabledStates.clear();
			}
			else
			{
				// If the set of components has been disabled (ie, the 'states' set has stored info),
				// do not store/change it. Otherwise the original states can't be restored
				if (!enabledStates.isEmpty())
				{
					return;
				}
	
				for (Component comp : components)
				{
					final boolean booleanValue = comp.isEnabled();
					enabledStates.put(comp, Boolean.valueOf(booleanValue));
					comp.setEnabled(false);
				}
			}
		}
	}

	public void setVisible(final boolean visible)
	{
		synchronized (visibleStates)
		{
			if (visible)
			{
				// If nothing to restore, return now.
				if (visibleStates.isEmpty())
				{
					return;
				}
	
				for (Component comp : visibleStates.keySet())
				{
					comp.setVisible(visibleStates.get(comp).booleanValue());
				}
	
				visibleStates.clear();
			}
			else
			{
				// If the set of components has been disabled (ie, the 'states' set has stored info),
				// do not store/change it. Otherwise the original states can't be restored
				if (! visibleStates.isEmpty())
				{
					return;
				}
	
				for (Component comp : components)
				{
					visibleStates.put(comp, Boolean.valueOf(comp.isVisible()));
					comp.setVisible(false);
				}
			}
		}
	
		revalidateAndRepaint();
	}

	/** debug method */
	public void printEnabledStatus()
	{
		int i = 0;
		for (Component comp : enabledStates.keySet())
		{
			final boolean booleanValue = enabledStates.get(comp).booleanValue();

			String name = comp.getName();
			System.out.println(String.format("%d: %d %s is %b", i++, comp.hashCode(), name != null ? name
					: "", booleanValue));
		}
	}

	// When some components' visibility are changed, the layout of components may change.
	private void revalidateAndRepaint()
	{
		for (Component comp : components)
		{
			if (comp instanceof JComponent)
			{
				((JComponent) comp).revalidate();
			}
			comp.repaint();
		}
	}
}
