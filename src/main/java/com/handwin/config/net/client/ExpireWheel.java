package com.handwin.config.net.client;

/**
 * 
 * @author fangliang
 *
 * @param <T>
 */
public class ExpireWheel<T> {
	
	public static final int CAPACITY_2P14 = 2 << ( 14 -1 ) ;
	
	public static final int CAPACITY_2P16 =  2 << ( 16 -1 ); 
	
	public static final int CAPACITY_2P18 = 2 << ( 18 - 1 );
	 
	public static final int CAPACITY_2P19 = 2 << ( 19 -1 );
	
	private Entry<T>[] wheel ;
	
	private int length ;
	
	
	@SuppressWarnings("unchecked")
	public ExpireWheel( int capacity ) {
		this.length = capacity ;
		wheel = new Entry[capacity]; 
	}
	
	public ExpireWheel() {
		this( CAPACITY_2P14 ) ; 
	}
	
	public void put( long id , T t ) {
		int index = calculateIndex(id) ;
		wheel[index] = new Entry<T>(id, t) ; 
 	}
	
	public T get( long id ) {
		int index = calculateIndex(id) ;
		Entry<T> entry = wheel[index];
		if( entry == null || entry.id != id ) {
			return null ;
		}
		return entry.t ;
	}
	
	
	public T remove(long id) {
		int index = calculateIndex(id) ;
		Entry<T> entry = wheel[index];
		if( entry != null && entry.id == id ) {
			wheel[index] = null ;
			return entry.t ;
		}
		return null ;
	}
	
	
	
	private int calculateIndex(long id) {
		return (int) id % this.length ;
	}
	
	private static class Entry<T> {
		final long id ;
		final T t ;
		Entry(long id, T t){ 
			this.id = id ;
			this.t = t ;
		}
		
	}
	
	
}
