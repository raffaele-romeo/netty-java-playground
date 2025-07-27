package org.netty.playground.timeserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.netty.playground.echoserver.EchoServerHandler;

import java.util.ArrayList;
import java.util.List;

public class TimeServer {
    private final int port;

    public TimeServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(Runtime.getRuntime().availableProcessors(), NioIoHandler.newFactory());

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    // This is the main channel and the only responsibility is to accept incoming connection
                    .channel(NioServerSocketChannel.class)
                    // Often the logging is the only handler on the main channel as, the channel does not have any other responsibility
                    // than accepting the connection
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // Netty creates a separate channel to manage the communication with the client once the connection is accepted
                    // It creates a new handler instance per channel.
                    // The logic should be defined in the pipeline as a list of consecutive steps
                    // NOTE: it is possible to share the same handler among different connections. You would need to instantiated the EchoServerHandler
                    // outside and pass the instance. Also, the EchoServerHandler needs to be annotated with @ChannelHandler.Sharable
                    .childHandler(new ChannelInitializer<SocketChannel>() { // This is created only once. The initChannel is executed every time a new client connection is established
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            // TimeEncoder is before TimeServerHandler because outbound events flow from tail to the head
                            socketChannel.pipeline().addFirst(new LoggingHandler(LogLevel.INFO)).addLast(new TimeEncoder(), new TimeServerHandler());
                        }
                    })

                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("Server started and listening on port " + port);

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 37;

        new TimeServer(port).run();
    }
}
