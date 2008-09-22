package com.peralex.utilities.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * This implements a "sliding window" trick to enable us to scroll a BufferedImage without doing memory copies, which
 * can get CPU intensive with large images and high framerates.
 * 
 * We treat the BufferedImage as 2 sections, and we write from top to bottom of the image, wrapping to top when we hit
 * bottom.
 * 
 * Typical usage of this class is something like:
 * <code>
 *   obj.scrollUp();
 *   obj.drawPixelBottom(0);
 *   ...
 *   obj.drawImageTo(graphics2D);
 * </code>
 *  
 * @author Noel Grandin
 */
public class ScrollableBufferedImage
{
	private BufferedImage oImage;

	/** this is the index of the next line we are going to draw to */
	private int iImageIndex = 0;

	private Graphics2D oGraphics2D;

	private Color oBackgroundColor = Color.BLACK;

	public ScrollableBufferedImage()
	{
	}

	/**
	 * Draw the image to a graphics context.
	 */
	public synchronized void drawImageTo(Graphics g, ImageObserver observer)
	{
		if (oImage==null)
		{
			// under JDK1.4, paint() might occur before the resize event that sets up the image
			return;
		}
		if (iImageIndex == 0)
		{
			g.drawImage(oImage, 0, 0, observer);
		}
		else
		{
			final int height1 = oImage.getHeight() - iImageIndex;
			final int width = oImage.getWidth();
			g.drawImage(oImage, 0, 0, width, height1, 0, iImageIndex, width, iImageIndex + height1, observer);
			g.drawImage(oImage, 0, height1, width, oImage.getHeight(), 0, 0, width, iImageIndex, observer);
		}
	}

	/**
	 * Draw the image to a graphics context, scaling it horizontally.
	 * We don't scale in the vertical axis because that sometimes results in very weird results when we
	 * go from a very small image to a large image.
	 */
	private synchronized void drawImageTo(Graphics g, int width, int height, ImageObserver observer)
	{
		if (oImage==null)
		{
			// often, will occur before the componentResized() event that sets up the image
			return;
		}
		
		/* Set the x, y coordinates such that the old image is copied to line up with the BOTTOM edge
		 * of the new image. */
		final int x = 0;
		final int y = height - oImage.getHeight();
		
		if (iImageIndex == 0)
		{
			g.drawImage(oImage, x, y, width, oImage.getHeight(), observer);
		}
		else
		{
			final int srcHeight1 = oImage.getHeight() - iImageIndex; // height of first piece
			final int srcWidth = oImage.getWidth();
			final int destWidth = width;
			g.drawImage(oImage, 
					x+0, y+0, // coordinate of the first corner of the destination rectangle 
					x+destWidth, y+srcHeight1, // coordinate of the second corner of the destination rectangle 
					0, iImageIndex, // coordinate of the first corner of the source rectangle
					srcWidth, iImageIndex + srcHeight1, // coordinate of the second corner of the source rectangle 
					observer);
			g.drawImage(oImage, 
					x+0, y+srcHeight1,  // coordinate of the first corner of the destination rectangle
					x+destWidth, y+oImage.getHeight(),  // coordinate of the second corner of the destination rectangle
					0, 0, // coordinate of the first corner of the source rectangle
					srcWidth, iImageIndex, // coordinate of the second corner of the source rectangle
					observer);
		}
	}

	public synchronized void setColor(Color color)
	{
		oGraphics2D.setColor(color);
	}

	/**
	 * Draw a pixel at co-ordinate X, relative to the bottom of the image
	 * 
	 * @param x x-coordinate
	 * @param relativeY negative value, 0 is bottom of image, -1 is one line up, etc.
	 */
	public synchronized void drawPixelBottomRelative(int x, int relativeY)
	{
		int y = iImageIndex - 1 - relativeY;
		if (y<0) {
			y += oImage.getHeight();
		}
		oGraphics2D.drawLine(x, y, x, y);
	}

	/**
	 * Draw a pixel at co-ordinate X, at the bottom of the image
	 */
	public synchronized void drawPixelBottom(int x)
	{
		int y = iImageIndex - 1;
		if (y<0) {
			y += oImage.getHeight();
		}
		oGraphics2D.drawLine(x, y, x, y);
	}

