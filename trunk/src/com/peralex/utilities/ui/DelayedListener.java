package com.peralex.utilities.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a generic delayed listener. Adds a little hysteresis into the execution of the action for event sources
 * that sometimes have high update rates.
 * 
 * E.g. you could use this to do something when the text in a textfield changes, but you don't necessarily want to
 * perform the action until the users has finished typing, so you use a delayed listener to wait until the user stops
 * pressing keys for 200ms until you perform the action.
 * 
 * @author Noel Grandin
 */
public final class DelayedListener<T> implements InvocationHandler, ActionListener
{
	// /**
	// * factory method for use when the parentListener only implements one listener interface.
	// */
	// @SuppressWarnings("unchecked")
	// public static <T> T create(T parentListener)
	// {
	// List<Class> interfaces = getAllInterfaces(parentListener.getClass());
	// if (interfaces.size()>1) {
	// throw new IllegalStateException("this class supports at least two interfaces ("
	// + interfaces.get(0) + "," + interfaces.get(1) + "), but this method can only handle one.");
	// }
	// if (interfaces.size()==0) {
	// throw new IllegalStateException("parentListener must support one interface");
	// }
	// return create((Class<T>) parentListener.getClass().getInterfaces()[0], parentListener);
	// }
	//
	// private static List<Class> getAllInterfaces(Class clazz) {
	// final List<Class> list = new ArrayList<Class>();
	// while (clazz!=null) {
	// list.addAll(Arrays.asList(clazz.getInterfaces()));
	// clazz = clazz.getSuperclass();
	// }
	// return list;
	// }

	/**
	 * 
	 * factory method
	 */
	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> listenerInterface, T parentListener)
	{
		// just in case someone tries to get funny and bypass the type checking.
		if (!listenerInterface.isAssignableFrom(parentListener.getClass()))
		{
			throw new IllegalStateException("parent listener must implement the listener interface");
		}
		T listener = (T) Proxy.newProxyInstance(DelayedListener.class.getClassLoader(), new Class[] { listenerInterface },
				new DelayedListener<T>(listenerInterface, parentListener));
		return listener;
	}

	private final javax.swing.Timer oTimer;

	private final T parentListener;

	private final Class<T> listenerInterface;

	private final Map<Method, Object[]> delayedCalls = new HashMap<Method, Object[]>();

	private DelayedListener(Class<T> listenerInterface, T parentListener)
	{
		this.listenerInterface = listenerInterface;
		this.parentListener = parentListener;
		oTimer = new javax.swing.Timer(100, this);
		oTimer.setRepeats(false);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		// if the method is not one of the methods on the listener interface
		if (method.getDeclaringClass() != this.listenerInterface)
		{
			return null;
		}

		synchronized (delayedCalls)
		{
			delayedCalls.put(method, args);
		}
		oTimer.stop();
		oTimer.start();
		return null;
	}

	public void actionPerformed(ActionEvent evt)
	{
		try
		{
			synchronized (delayedCalls)
			{
				for (Method method : delayedCalls.keySet())
				{
					method.invoke(parentListener, delayedCalls.get(method));
				}
				delayedCalls.clear();
			}
		}
		catch (IllegalArgumentException ex)
		{
			throw new RuntimeException(ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new RuntimeException(ex);
		}
		catch (InvocationTargetException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
}
