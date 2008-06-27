package com.peralex.utilities.ui.errordialog;

import java.awt.Frame;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.peralex.utilities.ui.SwingLib;

/**
 * Display error dialog for application exceptions.
 * 
 * @author Noel Grandin
 */
public final class ErrorDialog
{

	static boolean dontShowAnymore = false;

	/** not meant to be instantiated */
	private ErrorDialog()
	{
	}

	private static final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread t, Throwable ex)
		{
			ex.printStackTrace();
			ErrorDialog.show(ex);
		}
	};
	
	/**
	 * Configure a a default thread uncaught exception handler that will display an error dialog if an exception is thrown. 
	 */
	public static void configureDefaultUncaughtExceptionHandler()
	{
		// be tolerant of being called multiple times
		if (Thread.getDefaultUncaughtExceptionHandler()==handler) return;
		
		if (Thread.getDefaultUncaughtExceptionHandler()!=null) {
			throw new IllegalStateException("there is already a handler configured " + Thread.getDefaultUncaughtExceptionHandler());
		}
		
		Thread.setDefaultUncaughtExceptionHandler(handler);
	}
	
	/**
	 * show a non-fatal dialog with the stack trace.
	 */
	public static void show(Throwable t)
	{
		show("", t);
	}

	/**
	 * show a non-fatal dialog with the stack trace.
	 */
	public static void show(String msg, Throwable t)
	{
		if (dontShowAnymore)
			return;

		Frame activeFrame = SwingLib.findActiveFrame();
		show(msg, t, activeFrame, false);
	}
	
	/**
	 * show a non-fatal dialog with the stack trace.
	 */
	public static void show(Throwable t, JComponent parent)
	{
		show ("", t, parent);
	}

	/**
	 * show a non-fatal dialog with the stack trace.
	 */
	public static void show(String msg, Throwable t, JComponent parent)
	{
		if (dontShowAnymore)
			return;

		Frame activeFrame = SwingLib.findFrameParent(parent);
		if (activeFrame == null)
		{
			activeFrame = SwingLib.findActiveFrame();
		}
		show(msg, t, activeFrame, false);
	}
	
	/**
	 * show a dialog with the stack trace, and terminate the application.
	 */
	public static void showFatal(Throwable t)
	{
		showFatal("", t);
	}

	/**
	 * show a dialog with the stack trace, and terminate the application.
	 */
	public static void showFatal(String extraMsg, Throwable t)
	{
		if (dontShowAnymore)
			return;

		Frame activeFrame = SwingLib.findActiveFrame();
		show(extraMsg, t, activeFrame, true);
	}
	
	/**
	 * show a dialog with the stack trace, and terminate the application.
	 */
	public static void showFatal(Throwable t, JComponent parent)
	{
		showFatal("", t, parent);
	}

	/**
	 * show a dialog with the stack trace, and terminate the application.
	 */
	public static void showFatal(String extraMsg, Throwable t, JComponent parent)
	{
		if (dontShowAnymore)
			return;

		Frame activeFrame = SwingLib.findFrameParent(parent);
		if (activeFrame == null)
		{
			activeFrame = SwingLib.findActiveFrame();
		}
		show(extraMsg, t, activeFrame, true);
	}
	
	private static void show(String extraMsg, Throwable t, Frame activeFrame, final boolean isFatal)
	{
		// If an error happens on the new event loop created by me calling setVisible(), then I don't
		// want to display another error dialog or I can end annoying the user by showing tons of dialogs.
		for (StackTraceElement ste : t.getStackTrace())
		{
			if (ste.getClassName().equals(ErrorDialog.class.getCanonicalName()))
			{
				// last ditch effort
				try
				{
					System.out.println("ErrorDialog is being called while ErrorDialog is already visible. Not showing error.");
					logError(t);
				}
				catch (Exception ex)
				{
				}
				return;
			}
		}

		String msgBeforeSplit = extraMsg + t.toString();
		final String splitMsg = splitMessage(msgBeforeSplit).toString();

		final StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer));

		if (SwingUtilities.isEventDispatchThread())
		{
			_handle(activeFrame, isFatal, splitMsg, writer);
		}
		else
		{
			final Frame _activeFrame = activeFrame;
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						_handle(_activeFrame, isFatal, splitMsg, writer);
					}
				});
			}
			catch (InvocationTargetException ex)
			{
				// last ditch effort
				try
				{
					logError(ex);
				}
				catch (Exception ex2)
				{
				}
				System.exit(1);
			}
			catch (InterruptedException ex)
			{
				// last ditch effort
				try
				{
					logError(ex);
				}
				catch (Exception ex2)
				{
				}
				System.exit(1);
			}
		}
	}

	private static void _handle(Frame activeFrame, boolean isFatal, final String splitMsg,
			final StringWriter writer)
	{
		if (activeFrame==null)
		{
			// Having no frame means that the dialog has no presence on the taskbar, which means the user
			// can accidentally ignore it.
			activeFrame = new Frame("Application");
			activeFrame.setVisible(true);
		}
		
		final JDialog dialog = new JDialog(activeFrame, "Internal Problem Occurred", true);

		if (isFatal)
		{
			final DialogPanel panel = new DialogPanel(dialog, DialogPanel.ButtonStyle.TERMINATE, splitMsg, writer.getBuffer()
					.toString());
			dialog.setContentPane(panel);
			dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

			showDialog(activeFrame, dialog);

			if (panel.selectedExit)
			{
				System.exit(1);
			}
		}
		else
		{
			final DialogPanel panel = new DialogPanel(dialog, DialogPanel.ButtonStyle.OK, splitMsg, writer.getBuffer()
					.toString());
			dialog.setContentPane(panel);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			showDialog(activeFrame, dialog);
			
			if (panel.selectedExit)
			{
				System.exit(1);
			}
		}
	}

	private static void showDialog(final Frame activeFrame, final JDialog dialog)
	{
		dialog.pack();
		dialog.setLocationRelativeTo(activeFrame);
		dialog.setVisible(true);
	}

	/*
	 * Split the message into 80 character chunks so that the box is narrower than the screen. And format it into html, so
	 * that JLabel displays it correctly (JLabel will only do multi-line text if you give it html).
	 */
	private static StringBuilder splitMessage(String s)
	{
		final StringBuilder msg = new StringBuilder("<html>");
		final int LINE_LEN = 80;
		int startIdx = 0;
		for (int i = 0; i < s.length(); i++)
		{
			if (s.charAt(i) == '\n')
			{
				msg.append(s.substring(startIdx, i));
				msg.append("<P>");
				startIdx = i + 1;
			}
			else if (i - startIdx == LINE_LEN)
			{
				msg.append(s.substring(startIdx, i));
				msg.append("<P>");
				startIdx = i;
			}
		}
		// left-overs
		if (startIdx + 1 < s.length())
		{
			msg.append(s.substring(startIdx, s.length()));
		}
		msg.append("</html>");
		return msg;
	}

	private static void logError(Throwable ex)
	{
		ex.printStackTrace();
	}
}
