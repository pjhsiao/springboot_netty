package com.springboot.netty.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@Component
@Sharable
public class HelloServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LogManager.getLogger();
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		logger.info(HelloServerHandler.class.getName()+".channelActive menthod start");
//		logger.info("remoteAddress {}",ctx.channel().remoteAddress().toString());
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		logger.info(HelloServerHandler.class.getName()+".channelRead menthod start");
		ByteBuf inBuffer = ByteBuf.class.cast(msg);
		String receivedStr = inBuffer.toString(CharsetUtil.UTF_8);
		logger.info("the received str is 【{}】", receivedStr);
		ctx.write(Unpooled.copiedBuffer("Server say >> hello "+ receivedStr, CharsetUtil.UTF_8));
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		logger.info(HelloServerHandler.class.getName()+".channelReadComplete menthod start");
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(cause.getMessage());
		cause.printStackTrace();
		ctx.close();
	}
}
