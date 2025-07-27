package org.netty.playground.timeserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf message = (ByteBuf) msg;
//        try {
//            var numberOfSecondsBetween1900And1970 = 2208988800L;
//            long currentTimeMills = (message.readUnsignedInt() - numberOfSecondsBetween1900And1970) * 1000L;
//            System.out.println(new Date(currentTimeMills));
//            ctx.close();
//        } finally {
//            message.release();
//        }
        UnixTime m = (UnixTime) msg;
        System.out.println(m);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
