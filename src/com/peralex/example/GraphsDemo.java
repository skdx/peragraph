package com.peralex.example;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

/**
 * Need
 *   Range Cursor demo
 *   Cursor demo
 *   constellation graph cConstellationGraph
 *   cBarGraph
 *   measure cursors
 *   
 * @author Noel Grandin
 */
public class GraphsDemo
{

	private GraphsDemo() {}
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ignoreEx)
		{
		}

		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Multiple Line Graph", new GraphWithMultipleLines());
		tabbedPane.addTab("Scrolling Line Graph", new GraphWithScrollingLines());
		tabbedPane.addTab("Waterfall Graphh", new WaterfallGraphDemo());

		final JFrame frame = new JFrame("Graphs Demo");
		frame.getContentPane().add(tabbedPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
