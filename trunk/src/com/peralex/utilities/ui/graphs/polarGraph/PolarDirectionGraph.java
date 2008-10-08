package com.peralex.utilities.ui.graphs.polarGraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputAdapter;

import com.peralex.utilities.ui.ListenerSupport;

/**
 * Displays a "rainbow" compass with direction data and blocking regions overlaid.
 * 
 * @author Noel Grandin
 */
public class PolarDirectionGraph extends JComponent
{
	private final class BlockingManipulator extends MouseInputAdapter implements KeyListener
	{
		private BlockRegion block;

		private float start_degrees;

		private float end_degrees;
		
		/**
		 * I need to know if the user is dragging clockwise or counter-clockwise.
		 */
		private Boolean clockwise = null;

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (!blockingManipulationEnabled)
				return;
			
			// request focus so that the key listening works
			requestFocusInWindow();

			if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1)
			{
				final Point position = e.getPoint();
				double radius = CompassLib.compassR(position.x - translateX, position.y - translateY);
				if (radius < directionRadius)
				{
					start_degrees = (float) CompassLib.compassA_deg(position.x - translateX, position.y - translateY);
					end_degrees = start_degrees + 10;
					block = new BlockRegion(start_degrees, end_degrees);
					clockwise = null;
					blockRegions.add(block);
					repaint();
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			if (!blockingManipulationEnabled || block==null)
				return;

			if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK)
			{
				final Point position = e.getPoint();
				end_degrees = (float) CompassLib.compassA_deg(position.x - translateX, position.y - translateY);
				if (clockwise==null) {
					clockwise = end_degrees > start_degrees;
				}
				if (clockwise) {
					block.start_deg = start_degrees;
					block.end_deg = end_degrees;
				} else {
					block.start_deg = end_degrees;
					block.end_deg = start_degrees;
				}
				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (!blockingManipulationEnabled || block==null)
				return;
			
			block = null;
			listeners.fire().blockingChanged();
		}
		
		public void keyReleased(KeyEvent e)
		{
			// if the user pressed "escape" terminate the action
			if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
				blockRegions.remove(block);
				block = null;
				repaint();
			}
		}
		
		public void keyPressed(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	}

	/** display modes */
	private static final int DISPLAY_MODE_INSIDE_OUT = 0;

	private static final int DISPLAY_MODE_OUTSIDE_IN = 1;

	private BufferedImage oWheelImage;

	private final double[] magnitude = new double[360];

	private double directionRadius = 0;

	private double translateX = 0;

	private double translateY = 0;

	private static final int iDisplayMode = DISPLAY_MODE_OUTSIDE_IN;

	private final List<BlockRegion> blockRegions = new CopyOnWriteArrayList<BlockRegion>();

	private boolean blockingManipulationEnabled = false;

	private final ListenerSupport<IPolarDirectionGraphListener> listeners = ListenerSupport.create(IPolarDirectionGraphListener.class);

	/** show the N/S/E/W/NE/NW/SE/SW labels */
	private boolean directionLabelsVisible = true;
	
