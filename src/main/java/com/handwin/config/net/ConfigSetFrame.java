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
	
	@Override
	public void encode(ByteBuf out) {
		out.writeByte( this.getFrameType().getType() ) ; 
		byte[] data = portoSerializable.ser( configMessage ) ;
		out.writeInt( data.length ) ;
		out.writeBytes( data ) ;
	}

}
