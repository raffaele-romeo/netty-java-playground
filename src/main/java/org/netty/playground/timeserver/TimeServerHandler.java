package org.netty.playground.timeserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    // This method will be invoked when a connection is established and ready to generate traffic
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        final ByteBuf time = ctx.alloc().buffer(4);
//        var numberOfSecondsBetween1900And1970 = 2208988800L;
//        time.writeInt((int) (System.currentTimeMillis() / 1000L + numberOfSecondsBetween1900And1970));
//        final ChannelFuture channelFuture = ctx.writeAndFlush(time);
//        channelFuture.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture cf) throws Exception {
//                assert channelFuture == cf;
//                ctx.close(); // We cannot close the connection straightaway as it may close the connection even before the message is sent
//            }
//        });

        ChannelFuture f = ctx.writeAndFlush(new UnixTime());
        f.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
