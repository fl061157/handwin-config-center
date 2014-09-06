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
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
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
	
	private volatile Channel channel ;
	private final String host ;
	private final int port ;
	private EventLoopGroup loopGroup ;
	private Object lock = new Object() ;
//	private ReentrantReadWriteLock readAndWiteLock = new ReentrantReadWriteLock();
//	private  ReentrantReadWriteLock.ReadLock readLock = readAndWiteLock.readLock() ;
//	private  ReentrantReadWriteLock.WriteLock writeLock = readAndWiteLock.writeLock() ;
//	private Condition condition = writeLock.newCondition() ;
	private ConfigQueryFrameHandler configQueryFrameHandler ;
	private AtomicLong idGenerator = new AtomicLong(1) ; 
	private ExpireWheel<ResponseFuture<BaseFrame>> expireWheel = new ExpireWheel<ResponseFuture<BaseFrame>>();
	private AtomicBoolean start = new AtomicBoolean( false );
	
	
	public static final int RECONNECT_SECONDS = 5 ;
	public static final int WRITE_TIME_OUT =  5000 ;
	
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
		} else if( msg instanceof ConfigQueryFrame ) { //RPC 
			ConfigQueryFrame configQueryFrame =(ConfigQueryFrame) msg ;
			//System.out.println( "Region: " + configQueryFrame.getConfigMessage().getRegion() + "   Business: " + configQueryFrame.getConfigMessage().getBusiness()  ) ; 
			if( msg.getSequence() != 0 ) { //RPC 自己处理 否则 触发 Handle 异步更新之
				ResponseFuture<BaseFrame> responseFuture = expireWheel.remove( msg.getSequence() ) ; 
				if( responseFuture != null ) {
					responseFuture.setResponse(configQueryFrame).setSuccess() ;
				}
			} else {
				configQueryFrameHandler.handle(configQueryFrame);
			}
		} //TODO Other Message
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		 synchronized (lock) {
             channel = ctx.channel()  ;
             lock.notifyAll() ;
     }
//		writeLock.lock();
//		try {
//			condition.signalAll(); 
//		} finally {
//			writeLock.unlock(); 
//		} //TODO 发送自己的需求列表 ， 还是 访问时候触发
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
		if( channel == null ) {
			synchronized ( lock ) {
				InterruptedException interrupted = null ;
				while( channel == null ||  !channel.isActive() ) {
					if( interrupted != null ) {
						throw interrupted ;
					}
					try {
						lock.wait() ;
					} catch (InterruptedException e) {
						interrupted = e ;
					}
				}
			}
		}
		if( channel != null ) {
			channel.writeAndFlush( baseFrame ) ;
		}
	}
	
	public BaseFrame rpc(String region , String business ) throws InterruptedException , TimeoutException  {
		if( channel == null ) {
			synchronized ( lock ) {
				InterruptedException interrupted = null ;
				while( channel == null ||  !channel.isActive() ) {
					if( interrupted != null ) {
						throw interrupted ;
					}
					try {
						lock.wait() ;
					} catch (InterruptedException e) {
						interrupted = e ;
					}
				}
			}
		}
		if( channel != null ) {
			long sequence = idGenerator.getAndIncrement() ;
			ConfigQueryFrame frame = new ConfigQueryFrame().setBusiness(business).setRegion(region);
			frame.setSequence(sequence);
			frame =  frame.build()  ; 
			ResponseFuture<BaseFrame> responseFuture = new ResponseFuture<BaseFrame>();
			expireWheel.put( sequence, responseFuture ); 
			channel.writeAndFlush( frame ) ; 
			return responseFuture.await( 1000, TimeUnit.MILLISECONDS ).get() ; 
		} 
		return null ;
	}
	
	public ClientFrameHandler start() throws InterruptedException {
		if( start.compareAndSet(false, true) ) {
			configureBootstrap(new Bootstrap() , new NioEventLoopGroup( 2 ) ).connect().sync() ;
		}
		return this ;
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
