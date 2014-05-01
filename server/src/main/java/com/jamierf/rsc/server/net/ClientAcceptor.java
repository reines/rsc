package com.jamierf.rsc.server.net;

import com.codahale.metrics.MetricRegistry;
import com.jamierf.rsc.server.net.codec.packet.Packet;
import io.dropwizard.lifecycle.Managed;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class ClientAcceptor implements Managed {

    private final InetSocketAddress address;
    private final AtomicReference<Channel> channel;
    private final ServerBootstrap bootstrap;
    private final LogicHandler logicHandler;
    private final ClientPipelineFactory pipelineFactory;

    public ClientAcceptor(MetricRegistry metricRegistry, int port) {
        address = new InetSocketAddress(port);
        channel = new AtomicReference<>();

        logicHandler = new LogicHandler(metricRegistry);
        pipelineFactory = new ClientPipelineFactory(metricRegistry, logicHandler);

        final ExecutorService bossExecutor = Executors.newCachedThreadPool();
        final ExecutorService workerExecutor = Executors.newCachedThreadPool();

        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(bossExecutor, workerExecutor));
        bootstrap.setPipelineFactory(pipelineFactory);

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

        channel.set(bootstrap.bind(address));
    }

    @Override
    public synchronized void stop() throws Exception {
        final Channel channel = this.channel.getAndSet(null);
        if (channel == null)
            return;

        channel.close().awaitUninterruptibly();
        logicHandler.closeChannels().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }
}
