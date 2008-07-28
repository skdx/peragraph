package com.peralex.utilities.ui.graphs.waterfallGraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.peralex.utilities.ui.graphs.axisscale.NumberAxisScale;

/**
 * An axis component that displays a rainbow gradient for use with direction data.
 * 
 * @author Pieter de Vos
 */
public class DirectionAxis extends NumberAxisScale
{
	private static final float [] DEGREES_LABELS = { 
		180f, 120, 60f, 0f, 300f, 240f, 180f };
	
	/**
	 * Creates a new instance of DirectionAxis. This will be a Y-Axis.
	 */
	public DirectionAxis()
	{
		this.setLayout(null);
		this.setOpaque(false);

		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				clear();

				Insets insets = getInsets();
				final int iComponentHeight = getHeight() - insets.top - insets.bottom;
				for (int i = 0; i < DEGREES_LABELS.length; i++)
				{
					final int iPosition = Math.round(iComponentHeight / ((float) DEGREES_LABELS.length - 1) * i + insets.top);
					addLabel(iPosition, DEGREES_LABELS[i]);
				}

				repaint();
			}
		});

		// set up defaults
		setForeground(Color.BLACK);
		setBorder(new LineBorder(Color.BLACK));
	}

	/**
	 * Fill the component with the direction rainbow gradient.
	 */
	@Override
	public void paint(Graphics oGraphics)
	{
		final Graphics2D o2DG = (Graphics2D) oGraphics;

		o2DG.setPaint(new DirectionAxisBackgroundGradientPaint(0, 0, getHeight()-1));
		o2DG.fillRect(0, 0, getWidth() - 1, getHeight()-1);
		
		super.paint(oGraphics);
	}

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ignoreEx)
		{
		}
		JFrame oFrame = new JFrame();
		DirectionAxis oIAxis = new DirectionAxis();
		oFrame.getContentPane().add(oIAxis);
		oFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		oFrame.setBounds(300, 200, 100, 800);
		oFrame.setVisible(true);
	}
}
