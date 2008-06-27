package com.peralex.utilities.ui.helppopup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

import com.peralex.utilities.ui.images.IconManager;

/**
 * A help button that displays some text in a popup.
 * 
 * @author Noel Grandin
 */
public class HelpButton extends JButton
{

	private String sHelpText;
	private JPopupMenu oHelpPopup;

	public HelpButton(String sHelpText)
	{
		this.sHelpText = sHelpText;
		setIcon(IconManager.getSizedFor(HelpButton.class, "contexthelp.png", this, 1.2f));

		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setFocusable(false);

		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showPopup();
			}
		});
		
	}

	private void showPopup()
	{
		oHelpPopup = HelpPopup.createHelpPopup(sHelpText);
		
		/* Make the popup appear with it's top left corner in the middle of the button.
		 * I do it this way so that I can reliably capture the mouse-exit event and close the popup.
		 * 
		 * If I position the popup at the outside edge of the button, then the popup may appear either
		 * on top of the button, or to the side of the button (depending on screen space), which makes
		 * capturing the mouse-exit event impossible.
		 */
		oHelpPopup.show(this, getWidth()/2, getHeight()/2);
	}

	public void setHelpText(String sHelpText)
	{
		this.sHelpText = sHelpText;
	}
}
