package com.handwin.config.net;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Heartbeat 心跳管理模块
 * @author fangliang
 *
 */
public class ServerHeartBeatHandler extends ChannelHandlerAdapter {
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if( ! ( evt instanceof IdleStateEvent ) ) {
            return ;
		}
	    IdleStateEvent e = (IdleStateEvent) evt ;
	    if( e.state() == IdleState.READER_IDLE ) {
	            ctx.close() ;
	    }
	}
	

}
