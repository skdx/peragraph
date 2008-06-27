package com.peralex.utilities.ui;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * From http://weblogs.java.net/blog/alexfromsun/archive/2006/02/debugging_swing.html
 * 
 * @author Alexander Potochkin
 * @author Noel Grandin
 */
public final class EDT_ViolationChecker extends RepaintManager
{

	/**
	 * rather use the install method.
	 */
	private EDT_ViolationChecker()
	{
	}

	public static void install()
	{
		RepaintManager.setCurrentManager(new EDT_ViolationChecker());
	}

	@Override
	public synchronized void addInvalidComponent(JComponent oComponent)
	{
		checkThreadViolations();
		super.addInvalidComponent(oComponent);
	}

	@Override
	public void addDirtyRegion(JComponent oComponent, int x, int y, int w, int h)
	{
		checkThreadViolations();
		super.addDirtyRegion(oComponent, x, y, w, h);
	}

	private void checkThreadViolations()
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			return;
		}
		final Exception exception = new Exception();
		boolean repaint = false;
		boolean fromSwing = false;
		final StackTraceElement[] stackTrace = exception.getStackTrace();
		for (StackTraceElement element : stackTrace)
		{
			if (repaint && element.getClassName().startsWith("javax.swing."))
			{
				fromSwing = true;
			}
			if ("repaint".equals(element.getMethodName()))
			{
				repaint = true;
			}
		}
		if (repaint && !fromSwing)
		{
			// no problems here, since repaint() is thread safe
			return;
		}
		indicate(exception);
	}

	/**
	 * override this method to customize the logging mechanism
	 */
	public void indicate(Exception oException)
	{
		oException.printStackTrace();
	}

}