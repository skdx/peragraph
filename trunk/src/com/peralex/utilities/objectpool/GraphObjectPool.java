package com.peralex.utilities.objectpool;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A relatively simply class designed mainly for supplying arrays to the graph code.
 * 
 * This optimization is mainly useful when we have more than one graph on the screen.
 * It reduces memory usage, and makes better use of the CPU's L2 cache.
 * 
 * As a side effect, is also makes GC a little faster, by reducing the cycling of large memory
 * objects through the various generations in the collector.
 * 
 * @author Noel Grandin
 */
public final class GraphObjectPool
{

	/**
	 * we only allocate arrays of the largest size the code demands.
	 * That way, we need fewer individual arrays, and since they are mainly being used on the event thread,
	 * these arrays are recycled very quickly, so we typically only need one of them.
	 */
	private static int intArraySize = 100;
	/**
	 * pool of arrays.
	 */
	private static final Set<MyWeakReference> pool = new HashSet<MyWeakReference>();

	/** not meant to be instantiated. */
	private GraphObjectPool() {}
	
	/**
	 * returns an int[] that is at least minSize, but may be bigger
	 */
	public static synchronized int [] checkOutIntArray(int minSize)
	{
		if (minSize<=intArraySize)
		{
			// loop through the pool, looking for an array that has not been GC'ed
			for (Iterator<MyWeakReference> iter=pool.iterator(); iter.hasNext(); ) {
				final MyWeakReference ref = iter.next();
				iter.remove();
				int [] obj = ref.get();
				if (obj!=null) {
					Arrays.fill(obj, 0);
					return obj;
				}
			}
			// didn't find anything, so create a new array
			return new int[intArraySize];
		}
		else
		{
			// increase the array size in the pool
			intArraySize = minSize;
			// clear the pool
			pool.clear();
			// create a new object
			return new int[intArraySize];
		}
	}

	public static synchronized void checkIn(int [] object)
	{
		if (object==null) return;
		
		// ignore it, was checked out before we increased the size of the arrays in the pool
		if (object.length<intArraySize) return;
		
		MyWeakReference newWeakRef = new MyWeakReference(object);
		if (pool.contains(newWeakRef)) {
			throw new IllegalStateException("trying to check the same object in twice");
		}
		pool.add(newWeakRef);
	}

	private static class MyWeakReference extends WeakReference<int []> {
		
		private final int hashCode;
		
		public MyWeakReference(int [] referent)
		{
			super(referent);
			hashCode = referent.hashCode();
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + hashCode;
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			MyWeakReference other = (MyWeakReference) obj;
			if (hashCode != other.hashCode)
				return false;
			
			if (get()==other.get()) 
				return true;
			if (other.get()==null) {
				return false;
			}
			if (get()==null) {
				return false;
			}
			return other.get().equals(get());
		}
	}
	
}
