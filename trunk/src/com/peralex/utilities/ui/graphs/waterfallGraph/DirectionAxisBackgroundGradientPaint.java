package com.peralex.utilities.ui.graphs.waterfallGraph;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.ColorModel;

/**
 * I use a gradient paint because it's about 5x faster than painting lots of lines.
 * 
 * @author Noel Grandin
 */
class DirectionAxisBackgroundGradientPaint implements Paint
{
	private final Point2D mPoint;
	private final float height;
	
	public DirectionAxisBackgroundGradientPaint(double x, double y, float height)
	{
		this.mPoint = new Point2D.Double(x, y);
		this.height = height;
	}

	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds,
			AffineTransform xform, RenderingHints hints)
	{
		Point2D transformedPoint = xform.transform(mPoint, null);
		Point2D otherPoint = new Point2D.Float(0, height);
		Point2D transformedHeight = xform.deltaTransform(otherPoint, null);
		return new DirectionAxisBackgroundGradientContext(transformedPoint, transformedHeight);
	}

	public int getTransparency()
	{
		return OPAQUE;
	}
}
