package com.xxl.rpc.netcom.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.netcom.common.codec.RpcCallbackFuture;
import com.xxl.rpc.netcom.common.codec.RpcResponse;

/**
 * rpc netty client
 * @author xuxueli 2015-10-31 18:00:27
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
	private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
		RpcCallbackFuture future = RpcCallbackFuture.futurePool.get(response.getRequestId());
		future.setResponse(response);
		// 这个重新写会pool中，意义不明
		RpcCallbackFuture.futurePool.put(response.getRequestId(), future);
    }
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	logger.error(">>>>>>>>>>> xxl-rpc netty client caught exception", cause);
        ctx.close();
    }

}
