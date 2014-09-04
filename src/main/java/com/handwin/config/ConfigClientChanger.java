package com.handwin.config;

import com.handwin.config.net.ConfigQueryFrame;
import com.handwin.config.net.ConfigSetFrame;
import com.handwin.config.net.client.ClientFrameHandler;
import com.handwin.config.net.client.ConfigQueryFrameHandler;

public class ConfigClientChanger {
	
	public static class DefaultConfigQueryFrameHandler implements ConfigQueryFrameHandler {
		public void handle(ConfigQueryFrame configQuery) {
			System.out.println( "ConfigQueryHandler ............. " + configQuery.getConfigMessage().getContent() + "     "  ); 
		}
	}
	
	
    public static void main( String[] args ) throws Exception  {
    	ClientFrameHandler handler = new ClientFrameHandler("127.0.0.1", 5555, new DefaultConfigQueryFrameHandler() ) ; 
        handler.start() ; 
        ConfigSetFrame frame = new ConfigSetFrame() ;
        
        frame = frame.setBusiness("REDIS").setRegion("0086").setContent("192.168.1.255:6379").build() ; 
        handler.write(frame);   
    	
    }
	

}
