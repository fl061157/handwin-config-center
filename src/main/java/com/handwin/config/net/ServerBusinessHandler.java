package com.handwin.config.net;

import java.util.Set;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.handwin.config.bean.ConfigInfo;
import com.handwin.config.mapper.ConfigInfoMapper;
import com.handwin.config.net.SessionManager.Resources;
import com.handwin.config.proto.MessageProto;

/**
 * 
 * @author fangliang
 *
 */
@Sharable
@Service
public class ServerBusinessHandler extends SimpleChannelInboundHandler<BaseFrame> { 
	
	@Autowired
	private ConfigInfoMapper configInfoMapper ;
	
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, BaseFrame msg)
			throws Exception {
		if( msg instanceof ConfigQueryFrame ) { // I WANT
			ConfigQueryFrame configQueryFrame = (ConfigQueryFrame) msg ;
			String region = configQueryFrame.getConfigMessage().getRegion() ; //TODO 抽取出来
			String business = configQueryFrame.getConfigMessage().getBusiness() ;
			ConfigInfo configInfo = configInfoMapper.get(region, business) ; //TODO 处理异常
			MessageProto.ConfigMessage.Builder configMessageBuilder = MessageProto.ConfigMessage.newBuilder() ;
			configMessageBuilder.setBusiness( configInfo.getBusiness() ) ; 
			configMessageBuilder.setRegion( configInfo.getRegion() ) ;
			configMessageBuilder.setContent( configInfo.getContent() ) ; 
			MessageProto.ConfigMessage configMessage = configMessageBuilder.build() ;
			ConfigQueryFrame response = new ConfigQueryFrame() ;
			response.setConfigMessage(configMessage) ; 
			ctx.writeAndFlush( response ) ;  //TODO 需要注册关注的点 当点变化的时候 通知
			SessionManager.getInstance().subscribe(new Resources(region, business), ctx.channel() );  
			
  		} else if( msg instanceof ConfigSetFrame ) {
			ConfigSetFrame configSetFrame = (ConfigSetFrame) msg ; //TODO 通知
			ConfigInfo configInfo = new ConfigInfo() ;
			configInfo.setBusiness( configSetFrame.getConfigMessage().getBusiness() ) ; 
			configInfo.setRegion( configSetFrame.getConfigMessage().getRegion()  ) ;
			configInfo.setContent( configSetFrame.getConfigMessage().getContent() ) ;
			//TODO CHANGE TO UPDATE
			//configInfoMapper.create(configInfo) ;
			Set<Channel> channelSet = SessionManager.getInstance().lookUp(new Resources(configSetFrame.getConfigMessage().getRegion()  , configSetFrame.getConfigMessage().getBusiness() )) ;
			if( channelSet != null ) {
				MessageProto.ConfigMessage.Builder configMessageBuilder = MessageProto.ConfigMessage.newBuilder() ;
				configMessageBuilder.setBusiness( configInfo.getBusiness() ) ; 
				configMessageBuilder.setRegion( configInfo.getRegion() ) ;
				configMessageBuilder.setContent( configInfo.getContent() ) ; 
				MessageProto.ConfigMessage configMessage = configMessageBuilder.build() ;
				ConfigQueryFrame response = new ConfigQueryFrame() ;
				response.setConfigMessage(configMessage) ; 
				for( Channel channel : channelSet ) {
					channel.writeAndFlush( response ) ; 
				}
			}
  		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace(); 
		ctx.close() ;
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel() ;
		SessionManager.getInstance().unSubscribeAll(channel); 
	}
	

}
