package com.peralex.utilities.ui.images;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.AbstractButton;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Loads/resizes/caches icons.
 * 
 * @author Noel Grandin
 */
public final class IconManager
{

	private IconManager()
	{
	}

	private static BufferedImage getImage(String name)
	{
		try
		{
			return read2(name, IconManager.class.getResourceAsStream(name));
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/**
	 * bypass ImageIO implementation because I don't want it to do caching, since I do my own caching in this class.
	 */
	private static BufferedImage read2(String name, InputStream input) throws IOException
	{
		// find the correct reader by suffix, so we avoid classloading large image plugins we don't need (.e.g. JPEG)
		final String suffix = name.substring(name.lastIndexOf('.')+1);
		final Iterator<ImageReader> iterReader = ImageIO.getImageReadersBySuffix(suffix);
		if (iterReader.hasNext()) {
			final ImageReader reader = iterReader.next();
			// read using a memory-cache input stream, and set seek-forward-only, to minimize mem usage.
			reader.setInput(new MemoryCacheImageInputStream(input), true/*seekforwardOnly*/);
			return reader.read(0);
		} else {
			// try the default implementation as a back-up option
			return ImageIO.read(input);
		}
	}
	
	private static ImageIcon getIcon(String name)
	{
		return new ImageIcon(getImage(name));
	}

	/**
	 * Set an icon on the label
	 */
	public static void setIconOn(JLabel label, String name)
	{
		label.setIcon(getIcon(name));
	}

	/**
	 * Set an icon on the button, resized to fit the height of the font of the component.
	 */
	public static void setSizedIconOn(AbstractButton button, String name)
	{
		button.setIcon(getSizedFor(name, button));
	}

	/**
	 * Set an icon on the button, resized to fit a fraction of the height of the font of the component.
	 * 
	 * @param proportional a number between 0 and 1, indicating what proportion of the font size should be used for sizing
	 *          the icon.
	 */
	public static void setSizedIconOn(AbstractButton button, String name, float proportional)
	{
		final Icon icon = getSizedFor(name, button, proportional);
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
	public static ImageIcon getSizedFor(String name, Component component, float proportional)
	{
		if (proportional <= 0)
		{
			throw new IllegalArgumentException("" + proportional);
		}
		return getSizedHeight(name, component.getFontMetrics(component.getFont()), proportional);
	}

	/**
	 * Return an icon resized to fit a fraction of the height of the font of the component, so that it looks good on
	 * buttons and labels.
	 * 
	 * e.g. cIconManager.getSizedFor(MyClass.class, "icon.gif", 0.5f)
	 * 
	 * @param componentClass Class of the class where the resource lives
	 * @param resourceName filename of the resource
	 */
	public static ImageIcon getSizedFor(Class<?> componentClass, String resourceName, Component component, float proportional)
	{
		final String name = '/' + componentClass.getPackage().getName().replace('.', '/') + '/' + resourceName;
		return getSizedFor(name, component, proportional);
	}

	/**
	 * Return an icon resized to fit a fraction of the height of the font of the component, so that it looks good on
	 * buttons and labels.
	 * 
	 * e.g. cIconManager.getSizedFor(MyClass.class, "icon.gif")
	 * 
	 * @param componentClass class of the package where the resource lives
	 * @param resourceName filename of the resource
	 */
	public static ImageIcon getSizedFor(Class<?> componentClass, String resourceName, Component component)
	{
		String s = componentClass.getName();
		s = s.substring(0, s.lastIndexOf('.'));
		final String name = '/' + s.replace('.', '/') + '/' + resourceName;
		return getSizedFor(name, component);
	}

	/**
	 * Return an icon resized to fit the height of the font of the component, so that it looks good on buttons and labels.
	 */
	public static ImageIcon getSizedFor(String name, Component component)
	{
		// 0.8 appears to give a good result such that the icon is roughly the same height as the text
		return getSizedHeight(name, component.getFontMetrics(component.getFont()), 0.8f);
	}

	/**
	 * weakly cache the generated icons so that (a) we don't cache stuff we don't need (b) if somebody accidentally uses
	 * this class to load a large image, it will get GC'ed when we need memory.
	 */
	private static final Map<String, ImageIcon> cache = new WeakHashMap<String, ImageIcon>();

	private static ImageIcon getSizedHeight(String name, FontMetrics fontMetrics, float proportional)
	{
		final int height = Math.round((fontMetrics.getAscent() + fontMetrics.getDescent()) * proportional);
		final String compositeName = name + height;
		ImageIcon cachedIcon = cache.get(compositeName);
		if (cachedIcon != null)
		{
			return cachedIcon;
		}
		final BufferedImage loadedImage = getImage(name);
		ImageIcon scaledIcon = scale(loadedImage, height);
		cache.put(compositeName, scaledIcon);
		return scaledIcon;
	}
	
	private static ImageIcon scale(BufferedImage loadedImage, int desiredHeight)
	{
		final int desiredWidth = Math.round(loadedImage.getWidth() * (((float) desiredHeight) / loadedImage.getHeight()));
		final BufferedImage scaledImage = new BufferedImage(desiredWidth, desiredHeight, BufferedImage.TYPE_INT_ARGB_PRE);
		final Graphics2D g = scaledImage.createGraphics();
		// turn decent anti-aliasing on
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(loadedImage, 0, 0, desiredWidth, desiredHeight, null);
		return new ImageIcon(scaledImage);
	}

	/**
	 * -------------------------------------------------------------------------------------------
	 * The following methods using IIconFactory classes to create icons.
	 * -------------------------------------------------------------------------------------------
	 */
	
	/**
	 * Set an icon on the label
	 */
	public static void setIconOn(JLabel label, IIconFactory factory)
	{
		FactoryIconManager.setIconOn(label, factory);
	}

	/**
	 * Set an icon on the button, resized to fit the height of the font of the component.
	 */
	public static void setSizedIconOn(AbstractButton button, IIconFactory factory)
	{
		FactoryIconManager.setSizedIconOn(button, factory);
	}

	/**
	 * Set an icon on the button, resized to fit a fraction of the height of the font of the component.
	 * 
	 * @param proportional a number between 0 and 1, indicating what proportion of the font size should be used for sizing
	 *          the icon.
	 */
	public static void setSizedIconOn(AbstractButton button, IIconFactory factory, float proportional)
	{
		FactoryIconManager.setSizedIconOn(button, factory, proportional);
	}

	/**
	 * Return an icon resized to fit a fraction of the height of the font of the component, so that it looks good on
	 * buttons and labels.
	 * 
	 * @param proportional a number between 0 and 1, indicating what proportion of the font size should be used for sizing
	 *          the icon.
	 */
	public static Icon getSizedFor(IIconFactory factory, Component component, float proportional)
	{
		return FactoryIconManager.getSizedFor(factory, component, proportional);
	}

	/**
	 * Return an icon resized to fit a fraction of the height of the font of the component, so that it looks good on
	 * buttons and labels.
	 * 
	 * e.g. cIconManager.getSizedFor(MyClass.class, "icon.gif", 0.5f)
	 * 
	 * @param componentClass Class of the class where the resource lives
	 */
	public static Icon getSizedFor(Class<?> componentClass, IIconFactory factory, Component component, float proportional)
	{
		return FactoryIconManager.getSizedFor(componentClass, factory, component, proportional);
	}
	/**
	 * Return an icon resized to fit the height of the font of the component, so that it looks good on buttons and labels.
	 */
	public static Icon getSizedFor(IIconFactory factory, Component component)
	{
		return FactoryIconManager.getSizedFor(factory, component);
	}
}
