package com.peralex.utilities.ui.errordialog;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.TimerTask;

/**
 * Note: based on code from Aleksey Gureev, published in Heinz Kabutz's newsletter.
 * 
 * Alternative events dispatching queue. The benefit over the default Event Dispatch queue is that you can add as many
 * watchdog timers as you need and they will trigger arbitrary actions when processing of single event will take longer
 * than one timer period. <p/> Timers can be of two types:
 * <ul>
 * <li><b>Repetitive</b> - action can be triggered multiple times for the same "lengthy" event dispatching. </li>
 * <li><b>Non-repetitive</b> - action can be triggered only once per event dispatching.</li>
 * </ul>
 * <p/> The queue records time of the event dispatching start. This time is used by the timers to check if dispatching
 * takes longer than their periods. If so the timers trigger associated actions. <p/> In order to use this queue
 * application should call <code>install()</code> method. This method will create, initialize and register the
 * alternative queue as appropriate. It also will return the instance of the queue for further interactions. Here's an
 * example of how it can be done: <p/>
 * 
 * <pre>
 *    Action edtOverloadReport = ...;
 *    // install single-shot wg to report EDT overload after
 *    // 10-seconds timeout
 *    EventQueueWatchdog.install(10000, edtOverloadReport);
 * </pre>
 */
public class EventQueueWatchdog extends EventQueue
{
	// Main timer
	private final java.util.Timer timer = new java.util.Timer(true);

	// Group of informational fields for describing the event

	private final Object eventChangeLock = new Object();

	private volatile long eventDispatchingStart_nanos = -1;

	private volatile AWTEvent event = null;

	private volatile Thread eventDispatchThread = null;

	public interface IWatchdogListener {
		/**
		 * called when the timeout is exceeded
		 * 
		 * @param stacktrace contains a stacktrace of the thread that caused the problem
		 */
		void watchdog(String stacktrace);
	}
	
	/**
	 * Hidden utility constructor.
	 */
	private EventQueueWatchdog()
	{
	}

	/**
	 * Install a watchdog with a default timeout of 1 second that writes a stacktrace to System.out.
	 */
	public static void install()
	{
		install(1000);
	}

	/**
	 * Install a watchdog. It will print a message on System.out whenever an AWT thread action takes too long.
	 */
	public static void install(long timeout_ms)
	{
		install(timeout_ms, new IWatchdogListener()
		{
			public void watchdog(String stacktrace)
			{
				System.out.println("Event thread processing too long\n" + stacktrace);
			}
		});
	}
	
	/**
	 * Install a watchdog.
	 */
	public static void install(long timeout_ms, IWatchdogListener timeoutListener)
	{
		final EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		final EventQueueWatchdog newEventQueue = new EventQueueWatchdog();
		eventQueue.push(newEventQueue);

		final Watchdog checker = newEventQueue.new Watchdog(timeout_ms, timeoutListener);
		newEventQueue.timer.schedule(checker, timeout_ms, timeout_ms);
	}

	/**
	 * Record the event and continue with usual dispatching.
	 * 
	 * @param anEvent event to dispatch.
	 */
	@Override
	protected void dispatchEvent(AWTEvent anEvent)
	{
		synchronized (eventChangeLock)
		{
			event = anEvent;
			eventDispatchingStart_nanos = System.nanoTime();
			if (eventDispatchThread == null)
			{
				eventDispatchThread = Thread.currentThread();
			}
		}

		super.dispatchEvent(anEvent);

		synchronized (eventChangeLock)
		{
			event = null;
			eventDispatchingStart_nanos = -1;
		}
	}

	/**
	 * Checks if the processing of the event is longer than the specified <code>maxProcessingTime</code>. If so then
	 * listener is notified.
	 */
	private class Watchdog extends TimerTask
	{
		// Settings
		private final long maxProcessingTime_ms;

		private final IWatchdogListener listener;

		/**
		 * Event reported as "lengthy" last time.
		 */
		private AWTEvent lastReportedEvent = null;

		/**
		 * Creates timer.
		 * 
		 * @param maxProcessingTime_ms maximum event processing time before listener is notified.
		 * @param listener listener to notify.
		 */
		public Watchdog(long maxProcessingTime_ms, IWatchdogListener listener)
		{
			if (listener == null)
				throw new IllegalArgumentException("Listener cannot be null.");
			if (maxProcessingTime_ms < 0)
				throw new IllegalArgumentException("Max locking period should be greater than zero");
			this.maxProcessingTime_ms = maxProcessingTime_ms;
			this.listener = listener;
		}

		@Override
		public void run()
		{
			final long time_nanos;
			final AWTEvent currentEvent;

			// Get current event requisites
			synchronized (eventChangeLock)
			{
				time_nanos = eventDispatchingStart_nanos;
				currentEvent = event;
			}

			long currentTime_nanos = System.nanoTime();

			// Check if event is being processed longer than allowed
			if (time_nanos==-1) return;
			final long delta_ms = (currentTime_nanos - time_nanos) / 1000 / 1000;
			if ((delta_ms > maxProcessingTime_ms) && (currentEvent != lastReportedEvent))
			{
				final String stacktrace = toString(eventDispatchThread.getStackTrace());
				listener.watchdog(stacktrace);
				lastReportedEvent = currentEvent;
			}
		}

		private String toString(StackTraceElement[] stacktrace)
		{
			final StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : stacktrace)
			{
				if (element.getClassName().equals(EventQueueWatchdog.class.getName()))
				{
					// ignore stuff in the thread above this watchdog
					break;
				}
				// format the output carefully so that eclipse can parse and hyperlink
				// to code properly.
				builder.append("\tat ");
				builder.append(element.toString());
				builder.append("\n");

			}
			return builder.toString();
		}
	}
}