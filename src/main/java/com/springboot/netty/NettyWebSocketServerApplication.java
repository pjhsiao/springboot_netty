package com.springboot.netty;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.springboot.netty.handlers.HelloServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@SpringBootApplication
public class NettyWebSocketServerApplication implements CommandLineRunner{

	@Autowired
	private Person person;
	
	@Autowired
	private HelloServerHandler helloServerHandler;
	
	private static final Logger logger = LogManager.getLogger();
	private static final int port = 9090;
	
    public static void main(String[] args) {
    	logger.info("Start NettyWebSocketServer Application");
        SpringApplication.run(NettyWebSocketServerApplication.class, args);
    }
    
	@Override
	public void run(String... args) throws Exception {
		logger.info("Run Server Application");
		 EventLoopGroup bossGroup = new NioEventLoopGroup(1);// listen for 9090
		 EventLoopGroup workerGroup = new NioEventLoopGroup();
		   try {
		       ServerBootstrap serverBootstrap = new ServerBootstrap();
		       serverBootstrap.group(bossGroup, workerGroup)
		               .channel(NioServerSocketChannel.class)
		               .option(ChannelOption.SO_BACKLOG, 100)
		               .handler(new LoggingHandler(LogLevel.INFO)) 
		               .childHandler(new ChannelInitializer<SocketChannel>() {
		                   @Override
		                   public void initChannel(SocketChannel socketChannel) {
		                	   socketChannel.pipeline().addLast(helloServerHandler)
		                               .addLast(new ChannelHandler() {
										@Override
										public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
											logger.info("handlerRemoved method");
											logger.info("==========event end==========");  
										}
										
										@Override
										public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
											 logger.info("==========event start==========");  
											 logger.info("handlerAdded method");	
											 logger.info("{}", person.getMsg());
											 logger.info("handlerAdded remote ip: "+ctx.channel().remoteAddress().toString());  
										}
										
										@Override
										public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
											logger.error(cause.toString());
										}
									});
		                   }
		               });
		      
		       ChannelFuture cf = serverBootstrap.bind(port).sync();
		       logger.info("server started on port {}", port);
		       cf.channel().closeFuture().sync();
		   } catch (Exception e) {
		       logger.error("server exception", e);
		   } finally {
		       bossGroup.shutdownGracefully();
		       workerGroup.shutdownGracefully();
		       logger.info("server stopped");
		   }
	}
   
	@Component
	class Person{
		public String getMsg() {
			return "The component of person has injected";
		}
	}

}