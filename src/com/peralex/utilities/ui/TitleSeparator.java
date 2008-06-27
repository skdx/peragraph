package com.peralex.utilities.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * A JGoodies-FormLayout style separator. Looks good and takes up minimal space in forms.
 * 
 * @author Noel Grandin
 */
public class TitleSeparator extends JPanel
{
	private final JLabel label;

	public TitleSeparator()
	{
		this("");
	}

	public TitleSeparator(String text)
	{
		label = new JLabel(text);
		label.setFont(getFont());

		setLayout(new GridBagLayout());

		final GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.anchor = GridBagConstraints.WEST;
		gbc1.insets = new java.awt.Insets(0, 4, 0, 0);
		add(label, gbc1);

		final GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		gbc2.insets = new java.awt.Insets(0, 4, 0, 0);
		gbc2.weightx = 1;
		gbc2.fill = GridBagConstraints.BOTH;
		add(new LineComponent(), gbc2);
	}

	public String getTitle()
	{
		String s = label.getText();
		// remove the space on the end
		return s.substring(0, s.length() - 1);
	}

	public void setTitle(String text)
	{
		label.setText(text);
	}

	/**
	 * override to make the label have the same font as the component.
	 */
	@Override
	public void setFont(Font font)
	{
		super.setFont(font);
		if (label != null) // check for null because we get called indirectly from the constructor
		{
			label.setFont(font);
		}
	}

	private static class LineComponent extends JComponent
	{

		@Override
		public void paint(Graphics g)
		{
			super.paint(g);

			// draw a line in the background
			Color c = UIManager.getColor("Separator.foreground");
			if (c == null)
			{
				c = new Color(153, 153, 153);
			}
			g.setColor(c);
			g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
		}

	}

}
