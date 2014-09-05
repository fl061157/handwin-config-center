package com.handwin.config.net.client;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 
 * @author fangliang
 *
 * @param <Request>
 * @param <Response>
 */
public class ResponseFuture<T> { 

	
	private final boolean cancellable ;
	
	private boolean done ;
	
	private Throwable cause ;
	
	private int waiters ;
	
	private static final Throwable CANCELLED = new Throwable() ;
	
	private T response ;
	
	
	public ResponseFuture() {
		this.cancellable = false ;
	}
	
	public ResponseFuture<T> setResponse(T response) { 
		this.response = response;
		return this ;
	}
	
	 
	
	public T get() {
		return response ;
	}

	public boolean isDone() {
		return done ;
	}

	public boolean isSuccess() {
		return done && cause == null ;
	}

	public Throwable getCause() {
		if( cause != CANCELLED ) {
			return cause ;
		}
		return null  ;
	}
	
	
	//建议加上超时时间 
	public ResponseFuture<T> await() throws InterruptedException {
		if( Thread.interrupted() ) {
			throw new InterruptedException() ;
		}
		synchronized( this ) {
			
			while( !done ) {
				waiters++ ;
				try {
					this.wait();
				} finally {
					waiters-- ;
				}
			}
			
		}
		return this ;
	}

	public ResponseFuture<T> awaitUninterruptibly() {
		boolean interrupted = false ;
		synchronized( this ) {
			while( !done ) {
				waiters++ ;
				try {
					this.wait() ;
				} catch( InterruptedException e ) {
					interrupted = true ;
				} finally {
					waiters-- ;
				}
			}
		}
		if( interrupted ) {
			Thread.currentThread().interrupt() ;
		}
		return this;
	}

	
	private ResponseFuture<T> await0(long timeoutNanos, boolean interruptable) throws InterruptedException , TimeoutException  {
		
		if( interruptable && Thread.interrupted() ) {
			throw new InterruptedException() ;
		}
		long startTime = timeoutNanos <= 0 ? 0 : System.nanoTime() ;
		long waitTime = timeoutNanos ;
		boolean interrupted = false ;
		
		try {
			
			synchronized( this ) {
				
				if( waitTime <= 0 ) {
					throw new TimeoutException("Client TimeOut !") ;
				}
				if(  done  ) {
					return this ;
				}
				
				waiters++ ;
				
				try {
					while( true ) {
						try {
							this.wait(waitTime / 1000000, (int) (waitTime % 1000000));
						} catch (InterruptedException e) {
							if( interruptable ) {
								throw e ;
							} else {
								interrupted = true ;
							}
						}
						
						waitTime = timeoutNanos - (System.nanoTime() - startTime);
						
						if( waitTime <= 0 ) {
							throw new TimeoutException("Client TimeOut !") ;
						}
						if( done ) {
							return this ;
						}
					}
					
				} finally {
					waiters-- ;
				}
			}
			
			
		} finally {
			if( interrupted ) {
				Thread.interrupted() ;
			}
		}
	}
	
	
	public ResponseFuture<T> await(long timeout, TimeUnit unit)
			throws InterruptedException , TimeoutException {
		try {
			return await0( unit.toNanos(timeout) , true );
		} catch (InterruptedException e) {
			throw new InternalError( );
		}
	}

	public ResponseFuture<T> await(long timeoutMillis) throws InterruptedException , TimeoutException {
		try {
			return await0(MILLISECONDS.toNanos(timeoutMillis), true);
	    } catch (InterruptedException e) {
	    	throw new InterruptedException("Time Out !");
	    }
	}

	public ResponseFuture<T> awaitUninterruptibly(long timeout, TimeUnit unit) throws TimeoutException { 
		try {
			return await0( unit.toNanos(timeout) , false );
		} catch (InterruptedException e) {
			throw new InternalError();
		} 
	}

	public ResponseFuture<T> awaitUninterruptibly(long timeoutMillis) throws TimeoutException  {
		try {
			return await0(MILLISECONDS.toNanos(timeoutMillis), false );
	    } catch (InterruptedException e) {
	    	throw new InternalError();
	    }
	}
	
	
	/**
	 * 解除阻塞
	 * @return
	 */
	public boolean setSuccess() {
		synchronized( this ) {
			
			if( done ) {
				return false  ;
			}
			
			done = true ;
			
			if( waiters > 0 ) {
				
				notifyAll() ;
				
			}
			
			return true ;
			
		} 
	}
	
	
	public boolean setFailure( Throwable cause ) {
		
		synchronized( this ) {
			
			if( done ) {
				return false ;
			}
			
			this.cause = cause ;
			done = true ;
			
			if( waiters > 0 ) {
				
				notifyAll() ;
				
			}
			
			return true ;
		}
	}
	
	public boolean cancel() {
		
		if( !cancellable ) {
			return false ;
		}
		
		cause = CANCELLED ;
		done = true ;
		
		if( waiters > 0 ) {
			notifyAll() ;
		}
		
		return true ;
		
	}
	
	

}