	/** Creates new form cPolarDirectionGraph */
	public PolarDirectionGraph()
	{
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				paintWheelImage();
				repaint();
			}
		});

		final BlockingManipulator mil = new BlockingManipulator();
		addMouseMotionListener(mil);
		addMouseListener(mil);
		addKeyListener(mil);
	}

	@Override
	public void paint(Graphics g)
	{
		if (oWheelImage == null)
		{
			return;
		}
		final Graphics2D g2 = (Graphics2D) g;
		// draw the spectrum wheel
		g.drawImage(oWheelImage, 0, 0, this);

		if (!isEnabled())
			return;

		/*
		 * It is easier to do co-ordinates as if I was drawing a circle about the origin, so apply a translation. Also,
		 * translate co-ordinates to position the circle in the centre of the long-axis of the containing panel.
		 */
		g.setColor(Color.BLACK);
		g2.translate(translateX, translateY);
		// draw the lines
		for (int i = 0; i < 360; i++)
		{
			if (magnitude[i] != 0)
			{
				final int directionX, directionY;
				switch (iDisplayMode)
				{
				case DISPLAY_MODE_INSIDE_OUT:
					directionX = (int) CompassLib.cartesianX(i, magnitude[i]);
					directionY = (int) -CompassLib.cartesianY(i, magnitude[i]);
					g.drawLine(0, 0, directionX, directionY);
					break;
				case DISPLAY_MODE_OUTSIDE_IN:
					final int startX = (int) CompassLib.cartesianX(i, directionRadius);
					final int startY = (int) -CompassLib.cartesianY(i, directionRadius);
					directionX = (int) CompassLib.cartesianX(i, directionRadius - magnitude[i]);
					directionY = (int) -CompassLib.cartesianY(i, directionRadius - magnitude[i]);
					g.drawLine(startX, startY, directionX, directionY);
					break;
				default:
					throw new IllegalStateException("unknown display mode " + iDisplayMode);
				}
			}
		}

		// turn anti-aliasing on, so I don't get gaps between the lines I draw to make up the wheel
		// note: this doesn't matter so much for the direction data because that changes rapidly.
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		drawBlockingRegions(g2, -directionRadius, -directionRadius);
	}

	private void paintWheelImage()
	{
		final int width = this.getWidth();
		final int height = this.getHeight();
		// sometimes, while the component hierarchy is being constructed, this happens.
		if (width == 0 || height == 0)
			return;

		// the circle must be rectangular
		final int sideLength = Math.min(width, height);

		// create an image of the type packed RGB - makes dumping the data into the image faster
		oWheelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// the 0.9 factor leaves some space for the marker lines
		directionRadius = sideLength / 2f * 0.9f;
		final Graphics2D g = oWheelImage.createGraphics();
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
		// turn anti-aliasing on, so I don't get gaps between the lines I draw to make up the wheel
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		/*
		 * It is easier to do co-ordinates as if I was drawing a circle about the origin, so apply a translation. Also,
		 * translate co-ordinates to position the circle in the centre of the long-axis of the containing panel.
		 */
		translateX = sideLength / 2 + ((width - sideLength) / 2);
		translateY = sideLength / 2 + ((height - sideLength) / 2);
		g.translate(translateX, translateY);

		final Paint oldPaint = g.getPaint();
		if (isEnabled())
		{
			g.setPaint(new CompassBackgroundGradientPaint(0, 0, directionRadius));
		}
		else
		{
			g.setPaint(Color.GRAY);
		}
		g.fillOval((int)-directionRadius, (int)-directionRadius, (int) (directionRadius*2), (int) (directionRadius*2));
		
		g.setPaint(oldPaint);

		// draw the marker lines
		g.setColor(new Color(128, 128, 128)); // grey
		// draw crosses
		g.drawLine(0, -sideLength / 2, 0, sideLength / 2);
		g.drawLine(-sideLength / 2, 0, sideLength / 2, 0);
		// draw 2 circles
		g.drawOval((int) -directionRadius, (int) -directionRadius, (int) (directionRadius * 2d),
				(int) (directionRadius * 2d));
		g.drawOval((int) (-directionRadius / 2d), (int) (-directionRadius / 2d), (int) (directionRadius),
				(int) (directionRadius));
		// draw little ticks on edge of wheel
		final double maxTickLength = (sideLength / 2d - directionRadius);
		for (int i = 0; i < 360; i += 10)
		{
			// convert to cartesian co-ordinates
			final int x1 = (int) CompassLib.cartesianX(i, directionRadius);
			final int y1 = (int) CompassLib.cartesianY(i, directionRadius);
			final double tickRadius = directionRadius + (i % 30 == 0 ? maxTickLength : maxTickLength / 2f);
			final int x2 = (int) CompassLib.cartesianX(i, tickRadius);
			final int y2 = (int) CompassLib.cartesianY(i, tickRadius);
			g.drawLine(x1, y1, x2, y2);
		}

		g.setColor(new Color(70, 70, 70)); // grey
		Font font = getFont();
		final int fontHeight = g.getFontMetrics(font).getHeight();
		// resize the font to suit the current wheel size, but don't make the font any bigger than the default size
		float newFontHeight = Math.min(fontHeight, (float) (font.getSize() * (sideLength / 2d - directionRadius) * 0.9f
				* 2d / fontHeight));
		font = font.deriveFont(newFontHeight);
		g.setFont(font);

		final double compassTextRadius = directionRadius * 0.65f;
		
		Rectangle2D stringBounds = g.getFontMetrics(font).getStringBounds("0", g);
		g.drawString("0", (float) (-stringBounds.getWidth() / 2f), (float) (-directionRadius + stringBounds.getHeight()));

		stringBounds = g.getFontMetrics(font).getStringBounds("180", g);
		g.drawString("180", (float) (-stringBounds.getWidth() / 2f), (float) (directionRadius));

		stringBounds = g.getFontMetrics(font).getStringBounds("90", g);
		g.drawString("90", (float) (directionRadius - stringBounds.getWidth()), 0);

		g.drawString("270", (float) (-directionRadius), 0);
		
		if (directionLabelsVisible)
		{
			drawStringAtRadius("N", g, font, compassTextRadius, 0);
			drawStringAtRadius("S", g, font, compassTextRadius, 180);
			stringBounds = g.getFontMetrics(font).getStringBounds("E", g);
			g.drawString("E", (float) (compassTextRadius - (stringBounds.getWidth() / 2f)), 0);
			stringBounds = g.getFontMetrics(font).getStringBounds("W", g);
			g.drawString("W", (float) (-compassTextRadius - (stringBounds.getWidth() / 2f)), 0);
	
			drawStringAtRadius("SE", g, font, compassTextRadius, 90+45);
			drawStringAtRadius("SW", g, font, compassTextRadius, 180+45);
			drawStringAtRadius("NW", g, font, compassTextRadius, 270+45);
			drawStringAtRadius("NE", g, font, compassTextRadius, 45);
		}
		else
		{
			// draw an arrow-head at the top
			
			final float arrowSize = sideLength / 2f * 0.15f;

			final float x = (float) (CompassLib.cartesianX(0, directionRadius) - (arrowSize / 2f));
			final float y = (float) (-CompassLib.cartesianY(0, directionRadius) - arrowSize);
			final int x1 = (int)x;
			final int y1 = (int)y;
			final int x2 = (int) (x + arrowSize);
			final int y2 = (int)y;
			final int x3 = (int) (x + arrowSize/2f);
			final int y3 = (int) (y + arrowSize);
			g.fillPolygon( new int[] { x1, x2, x3}, new int[] { y1, y2, y3}, 3);
		}
	}

	private void drawBlockingRegions(Graphics2D g, double x, double y)
	{
		g.setColor(new Color(0, 0, 0, 180)); // black, a little bit transparent

		final double diameter = directionRadius * 2;

		final List<BlockRegion> processedList = processList(blockRegions);
		for (BlockRegion region : processedList)
		{
			// Note: Arc2D angles use the polar co-ordinate convention
			final double start = CompassLib.compass2polar(region.getStart_deg());
			final double extent = region.getExtent_deg();
			g.fill(new Arc2D.Double(x, y, diameter, diameter, start, -extent, Arc2D.PIE));
		}
	}
	
	/**
	 * split any blocking regions that span 0 degrees into regions
	 */
	private static List<BlockRegion> processList(List<BlockRegion> blockRegions)
	{
		final ArrayList<BlockRegion> result = new ArrayList<BlockRegion>();

		// Some block regions goes across 0 degrees. Break them into two.
		for (BlockRegion blockRegion : blockRegions)
		{
			if (blockRegion.getStart_deg() <= blockRegion.getEnd_deg())
			{
				result.add(blockRegion);
			}
			else
			{
				final BlockRegion b1 = new BlockRegion(blockRegion.getStart_deg(), 360);
				if (b1.getExtent_deg() != 0)
				{
					result.add(b1);
				}

				final BlockRegion b2 = new BlockRegion(0, blockRegion.getEnd_deg());
				if (b2.getExtent_deg() != 0)
				{
					result.add(b2);
				}
			}
		}

		return result;
	}

	/**
	 * Draw a string, centred on the midpoint of the bounding box of the text.
	 */
	private static void drawStringAtRadius(String s, Graphics2D g, Font font, double radius, double angle_deg)
	{
		final int descent = g.getFontMetrics(font).getDescent();
		final Rectangle2D stringBounds = g.getFontMetrics(font).getStringBounds(s, g);
		// HACK: have no idea why I need the "+1", but it makes the text centre better.
		final float x = (float) (CompassLib.cartesianX(angle_deg, radius) - (stringBounds.getWidth() / 2f) + 1);
		final float y = (float) (-CompassLib.cartesianY(angle_deg, radius) + (stringBounds.getHeight() / 2) - descent);
		g.drawString(s, x, y);
	}

	/**
	 * update direction values and trigger a repaint
	 * 
	 * @param direction an array of 360 direction values.
	 */
	public void updateDirection(int[] direction)
	{
		if (direction == null)
		{
			throw new IllegalArgumentException("direction may not be null");
		}
		if (direction.length != 360)
		{
			throw new IllegalArgumentException(direction.length + "!= 360");
		}
		
		// find the maximum direction
		double maxDirection = 0;
		for (int i = 0; i < 360; i++)
		{
			maxDirection = Math.max(maxDirection, direction[i]);
		}

		// check for zero to prevent divide/zero error
		if (maxDirection == 0)
		{
			Arrays.fill(magnitude, 0);
		}
		else
		{
			// calculate the co-ordinates of the lines
			for (int i = 0; i < 360; i++)
			{
				magnitude[i] = direction[i] / maxDirection * directionRadius;
				// ignore negative data
				magnitude[i] = Math.max(0, magnitude[i]);
			}
		}

		repaint();
	}

	public void setBlockRegions(final List<BlockRegion> newBlockRegions)
	{
		// Since we use CopyOnAddArrayList, we can 'safely' remove the content even if the event thread is painting.
		blockRegions.clear();
		blockRegions.addAll(newBlockRegions);
		repaint();
	}
	
	public List<BlockRegion> getBlockRegions()
	{
		return this.blockRegions;
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		paintWheelImage();
		repaint();
	}
	
	public void setBlockingManipulationEnabled(boolean enabled)
	{
		this.blockingManipulationEnabled = enabled;
	}
	
	public boolean isBlockingManipulationEnabled()
	{
		return this.blockingManipulationEnabled;
	}

	public void addControlListener(IPolarDirectionGraphListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
		}
		JFrame oTestFrame = new JFrame();
		final PolarDirectionGraph oTestControl = new PolarDirectionGraph();
		oTestFrame.getContentPane().add(oTestControl);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		oTestFrame.setBounds((screenSize.width - 150) / 2, (screenSize.height - 250) / 2, 254, 174);
		oTestFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		oTestFrame.setVisible(true);
	}

	public boolean isDirectionLabelsVisible()
	{
		return directionLabelsVisible;
	}

	public void setDirectionLabelsVisible(boolean showDirectionLabels)
	{
		if (this.directionLabelsVisible==showDirectionLabels) return;
		
		this.directionLabelsVisible = showDirectionLabels;
		paintWheelImage();
		repaint();
	}
}
