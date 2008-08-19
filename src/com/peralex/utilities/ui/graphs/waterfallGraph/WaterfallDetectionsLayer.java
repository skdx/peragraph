package com.peralex.utilities.ui.graphs.waterfallGraph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.Arrays;

import com.peralex.utilities.ui.ScrollableBufferedImage;

/**
 * When drawing the detections on a waterfall, we do not want any of them to get lost
 * when we resize the waterfall. 
 * They sometimes go missing because the rescaling might drop that pixel.
 * 
 * So I wrap a ScrollableBufferedImage and do some work here to make sure that
 * every detection is drawn to a pixel regardless of the resizing.
 * 
 * @author Noel Grandin
 */
class WaterfallDetectionsLayer
{
	// the image we actually draw to the screen
	private final ScrollableBufferedImage displayedImage = new ScrollableBufferedImage();
	// the reference image, at the same width as the data we get from the server
	private final ScrollableBufferedImage referenceImage = new ScrollableBufferedImage();
	
	public WaterfallDetectionsLayer()
	{
		// make the background be transparent
		displayedImage.setBackgroundColor(new Color(0, 0, 0, 0));
		referenceImage.setBackgroundColor(new Color(0, 0, 0, 0));
	}
	
	/**
	 * Draw the image to a graphics context.
	 */
	public void drawImageTo(Graphics2D g, ImageObserver observer, boolean isZoomed,
			double min, double max, double minZoomLimit, double maxZoomLimit,
			int displayWidth)
	{
		if (!displayedImage.isImageCreated()) {
			return;
		}
		
		final AffineTransform prevTransform = g.getTransform();
		
		if (displayWidth != displayedImage.getWidth())
		{
			resizeDisplayWidth(displayWidth);
		}
		
		if (isZoomed)
		{
			/* Notes
			 * (1) We are both transforming this image such that we take a section of the data and scale
			 *    it to fit the whole available display width.
			 * (2) We are only transforming along the x-axis
			 */
			
			final double totalXRange = maxZoomLimit - minZoomLimit;
			final float imageWidth_px = displayedImage.getWidth();
			// note: we adjust by half a bin width so that the points on the FFT line up nicely with the
			// centre of bins on the waterfall when zooming.
			final float binWidth_px = displayWidth / imageWidth_px;

			// move the image such the zoomed portion ends up with it's left edge on the left of the screen.
			final double translate_px = - ((min - minZoomLimit) / totalXRange) * imageWidth_px
														- (binWidth_px/2);

			/* Scale zoomed portion to fit available space */
			final double zoomedWidthBeforeScaling_px = (max - min) / totalXRange * imageWidth_px;
			final double scaling = displayWidth / zoomedWidthBeforeScaling_px;
			
			/* The Graphics2D class has the rule "last-specified-first-applied".
			 * So in order to translate and then scale, we need to first call scale() and then translate(). */
      g.scale(scaling, 1.0);
      g.translate(translate_px, 0);
    }

		displayedImage.drawImageTo(g, observer);
		
		// restore previous transform
		g.setTransform(prevTransform);
	}

	public synchronized void setColor(Color color)
	{
		displayedImage.setColor(color);
		referenceImage.setColor(color);
	}

	/**
	 * these 2 calculations are a little tricky - we're scaling a pixel from one image to another.
	 * Under these conditions we have to take into account that when you draw a pixel at position x,
	 * you are actually painting the area on the screen which is [x-0.5,x+0.5].
	 */
	private int transformXStart(int x)
	{
		return (int) Math.floor((x-0.5)/referenceImage.getWidth() * displayedImage.getWidth());
	}
	private int transformXEnd(int x)
	{
		return (int) Math.ceil((x+0.5)/referenceImage.getWidth() * displayedImage.getWidth());
	}
	
