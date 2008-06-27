package com.peralex.utilities.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Utility methods for swing.
 * 
 * @author Noel Grandin
 */
public final class SwingLib
{

	private SwingLib()
	{
	}

	private static final double DARKER_FACTOR = 0.9;

	public static Color slightlyDarker(Color col)
	{
		return new Color(Math.max((int) (col.getRed() * DARKER_FACTOR), 0), Math.max((int) (col
				.getGreen() * DARKER_FACTOR), 0), Math.max((int) (col.getBlue() * DARKER_FACTOR), 0));
	}

	/**
	 * If we are on the event-thread, perform the runnable now, else use SwingUtilities.invokeAndWait.
	 */
	public static void invokeAndWaitSafe(Runnable runnable)
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			runnable.run();
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait(runnable);
			}
			catch (InterruptedException ex)
			{
				throw new RuntimeException(ex);
			}
			catch (InvocationTargetException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Replace values in combobox model while maintaining selection. Note that this assumes that the combobox is using the
	 * DefaultComboBoxModel.
	 */
	public static void replaceComboBoxModel(JComboBox comboBox, String[] newValues)
	{
		final Object selected = ((DefaultComboBoxModel) comboBox.getModel()).getSelectedItem();
		// note: it would be better if I modifed the values in the existing model
		// rather than replacing the model, but the DefaultComboBoxModel has no
		// bulk add method.
		DefaultComboBoxModel newModel = new DefaultComboBoxModel(newValues);
		newModel.setSelectedItem(selected);
		comboBox.setModel(newModel);
	}

	/**
	 * I hate ownerless dialogs. With this method, we can find the currently visible frame and attach the dialog to that,
	 * instead of always attaching it to null.
	 */
	public static Frame findActiveFrame()
	{
		Frame[] frames = Frame.getFrames();
		for (Frame frame : frames)
		{
			if (frame.isVisible())
			{
				return frame;
			}
		}
		return null;
	}

	/**
	 * Run up the containment tree looking for a specific parent
	 */
	public static Container findParent(Container child, Class<?> parentClass)
	{
		while (child != null && child.getClass() != parentClass)
		{
			child = child.getParent();
		}
		return child;
	}

	/**
	 * Run up the containment tree until we find a Frame
	 */
	public static Frame findFrameParent(Component child)
	{
		while (child != null && !(child instanceof Frame))
		{
			child = child.getParent();
		}
		return (Frame) child;
	}

	/**
	 * Some large dialogs will go off the edge of the screen.
	 * 
	 * Note: I use this method for JFrames and JDialogs. Window is the common ancestor superclass.
	 */
	public static void resizeDialogToFitScreen(Window dialog)
	{
		resizeDialogToFitScreen(dialog, 1f);
	}

	/**
	 * Some large dialogs will go off the edge of the screen.
	 * 
	 * Note: I use this method for JFrames and JDialogs. Window is the common ancestor superclass.
	 * 
	 * @param fillFactor a value between 0 and 1 indicating how much of the screen to fill.
	 */
	public static void resizeDialogToFitScreen(Window dialog, float fillFactor)
	{
		final GraphicsConfiguration gc = dialog.getGraphicsConfiguration();
		final Rectangle dialogBounds = dialog.getBounds();
		final Rectangle graphicsBounds = gc.getBounds();
		final float availableScreenHeight = graphicsBounds.height * fillFactor;
		final float availableScreenWidth = graphicsBounds.width * fillFactor;
		final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);

		final int dialogBottomEdge = dialogBounds.y + dialogBounds.height;
		final float maximumBottomEdge = graphicsBounds.y + availableScreenHeight - screenInsets.bottom;
		final int excessHeight = Math.round(dialogBottomEdge - maximumBottomEdge);

		final int dialogRightEdge = dialogBounds.x + dialogBounds.width;
		final float maximumRightEdge = graphicsBounds.x + availableScreenWidth - screenInsets.right;
		final int excessWidth = Math.round(dialogRightEdge - maximumRightEdge);
		
		if (excessHeight > 0 || excessWidth > 0)
		{
			if (excessHeight > 0)
			{
				dialogBounds.height -= excessHeight;
			}
			if (excessWidth > 0)
			{
				dialogBounds.width -= excessWidth;
			}
			Dimension d = dialogBounds.getSize();
			dialog.setSize(d);
		}
	}

	/**
	 * this does a similar thing to Window#setLocationRelativeTo, but it takes the screen insets into account.
	 */
	public static void centreOnScreen(Window window)
	{
		final GraphicsConfiguration gc = window.getGraphicsConfiguration();
		final Rectangle dialogBounds = window.getBounds();
		final Rectangle screenBounds = gc.getBounds();
		final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				window.getGraphicsConfiguration());

		final int x = Math.round(screenBounds.x
				+ (screenBounds.width - dialogBounds.width - screenInsets.left - screenInsets.right) / 2f);
		final int y = Math
				.round(screenBounds.y
						+ (screenBounds.height - dialogBounds.height - screenInsets.bottom - screenInsets.top)
						/ 2f);

		window.setLocation(x, y);
	}

	/**
	 * Recursively set the enabled property of a UI component and all of it's children.
	 */
	public static void setEnabledRecursive(Container component, boolean enabled)
	{
		component.setEnabled(enabled);
		final int cnt = component.getComponentCount();
		for (int i = 0; i < cnt; i++)
		{
			Component child = component.getComponent(i);
			if (child instanceof Container)
			{
				setEnabledRecursive((Container) child, enabled);
			}
			else
			{
				child.setEnabled(enabled);
			}
		}

	}

	/**
	 * refresh a combobox with new data, while maintaining the selection. Note: for this to work, the data objects must
	 * implement equals().
	 */
	public static void refreshComboBox(JComboBox combo, List<?> list)
	{
		if (!(combo.getModel() instanceof DefaultComboBoxModel))
		{
			throw new IllegalArgumentException(
					"this method only works in the JComboBox uses DefaultComboBoxModel");
		}

		final Object selectedObj = combo.getSelectedItem();
		Object newSelected = null;
		if (selectedObj != null)
		{
			for (Object obj : list)
			{
				if (obj.equals(selectedObj))
				{
					newSelected = obj;
					break;
				}
			}
		}
		combo.setModel(new DefaultComboBoxModel(new Vector<Object>(list)));
		if (combo.isEditable() && newSelected == null)
		{
			// if the combo is editable, use the previous selected value
			combo.setSelectedItem(selectedObj);
		}
		else
		{
			combo.setSelectedItem(newSelected);
		}
	}

	/**
	 * Make the mouse-wheel scrolling be approx one line at a time
	 */
	public static void configureLineScrolling(JScrollPane scrollPane)
	{
		// make the mouse-wheel scrolling be approx one line at a time
		final JLabel testLabel = new JLabel("XXX");
		final int height = testLabel.getPreferredSize().height;
		scrollPane.getVerticalScrollBar().setUnitIncrement(height);
	}

	/**
	 * append a string onto the end of a text area, and scroll the text to be in view.
	 */
	public static void appendAndScroll(JTextArea textArea, String s)
	{
		textArea.append(s);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
