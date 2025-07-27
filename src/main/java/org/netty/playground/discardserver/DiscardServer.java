package org.netty.playground.discardserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.ArrayList;
import java.util.List;

public class DiscardServer {
    private int[] ports;

    public DiscardServer(int[] ports) {
        this.ports = ports;
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
                    // The logic should be defined in the pipeline as a list of consecutive steps
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addFirst(new LoggingHandler(LogLevel.INFO)).addLast(new DiscardServerHandler());
                        }
                    })

                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            List<ChannelFuture> futures = new ArrayList<>();

            for (int port : ports) {
                ChannelFuture future = serverBootstrap.bind(port).sync();
                System.out.println("Server started and listening on port " + port);
                futures.add(future);
            }

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            futures.getFirst().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int[] ports = {8080, 8081, 8082};

        new DiscardServer(ports).run();
    }
}
