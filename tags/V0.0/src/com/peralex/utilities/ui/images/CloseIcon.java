package com.peralex.utilities.ui.images;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * 
 * @author Noel Grandin
 */
public final class CloseIcon implements IIconFactory
{
	public static final CloseIcon INSTANCE = new CloseIcon();
	
	private CloseIcon() {}
	
	public ImageIcon createIcon(int height)
	{
		final BufferedImage oImage = new BufferedImage(height, height, BufferedImage.TYPE_4BYTE_ABGR);
		final Graphics2D g = oImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(height/8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		g.drawLine(1, 1, height-2, height-2);
		g.drawLine(height-2, 1, 1, height-2);
		
		g.dispose();
		return new ImageIcon(oImage);
	}

}
