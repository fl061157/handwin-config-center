package com.handwin.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.handwin.config.net.ServerChannelInitializer;

/**
 * 
 * @author fangliang
 *
 */
@Service
public class ConfigServer {

	@Autowired
	private ServerChannelInitializer serverChannelInitializer ;
	
	private EventLoopGroup bossGroup = new NioEventLoopGroup(1) ;
	private EventLoopGroup workerGroup = new NioEventLoopGroup( Runtime.getRuntime().availableProcessors() * 2 ) ;
	private ServerBootstrap serverBootstrap = new ServerBootstrap() ;
	
	public void start() throws Exception {  // RabbitMQ
		serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
		.childHandler(serverChannelInitializer);
		try {
			 ChannelFuture channelFuture = serverBootstrap.bind(5555).sync() ;
			 channelFuture.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println("OperationComplete Success :" + future.isSuccess() ) ;
				}
			 }) ;
			 Channel channel = channelFuture.channel() ;
			 ChannelFuture closeFuture = channel.closeFuture() ;
             closeFuture.addListener( new GenericFutureListener<Future<Void>>() {
                     public void operationComplete(Future<Void> future) throws Exception {
                    	 System.out.println( "Close ConfigServer !"  ) ;
                     };
             } ) ;
             closeFuture.sync();
		} catch( Exception e ) {
			e.printStackTrace(); 
		} finally {
			bossGroup.shutdownGracefully() ;
			workerGroup.shutdownGracefully() ;
		}
		
	}
	
	public static void main( String[] args ) throws Exception {
		ApplicationContext context = new FileSystemXmlApplicationContext( 
                new String[] { "src/main/resources/spring/root-context.xml" } ) ;
		ConfigServer configServer = context.getBean( ConfigServer.class ) ; 
		configServer.start(); 
	}
	
	
	
	
	
}
