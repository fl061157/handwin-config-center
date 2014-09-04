package com.handwin.config.net.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.handwin.config.net.BaseFrame;

/**
 * 
 * @author fangliang
 *
 */
public class ClientFrameEncoder extends MessageToByteEncoder<BaseFrame> {
	
	@Override
	protected void encode(ChannelHandlerContext ctx, BaseFrame msg, ByteBuf out)
			throws Exception {
		msg.encode(out) ; 
	}

}
