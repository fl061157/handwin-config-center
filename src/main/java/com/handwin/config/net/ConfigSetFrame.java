package com.handwin.config.net;

import io.netty.buffer.ByteBuf;

import com.handwin.config.proto.MessageProto;
import com.handwin.config.proto.PortoSerializable;

/**
 * 
 * @author fangliang
 *
 */
public class ConfigSetFrame extends BaseFrame {
	
	private MessageProto.ConfigMessage configMessage ;
	private MessageProto.ConfigMessage.Builder builder ;
	private static PortoSerializable<MessageProto.ConfigMessage> portoSerializable = new PortoSerializable<MessageProto.ConfigMessage>(MessageProto.ConfigMessage.getDefaultInstance()) ;
	
	public ConfigSetFrame() {
		super(FrameType.ConfigSet) ;
	}
	
	public MessageProto.ConfigMessage getConfigMessage() {
		return configMessage;
	}
	
	public void setConfigMessage(MessageProto.ConfigMessage configMessage) {
		this.configMessage = configMessage;
	}
	
	public ConfigSetFrame setBusiness(String business) {
		if( builder == null ) {
			builder =  MessageProto.ConfigMessage.newBuilder() ;
		}
		builder.setBusiness( business ) ; 
		return this ;
	}
	
	public ConfigSetFrame setRegion(String region) {
		if( builder == null ) {
			builder =  MessageProto.ConfigMessage.newBuilder() ;
		}
		builder.setRegion( region ) ;
		return this ;
	}
	
	public ConfigSetFrame setContent(String content) {
		if( builder == null ) {
			builder =  MessageProto.ConfigMessage.newBuilder() ;
		}
		builder.setContent( content ) ;
		return this ;
	}
	
	
	public ConfigSetFrame build() {
		if( builder != null ) {
			configMessage = builder.build() ;
		}
		return this ;
	}
	
	
	@Override
	public void encode(ByteBuf out) {
		out.writeByte( this.getFrameType().getType() ) ; 
		byte[] data = portoSerializable.ser( configMessage ) ;
		out.writeInt( data.length ) ;
		out.writeBytes( data ) ;
	}

}
