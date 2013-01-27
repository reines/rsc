package com.jamierf.rsc.server.net;

import com.jamierf.rsc.server.net.codec.packet.Packet;
import com.yammer.dropwizard.lifecycle.Managed;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.internal.ExecutorUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class ClientAcceptor implements Managed {

    private final InetSocketAddress address;
    private final AtomicReference<Channel> channel;
    private final ExecutorService bossExecutor;
    private final ExecutorService workerExecutor;
    private final LogicHandler logicHandler;
    private final ClientPipelineFactory pipelineFactory;

    public ClientAcceptor(int port) {
        address = new InetSocketAddress(port);
        channel = new AtomicReference<Channel>();

        bossExecutor = Executors.newCachedThreadPool();
        workerExecutor = Executors.newCachedThreadPool();

        logicHandler = new LogicHandler();
        pipelineFactory = new ClientPipelineFactory(logicHandler);
    }

    public void addPacketHandler(int id, PacketHandler handler) {
        logicHandler.addPacketHandler(handler);

        this.addPacketType(id, handler.getRequestType());
    }

    public void addPacketType(int id, Class<? extends Packet> type) {
        pipelineFactory.addPacketType(id, type);
    }

    @Override
    public synchronized void start() throws Exception {
        if (channel.get() != null)
            throw new IllegalStateException("Cannot start client acceptor, already started");

        final NioServerSocketChannelFactory serverSocketFactory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
        final ServerBootstrap bootstrap = new ServerBootstrap(serverSocketFactory);

        bootstrap.setPipelineFactory(pipelineFactory);

        channel.set(bootstrap.bind(address));
    }

    @Override
    public synchronized void stop() throws Exception {
        final Channel channel = this.channel.getAndSet(null);
        if (channel == null)
            return;

        channel.close().sync();

        ExecutorUtil.terminate(bossExecutor, workerExecutor);
    }
}
