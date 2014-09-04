package com.handwin.config.net.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.handwin.config.net.ConfigQueryFrame;
import com.handwin.config.net.FrameType;
import com.handwin.config.net.PongFrame;
import com.handwin.config.proto.MessageProto;
import com.handwin.config.proto.PortoSerializable;

/**
 * 
 * @author fangliang
 *
 */
public class ClientFrameDecoder extends ByteToMessageDecoder {

	enum State {
		Frame ,
		DataBegin ,
		DataEnd ;
	}
	
	private State state ;
	private FrameType frameType ;
	private int dataLength ;
	private PortoSerializable<MessageProto.ConfigMessage>  configPorto = new PortoSerializable<MessageProto.ConfigMessage>(MessageProto.ConfigMessage.getDefaultInstance()) ; 
	
	
	public ClientFrameDecoder() {
		this.state = State.Frame ;
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		switch (state) { 
		case Frame:
			if( in.readableBytes() < FrameType.FRAME_TYPE_BYTES_LENGTH ) {
				return ;
			}
			byte frameTypeByte = in.readByte() ;
			frameType = FrameType.getFrameType(frameTypeByte) ;
			if( frameType == null ) {
				ctx.fireExceptionCaught( new Exception("FrameType Not Exists Exception !") ) ;
			}
			switch (frameType) { 
			case Pong :
				out.add( new PongFrame() )  ; 
				return ;
			case ConfigQuery ://Query Response Set 暂时不设Response
				state = State.DataBegin ;
				return ;
			default:
				return ;
			}
		case DataBegin :
			if( in.readableBytes() < 4 ) {
				return ;
			}
			dataLength = in.readInt() ;
			state = State.DataEnd ;
			return ;
		case DataEnd :
			if( frameType == null || dataLength == 0 ) {
				ctx.fireExceptionCaught( new Exception("Data State Error Exception !") ) ; 
				return ;
			}
			if( in.readableBytes() < dataLength ) {
				return ;
			}
			byte[] dataBytes = new byte[ dataLength ] ;
			in.readBytes( dataBytes ) ; 
			if( frameType == FrameType.ConfigQuery ) {
				try {
					MessageProto.ConfigMessage message = configPorto.der(dataBytes, MessageProto.ConfigMessage.class) ;
					if( message == null ) {
						throw new Exception() ;
					}
					ConfigQueryFrame configFrame = new ConfigQueryFrame() ;
					configFrame.setConfigMessage( message ) ;
					out.add( configFrame ) ;
				} catch (Exception e) {
					ctx.fireExceptionCaught( new Exception("PortoParse Config Exception !") ) ; 
					return ;
				} 
			} //TODO 其余暂不支持 异常情形处理
			state = State.Frame ;
			return ;
		default:
			ctx.fireExceptionCaught(new Exception("Not Exists State Exception! ")) ; 
		}
	}
	
	
	@Override
    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in,
                    List<Object> out) throws Exception {
            decode(ctx, in, out) ;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                    throws Exception {
            super.exceptionCaught(ctx, cause);
            cause.printStackTrace() ;
    }

}
