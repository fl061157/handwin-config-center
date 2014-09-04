package com.handwin.config.net;

import io.netty.buffer.ByteBuf;

/**
 * 
 * @author fangliang
 *
 */
public abstract class BaseFrame {
	
	private FrameType frameType ;
	public BaseFrame(FrameType frameType  ) {
		this.frameType = frameType ;
	}
	
	public FrameType getFrameType() {
		return frameType;
	}

	public abstract void encode(ByteBuf out ) ;
	
	
}
