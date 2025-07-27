package org.netty.playground.echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // It does not write the message to the wire. It only buffers it internally
        ctx.write(msg);
        // It writes the buffered message out to the wire and
        // Netty automatically release the message
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //Close the connection when an exception is raised
        cause.printStackTrace();
        ctx.close();
    }
}
