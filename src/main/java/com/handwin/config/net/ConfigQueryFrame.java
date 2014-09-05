package com.handwin.config.net;

import io.netty.buffer.ByteBuf;

import com.handwin.config.proto.MessageProto;
import com.handwin.config.proto.PortoSerializable;

/**
 * 
 * @author fangliang
 *
 */
public class ConfigQueryFrame extends BaseFrame {
	
	private MessageProto.ConfigMessage.Builder builder ;
	private MessageProto.ConfigMessage configMessage ;
	private static PortoSerializable<MessageProto.ConfigMessage> portoSerializable = new PortoSerializable<MessageProto.ConfigMessage>(MessageProto.ConfigMessage.getDefaultInstance()) ; 
	
	public ConfigQueryFrame() {
		super(FrameType.ConfigQuery) ;
	}
	
	public MessageProto.ConfigMessage getConfigMessage() {
		return configMessage;
	}
	
	public void setConfigMessage(MessageProto.ConfigMessage configMessage) {
		this.configMessage = configMessage;
	}
	
	public ConfigQueryFrame setBusiness(String business) {
		if( builder == null ) {
			builder =  MessageProto.ConfigMessage.newBuilder() ;
		}
		builder.setBusiness( business ) ; 
		return this ;
	}
	
	public ConfigQueryFrame setRegion(String region) {
		if( builder == null ) {
			builder =  MessageProto.ConfigMessage.newBuilder() ;
		}
		builder.setRegion( region ) ;
		return this ;
	}
	
	public ConfigQueryFrame build() {
		if( builder != null ) {
			if( this.getSequence() != 0 ) {
				builder.setSequence( (int)this.getSequence() ) ;  //TODO 此处类型有问题
			}
			configMessage = builder.build() ;
		}
		return this ;
	}
	
	@Override
	public long getSequence() {
		if( configMessage != null ) {
			return configMessage.getSequence() ;
		} else {
			return super.getSequence() ;
		}
		
	}
	
	
	@Override
	public void encode(ByteBuf out) {
		out.writeByte( this.getFrameType().getType() ) ; 
		byte[] data = portoSerializable.ser( configMessage ) ;
		out.writeInt( data.length ) ;
		out.writeBytes( data ) ;
	}
	

}
