package com.handwin.config.net;

import io.netty.buffer.ByteBuf;

/**
 * 
 * @author fangliang
 *
 */
public class PingFrame extends BaseFrame {
	
	public PingFrame() {
		super(FrameType.Ping) ;
	}
	
	@Override
	public void encode(ByteBuf out) {
		out.writeByte( this.getFrameType().getType() ) ;
	}
}
