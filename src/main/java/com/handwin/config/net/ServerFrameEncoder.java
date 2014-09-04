package com.handwin.config.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * @author fangliang
 *
 */
public class ServerFrameEncoder extends MessageToByteEncoder<BaseFrame> {
	
	@Override
	protected void encode(ChannelHandlerContext ctx, BaseFrame msg, ByteBuf out)
			throws Exception {
		msg.encode(out) ;
	}
	
}
