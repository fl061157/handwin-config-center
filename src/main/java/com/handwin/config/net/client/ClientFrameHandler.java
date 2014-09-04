package com.handwin.config.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.handwin.config.net.BaseFrame;
import com.handwin.config.net.ConfigQueryFrame;
import com.handwin.config.net.PongFrame;

/**
 * 
 * @author fangliang
 *
 */
@Sharable
public class ClientFrameHandler extends SimpleChannelInboundHandler<BaseFrame> {
	
	private Channel channel ;
	private final String host ;
	private final int port ;
	private EventLoopGroup loopGroup ;
	//private Object lock = new Object() ;
	private ReentrantReadWriteLock readAndWiteLock = new ReentrantReadWriteLock();
	private  ReentrantReadWriteLock.ReadLock readLock = readAndWiteLock.readLock() ;
	private  ReentrantReadWriteLock.WriteLock writeLock = readAndWiteLock.writeLock() ;
	private Condition condition = writeLock.newCondition() ;
	private ConfigQueryFrameHandler configQueryFrameHandler ;
	
	
	public static final int RECONNECT_SECONDS = 5 ;
	public static final int WRITE_TIME_OUT =  500 ;
	
	public ClientFrameHandler(String host , int port , ConfigQueryFrameHandler configQueryFrameHandler) {
		this.host = host ;
		this.port = port ;
		this.configQueryFrameHandler = configQueryFrameHandler ;
	}
	
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, BaseFrame msg)
			throws Exception {
		if( msg instanceof PongFrame ) {
			//LOG IT
			System.out.println("That's Pong Frame ") ;
		} else if( msg instanceof ConfigQueryFrame ) {
			ConfigQueryFrame configQueryFrame =(ConfigQueryFrame) msg ;
			System.out.println( "Region: " + configQueryFrame.getConfigMessage().getRegion() + "   Business: " + configQueryFrame.getConfigMessage().getBusiness()  ) ; 
			configQueryFrameHandler.handle(configQueryFrame);
		} //TODO Other Message
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		writeLock.lock();
		try {
			channel = ctx.channel() ; 
			condition.signalAll(); 
		} finally {
			writeLock.unlock(); 
		} //TODO 发送自己的需求列表 ， 还是 访问时候触发
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		final EventLoop loop = ctx.channel().eventLoop();
        channel = null ;
        loop.schedule(new Runnable() {
            public void run() { 
                try {
                	start( loop ) ;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, RECONNECT_SECONDS , TimeUnit.SECONDS);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close() ;
		cause.printStackTrace(); 
	}
	
	public void write(BaseFrame baseFrame ) throws InterruptedException {
		readLock.lock(); 
		try {
			long start = System.currentTimeMillis();
			while( channel == null || ! channel.isActive() ) {
				try {
					condition.await( WRITE_TIME_OUT , TimeUnit.MILLISECONDS ) ;
				} catch (Exception e) {
					throw new RuntimeException("Channel Is InActive !");
				}
				if ( System.currentTimeMillis() - start > WRITE_TIME_OUT ) {
                     break;
                }
			}
			if( channel == null || ! channel.isActive() ) {
				throw new RuntimeException("Channel Is InActive !");
			}
			channel.writeAndFlush( baseFrame ) ; 
		} finally {
			readLock.unlock(); 
		}
	}
	
	
	public void start() throws InterruptedException {
		configureBootstrap(new Bootstrap() , new NioEventLoopGroup( 2 ) ).connect().sync() ;
	}
	
	public void start( EventLoopGroup loopGroup ) throws InterruptedException {
        this.loopGroup = loopGroup ;
        configureBootstrap(new Bootstrap() , loopGroup ).connect().sync() ;
	}
	
	public void shutDown() {
		if( this.loopGroup != null ) {
			this.loopGroup.shutdownGracefully() ;
		}
	}
	
	private synchronized Bootstrap configureBootstrap(Bootstrap bootStrap , EventLoopGroup eventLoopGroup )  {
        try {
        	bootStrap.group(eventLoopGroup).channel(NioSocketChannel.class).remoteAddress( host , port  ) 
                                 .handler( new ClientChannelInitializer( this ) ) ;
        } catch (Exception e) {
                e.printStackTrace() ;
        }
        return bootStrap ;
	}
	

}
