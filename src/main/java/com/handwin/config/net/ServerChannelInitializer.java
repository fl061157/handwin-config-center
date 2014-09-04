package com.handwin.config.net;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 
 * @author fangliang
 *
 */
@Service
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	final static String SERVER_IDLE_STATE_HANDLER = "ServerIdleStateHandler";
	final static int ALL_IDLE_TIMES = 60 ;
	final static String SERVER_HEART_BEAT_HANDLER = "SeverHeartBeatHandler";
	final static String SERVER_DECODER  = "ServerDecoder";
	final static String SERVER_BUSINESS_HANDLER = "SERVER_BUSINESS_HANDLER";
	final static String SERVER_ENCODER = "ServerEncoder";
	
	@Autowired
	private ServerBusinessHandler serverBusinessHandler ;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		 ChannelPipeline pipeline = ch.pipeline();
         pipeline.addLast(SERVER_IDLE_STATE_HANDLER , new IdleStateHandler( 0 , 0 , ALL_IDLE_TIMES  ) ) ;
         pipeline.addLast(SERVER_HEART_BEAT_HANDLER , new ServerHeartBeatHandler() ) ;
         pipeline.addLast(SERVER_ENCODER , new ServerFrameEncoder() ) ;
         pipeline.addLast(SERVER_DECODER , new ServerFrameDecoder() ) ; 
         pipeline.addLast(SERVER_BUSINESS_HANDLER, serverBusinessHandler ) ;
	}

}
