package com.peralex.utilities.ui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A support class so I don't have to code lots of notifyXXX() or fireXXX() methods.
 * You use it like this:
 * 
 * <code>
 * public class SongPlayer {
 *   public interface IMyListener {
 *      nowPlaying(String song);
 *   }
 *   private final ListenerSupport<IMyListener> listeners = ListenerSupport.create(IMyListener.class);
 *   
 *   public void addListener(IMyListener l) {
 *     this.listeners.add(l);
 *   }
 *   
 *   public void removeListener(IMyListener l) {
 *     this.listeners.remove(l);
 *   }
 *   
 *   ....
 *   
 *   private void onSongChange(String song) {
 *     listeners.fire().nowPlaying(song);
 *   }
 *   
 * }
 * </code>
 * 
 * @author Noel Grandin
 * @param <T> listener interface
 */
public class ListenerSupport<T>
{
	/**
	 * factory method to reduce code clutter.
	 */
	public static <T> ListenerSupport<T> create(Class<T> listenerClass)
	{
		return new ListenerSupport<T>(listenerClass);
	}

	/**
	 * Using a CopyOnWriteArrayList is necessary because add()/remove() and fire() can happen
	 * simultaneously. 
	 * And it's cheaper than synchronising since traversals vastly out-number modifications.
	 */
	private final List<T> listeners = new CopyOnWriteArrayList<T>();

	private final T fireProxy;
	
	private final Class<T> listenerClass;
	
	private boolean bDontFire = false;

	@SuppressWarnings("unchecked")
	public ListenerSupport(Class<T> listenerClass)
	{
		this.listenerClass = listenerClass;
		this.fireProxy = (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), 
				new Class [] { listenerClass } , new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				if (bDontFire) return null;
				
				// if the method is not one of the methods on the listener interface
				if (method.getDeclaringClass() != ListenerSupport.this.listenerClass) {
					return null;
				}
				try {
					for (T listener : listeners) {
						method.invoke(listener, args);
					}
					return null;
				} catch (InvocationTargetException ex) {
					throw ex.getCause();
				}
			}
		});
	}
	
	/**
	 * The runnable is executed synchronously, and while it is running, no listeners are fired.
	 * This is useful when we want to make some changes to the UI, but we don't want notifications to get fired.
	 */
	public void noFire(Runnable runnable)
	{
		bDontFire = true;
		try {
			runnable.run();
		} finally {
			bDontFire = false;
		}
	}

	public T fire()
	{
		return fireProxy;
	}

	public void add(T listener)
	{
		this.listeners.add(listener);
	}
	
	public void remove(T listener)
	{
		this.listeners.remove(listener);
	}
	
}
