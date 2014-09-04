package com.handwin.config.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

public class PortoSerializable< M extends MessageLite > { 
	
	private M messageLite ;
	
	public PortoSerializable( M messageLite ) {
		this.messageLite = messageLite ;
	}
	

	public byte[] ser(M t) { 
		return t.toByteArray() ;
	}

	@SuppressWarnings("unchecked")
	public M der(byte[] bytes, Class<M> tClass) { 
		M result = null ;
		try {
			result = (M)messageLite.getParserForType().parseFrom( bytes ) ;
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return result ;
	}
	

}
