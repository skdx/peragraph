package com.peralex.utilities.ui.graphs.polarGraph;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import com.peralex.utilities.ui.graphs.DirectionLib;

class CompassBackgroundGradientContext implements PaintContext
{
	private final Point2D centrePoint;
	private final Color startColor = Color.WHITE;
	private final double radius;

	public CompassBackgroundGradientContext(Point2D centrePoint, double radius)
	{
		this.centrePoint = centrePoint;
		this.radius = radius;
	}

	public void dispose()
	{
	}

	public ColorModel getColorModel()
	{
		return ColorModel.getRGBdefault();
	}

	public Raster getRaster(int x, int y, int w, int h)
	{
		final WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);

		final int[] data = new int[w * h * 4];
		for (int j = 0; j < h; j++)
		{
			for (int i = 0; i < w; i++)
			{
				final double angle = CompassLib.compassA_deg(x + i - centrePoint.getX(), y + j - centrePoint.getY());
				final Color rainbowColor = DirectionLib.degreesToColor((float) angle);

				final double distance = centrePoint.distance(x + i, y + j);
				double ratio = distance / radius;
				if (ratio > 1.0)
					ratio = 1.0;

				final int base = (j * w + i) * 4;
				data[base + 0] = (int) (startColor.getRed() + ratio * (rainbowColor.getRed() - startColor.getRed()));
				data[base + 1] = (int) (startColor.getGreen() + ratio * (rainbowColor.getGreen() - startColor.getGreen()));
				data[base + 2] = (int) (startColor.getBlue() + ratio * (rainbowColor.getBlue() - startColor.getBlue()));
				data[base + 3] = (int) (startColor.getAlpha() + ratio * (rainbowColor.getAlpha() - startColor.getAlpha()));
			}
		}
		raster.setPixels(0, 0, w, h, data);

		return raster;
	}
}