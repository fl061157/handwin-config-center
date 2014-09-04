package com.handwin.config.net.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 
 * @author fangliang
 *
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	static final int READER_IDLE_TIME_SECONDS = 10   ;
    static final int WRITER_IDLE_TIME_SECONDS = 5   ;
    static final int ALL_IDLE_TIME_SECONDS = 0 ;
    static final String CLIENT_FRAME_ENCODER = "ClientFrameEncoder";
    static final String IDLE_STATE_HANDLER = "IdleStateHandler";
    static final String HEART_BEAT_HANDLER = "HeartBeatHandler";
    static final String CLIENT_FRAME_DECODER = "ClientFrameDecoder";
    static final String CLIENT_FRAME_HANDLER = "ClientFrameHandler";
    
	private ClientFrameHandler clientFrameHandler ;
	public ClientChannelInitializer(ClientFrameHandler clientFrameHandler) {
		this.clientFrameHandler = clientFrameHandler ;
	}
	
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(  CLIENT_FRAME_ENCODER , new ClientFrameEncoder()) ;
		pipeline.addLast( IDLE_STATE_HANDLER , new IdleStateHandler(READER_IDLE_TIME_SECONDS,
                 WRITER_IDLE_TIME_SECONDS , ALL_IDLE_TIME_SECONDS) ) ;
		pipeline.addLast(HEART_BEAT_HANDLER , new ClientHeartbeatHandler() ) ;
		pipeline.addLast(CLIENT_FRAME_DECODER, new ClientFrameDecoder() ) ;
		pipeline.addLast(CLIENT_FRAME_HANDLER, clientFrameHandler ) ;
	}

}
