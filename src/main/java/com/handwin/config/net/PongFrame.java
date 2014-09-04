package com.handwin.config.net;

import io.netty.buffer.ByteBuf;

/**
 * 
 * @author fangliang
 *
 */
public class PongFrame extends BaseFrame {
	
	public PongFrame() {
		super(FrameType.Pong) ;
	}
	
	@Override
	public void encode(ByteBuf out) {
		out.writeByte( this.getFrameType().getType() ) ;
	}
	
}