	/**
	 * Draw a line at from co-ordinate xStart to co-ordinate xEnd, at the bottom of the image
	 */
	public synchronized void drawLineBottom(int xStart, int xEnd)
	{
		int y = iImageIndex - 1;
		if (y<0) {
			y += oImage.getHeight();
		}
		oGraphics2D.drawLine(xStart, y, xEnd, y);
	}
	
	/**
	 * Draw a line at from co-ordinate xStart to co-ordinate xEnd, relative to the bottom of the image
	 * 
	 * @param relativeY negative value, 0 is bottom of image, -1 is one line up, etc.
	 */
	public synchronized void drawLineBottomRelative(int xStart, int xEnd, int relativeY)
	{
		int y = iImageIndex - 1 - relativeY;
		if (y<0) {
			y += oImage.getHeight();
		}
		oGraphics2D.drawLine(xStart, y, xEnd, y);
	}
	
	public void drawRGB_Bottom(int[] aiImageData)
	{
		drawRGB_Bottom(aiImageData, aiImageData.length);
	}
	
	private int whingeAboutDataLength = 0;
	
	/**
	 * Draw a line of RGB data at the bottom of the image
	 * @param len the length of data in the array to use
	 */
	public synchronized void drawRGB_Bottom(int[] aiImageData, int len)
	{
		if (oImage==null)
		{
			return;
		}
		int y = iImageIndex - 1;
		if (y<0) {
			y += oImage.getHeight();
		}
		if (len!=oImage.getWidth())
		{
			// perform a very rough scaling - this shouldn't happen very often, so it's not a problem.
			final int [] newImageData = new int[oImage.getWidth()];
			final float scalingFactor = len / (float)oImage.getWidth();
			for (int i=0; i<newImageData.length; i++)
			{
				newImageData[i] = aiImageData[(int) (i * scalingFactor)];
			}

			// draw the scaled data
			oImage.getRaster().setDataElements(0, y, newImageData.length, 1, newImageData);
			
			// allow the server a little time before whinging - if we receive 20 of these then someone has
			// forgotten to program a call to the server to resample the data.
			whingeAboutDataLength++;
			if (whingeAboutDataLength>20)
			{
				new Throwable("aiImageData is length " + len + " while image is width " + oImage.getWidth()  
						+ ". Make sure to set the correct size in setSizeAndClear()/resizeExisting()").printStackTrace();
			}
		}
		else
		{
			whingeAboutDataLength = 0;
			oImage.getRaster().setDataElements(0, y, len, 1, aiImageData);
		}
  }
        
	/**
	 * This method will cause the display to clear.
	 */
	public synchronized void clearImage()
	{
		iImageIndex = 0;
		if (oGraphics2D!=null)
		{
			oGraphics2D.setColor(oBackgroundColor);
			oGraphics2D.fillRect(0, 0, oImage.getWidth(), oImage.getHeight());
		}
	}

	/**
	 * Note that the width here is not necessarily the component width - it's the width
	 * of the data that you're going to give me in the draw* methods.
	 */
	public synchronized void setSizeAndClear(int width, int height)
	{
		/* Under some conditions, components can get shrunk to the point that they have zero height.
		 * Rather than use complicated logic to track this, just clamp our height to 1. */
		if (height==0) {
			height = 1;
		}
		if (width==0) {
			width = 1;
		}
		
		// Recreate the Image this graph is drawn
    // create an image of the type packed RGB - makes dumping the data into the image faster
    oImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		iImageIndex = 0;
		oGraphics2D = oImage.createGraphics();
		oGraphics2D.setColor(oBackgroundColor);
		oGraphics2D.fillRect(0, 0, width, height);
		// Change the compositing rule so that when I write transparent pixels
		// they replace existing pixels, instead of being merged. 
		// WaterfallDetectionsLayer relies on this.
		oGraphics2D.setComposite(AlphaComposite.Src);
	}