	/**
	 * Draw a pixel at co-ordinate X, relative to the bottom of the image
	 * 
	 * @param x
	 * @param relativeY negative value, 0 is bottom of image, -1 is one line up, etc.
	 */
	public synchronized void drawPixelBottomRelative(int x, int relativeY)
	{
		referenceImage.drawPixelBottomRelative(x, relativeY);
		final int startX = (int) Math.floor(x/(float)referenceImage.getWidth() * displayedImage.getWidth());
		final int endX = (int) Math.ceil(x/(float)referenceImage.getWidth() * displayedImage.getWidth());
		displayedImage.drawLineBottomRelative(startX, endX, relativeY);
	}

	/**
	 * Draw a pixel at co-ordinate X, at the bottom of the image
	 */
	public synchronized void drawPixelBottom(int x)
	{
		referenceImage.drawPixelBottom(x);
		final int startX = transformXStart(x);
		final int endX = transformXEnd(x);
		displayedImage.drawLineBottom(startX, endX);
	}

	/**
	 * Draw a line at from co-ordinate xStart to co-ordinate xEnd, at the bottom of the image
	 */
	public synchronized void drawLineBottom(int xStart, int xEnd)
	{
		referenceImage.drawLineBottom(xStart, xEnd);
		final int startX = transformXStart(xStart);
		final int endX = transformXEnd(xEnd);
		displayedImage.drawLineBottom(startX, endX);
	}
	
	/**
	 * This method will cause the display to clear.
	 */
	public synchronized void clearImage()
	{
		referenceImage.clearImage();
		displayedImage.clearImage();
	}

	public synchronized void setDisplaySizeAndClear(int width, int height)
	{
		displayedImage.setSizeAndClear(width, height);
		// if we haven't received any data yet, the reference image doesn't have a width
		final int referenceWidth = referenceImage.getWidth()==-1 ? width : referenceImage.getWidth();
		referenceImage.setSizeAndClear(referenceWidth, height);
	}
	
	/**
	 * resize the existing image, using the old image to pre-populate the new image
	 */
	public synchronized void resizeHeight(int height)
	{
		referenceImage.resizeExisting(referenceImage.getWidth(), height);
		displayedImage.resizeExisting(displayedImage.getWidth(), height);
	}
	
	public synchronized void resizeDataWidth(int width)
	{
		referenceImage.setSizeAndClear(width, referenceImage.getHeight());
		displayedImage.clearImage();
	}
	
	public synchronized void resizeDisplayWidth(int width)
	{
		// clear existing displayed image
		displayedImage.setSizeAndClear(width, displayedImage.getHeight());
		
		final int [] displayData = new int [displayedImage.getWidth()];
		// now recreate the displayed image
		for (int i=0; i<referenceImage.getHeight(); i++) {
			int [] referenceData = referenceImage.getImageLine(i);
			Arrays.fill(displayData, 0);
			// now loop over the __destination__. That way we don't leave any holes if
			// the sizes are very different.
			for (int j=0; j<displayData.length; j++) {
				// calculate co-ordinate in reference image
				int refX = Math.round(j/(float)displayedImage.getWidth() * referenceImage.getWidth());
				// clamp to array length
				refX = Math.min(refX, referenceData.length-1);
				displayData[j] = referenceData[refX];
			}
			displayedImage.scrollUp(displayData);
		}
	}
	
	/**
	 * Scroll up, filling in the line with background color
	 */
	public synchronized void scrollUp()
	{
		referenceImage.scrollUp();
		displayedImage.scrollUp();
	}

	/**
	 * Scroll down, filling in the line with background color
	 */
	public synchronized void scrollDown()
	{
		referenceImage.scrollDown();
		displayedImage.scrollDown();
	}

	public boolean isImageCreated()
	{
		return referenceImage.isImageCreated();
	}
	
	public synchronized int getDisplayWidth()
	{
		return displayedImage.getWidth();
	}

	public synchronized int getDataWidth()
	{
		return referenceImage.getWidth();
	}
	
	public synchronized int getHeight()
	{
		return displayedImage.getHeight();
	}
}
