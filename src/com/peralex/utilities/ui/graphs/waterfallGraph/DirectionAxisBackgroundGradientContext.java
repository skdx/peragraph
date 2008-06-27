package com.peralex.utilities.ui.graphs.waterfallGraph;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import com.peralex.utilities.ui.graphs.DirectionLib;

class DirectionAxisBackgroundGradientContext implements PaintContext
{
	private final Point2D startPoint;
	private final Point2D height;

	public DirectionAxisBackgroundGradientContext(Point2D startPoint, Point2D height)
	{
		this.startPoint = startPoint;
		this.height = height;
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
			double y1 = startPoint.distance(0, y + j);
			double y2 = height.distance(0,0);
			double deg = 180f - y1 / y2 * 360f;
			final Color col = DirectionLib.degreesToColor((float) deg);
			for (int i = 0; i < w; i++)
			{
				final int base = (j * w + i) * 4;
				data[base + 0] = col.getRed();
				data[base + 1] = col.getGreen();
				data[base + 2] = col.getBlue();
				data[base + 3] = col.getAlpha();
			}
		}
		raster.setPixels(0, 0, w, h, data);

		return raster;
	}
}