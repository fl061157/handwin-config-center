package com.handwin.config.net.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import com.handwin.config.net.PingFrame;

/**
 * 
 * @author fangliang
 *
 */
public class ClientHeartbeatHandler extends ChannelHandlerAdapter {
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if( ! ( evt instanceof IdleStateEvent ) ) {
            return ;
		}
		IdleStateEvent e = (IdleStateEvent) evt ;
	    if( e.state() == IdleState.READER_IDLE ) {
	            ctx.close() ;
	    } else if( e.state() == IdleState.WRITER_IDLE  ) {
	            ctx.writeAndFlush( new PingFrame() ) ;
	    }
	}
	

}
