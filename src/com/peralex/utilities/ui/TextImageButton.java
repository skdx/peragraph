package com.peralex.utilities.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Custom class overriding JButton class for creating image-like text buttons. 
 * Paints text in colour, in caps and with a drop-shadow.
 *
 * @author Pieter de Vos
 */
public class TextImageButton extends JButton
{
	
	private static final Color TEXT_COLOR = new Color(89, 139, 206);
	private static final Color SHADOW_COLOR;
	static {
		float [] hsb = new float[3];
		Color.RGBtoHSB(89, 139, 206, hsb);
		SHADOW_COLOR = Color.getHSBColor(hsb[0]/*hue*/, hsb[1]/*saturation*/, hsb[2]/2f/*brightness*/);
	}
	
	/** SansSerif fonts make nice icons */
	private static final Font ICON_FONT = new Font("SansSerif", Font.BOLD, 10);
	
	@Override
	public void setText(String oText)
	{
		super.setText(oText.toUpperCase());		
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		final Graphics2D g2D = (Graphics2D) g;

		BufferedImage oTextImage = g2D.getDeviceConfiguration().createCompatibleImage(getWidth(), getHeight());
		BufferedImage oDropShadowImage = g2D.getDeviceConfiguration().createCompatibleImage(getWidth(), getHeight());

		Graphics2D oTextImageG = oTextImage.createGraphics();
		Graphics2D oDropShadowG = oDropShadowImage.createGraphics();

		// set buffered image background colour to current system control colour
		oTextImageG.setBackground(SystemColor.control);
		oTextImageG.clearRect(0, 0, getWidth(), getHeight());
		oDropShadowG.setBackground(SystemColor.control);
		oDropShadowG.clearRect(0, 0, getWidth(), getHeight());

		// render shadow
		oTextImageG.setColor(SHADOW_COLOR);
		oTextImageG.setFont(ICON_FONT);

		final FontMetrics oFM = g2D.getFontMetrics();
		final String oText = getText()==null ? "" : getText();
		
		// calculate centre of image. subtract half the string width from half the total width.
		oTextImageG.drawString(oText, (getWidth() / 2) - (oFM.stringWidth(oText) / 2), (getHeight() / 2) + 4);
		oTextImageG.dispose();

		//offset shadow by one to create drop shadow effect.
		oDropShadowG.drawImage(oTextImage, 1, 1, null);

		// render text over shadow
		oDropShadowG.setFont(ICON_FONT);
		oDropShadowG.setColor(TEXT_COLOR);
		oDropShadowG.drawString(oText, (getWidth() / 2) - (oFM.stringWidth(oText) / 2), (getHeight() / 2) + 4);
		oDropShadowG.dispose();

		g2D.drawImage(oDropShadowImage, 0, 0, null);
	}

	/**
	 * Convert text into an icon such that the icon still looks like an icon and not just a button.
	 */
	public static Icon createTextImageFor(Component comp, String oText)
	{
		return createTextImageFor(comp, oText, new Insets(0,0,0,0));
	}
	
	/**
	 * Convert text into an icon such that the icon still looks like an icon and not just a button.
	 */
	public static Icon createTextImageFor(Component comp, String oText, Color backgroundColor)
	{
		Parameters params = new Parameters(comp);
		params.text = oText;
		params.backgroundColor = backgroundColor;
		return createTextImageFor(params);
	}
	
	/**
	 * Convert text into an icon such that the icon still looks like an icon and not just a button.
	 */
	public static Icon createTextImageFor(Component comp, String oText, Insets margins)
	{
		return createTextImageFor(comp, oText, margins, 1f);
	}
	
	/**
	 * Convert text into an icon such that the icon still looks like an icon and not just a button.
	 */
	public static Icon createTextImageFor(Component comp, String oText, Insets margins, float fontScalingFactor)
	{
		Parameters params = new Parameters(comp);
		params.text = oText;
		params.margins = margins;
		params.fontScalingFactor = fontScalingFactor;
		return createTextImageFor(params);
	}

	/** parameters class, sets up all the default parameters */
	public static class Parameters {
		public String text = "";
		public Insets margins = new Insets(0, 0, 0, 0);
		public float fontScalingFactor = 1f;
		public Color backgroundColor = new Color(0,0,0,0); // transparent
		public Color textColor = TEXT_COLOR;
		public Font textFont;
		public final Component comp;
		
		public Parameters(Component comp) {
			this.comp = comp;
			// Scale the icon font to be the same size as the default font.
			final int standardFontHeight = comp.getFontMetrics(comp.getFont()).getHeight();
			final int iconFontDefaultHeight = comp.getFontMetrics(ICON_FONT).getHeight();
			final float newIconFontSize = ((float)ICON_FONT.getSize()) * standardFontHeight / iconFontDefaultHeight * fontScalingFactor;
			this.textFont = ICON_FONT.deriveFont(newIconFontSize);
		}
	}
	
	/**
	 * Convert text into an icon such that the icon still looks like an icon and not just a button.
	 */
	public static Icon createTextImageFor(Parameters params)
	{
		// create a test image so I can figure out how much space I need
		final BufferedImage oTestImage = new BufferedImage(50, 50, BufferedImage.TYPE_4BYTE_ABGR);
		final Graphics2D testG = oTestImage.createGraphics();
		testG.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		testG.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		final FontMetrics fontMetrics = testG.getFontMetrics(params.textFont);
		final Rectangle2D textBounds = fontMetrics.getStringBounds(params.text, testG);
		final LineMetrics lineMetrics = fontMetrics.getLineMetrics(params.text, testG);

		// now create the real image
		final int textWidth = (int) Math.ceil(textBounds.getWidth());
		final int textHeight = (int) Math.ceil(lineMetrics.getAscent() + lineMetrics.getDescent());
		final int imageWidth = textWidth + params.margins.left + params.margins.right;
		final int imageHeight = textHeight + params.margins.top + params.margins.bottom;
		final BufferedImage oImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
		final Graphics2D g = oImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		// calculate position of baseline
		final float textBaselineX = params.margins.left;
		final float textBaselineY = params.margins.top + fontMetrics.getAscent();
		
		g.setBackground(params.backgroundColor);
		g.clearRect(0, 0, oImage.getWidth(), oImage.getHeight());
		
		// render text
		g.setFont(params.textFont);
		g.setColor(params.textColor);
		g.drawString(params.text, textBaselineX, textBaselineY);
		
		g.dispose();

		return new ImageIcon(oImage);
	}
	
}
