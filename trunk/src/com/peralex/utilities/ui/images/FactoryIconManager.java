package com.peralex.utilities.ui.images;

import java.awt.Component;
import java.awt.FontMetrics;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.AbstractButton;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Some icons are better drawn using Graphics2D code.
 * 
 * @author Noel Grandin
 */
class FactoryIconManager
{

	private FactoryIconManager()
	{
	}

	/**
	 * Set an icon on the label
	 */
	public static void setIconOn(JLabel label, IIconFactory factory)
	{
		label.setIcon(getSizedFor(factory, label));
	}

	/**
	 * Set an icon on the button, resized to fit the height of the font of the component.
	 */
	public static void setSizedIconOn(AbstractButton button, IIconFactory factory)
	{
		button.setIcon(getSizedFor(factory, button));
	}

	/**
	 * Set an icon on the button, resized to fit a fraction of the height of the font of the component.
	 * 
	 * @param proportional a number between 0 and 1, indicating what proportion of the font size should be used for sizing
	 *          the icon.
	 */
	public static void setSizedIconOn(AbstractButton button, IIconFactory factory, float proportional)
	{
		Icon icon = getSizedFor(factory, button, proportional);
		button.setIcon(icon);
		button.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)icon).getImage())));
	}

	/**
	 * Return an icon resized to fit a fraction of the height of the font of the component, so that it looks good on
	 * buttons and labels.
	 * 
	 * @param proportional a number between 0 and 1, indicating what proportion of the font size should be used for sizing
	 *          the icon.
	 */
	public static ImageIcon getSizedFor(IIconFactory factory, Component component, float proportional)
	{
		if (proportional <= 0)
		{
			throw new IllegalArgumentException("" + proportional);
		}
		return getSizedHeight(factory, component.getFontMetrics(component.getFont()), proportional);
	}

	/**
	 * Return an icon resized to fit a fraction of the height of the font of the component, so that it looks good on
	 * buttons and labels.
	 * 
	 * e.g. cIconManager.getSizedFor(MyClass.class, "icon.gif", 0.5f)
	 * 
	 * @param componentClass Class of the class where the resource lives
	 */
	public static ImageIcon getSizedFor(Class<?> componentClass, IIconFactory factory, Component component, float proportional)
	{
		return getSizedFor(factory, component, proportional);
	}

	/**
	 * Return an icon resized to fit the height of the font of the component, so that it looks good on buttons and labels.
	 */
	public static ImageIcon getSizedFor(IIconFactory factory, Component component)
	{
		// 0.8 appears to give a good result such that the icon is roughly the same height as the text
		return getSizedHeight(factory, component.getFontMetrics(component.getFont()), 0.8f);
	}

	/**
	 * weakly cache the generated icons so that (a) we don't cache stuff we don't need (b) if somebody accidentally uses
	 * this class to load a large image, it will get GC'ed when we need memory.
	 */
	private static final Map<MapKey, ImageIcon> cache = new WeakHashMap<MapKey, ImageIcon>();

	private static ImageIcon getSizedHeight(IIconFactory factory, FontMetrics fontMetrics, float proportional)
	{
		final int height = Math.round((fontMetrics.getAscent() + fontMetrics.getDescent()) * proportional);
		final MapKey compositeName = new MapKey(factory, height);
		ImageIcon cachedIcon = cache.get(compositeName);
		if (cachedIcon != null)
		{
			return cachedIcon;
		}
		final ImageIcon scaledIcon = factory.createIcon(height);
		cache.put(compositeName, scaledIcon);
		return scaledIcon;
	}

	private static class MapKey {
		public final IIconFactory factory;
		public final int height;
		public MapKey(IIconFactory factory, int height) {
			this.factory = factory;
			this.height = height;
		}
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((factory == null) ? 0 : factory.hashCode());
			result = prime * result + height;
			return result;
		}
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final MapKey other = (MapKey) obj;
			if (factory == null)
			{
				if (other.factory != null)
					return false;
			}
			else if (!factory.equals(other.factory))
				return false;
			if (height != other.height)
				return false;
			return true;
		}
		
	}
}