	/**
	 * Resize the existing image, using the old image to pre-populate the new image.
	 * 
	 * Note that the width here is not necessarily the component width - it's the width
	 * of the data that you're going to give me in the draw* methods.
	 */
	public synchronized void resizeExisting(int width, int height)
	{
		if (oImage==null)
		{
			setSizeAndClear(width, height);
			return;
		}

		/* Under some conditions, components can get shrunk to the point that they have zero height.
		 * Rather than use complicated logic to track this, just clamp our height to 1. */
		if (height==0) {
			height = 1;
		}
		if (width==0) {
			width = 1;
		}
		
		// create new image
    final BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D newGraphics2D = newImage.createGraphics();
    newGraphics2D.setColor(oBackgroundColor);
    newGraphics2D.setComposite(AlphaComposite.Src);
		// copy existing image to new image
    drawImageTo(newGraphics2D, width, height, null);

    // reset the index
    iImageIndex = height-1;
    
		// set the fields to the new image
    oImage = newImage;
		oGraphics2D = newGraphics2D;
	}
	
	/**
	 * Scroll up, and add a line to the bottom of the image
	 * 
	 * @param aiImageData an array of RGB int values
	 */
	public synchronized void scrollUp(int[] aiImageData)
	{
		oImage.setRGB(0, iImageIndex, oImage.getWidth(), 1, aiImageData, 0, 0);

		iImageIndex++;
		if (iImageIndex == oImage.getHeight())
		{
			iImageIndex = 0;
		}
	}

	/**
	 * Scroll up, filling in the line with background color
	 */
	public synchronized void scrollUp()
	{
		if (oGraphics2D==null)
		{
			return;
		}
		oGraphics2D.setColor(oBackgroundColor);
		oGraphics2D.drawLine(0, iImageIndex, oImage.getWidth(), iImageIndex);

		iImageIndex++;
		if (iImageIndex == oImage.getHeight())
		{
			iImageIndex = 0;
		}
	}

	/**
	 * Scroll down, and add a line to the top of the image
	 * 
	 * @param aiImageData an array of RGB int values
	 */
	public synchronized void scrollDown(int[] aiImageData)
	{
		if (iImageIndex == 0)
		{
			iImageIndex = oImage.getHeight();
		}
		iImageIndex--;
		oImage.setRGB(0, iImageIndex, oImage.getWidth(), 1, aiImageData, 0, 0);
	}

	/**
	 * Scroll down, filling in the line with background color
	 */
	public synchronized void scrollDown()
	{
		if (iImageIndex == 0)
		{
			iImageIndex = oImage.getHeight();
		}
		iImageIndex--;

		oGraphics2D.setColor(oBackgroundColor);
		oGraphics2D.drawLine(0, iImageIndex, oImage.getWidth(), iImageIndex);
	}

	public synchronized void setBackgroundColor(Color backgroundColor)
	{
		this.oBackgroundColor = backgroundColor;
	}

	/**
	 * get a subimage - sometimes useful for generating zooms
	 */
	public synchronized BufferedImage getSubimage(int x, int y, int width, int height, ImageObserver observer)
	{
		// Copy the waterfall into a temporary image so that I can get a subimage.
		// We could avoid the temporary image at the cost of some complexity, but for an infrequent
		// operation, it is not worth it.
		GraphicsConfiguration oGc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		BufferedImage tmpImage = oGc.createCompatibleImage(oImage.getWidth(), oImage.getHeight());
		Graphics2D tmpGraphics = tmpImage.createGraphics();
		drawImageTo(tmpGraphics, observer);

		return tmpImage.getSubimage(x, y, width, height);
	}

	public boolean isImageCreated()
	{
		return oImage!=null;
	}
	
	public synchronized int getWidth()
	{
		return oImage==null ? -1 : oImage.getWidth();
	}

	public synchronized int getHeight()
	{
		return oImage==null ? -1 : oImage.getHeight();
	}
	
	/**
	 * get a line of image data
	 * 
	 * @param y the y-coordinate of the data
	 */
	public synchronized int [] getImageLine(int y)
	{
		y =  iImageIndex + 1 + y;
		if (y>=oImage.getHeight()) {
			y -= oImage.getHeight();
		}
		int [] outData = new int [oImage.getWidth()];
		oImage.getRaster().getDataElements(0, y, oImage.getWidth(), 1, outData);
		return outData;
	}
	
}
