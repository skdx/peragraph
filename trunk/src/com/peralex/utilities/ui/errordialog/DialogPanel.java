package com.peralex.utilities.ui.errordialog;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;

import com.peralex.utilities.ui.GBFormBuilder;
import com.peralex.utilities.ui.SwingLib;

/**
 * 
 * @author Noel Grandin
 */
final class DialogPanel extends JPanel
{

	public static enum ButtonStyle
	{
		OK, TERMINATE
	}

	public boolean selectedExit;

	private final JScrollPane scrollPane;

	private final JDialog parent;
	
	public DialogPanel(JDialog dialog, ButtonStyle buttonStyle, String msg, String stacktrace)
	{
		this.parent = dialog;
		
		final GBFormBuilder builder = new GBFormBuilder(this);
		builder.setDefaultBorder();
		builder.setFormStretch(GBFormBuilder.Stretch.BOTH);

		final JButton advancedButton = new JButton("Advanced >>");
		advancedButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				scrollPane.setVisible(!scrollPane.isVisible());
				((JButton) e.getSource()).setText(scrollPane.isVisible() ? "Advanced <<" : "Advanced >>");
				parent.pack();
				// leave a little space between the dialog and the edge of the screen
				SwingLib.resizeDialogToFitScreen(parent, 0.99f);
			}
		});
		advancedButton.setDefaultCapable(false);

		final JTextArea stackTraceArea = new JTextArea(20, 120);
		stackTraceArea.setText(stacktrace);
		stackTraceArea.setEditable(false);
		stackTraceArea.setCaretPosition(0);
		
		final JButton copyToClipBoard = new JButton("Copy stacktrace");
		copyToClipBoard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// get the current text position
				final int textPos = stackTraceArea.getCaretPosition();
				// copy all of the log to the clipboard
				final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				stackTraceArea.selectAll();
				stackTraceArea.getTransferHandler().exportToClipboard(stackTraceArea, clipboard, TransferHandler.COPY);
				// reset the selection
				stackTraceArea.setCaretPosition(textPos);
				stackTraceArea.moveCaretPosition(textPos);
			}
		});
		
		final JCheckBox dontShowAnymoreCheckbox = new JCheckBox("Don't show this dialog anymore");
		dontShowAnymoreCheckbox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ErrorDialog.dontShowAnymore = dontShowAnymoreCheckbox.isSelected();
			}
		});
		
		scrollPane = new JScrollPane(stackTraceArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVisible(false);

		if (buttonStyle==ButtonStyle.OK)
		{
			final JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					parent.setVisible(false);
				}
			});
			
			dialog.getRootPane().setDefaultButton(okButton);

			final JButton exitButton = new JButton("Exit Application");
			exitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedExit = true;
					parent.setVisible(false);
				}
			});
			exitButton.setDefaultCapable(false);
			
			builder.appendLn(new JLabel(msg), 1);
			builder.appendLn(builder.buildCenteredBar(okButton, copyToClipBoard, advancedButton, exitButton), 1);
			builder.appendLn(dontShowAnymoreCheckbox, 1);
			builder.appendLn(scrollPane, 1, 1, GBFormBuilder.Fill.BOTH, GBFormBuilder.Stretch.BOTH);
		}
		else if (buttonStyle==ButtonStyle.TERMINATE)
		{
			final JButton exitButton = new JButton("Exit Application");
			exitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedExit = true;
					parent.setVisible(false);
				}
			});
			exitButton.setDefaultCapable(false);
			
			
			builder.appendLn(new JLabel(msg), 1);
			builder.appendLn(builder.buildCenteredBar(exitButton, copyToClipBoard, advancedButton), 1);
			builder.appendLn(scrollPane, 1, 1, GBFormBuilder.Fill.BOTH, GBFormBuilder.Stretch.BOTH);
		}
		else
		{
			throw new IllegalStateException("unknown button style " + buttonStyle);
		}
	}
	
}