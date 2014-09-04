package com.handwin.config;

import java.util.concurrent.atomic.AtomicInteger;

import com.handwin.config.net.ConfigQueryFrame;
import com.handwin.config.net.client.ClientFrameHandler;
import com.handwin.config.net.client.ConfigQueryFrameHandler;

/**
 * Hello world!
 *
 */
public class App {
	static AtomicInteger count = new AtomicInteger(0); 
	public static class DefaultConfigQueryFrameHandler implements ConfigQueryFrameHandler {
		public void handle(ConfigQueryFrame configQuery) {
			System.out.println( "ConfigQueryHandler ............. " + configQuery.getConfigMessage().getContent() + "     " + count.getAndIncrement() ); 
		}
	}
	
	
    public static void main( String[] args ) throws Exception  {
    	ClientFrameHandler handler = new ClientFrameHandler("127.0.0.1", 5555, new DefaultConfigQueryFrameHandler() ) ; 
        handler.start() ; 
        ConfigQueryFrame frame = new ConfigQueryFrame() ;
        frame = frame.setBusiness("REDIS").setRegion("0086").build() ;
        for( int i = 0 ; i < 100000 ; i++ ) {
        	handler.write(frame);   
        }
    	
    }
}
