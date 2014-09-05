package com.handwin.config.net;

import io.netty.buffer.ByteBuf;

/**
 * 
 * @author fangliang
 *
 */
public abstract class BaseFrame {
	
	private FrameType frameType ;
	private long sequence ;
	public BaseFrame(FrameType frameType  ) {
		this.frameType = frameType ;
	}
	
	public FrameType getFrameType() {
		return frameType;
	}

	public abstract void encode(ByteBuf out ) ;
	
	public long getSequence() {
		return sequence;
	}
	
	public void setSequence(long sequence) {
		this.sequence = sequence;
	}
	
	
}
